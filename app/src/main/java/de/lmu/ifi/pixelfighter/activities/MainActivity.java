package de.lmu.ifi.pixelfighter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

import butterknife.ButterKnife;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.UserData;
import de.lmu.ifi.pixelfighter.services.android.Pixelfighter;
import de.lmu.ifi.pixelfighter.services.firebase.Database;
import de.lmu.ifi.pixelfighter.services.firebase.GenericReference;
import de.lmu.ifi.pixelfighter.utils.StartActivityHelper;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        // FB check current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // if null -> Register Activity to signIn
        if(user == null) {
            StartActivityHelper.start(this).registerActivity();
            return;
        }
        // else load Settings for user from Database
        else {
            Database.UserData(user.getUid()).addSingleListener(new GenericReference.ValueListener<UserData>() {
                @Override
                public void onData(UserData userData) {
                    if(userData == null) {
                        userData = new UserData(user.getUid(), UUID.randomUUID().toString());
                        Database.UserData(user.getUid()).setValue(userData);
                    }
                    Pixelfighter.getInstance().setUserData(userData);
                    Log.d("UserData", userData.toString());
                    StartActivityHelper.start(MainActivity.this).menuActivity();
                }

                @Override
                public void onError(GenericReference.Error error) {
                    Log.e("MainActivity", "Fetch UserData Error: " + error.toString());
                }
            });
        }

    }
}
