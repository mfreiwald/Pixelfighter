package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.DataSnapshot;

import de.lmu.ifi.pixelfighter.models.BaseKeyModel;
import de.lmu.ifi.pixelfighter.models.BaseModel;

/**
 * Created by michael on 23.11.17.
 */

public abstract class BaseKeyService<Model extends BaseKeyModel> extends BaseService<Model> {

    public BaseKeyService(String childRef) {
        super(childRef);
    }

    @Override
    public Model wrapKey(DataSnapshot dataSnapshot, Model model) {
        model.setKey(dataSnapshot.getKey());
        return model;
    }


}
