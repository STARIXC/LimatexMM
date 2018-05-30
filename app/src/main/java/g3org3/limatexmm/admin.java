package g3org3.limatexmm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class admin extends AppCompatActivity {

    android.support.v7.widget.Toolbar appBar;
    Button adminClearOrders;
    Button adminClearUsers;
    Button adminClearUser;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);


        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        adminClearOrders = findViewById(R.id.adminClearOrders);
        adminClearUsers = findViewById(R.id.adminClearUsers);
        adminClearUser = findViewById(R.id.adminClearUser);

        appBar = findViewById(R.id.appBar);
        this.setSupportActionBar(appBar);

        mContext = this;
        adminClearOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setTitle("Sterge tot")
                        .setMessage("Chiar vrei sa stergi toate comenzile de azi?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                                FirebaseDatabase database;
                                DatabaseReference ref;
                                database = FirebaseDatabase.getInstance();
                                String date = String.valueOf(android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date()));
                                ref = database.getReference("pizza/" + date);
                                ref.removeValue();
                                Toast.makeText(getApplicationContext(), "Comenzile de azi au fost sterse", Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

        adminClearUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Functie neimplementata", Toast.LENGTH_LONG).show();
            }
        });

        adminClearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "Functie neimplementata", Toast.LENGTH_LONG).show();
            }
        });

    }
}
