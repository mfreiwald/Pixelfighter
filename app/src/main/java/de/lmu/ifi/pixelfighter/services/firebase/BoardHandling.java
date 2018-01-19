package de.lmu.ifi.pixelfighter.services.firebase;

import de.lmu.ifi.pixelfighter.game.Rules;
import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 19.01.18.
 */

public class BoardHandling {

    private final String gameKey;

    public BoardHandling(String gameKey) {
        this.gameKey = gameKey;
    }

    public void placePixel(final Board board, final int x, final int y, final String uid, final Team team, final PixelModification modification, final ServiceCallback<Pixel> callback) {
        Database.Game(gameKey).Pixel(x, y).runTransaction(new GenericReference.Handler<Pixel>() {
            @Override
            public Pixel doTransaction(Pixel mutable) {
                Rules rules = new Rules(board, team, x, y);

                // Check if Pixel can be set
                if (!rules.isFree()) return null;

                // Check if Pixel is at own Team
                // need current board
                if (!rules.isAtOwnTeam()) return null;

                // Check if Pixel was modificated
                if (mutable.getPixelMod() != PixelModification.None) {
                    // do something with the modification
                    // exp. earn it
                }

                mutable.setPlayerKey(uid);
                mutable.setTeam(team);
                mutable.setPixelMod(modification);
                return mutable;
            }

            @Override
            public void onComplete(boolean changed, Pixel object) {
                if (changed) {
                    callback.success(object);
                } else {
                    callback.failure("Not valid to set");
                }
            }
        });
    }


}
