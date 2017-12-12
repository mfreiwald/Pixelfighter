package de.lmu.ifi.pixelfighter.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.lmu.ifi.pixelfighter.R;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.button_reset)
    public void onClickReset() {
        //Delete Statistics from Firebase
    }

}
