package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.DataSnapshot;

import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Game;

/**
 * Created by michael on 28.11.17.
 */

public class BoardService extends BaseService<Board> {

    private Board board;

    BoardService(Game game) {
        super("games/"+game.getKey()+"/board");
        this.board = game.getBoard();
    }

    @Override
    protected Board wrap(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(Board.class);
    }

    public void registerListiners() {
        for(int x=0; x<this.board.getWidth(); x++) {
            for(int y=0; y<this.board.getHeight(); y++) {
                // TODO:
                dbRef.child("{x}").child("{y}");

            }
        }
    }
}
