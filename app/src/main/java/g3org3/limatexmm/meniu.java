package g3org3.limatexmm;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class meniu extends AppCompatActivity {

    Button casa;
    Button bucatarie;
    Button monitor;
    Button admin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meniu);

        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        casa = findViewById(R.id.casa);
        bucatarie = findViewById(R.id.bucatarie);
        monitor = findViewById(R.id.monitor);
        admin = findViewById(R.id.admin);


        casa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(meniu.this, casa.class);
                startActivity(myIntent);
            }
        });


        bucatarie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(meniu.this, bucatarie.class);
                startActivity(myIntent);
            }
        });



        monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(meniu.this, orders.class);
                startActivity(myIntent);
            }
        });


        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(meniu.this, admin.class);
                startActivity(myIntent);
            }
        });


    }







}
