package de.lmu.ifi.pixelfighter.services.firebase;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 18.01.18.
 */

public abstract class GenericReference<CLS> {
    public final DatabaseReference reference;

    public GenericReference(DatabaseReference reference) {
        this.reference = reference;
    }

    public abstract CLS wrap(DataSnapshot dataSnapshot);
    public abstract CLS wrap(MutableData mutableData);

    private Map<ValueListener, ValueEventListener> listeners = new HashMap<>();

    public void addListener(@NonNull final ValueListener<CLS> listener) {
        if(listener == null) throw new IllegalArgumentException("ValueListener is null");
        ValueEventListener valueEventListener = generateValueEventListener(listener);
        listeners.put(listener, valueEventListener);
        reference.addValueEventListener(valueEventListener);
    }

    public void removeListener(@NonNull final ValueListener<CLS> listener) {
        if(listener == null) throw new IllegalArgumentException("ValueListener is null");
        reference.removeEventListener(listeners.remove(listener));
    }

    public void addSingleListener(@NonNull final ValueListener<CLS> listener) {
        if(listener == null) throw new IllegalArgumentException("ValueListener is null");
        reference.addListenerForSingleValueEvent(generateValueEventListener(listener));
    }

    private ValueEventListener generateValueEventListener(final ValueListener<CLS> listener) {
        return new ValueEventListener() {
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
        };
    }

    public void setValue(@NonNull final CLS value) {
        reference.setValue(value);
    }

    public void setValue(final CLS value, @NonNull final CompletionListener completionListener) {
        if(completionListener == null) throw new IllegalArgumentException("CompletionListener is null");
        reference.setValue(value, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                completionListener.onComplete();
            }
        });
    }

    public void runTransaction(final Handler<CLS> handler) {
        reference.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                CLS result = handler.doTransaction(wrap(mutableData));
                if(result == null) {
                    return Transaction.abort();
                } else {
                    mutableData.setValue(result);
                    return Transaction.success(mutableData);
                }
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                handler.onComplete(b, wrap(dataSnapshot));
            }
        });
    }


    public interface CompletionListener {
        void onComplete();
    }

    public interface ValueListener<CLS> {
        void onData(CLS object);
        void onError(Error error);
    }

    public interface Handler<CLS> {
        CLS doTransaction(CLS mutable);
        void onComplete(boolean changed, CLS object);
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
