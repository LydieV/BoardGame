package fr.iutlens.mmi.buildthefrance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AccueilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accueil);

        View view = findViewById(R.id.button);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jouer(view);
            }
        });

        View view1 = findViewById(R.id.buttoninstruc);
        view1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view1){
                instruc(view1);
            }
        });
    }

    private void instruc(View view1) {
        Button button1 = (Button) findViewById(R.id.buttoninstruc);
        Intent intent1 = new Intent (this, InstructionsActivity.class);
        startActivity(intent1);
    }

    public void jouer(View view) {
        Button button = (Button) findViewById(R.id.button);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
