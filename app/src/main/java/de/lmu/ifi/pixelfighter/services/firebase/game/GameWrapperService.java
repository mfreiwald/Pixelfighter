package de.lmu.ifi.pixelfighter.services.firebase.game;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.lmu.ifi.pixelfighter.models.game.Board;
import de.lmu.ifi.pixelfighter.models.game.Game;
import de.lmu.ifi.pixelfighter.models.game.GamePlayer;

/**
 * Created by michael on 17.01.18.
 */

public class GameWrapperService {

    public static final String FB_GAMES = "games2";
    public static final String FB_GAMES_GAME = "game";
    public static final String FB_GAMES_BOARD = "board";
    public static final String FB_GAMES_PLAYERS = "players";

    DatabaseReference dbRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference gameRef;
    DatabaseReference boardRef;
    DatabaseReference playerRef;

    private Game game;
    private Board board;
    private GamePlayer player;
    private final Callback callback;

    public final GamePlayerService playerService;

    private ValueEventListener gameListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Game tmpGame = dataSnapshot.getValue(Game.class);
            if(tmpGame == null) return;
            game = tmpGame;
            callback.onGameChanged(game);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener boardListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Board tmpBoard = dataSnapshot.getValue(Board.class);
            if(tmpBoard == null) return;
            board = tmpBoard;
            callback.onBoardChanged(board);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private ValueEventListener playerListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            GamePlayer tmpPlayer = dataSnapshot.getValue(GamePlayer.class);
            if(tmpPlayer == null) return;
            player = tmpPlayer;
            playerService.setPlayer(player);
            callback.onPlayerChanged(player);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public GameWrapperService(String gameKey, String playerKey, Callback callback) {
        gameRef = dbRootRef.child(FB_GAMES).child(gameKey).child(FB_GAMES_GAME);
        boardRef = dbRootRef.child(FB_GAMES).child(gameKey).child(FB_GAMES_BOARD);
        playerRef = dbRootRef.child(FB_GAMES).child(gameKey).child(FB_GAMES_PLAYERS).child(playerKey);
        this.callback = callback;
        this.playerService = new GamePlayerService(gameKey, playerKey);
    }

    public void onResume() {
        gameRef.addValueEventListener(gameListener);
        boardRef.addValueEventListener(boardListener);
        playerRef.addValueEventListener(playerListener);
    }

    public void onPause() {
        gameRef.removeEventListener(gameListener);
        boardRef.removeEventListener(boardListener);
        playerRef.removeEventListener(playerListener);
    }

    public Game getGame() {
        return game;
    }

    public Board getBoard() {
        return board;
    }

    public GamePlayer getPlayer() {
        return player;
    }

    public interface Callback {
        void onGameChanged(Game game);
        void onBoardChanged(Board board);
        void onPlayerChanged(GamePlayer player);
    }

}
