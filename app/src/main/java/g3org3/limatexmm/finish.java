package g3org3.limatexmm;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class finish extends AppCompatActivity {

    Button finishOrder;
    TextView dataBase_label;
    AutoCompleteTextView client_phone;
    EditText client_name;
    EditText client_address;
    Button cancelOrder;
    RecyclerView rv_cart2;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    boolean connected = false;

    List<userList> clients_extracted;
    List<String> reg_phone_numbers;


    private void writeNewUser(String user_addr, String user_name, String user_phone) {

        //get current date
        Date userJoin = new Date();

        userList userList = new userList(user_addr, user_name, user_phone, "1", userJoin, "0");
        String docId = db.collection("clients").document().getId();
        // Add a new document with a generated ID
        db.collection("clients").document(docId).set(userList);

    }

    public void db_connect() {
        if (!connected) {
            dataBase_label.setText("Nu se poate conecta la baza de date !");
            client_phone.setEnabled(false);
            client_name.setEnabled(false);
            client_address.setEnabled(false);
            finishOrder.setEnabled(false);
        } else {
            dataBase_label.setText("Conectat la baza de date! " + clients_extracted.size() + " numere de telefon inregistrate!");
            client_phone.setEnabled(true);
            client_name.setEnabled(true);
            client_address.setEnabled(true);
            finishOrder.setEnabled(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        // Intent intent = getIntent();

        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        dataBase_label = findViewById(R.id.dataBase_label);

        client_phone = findViewById(R.id.client_phone);
        client_name = findViewById(R.id.client_name);
        client_address = findViewById(R.id.client_address);
        cancelOrder = findViewById(R.id.cancelOrder);
        finishOrder = findViewById(R.id.finishOrder);
        rv_cart2 = findViewById(R.id.rv_cart2);

        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeNewUser("Poenari", "Elena", "07218032213");
                dataBase_label.setText("Adaugat in baza de date: ");
                Toast.makeText(getApplicationContext(), "ADDED!", Toast.LENGTH_SHORT).show();
            }
        });

        //create list to store clients
        clients_extracted = new ArrayList<>();

        //create list to store phone numbers
        reg_phone_numbers = new ArrayList<>();

        //Get the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        connected = false;
        db_connect();

        //enter the collection clients // get every document
        db.collection("clients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        //get values inside document
                        userList userList = document.toObject(g3org3.limatexmm.userList.class);

                        //add each client to internal basedata
                        clients_extracted.add(userList);

                        //add every phone number extracted to AutoComplet phone number TextView
                        reg_phone_numbers.add(userList.getUserPhone());

                        connected = true;
                        //show message about basedata
                        db_connect();
                    }
                } else {
                    connected = false;
                    //show message about basedata
                    db_connect();
                }
            }
        });


        //setup autocomplete textView
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, reg_phone_numbers);
        client_phone.setAdapter(adapter);

        //lose focus after text is correct
        client_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (client_phone.getText().length() == 10) {
                    client_phone.clearFocus();
                }
            }
        });

        //after textbox lose focus
        client_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                if (b) {
                    // Open keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(client_phone, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(client_phone.getWindowToken(), 0);
                }


                //if number is not valid
                if (client_phone.getText().length() < 10) {
                    client_phone.setText("");
                    Toast.makeText(getApplicationContext(), "Numar introdus gresit!", Toast.LENGTH_SHORT).show();
                } else {
                    //if number IS VALID
                    //Search it in extracted DB


                    for (int i = 0; i < clients_extracted.size(); i++) {

                        //  if (i == i) {
                        //NEED TO add mod for not stressing UI too much
                        dataBase_label.setText(i + "/" + clients_extracted.size());
                        // }

                        if (clients_extracted.get(i).getUserPhone().contains(client_phone.getText().toString())) {
                            client_name.setText(clients_extracted.get(i).getUserName());
                            client_address.setText(clients_extracted.get(i).getUserAddr());

                            Date temp_date = clients_extracted.get(i).getUserJoin();
                            Date temp_date2 = new Date();

                            long diff = temp_date2.getTime() - temp_date.getTime();

                            String jDays = String.valueOf(diff / 1000 / 60 / 60 / 24 );

                            dataBase_label.setText("Finalizeaza comanda, Stiu pe " + clients_extracted.get(i).getUserName() + " de " + jDays + " zile, are " + clients_extracted.get(i).getUserOrders() + " comenzi la noi.");


                            break; //stop searching
                        }
                        if (i == clients_extracted.size() - 1 ) {
                            dataBase_label.setText(":(  Clientul nu exista, adauga datele si finalizeaza comanda pentru a-l adauga!");
                        }
                    }
                }
            }
        });


    }


    // private static String[] reg_phone_numbers = new String[]{
    //          "0782312332", "0231232232"
    //  };


}
