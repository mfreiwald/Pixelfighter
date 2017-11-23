package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.DataSnapshot;

import java.util.List;

import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.models.callbacks.GameCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 23.11.17.
 */

public class GameService extends BaseKeyService<Game> {

    private static GameService INSTANCE;
    public static GameService getInstance() {
        if(INSTANCE == null)
            INSTANCE = new GameService();
        return INSTANCE;
    }

    private GameService() {
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
                boolean found = false;
                for(Game game : games) {
                    if(game.isActive()) {
                        callback.success(game);
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    callback.failure("No active game found!");
                }
            }

            @Override
            public void failure(String message) {

            }
        });

    }

    public static void load(String key, final GameCallback callback) {
        getInstance().findSingle(key, new ServiceCallback<Game>() {
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
                callback.onError(message);
            }
        });
    }

    public static void joinGame(final Callback<Game> callback) {
        GameService.getInstance().searchActiveGames(new ServiceCallback<Game>() {
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

}
