package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;
import de.lmu.ifi.pixelfighter.models.UserData;
import de.lmu.ifi.pixelfighter.services.firebase.Database;
import de.lmu.ifi.pixelfighter.services.firebase.GenericReference;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.enterUsernameTextView)
    TextView textView;
    @BindView(R.id.usernameEditText)
    EditText userNameEditText;

    private boolean registerCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        userNameEditText.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER) {
                    register();
                    return true;
                }
                return false;
            }
        });
    }


    @OnClick(R.id.registerButton)
    public void register() {
        if(registerCalled) return;
        registerCalled = true;

        if(userNameEditText.getText().toString().isEmpty()) {
            textView.setTextColor(Color.RED);
            return;
        }
        textView.setTextColor(getColor(R.color.colorAccent));

        final String username = userNameEditText.getText().toString();
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = task.getResult().getUser();

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(username).build();
                user.updateProfile(profileUpdates);

                UserData userData = new UserData(user.getUid(), username,0,0,0);
                Database.UserData(user.getUid()).setValue(userData, new GenericReference.CompletionListener() {
                    @Override
                    public void onComplete() {
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
