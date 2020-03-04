package fr.iutlens.mmi.buildthefrance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        View view = findViewById(R.id.buttonRetour);
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                retour(view);
            }
        });
    }

    private void retour(View view){
        Button button = (Button) findViewById(R.id.buttonRetour);
        Intent intent = new Intent(this, AccueilActivity.class);
        startActivity(intent);
    }
}