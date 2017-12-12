package de.lmu.ifi.pixelfighter.game;

import android.util.Log;

import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Team;

/**
 * Created by michael on 11.12.17.
 */

public class Rules {

    private static boolean ALLOW_DIAGONAL = false;

    public static boolean validate(final Board board, final Team team, final int x, final int y) {

        Log.d("Rules", "Validate " + team + " at ("+x+","+y+")");
        Rules rules = new Rules(board, team, x, y);

        Log.d("Rules", "isFree = " + rules.isFree());
        if(!rules.isFree())
            return false;

        Log.d("Rules", "isAtOwnTeam = " + rules.isAtOwnTeam());
        if(!rules.isAtOwnTeam())
            return false;


        return true;
    }

    private final Board board;
    private final Team team;
    private final int x;
    private final int y;

    public Rules(Board board, Team team, int x, int y) {
        this.board = board;
        this.team = team;
        this.x = x;
        this.y = y;
    }

    private boolean isFree() {
        return this.board.getPixels().get(x).get(y).getTeam() == Team.None &&
                this.board.getPixels().get(x).get(y).getPlayerKey().isEmpty();
    }

    private boolean isAtOwnTeam() {
        if(!containsColor())
            return true;
        // - - -
        // - x -
        // - - -


        for(int _x = this.x-1; _x <= this.x+1; _x++) {
            for(int _y = this.y-1; _y <= this.y+1; _y++) {
                if(_x < 0 || _x >= this.board.getWidth())
                    continue;
                if(_y < 0 || _y >= this.board.getHeight())
                    continue;

                if(!ALLOW_DIAGONAL) {
                    if(
                            (_x == this.x-1 && _y == this.y-1) ||
                            (_x == this.x+1 && _y == this.y-1) ||
                            (_x == this.x-1 && _y == this.y+1) ||
                            (_x == this.x+1 && _y == this.y+1)
                            )
                        continue;
                }

                Log.d("Rules:isAtOwnTeam", "Check field ("+_x+","+_y+")");
                if(this.board.getPixels().get(_x).get(_y).getTeam().equals(this.team))
                    return true;
            }
        }
        return false;
    }

    private boolean containsColor()
    {
        for(int x=0; x<board.getWidth(); x++) {
            for(int y=0; y<board.getHeight(); y++) {
                if(board.getPixels().get(x).get(y).getTeam() == this.team) {
                    Log.d("Rules", "ContainsColor True");
                    return true;
                }
            }
        }
        Log.d("Rules", "ContainsColor False");
        return false;
    }
}
