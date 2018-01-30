package de.lmu.ifi.pixelfighter.game;

import java.util.concurrent.ThreadLocalRandom;

import de.lmu.ifi.pixelfighter.models.Team;

/**
 * Created by michael on 12.12.17.
 */

public class RandomTeam {

    public static Team evaluateRandomTeam() {
        return Team.values()[random(1, 4)];
    }

    public static int random(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

}
