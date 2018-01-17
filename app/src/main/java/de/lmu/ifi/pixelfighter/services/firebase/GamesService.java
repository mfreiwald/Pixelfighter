package de.lmu.ifi.pixelfighter.services.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.models.callbacks.GameCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 23.11.17.
 */

public class GamesService extends BaseKeyService<Game> {

    private static GamesService INSTANCE;
    public static GamesService getInstance() {
        if(INSTANCE == null)
            INSTANCE = new GamesService();
        return INSTANCE;
    }

    private GamesService() {
        super("games");
    }

    @Override
    public Game wrap(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(Game.class);
    }

    private void searchActiveGames(final ServiceCallback<Game> callback) {

        findAll(new ServiceCallback<List<Game>>() {
            @Override
            public void success(List<Game> games) {

                // ToDo: Besseren Suchalgorithmus
                boolean found = false;
                for(Game game : games) {
                    Log.d("Games", "Game with Key " + game.getKey());
                    if(game.isActive()) {
                        callback.success(game);
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    //callback.failure("No active game found!");
                    createNewGame(callback);
                }
            }

            @Override
            public void failure(String message) {
                callback.failure(message);
            }
        });

    }

    final static int DEFAULT_SIZE = 20;
    // ToDo: Sollte vom Server gelöst weden
    private void createNewGame(ServiceCallback<Game> callback) {
        Board board = new Board(DEFAULT_SIZE,DEFAULT_SIZE);
        Game game = new Game(board);
        Log.d("GamesService", "Add Game " + game.toString());
        add(game, callback);
    }

    public void loadGame(String key, final GameCallback callback) {
        findSingle(key, new ServiceCallback<Game>() {
            @Override
            public void success(Game game) {
                if(game.isActive()) {
                    callback.onLoaded(game);
                } else {
                    callback.onClosed();
                }
            }

            @Override
            public void failure(String message) {
                if(message.contains("Model is null")) {
                    callback.onModelNotExists();
                } else {
                    callback.onError(message);
                }
            }
        });
    }

    public void searchGame(final Callback<Game> callback) {
        searchActiveGames(new ServiceCallback<Game>() {
            @Override
            public void success(Game game) {
                callback.onLoaded(game);
            }

            @Override
            public void failure(String message) {
                callback.onError(message);
            }
        });
    }

    public void joinGame(Game game, final Player player, final Team team, final Callback<Game> callback) {
        this.dbRef.child(game.getKey()).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Game game = mutableData.getValue(Game.class);
                if(game == null) {
                    return Transaction.success(mutableData);
                }
                Map teams = game.getPlayers().get(team.name());
                if(teams == null) {
                    game.getPlayers().put(team.name(), new HashMap<String, GamePlayer>());
                }
                GamePlayer gamePlayer = new GamePlayer();
                gamePlayer.setPlayerKey(player.getKey());
                game.getPlayers().get(team.name()).put(player.getKey(), gamePlayer);
                mutableData.setValue(game);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if(databaseError == null) {
                    Game game = wrapModel(dataSnapshot); //dataSnapshot.getValue(Game.class);
                    callback.onLoaded(game);
                } else {
                    callback.onError(databaseError.getMessage());
                }
            }
        });
    }

    public void searchAndJoinGame(final Player player, final Team team, final Callback<Game> callback) {
        searchGame(new Callback<Game>() {
            @Override
            public void onLoaded(Game game) {
                joinGame(game, player, team, callback);
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

}
