package de.lmu.ifi.pixelfighter.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by michael on 23.11.17.
 */

public class Player extends BaseKeyModel {

    private String username;
    private String activeGameKey;

}
