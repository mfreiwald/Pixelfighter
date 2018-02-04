package de.lmu.ifi.pixelfighter.models.callbacks;

/**
 * Created by michael on 23.11.17.
 */

public interface Callback<Model> {
    void onLoaded(Model model);

    void onError(String message);
}
