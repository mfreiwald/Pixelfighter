package de.lmu.ifi.pixelfighter.services.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by michael on 18.01.18.
 */

public abstract class GenericReference<CLS> {
    private final DatabaseReference reference;

    public GenericReference(DatabaseReference reference) {
        this.reference = reference;
    }

    public abstract CLS wrap(DataSnapshot dataSnapshot);

    public void addSingleListener(@NonNull final ValueListener<CLS> listener) {
        if(listener == null) throw new IllegalArgumentException("ValueListener is null");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final CLS object = wrap(dataSnapshot);
                if (object == null) {
                    listener.onError(Error.Null);
                } else {
                    listener.onData(object);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Error e = Error.Database;
                e.databaseError = databaseError;
                listener.onError(e);
            }
        });
    }

    public void setValue(@NonNull final CLS value) {
        reference.setValue(value);
    }

    public void setValue(final CLS value, @NonNull final CompletionListener<CLS> completionListener) {
        if(completionListener == null) throw new IllegalArgumentException("CompletionListener is null");
        reference.setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                completionListener.onComplete();
            }
        });
    }


    public interface CompletionListener<CLS> {
        void onComplete();
    }

    public interface ValueListener<CLS> {
        void onData(CLS object);
        void onError(Error error);
    }

    public enum Error {
        Database,
        Exception,
        Null;

        public DatabaseError databaseError;
        public Exception exception;

        @Override
        public String toString() {
            String result = this.name();
            switch(this) {
                case Database:
                    return result += ": " + databaseError.toString();
                case Exception:
                    return result += ": " + exception.toString();
            }
            return result;
        }
    }
}
