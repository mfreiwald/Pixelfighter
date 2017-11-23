package de.lmu.ifi.pixelfighter.models;

import com.google.firebase.database.Exclude;

/**
 * Created by michael on 23.11.17.
 */

public abstract class BaseKeyModel extends BaseModel {

    @Exclude
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
