package fr.iutlens.mmi.boardgame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        GameView gameview = findViewById(R.id.gameView);
        TextView textView = findViewById(R.id.textViewScore);

        gameview.setScoreView(textView);
    }
}
