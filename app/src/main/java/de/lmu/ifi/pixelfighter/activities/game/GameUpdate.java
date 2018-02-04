package de.lmu.ifi.pixelfighter.activities.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.firebase.Database;
import de.lmu.ifi.pixelfighter.services.firebase.GenericReference;

/**
 * Created by michael on 28.01.18.
 */

public class GameUpdate {

    private final String gameKey;
    private final String uid;

    private final GenericReference<Game> gameReference;
    private final GenericReference<Boolean> activeGameReference;
    private GenericReference<GamePlayer> gamePlayerReference;
    private final List<GenericReference<Pixel>> pixelReferences;

    private final OnGameUpdateCallback gameUpdates;

    /**
     * Ist erst nach @onGameReady nicht mehr null
     */
    private GameSettings gameSettings;

    public GameUpdate(String gameKey, String uid, final OnGameUpdateCallback gameUpdates) {
        this.gameKey = gameKey;
        this.uid = uid;
        gameReference = Database.Game(gameKey).Game();
        activeGameReference = Database.Game(gameKey).GameActive();
        pixelReferences = new ArrayList<>();
        this.gameUpdates = gameUpdates;
    }

    public void load() {
        gameReference.addSingleListener(new GenericReference.ValueListener<Game>() {
            @Override
            public void onData(Game object) {
                GameSettings gameSettings = new GameSettings(gameKey, uid);
                gameSettings.setBoard(object.getBoard());
                gameSettings.setTeam(searchTeam(object, uid));
                gameSettings.setGamePlayer(searchGamePlayer(object, uid));

                Map<Team, Integer> statics = new HashMap<>();
                statics.put(Team.None, 0);
                statics.put(Team.Blue, 0);
                statics.put(Team.Green, 0);
                statics.put(Team.Red, 0);
                statics.put(Team.Yellow, 0);

                gamePlayerReference = Database.Game(gameKey).GamePlayer(uid, gameSettings.getTeam());

                for (int x = 0; x < object.getBoard().getWidth(); x++) {
                    for (int y = 0; y < object.getBoard().getHeight(); y++) {
                        pixelReferences.add(Database.Game(gameKey).Pixel(x, y));

                        Team pixelTeam = object.getBoard().getPixels().get(x).get(y).getTeam();
                        statics.put(pixelTeam, statics.get(pixelTeam)+1);
                    }
                }
                gameSettings.setStatics(statics);
                onGameReady(gameSettings);
            }

            @Override
            public void onError(GenericReference.Error error) {

            }
        });
    }

    private void onGameReady(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
        this.gameUpdates.onGameReady(gameSettings);
    }

    public void addListeners() {
        if(this.gameSettings == null) return;

        activeGameReference.addListener(activeListener);
        gamePlayerReference.addListener(gamePlayerListener);
        for(GenericReference<Pixel> pixelReference : pixelReferences) {
            pixelReference.addListener(pixelListener);
        }
    }

    public void removeListeners() {
        if(this.gameSettings == null) return;

        activeGameReference.removeListener(activeListener);
        gamePlayerReference.removeListener(gamePlayerListener);
        for(GenericReference<Pixel> pixelReference : pixelReferences) {
            pixelReference.removeListener(pixelListener);
        }
    }
    
    private GenericReference.ValueListener<Boolean> activeListener = new GenericReference.ValueListener<Boolean>() {
        @Override
        public void onData(Boolean object) {
            if(!object) {
                gameUpdates.onGameOver();
            }
        }

        @Override
        public void onError(GenericReference.Error error) {

        }
    };

    private GenericReference.ValueListener<GamePlayer> gamePlayerListener = new GenericReference.ValueListener<GamePlayer>() {
        @Override
        public void onData(GamePlayer object) {
            gameSettings.setGamePlayer(object);
            gameUpdates.onGamePlayerChanged(object);
        }

        @Override
        public void onError(GenericReference.Error error) {

        }
    };

    private GenericReference.ValueListener<Pixel> pixelListener = new GenericReference.ValueListener<Pixel>() {
        @Override
        public void onData(Pixel object) {
            gameSettings.getBoard().getPixels().get(object.getX()).set(object.getY(), object);
        }

        @Override
        public void onError(GenericReference.Error error) {

        }
    };

    private Team searchTeam(Game object, String uid) {
        // search for my team
        for(Map.Entry<String, Map<String, GamePlayer>> playersInTeam : object.getPlayers().entrySet()) {
            if(playersInTeam.getValue().containsKey(uid)) {
                return Team.valueOf(playersInTeam.getKey());
            }
        }
        return Team.None;
    }

    private GamePlayer searchGamePlayer(Game object, String uid) {
        for(Map.Entry<String, Map<String, GamePlayer>> playersInTeam : object.getPlayers().entrySet()) {
            if(playersInTeam.getValue().containsKey(uid)) {
                return playersInTeam.getValue().get(uid);
            }
        }
        return null;
    }

}
