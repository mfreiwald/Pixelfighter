package de.lmu.ifi.pixelfighter.services.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.pixelfighter.models.BaseModel;
import de.lmu.ifi.pixelfighter.models.callbacks.Callback;
import de.lmu.ifi.pixelfighter.services.firebase.callbacks.ServiceCallback;

/**
 * Created by michael on 23.11.17.
 */

public abstract class BaseService<Model extends BaseModel> {

    protected final DatabaseReference dbRootRef;
    protected final DatabaseReference dbRef;

    public BaseService(String childRef) {
        dbRootRef = FirebaseDatabase.getInstance().getReference();
        dbRef = dbRootRef.child(childRef);
    }

    protected abstract Model wrap(DataSnapshot dataSnapshot);
    protected Model wrapKey(DataSnapshot dataSnapshot, Model model) {
        return model;
    }

    protected void findSingle(String key, final ServiceCallback<Model> callback) {
        dbRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Model m = wrapKey(dataSnapshot, wrap(dataSnapshot));
                if(m == null) callback.failure("Error");
                else callback.success(m);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.failure(databaseError.getMessage());
            }
        });
    }

    protected void findAll(final ServiceCallback<List<Model>> callback) {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Model> result = new ArrayList<>();
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    Model childObj = wrapKey(child, wrap(child));
                    result.add(childObj);
                }
                callback.success(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.failure(databaseError.getMessage());
            }
        });
    }


}
