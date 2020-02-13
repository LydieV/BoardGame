package fr.iutlens.mmi.buildthefrance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PerduActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perdu);

        int score = getIntent().getIntExtra("score", 0);
        TextView texte = findViewById(R.id.textScore);
        texte.setText("Score : "+ score);


        View view = findViewById(R.id.replay);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perdre(view);
            }
        });

        View view1 = findViewById(R.id.home);
        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                accueil(view1);
            }
        });
    }

    public void perdre(View view){
        Button button = (Button) findViewById(R.id.replay);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void accueil(View view1){
        Button button = (Button) findViewById(R.id.home);
        Intent intent = new Intent(this, AccueilActivity.class);
        startActivity(intent);
    }
}
