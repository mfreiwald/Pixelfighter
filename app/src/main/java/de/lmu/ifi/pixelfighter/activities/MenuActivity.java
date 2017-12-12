package de.lmu.ifi.pixelfighter.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_desc)
    public void onClickDescription() {
        Intent intentDesc = new Intent(MenuActivity.this, GameDescActivity.class);
        startActivity(intentDesc);
    }

    @OnClick(R.id.button_stats)
    public void onClickStats() {
        Intent intentStats = new Intent(MenuActivity.this, StatisticsActivity.class);
        startActivity(intentStats);
    }

    @OnClick(R.id.button_game)
    public void onClickGame() {
        Log.d("Menu", "Start Main Activity");
        Intent intentGame = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intentGame);
    }

}
