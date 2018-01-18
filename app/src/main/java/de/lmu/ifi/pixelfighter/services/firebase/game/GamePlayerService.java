package de.lmu.ifi.pixelfighter.services.firebase.game;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.lmu.ifi.pixelfighter.models.PixelModification;
import de.lmu.ifi.pixelfighter.models.Team;
import de.lmu.ifi.pixelfighter.models.game.GamePlayer;

import static de.lmu.ifi.pixelfighter.services.firebase.game.GameWrapperService.FB_GAMES;
import static de.lmu.ifi.pixelfighter.services.firebase.game.GameWrapperService.FB_GAMES_PLAYERS;

/**
 * Created by michael on 17.01.18.
 */

public class GamePlayerService {

    private final DatabaseReference dbRootRef = FirebaseDatabase.getInstance().getReference();
    private final DatabaseReference dbRef;

    private GamePlayer player;

    public GamePlayerService(String gameKey, String playerKey) {
        dbRef = dbRootRef.child(FB_GAMES).child(gameKey).child(FB_GAMES_PLAYERS).child(playerKey);
    }

    protected void setPlayer(GamePlayer player) {
        this.player = player;
    }

    public void chooseTeam(Team team) {
        if(this.player == null) return;
        if(this.player.getTeam() != Team.None) return;
        dbRef.child("team").setValue(team.name());
    }

    public void placeModification(PixelModification modification) {
        if(this.player == null) return;
        dbRef.child("weaponAmount").child(modification.name()).setValue(this.player.getWeaponAmount().get(modification.name())-1);
    }

    public void foundModification(PixelModification modification) {
        if(this.player == null) return;
        dbRef.child("weaponAmount").child(modification.name()).setValue(this.player.getWeaponAmount().get(modification.name())+1);
    }





}
