package de.lmu.ifi.pixelfighter.game;

import java.util.concurrent.ThreadLocalRandom;

import de.lmu.ifi.pixelfighter.models.Team;

/**
 * Created by michael on 12.12.17.
 */

public class RandomTeam {

    public static Team evaluateRandomTeam() {
        return Team.values()[random()];
    }

    private static int random() {
        return ThreadLocalRandom.current().nextInt(1, 4 + 1);
    }

}
