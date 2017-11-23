package de.lmu.ifi.pixelfighter.demo.demo3;

import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by michael on 23.11.17.
 */

public class Board2 {
    public static void loadToFB() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Board2 b = new Board2();
        b.reset();
        Log.d("Board2", "board created");
        database.getReference("board3").setValue(b);
        Log.d("Board2", "board uploaded");

    }

    public Map<String, Map<String, FieldValue>> pixels;

    Board2() {

    }

    public void reset() {
        pixels = new HashMap<>();
        int size_x = 3;
        int size_y = 5;


        for(int x_i=0; x_i<size_x; x_i++) {
            String x = Integer.toString(x_i);

            Map<String, FieldValue> row = pixels.get(x);
            if(row == null) {
                Log.d("Board2", "Put x" + x );
                pixels.put(x, new HashMap<String, FieldValue>());
            }
            row = pixels.get(x);

            for(int y_i=0; y_i<size_y; y_i++) {
                String y = Integer.toString(y_i);
                FieldValue col = row.get(y);
                if(col == null) {
                    Log.d("Board2", "Put y" + y );
                    row.put(y, new FieldValue("gray"));
                }

            }
        }
    }

    public Map<String, Map<String, FieldValue>> getPixels() {
        return pixels;
    }

    public void setPixels(Map<String, Map<String, FieldValue>> pixels) {
        this.pixels = pixels;
    }

    public class FieldValue {
        public String color;

        public FieldValue() {
        }

        public FieldValue(String color) {
            this.color = color;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

}
