package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.DataSnapshot;

import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 23.11.17.
 */

public class AuthenticationService extends BaseKeyService<Player> {

    private static AuthenticationService INSTANCE;
    public static AuthenticationService getInstance() {
        if(INSTANCE == null)
            INSTANCE = new AuthenticationService();
        return INSTANCE;
    }

    private AuthenticationService() {
        super("players");
    }

    @Override
    public Player wrap(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(Player.class);
    }

    public void register(String username, final Callback<Player> callback) {
        Player player = new Player(username);
        getInstance().add(player, new ServiceCallback<Player>() {
            @Override
            public void success(Player player) {
                callback.onLoaded(player);
            }

            @Override
            public void failure(String message) {
                callback.onError(message);
            }
        });
    }

    public void login(String key, final Callback<Player> callback) {
        getInstance().findSingle(key, new ServiceCallback<Player>() {
            @Override
            public void success(Player player) {
                callback.onLoaded(player);
            }

            @Override
            public void failure(String message) {
                callback.onError(message);
            }
        });
    }
}
