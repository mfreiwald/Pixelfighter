package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.UpdateCallback;

/**
 * Created by michael on 14.12.17.
 */

public class GameService {

    protected final DatabaseReference dbRef;
    private final Game game;
    private final Callback callback;
    private Map<DatabaseReference, ValueEventListener> listeners = new HashMap<>();

    public GameService(Game game, Callback callback) {
        dbRef = FirebaseDatabase.getInstance().getReference().child("games/"+game.getKey());
        this.game = game;
        this.callback = callback;
        createListeners();
    }

    private void createListeners() {
        activeListener();
    }

    public void register() {
        for(Map.Entry<DatabaseReference, ValueEventListener> listener : listeners.entrySet()) {
            listener.getKey().addValueEventListener(listener.getValue());
        }
    }

    public void unregister() {
        for(Map.Entry<DatabaseReference, ValueEventListener> listener : listeners.entrySet()) {
            listener.getKey().removeEventListener(listener.getValue());
        }
    }

    private void activeListener() {
        DatabaseReference ref = dbRef.child("active");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                game.setActive(dataSnapshot.getValue(Boolean.class));
                if(!game.isActive()) {
                    callback.onGameOver();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        listeners.put(ref, listener);
    }

    public interface Callback {
        void onGameOver();
    }
}
