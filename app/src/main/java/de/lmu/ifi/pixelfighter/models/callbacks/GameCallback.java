package de.lmu.ifi.pixelfighter.models.callbacks;

import de.lmu.ifi.pixelfighter.models.Game;

/**
 * Created by michael on 23.11.17.
 */

public interface GameCallback extends Callback<Game> {
    void onClosed(); //Parameter with statics?
    void onModelNotExists();
}
