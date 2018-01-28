package de.lmu.ifi.pixelfighter.activities.game;

import de.lmu.ifi.pixelfighter.activities.ZoomableGameActivity;
import de.lmu.ifi.pixelfighter.models.GamePlayer;

/**
 * Created by michael on 28.01.18.
 */

public interface OnGameUpdateCallback {
    void onGameReady(GameSettings gameSettings);
    void onGameOver();
    void onGamePlayerChanged(GamePlayer gamePlayer);
}
