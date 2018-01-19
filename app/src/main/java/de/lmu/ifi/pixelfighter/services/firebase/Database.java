package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;

import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Game;
import de.lmu.ifi.pixelfighter.models.Pixel;
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

            @Override
            public UserData wrap(MutableData mutableData) {
                return mutableData.getValue(UserData.class);
            }
        };
    }

    public static GameResult Game(String key) {
        return new GameResult(key);
    }

    public static class GameResult {

        private final String key;
        private final DatabaseReference shortReference;
        private GameResult(String key) {
            this.key = key;
            shortReference = FirebaseDatabase.getInstance().getReference().child("games").child(key);
        }

        public GenericReference<Game> Game() {
            return new GenericReference<Game>(shortReference) {
                @Override
                public Game wrap(DataSnapshot dataSnapshot) {
                    return dataSnapshot.getValue(Game.class);
                }

                @Override
                public Game wrap(MutableData mutableData) {
                    return mutableData.getValue(Game.class);
                }
            };
        }

        public GenericReference<Board> Board() {
            return new GenericReference<Board>(shortReference.child("board")) {
                @Override
                public Board wrap(DataSnapshot dataSnapshot) {
                    return dataSnapshot.getValue(Board.class);
                }

                @Override
                public Board wrap(MutableData mutableData) {
                    return mutableData.getValue(Board.class);
                }
            };
        }

        public GenericReference<Pixel> Pixel(int x, int y) {
            return new GenericReference<Pixel>(shortReference.child("board").child("pixels").child(Integer.toString(x)).child(Integer.toString(y))) {
                @Override
                public Pixel wrap(DataSnapshot dataSnapshot) {
                    return dataSnapshot.getValue(Pixel.class);
                }

                @Override
                public Pixel wrap(MutableData mutableData) {
                    return mutableData.getValue(Pixel.class);
                }
            };
        }
    }






}
