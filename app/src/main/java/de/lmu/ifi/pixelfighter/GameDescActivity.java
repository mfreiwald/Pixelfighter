package de.lmu.ifi.pixelfighter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;

public class GameDescActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_desc);
        setTitle("");
        ButterKnife.bind(this);
    }
}
