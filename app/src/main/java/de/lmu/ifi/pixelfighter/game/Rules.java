package de.lmu.ifi.pixelfighter.game;

import android.util.Log;

import java.util.ArrayList;

import de.lmu.ifi.pixelfighter.models.Board;
import de.lmu.ifi.pixelfighter.models.Pixel;
import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;

/**
 * Created by michael on 11.12.17.
 */

public class Rules {

    public final static int X_DEFAULT_SIZE = 20;

    public final static int Y_DEFAULT_SIZE = 20;

    private static boolean ALLOW_DIAGONAL = false;

    private final static double PERCNTGE_OF_NECES_SURR_ENEMIES = 0.5;

    public final static double BOMB_PLCMNT_PROB = 0.1;

    public static boolean validate(final Board board, final Team team, final int x, final int y) {

        Log.d("Rules", "Validate " + team + " at (" + x + "," + y + ")");
        Rules rules = new Rules(board, team, x, y);

        Log.d("Rules", "isFree = " + rules.isFree());
        if (!rules.isFree())
            return false;

        Log.d("Rules", "isAtOwnTeam = " + rules.isAtOwnTeam());
        if (!rules.isAtOwnTeam())
            return false;


        return true;
    }

    public static ArrayList<Pixel> checkForEnemiesToConvert(Board board, final Team team, final int x, final int y) {
        Rules rules = new Rules(board, team, x, y);

        ArrayList<Pixel> adjacentPixels = rules.extractSurroundingPixelsFor(x, y);
        ArrayList<Pixel> adjacentEnemies = rules.extractSurroundingEnemies(adjacentPixels);

        ArrayList<Pixel> updateList = rules.calculateOverwritings(adjacentEnemies, adjacentPixels.size());

        return updateList;
    }

    //Bombe darf nur auf eigener Farbe platziert werden
    public static boolean validateForPixelModification(final Team team, Pixel currentPixel) {
        return currentPixel.getTeam().equals(team);
    }

    public static PixelModification checkForLootModification(Board board, Pixel currentPixel) {
        if (board.getPixels().get(currentPixel.getX()).get(currentPixel.getY()).getPixelMod().equals(PixelModification.Bomb)) {
            Log.d("RULES", "empty Pixel had bomb");
            return PixelModification.Bomb;
        } else {
            Log.d("RULES", "empty Pixel had no Mod");
            return PixelModification.None;
        }
    }


    private final Board board;
    private final Team team;
    private final int x;
    private final int y;

    private Rules(Board board, Team team, int x, int y) {
        this.board = board;
        this.team = team;
        this.x = x;
        this.y = y;
    }

    private ArrayList<Pixel> extractSurroundingPixelsFor(final int x, final int y) {
        ArrayList<Pixel> adjacentPixels = new ArrayList<>();

        for (int _x = x - 1; _x <= x + 1; _x++) {
            for (int _y = y - 1; _y <= y + 1; _y++) {
                if (_x == x && _y == y)
                    continue;
                if (_x < 0 || _x >= board.getWidth())
                    continue;
                if (_y < 0 || _y >= board.getHeight())
                    continue;

                adjacentPixels.add(board.getPixels().get(_x).get(_y));

            }
        }

        Log.d("RULES", "adj. Pixel amount: " + adjacentPixels.size());

        return adjacentPixels;
    }

    private ArrayList<Pixel> extractSurroundingEnemies(ArrayList<Pixel> adjacentPixels) {
        ArrayList<Pixel> adjacentEnemies = new ArrayList<>();

        for (Pixel pixel : adjacentPixels) {
            Team enemyTeam = pixel.getTeam();
            if (!enemyTeam.equals(team) && enemyTeam != Team.None) {
                adjacentEnemies.add(pixel);
            }
        }

        Log.d("RULES", "adj. enemies amount:" + adjacentEnemies.size());

        return adjacentEnemies;
    }

    private ArrayList<Pixel> calculateOverwritings(ArrayList<Pixel> adjacentEnemies, int numberOfSurrPixels) {
        ArrayList<Pixel> updateList = new ArrayList<>();
        int absoluteAmtOfNecPixels = (int) (Math.ceil(numberOfSurrPixels * PERCNTGE_OF_NECES_SURR_ENEMIES)); //ceil rundet IMMER auf
        Log.d("RULES", "Neccs. surrd. enemies to convert: " + absoluteAmtOfNecPixels);

        //If there is at least one enemy, check this enemy's surrounding pixels,
        // to see if there are 3 or more ally pixels -> would turn this pixel into ally
        if (adjacentEnemies.size() > 0) {
            for (Pixel enemy : adjacentEnemies) {
                Team enemyTeamName = enemy.getTeam();
                ArrayList<Pixel> adjacentAllies = new ArrayList<>();

                for (Pixel surroundingPixel : extractSurroundingPixelsFor(enemy.getX(), enemy.getY())) {
                    //Allies of the initial pixel coming into checkSurroundingPixels
                    Team _team = surroundingPixel.getTeam();
                    if (!_team.equals(enemyTeamName) && _team != Team.None) {
                        adjacentAllies.add(surroundingPixel);
                    }
                }

                Log.d("RULES", "Allies amount: " + adjacentAllies.size());

                //Turn this enemy into an ally
                if (adjacentAllies.size() >= absoluteAmtOfNecPixels
                        && checkIfAlliesStandTogether(adjacentAllies)
                    // => Hier check, ob diese Allies sich auch gegenseitig berühren und Mauer bilden
                        ) {
                    if (enemy.getPixelMod() == PixelModification.None) {
                        enemy.setTeam(team);
                        updateList.add(enemy);
                    } else {
                        Log.d("RULES", "Found MOD on Pixel, running check");
                        updateList.addAll(calculateAffectedPixelsByMod(enemy));
                    }
                }
            }
        }

        Log.d("RULES", "updateList: " + updateList.toString());
        return updateList;
    }

    private boolean checkIfAlliesStandTogether(ArrayList<Pixel> adjacentAllies) {

        for (Pixel ally : adjacentAllies) {
            if (!isAtOwnTeam(ally))
                return false;
        }

        return true;
    }

    private ArrayList<Pixel> calculateAffectedPixelsByMod(Pixel pixel) {
        ArrayList<Pixel> affectedPixels = new ArrayList<>();

        if (pixel.getPixelMod() == PixelModification.Bomb) {
            Team newTeam = pixel.getTeam();
            affectedPixels.addAll(extractSurroundingPixelsFor(pixel.getX(), pixel.getY()));
            pixel.setPixelMod(PixelModification.None); //Reset mod

            for (Pixel p : affectedPixels) {
                p.setTeam(newTeam);
            }

            affectedPixels.add(pixel); //Add central pixel too

            Log.d("RULES", affectedPixels.size() + " Pixels affected by Mod: Bomb. Coloring into Team: " + newTeam);
        }

        return affectedPixels;
    }

    private boolean isFree() {
        return this.board.getPixels().get(x).get(y).getTeam() == Team.None &&
                this.board.getPixels().get(x).get(y).getPlayerKey().isEmpty();
    }

    private boolean isAtOwnTeam() {
        if (!containsColor())
            return true;
        // - - -
        // - x -
        // - - -

        for (int _x = this.x - 1; _x <= this.x + 1; _x++) {
            for (int _y = this.y - 1; _y <= this.y + 1; _y++) {
                if (_x < 0 || _x >= this.board.getWidth())
                    continue;
                if (_y < 0 || _y >= this.board.getHeight())
                    continue;

                if (!ALLOW_DIAGONAL) {
                    if (
                            (_x == this.x - 1 && _y == this.y - 1) ||
                                    (_x == this.x + 1 && _y == this.y - 1) ||
                                    (_x == this.x - 1 && _y == this.y + 1) ||
                                    (_x == this.x + 1 && _y == this.y + 1)

                            )
                        continue;
                }

                if (this.board.getPixels().get(_x).get(_y).getTeam().equals(this.team))
                    return true;
            }
        }
        return false;
    }

    private boolean isAtOwnTeam(Pixel pixel) {

        for (int _x = pixel.getX() - 1; _x <= pixel.getX() + 1; _x++) {
            for (int _y = pixel.getY() - 1; _y <= pixel.getY() + 1; _y++) {
                if (_x < 0 || _x >= this.board.getWidth())
                    continue;
                if (_y < 0 || _y >= this.board.getHeight())
                    continue;

                if (!ALLOW_DIAGONAL) {
                    if (
                            (_x == pixel.getX() - 1 && _y == pixel.getY() - 1) ||
                                    (_x == pixel.getX() + 1 && _y == pixel.getY() - 1) ||
                                    (_x == pixel.getX() - 1 && _y == pixel.getY() + 1) ||
                                    (_x == pixel.getX() + 1 && _y == pixel.getY() + 1)

                            )
                        continue;
                }

                if (pixel.getTeam().equals(this.team))
                    return true;
            }
        }
        return false;
    }


    //Hier sollten noch booleans hin, um das pro Farbe nur jeweils 1 mal am Anfang durchlaufen zu müssen
    private boolean containsColor() {
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                if (board.getPixels().get(x).get(y).getTeam() == this.team) {
                    Log.d("Rules", "ContainsColor True");
                    return true;
                }
            }
        }
        Log.d("Rules", "ContainsColor False");
        return false;
    }
}
