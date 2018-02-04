package de.lmu.ifi.pixelfighter.services.firebase.callbacks;

/**
 * Created by michael on 23.11.17.
 */

public interface ServiceCallback<Model> {
    void success(Model model);

    void failure(String message);
}
