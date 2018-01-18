package de.lmu.ifi.pixelfighter.services.firebase.game;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.game.Board;
import de.lmu.ifi.pixelfighter.models.game.Game;
import de.lmu.ifi.pixelfighter.models.game.GamePlayer;

import static de.lmu.ifi.pixelfighter.services.firebase.game.GameWrapperService.FB_GAMES;
import static de.lmu.ifi.pixelfighter.services.firebase.game.GameWrapperService.FB_GAMES_BOARD;
import static de.lmu.ifi.pixelfighter.services.firebase.game.GameWrapperService.FB_GAMES_GAME;
import static de.lmu.ifi.pixelfighter.services.firebase.game.GameWrapperService.FB_GAMES_PLAYERS;

/**
 * Created by michael on 17.01.18.
 */

public class GamesService {

    DatabaseReference dbRootRef = FirebaseDatabase.getInstance().getReference();

    public String createGame() {
        DatabaseReference newGameRef = dbRootRef.child(FB_GAMES).push();
        newGameRef.child(FB_GAMES_GAME).setValue(new Game());
        newGameRef.child(FB_GAMES_BOARD).setValue(new Board(20, 20));
        newGameRef.child(FB_GAMES_PLAYERS).setValue("");
        return newGameRef.getKey();
    }

    public void joinGame(final String gameKey, final String playerKey) {
        final DatabaseReference dbRef = dbRootRef.child(FB_GAMES).child(gameKey);

        // check if player contains
        dbRef.child(FB_GAMES_PLAYERS).child(playerKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GamePlayer tmpPlayer = dataSnapshot.getValue(GamePlayer.class);
                if(tmpPlayer == null) {
                    dbRef.child(FB_GAMES_PLAYERS).child(playerKey).setValue(new GamePlayer(playerKey, Team.None));
                    Log.d("JoinGame", "Player added to Game");
                } else {
                    Log.d("JoinGame", "Already in Game");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
