package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.DataSnapshot;

import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 23.11.17.
 */

public class AuthenticationService extends BaseKeyService<Player> {

    public AuthenticationService(String childRef) {
        super(childRef);
    }

    @Override
    public Player wrap(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(Player.class);
    }

    public void register(String username, final ServiceCallback<Player> callback) {



    }
}
