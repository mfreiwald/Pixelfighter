package de.lmu.ifi.pixelfighter.demo3;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import de.lmu.ifi.pixelfighter.MainActivity;
import de.lmu.ifi.pixelfighter.R;

public class GameActivity extends AppCompatActivity {

    private Game game = MainActivity.game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        updateView();
    }


    public void click(View view) {
        String tag = (String)view.getTag();

        String[] split = tag.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);

        Log.d("Click", "On view ("+x+","+y+")");
        game.setPixel(x, y, new Game.Callback() {
            @Override
            public void success() {
                updateView();
            }
        });
    }

    private void updateView() {
        ViewGroup rootView = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);

        List<Pixel> pixelList = this.game.getBoard().getPixelList();
        for(int i=0; i<pixelList.size(); i++) {
            Pixel pixel = pixelList.get(i);
            Button button = (Button)rootView.getChildAt(i);
            if(pixel.getTeamName().equals("red")) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            } else if (pixel.getTeamName().equals("green")) {
                button.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            }

        }
    }
}
