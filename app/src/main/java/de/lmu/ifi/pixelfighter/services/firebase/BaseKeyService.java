package de.lmu.ifi.pixelfighter.services.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import de.lmu.ifi.pixelfighter.models.BaseKeyModel;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

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


    protected void add(final Model model, final ServiceCallback<Model> callback) {
        String key = dbRef.push().getKey();
        DatabaseReference objRef = dbRef.child(key).getRef();
        model.setKey(key);
        objRef.setValue(model, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError == null) callback.success(model);
                else callback.failure(databaseError.getMessage());
            }
        });
    }
}
