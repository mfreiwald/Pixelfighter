package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.UserData;

/**
 * Created by michael on 18.01.18.
 */

public class Database {


    public static GenericReference<UserData> UserData(String uid) {
        return new GenericReference<UserData>(FirebaseDatabase.getInstance().getReference().child("users").child(uid)) {
            @Override
            public UserData wrap(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(UserData.class);
            }
        };
    }


    public static GenericReference<Game> Game(String key) {
        return new GenericReference<Game>(FirebaseDatabase.getInstance().getReference().child("games").child(key)) {
            @Override
            public Game wrap(DataSnapshot dataSnapshot) {
                return dataSnapshot.getValue(Game.class);
            }
        };
    }



}
