package de.lmu.ifi.pixelfighter.services.firebase.callbacks;

/**
 * Created by michael on 11.12.17.
 */

public interface UpdateCallback<Model> {
    void onUpdate(Model model);
}
