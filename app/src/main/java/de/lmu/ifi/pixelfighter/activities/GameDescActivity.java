package de.lmu.ifi.pixelfighter.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import de.lmu.ifi.pixelfighter.R;

public class GameDescActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_desc);
        setTitle("");
        ButterKnife.bind(this);
    }
}
