package de.lmu.ifi.pixelfighter.demo3;

import android.graphics.Color;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by michael on 20.11.17.
 */

public class Pixel {


    int id;
    int x;
    int y;
    String teamName;

    public Pixel() {
    }

    public Pixel(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.teamName = "";
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
