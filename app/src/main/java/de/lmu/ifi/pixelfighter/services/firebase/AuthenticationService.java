package de.lmu.ifi.pixelfighter.services.firebase;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
        this.add(player, new ServiceCallback<Player>() {
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

    public void login(final String key, final Callback<Player> callback) {
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        firebaseLogin(task.getResult().getUser(), key, callback);
                    } else {
                        callback.onError(task.getException().getMessage());
                    }
                }
            });
        } else {
            firebaseLogin(FirebaseAuth.getInstance().getCurrentUser(), key, callback);
        }
    }

    private void firebaseLogin(FirebaseUser user, final String key, final Callback<Player> callback) {
        this.findSingle(key, new ServiceCallback<Player>() {
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
