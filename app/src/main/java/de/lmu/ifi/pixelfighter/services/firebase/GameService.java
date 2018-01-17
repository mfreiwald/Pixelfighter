package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.GamePlayer;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.Player;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.UpdateCallback;

/**
 * Created by michael on 14.12.17.
 */

public class GameService {

    protected final DatabaseReference dbRef;
    private final Game game;
    private final String playerKey;
    private final Team team;
    private GamePlayer gamePlayer;
    private final Callback callback;
    private Map<DatabaseReference, ValueEventListener> listeners = new HashMap<>();

    public GameService(Game game, String playerKey, Callback callback) {
        dbRef = FirebaseDatabase.getInstance().getReference().child("games/"+game.getKey());
        this.game = game;
        this.playerKey = playerKey;
        Team foundTeam = null;
        GamePlayer foundGamePlayer = null;
        for(Map.Entry<String, Map<String, GamePlayer>> team : this.game.getPlayers().entrySet()) {
            String teamKey = team.getKey();
            for(Map.Entry<String, GamePlayer> playerOfTeam : team.getValue().entrySet()) {
                if(playerOfTeam.getKey().equals(playerKey)) {
                    foundTeam = Team.valueOf(teamKey);
                    foundGamePlayer = playerOfTeam.getValue();
                    break;
                }
            }
            if(foundTeam != null && foundGamePlayer != null) break;
        }

        if(foundTeam == null && foundGamePlayer == null) ;

        this.team = foundTeam;
        this.gamePlayer = foundGamePlayer;
        this.callback = callback;
        createListeners();
    }

    private void createListeners() {
        activeListener();
        playerListener();
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

    private void playerListener() {

        DatabaseReference ref = dbRef.child("players").child(this.team.name()).child(this.playerKey);
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                gamePlayer = dataSnapshot.getValue(GamePlayer.class);
                callback.onGamePlayerChanged(gamePlayer);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        listeners.put(ref, listener);
    }

    public void foundBomb() {
        this.gamePlayer.setBombAmount(this.gamePlayer.getBombAmount()+1);
        updateGamePlayer();
    }

    public void placedBomb() {
        this.gamePlayer.setBombAmount(this.gamePlayer.getBombAmount()-1);
        updateGamePlayer();
    }

    private void updateGamePlayer() {
        DatabaseReference ref = dbRef.child("players").child(this.team.name()).child(this.playerKey);
        ref.setValue(this.gamePlayer, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                callback.onGamePlayerChanged(gamePlayer);
            }
        });
    }

    public interface Callback {
        void onGameOver();
        void onGamePlayerChanged(GamePlayer gamePlayer);
    }
}
