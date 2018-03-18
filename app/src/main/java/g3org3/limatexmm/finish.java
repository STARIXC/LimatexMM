package g3org3.limatexmm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.UserHandle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
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
import java.util.Arrays;
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
    TextView client_address_label;
    Button deliverSwitch;
    TextView totalLabel;
    Boolean deliver = true;

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

            client_phone.setBackgroundColor(Color.GRAY);
            client_name.setBackgroundColor(Color.GRAY);
            client_address.setBackgroundColor(Color.GRAY);
            finishOrder.setBackgroundColor(Color.GRAY);

        } else {
            dataBase_label.setText("Conectat la baza de date! " + reg_phone_numbers.size() + " numere de telefon inregistrate!");
            client_phone.setEnabled(true);
            client_name.setEnabled(true);
            client_address.setEnabled(true);
            finishOrder.setEnabled(true);

            client_phone.setBackgroundColor(getThemeAccentColor(this));
            client_name.setBackgroundColor(getThemeAccentColor(this));


            //setup autocomplete textView
            adapter = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item, reg_phone_numbers);
            client_phone.setAdapter(adapter);


        }
    }

    ArrayAdapter<String> adapter;

    Boolean meet_minimum = false;
    Double minimum_current = 0.0;
    Double cart_value = 0.0;
    String last_city = "";
    public void check_minimum_city(String address) {

        //populate comments list
        int id = this.getResources().getIdentifier("citys", "array", this.getPackageName());
        List<String> Lines = Arrays.asList(getResources().getStringArray(id));

        meet_minimum = false;
        minimum_current = 150.0;

        for (int i = 0; i < Lines.size(); i++) {
            //search for city name
            if (address.toLowerCase().contains(Lines.get(i).toLowerCase())) {
                last_city = Lines.get(i);
                minimum_current = Double.valueOf(Lines.get(i+1));
                if (cart_value > minimum_current){
                    meet_minimum = true;
                }
                break;
            }

            //jump over minimum order value
            i++;
        }



        calculate_total();

    }


    public void check_forms() {


        check_minimum_city(client_address.getText().toString());


        boolean temp_client = false;
        if (client_phone.length() == 10) {
            client_phone.setBackgroundColor(Color.rgb(14, 165, 87));
            temp_client = true;
        } else {
            client_phone.setBackgroundColor(getThemeAccentColor(this));
        }

        boolean temp_name = false;
        if (client_name.length() > 2) {
            temp_name = true;
            client_name.setBackgroundColor(Color.rgb(14, 165, 87));
        } else {
            client_name.setBackgroundColor(getThemeAccentColor(this));
        }


        if (!meet_minimum) {
            if (last_city.length() > 2) {
                finishOrder.setBackgroundColor(Color.GRAY);
                finishOrder.setEnabled(false);
                client_address.setBackgroundColor(getThemeAccentColor(this));

            } else {
                finishOrder.setBackgroundColor(Color.GRAY);
                finishOrder.setEnabled(false);
                client_address.setBackgroundColor(Color.GRAY);
            }
        } else {
            client_address.setBackgroundColor(Color.rgb(14, 165, 87));
            if (temp_name) {
                if (temp_client) {

                    client_name.setBackgroundColor(Color.rgb(14, 165, 87));

                    finishOrder.setEnabled(true);
                    finishOrder.setBackgroundColor(getThemeAccentColor(this));

                }

            }
        }

    }
    public static int getThemeAccentColor (final Context context) {
        final TypedValue value = new TypedValue ();
        context.getTheme ().resolveAttribute (R.attr.colorPrimaryDark, value, true);
        return value.data;
    }

    public void calculate_total(){

        SpannableStringBuilder temp_span = new SpannableStringBuilder();
        temp_span.append("Produse: " + cart_value + " lei \n\n", new UnderlineSpan(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (deliver) {
            if (last_city.length() > 2) {

                if (meet_minimum) {
                    temp_span.append("MINIM COMANDA ATINS!");

                } else {
                    temp_span.append("Minimul in : " + last_city + " \n este de: " + minimum_current);
                }


            } else {
                temp_span.append("Adresa \n nu este completata");
            }
        } else {
            temp_span.append("Fara livrare!");
        }


        totalLabel.setText(temp_span);



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        cart_value = 0.0;
        last_city = "";
        meet_minimum = false;
        minimum_current = 0.0;

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        cart_value = Double.valueOf(intent.getStringExtra(casa.CART_VALUE_EX));


        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        dataBase_label = findViewById(R.id.dataBase_label);

        client_phone = findViewById(R.id.client_phone);
        client_name = findViewById(R.id.client_name);
        client_address = findViewById(R.id.client_address);
        cancelOrder = findViewById(R.id.cancelOrder);
        finishOrder = findViewById(R.id.finishOrder);
        deliverSwitch = findViewById(R.id.deliverSwitch);
        totalLabel = findViewById(R.id.totalLabel);
        client_address_label = findViewById(R.id.client_address_label);


        deliverSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deliver) {
                    deliverSwitch.setText("NU");
                    deliverSwitch.setBackgroundResource(R.drawable.ripple_button_darker);
                    deliver = false;
                    client_address.setVisibility(View.GONE);
                    client_address_label.setVisibility(View.GONE);

                } else {
                    deliverSwitch.setText("DA");
                    deliverSwitch.setBackgroundResource(R.drawable.ripple_button);
                    client_address.setVisibility(View.VISIBLE);
                    client_address_label.setVisibility(View.VISIBLE);
                    deliver = true;
                }
                check_minimum_city(client_address.getText().toString());
            }
        });


        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        /*
                writeNewUser("Poenari", "Elena", "07218032213");
                dataBase_label.setText("Adaugat in baza de date: ");
                Toast.makeText(getApplicationContext(), "ADDED!", Toast.LENGTH_SHORT).show();
        */


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


        check_forms();

        check_minimum_city("");
        client_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                check_forms();
            }
        });

        client_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                check_forms();
            }
        });



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
                    // Close keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(client_phone.getWindowToken(), 0);
                }

                check_forms();

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
                } else {
                    //if number IS VALID
                    //Search it in extracted DB


                    client_name.setEnabled(true);
                    client_address.setEnabled(true);

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

                            String jDays = String.valueOf(diff / 1000 / 60 / 60 / 24);

                            dataBase_label.setText("Finalizeaza comanda, Stiu pe " + clients_extracted.get(i).getUserName() + " de " + jDays + " zile, are " + clients_extracted.get(i).getUserOrders() + " comenzi la noi.");

                            client_name.setEnabled(false);
                            client_address.setEnabled(false);

                            // Close keyboard
                            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(client_phone.getWindowToken(), 0);


                            break; //stop searching
                        }
                        if (i == clients_extracted.size() - 1) {
                            dataBase_label.setText(":(  Clientul nu exista, adauga datele si finalizeaza comanda pentru a-l adauga!");
                        }
                    }
                }
            }
        });



    }


}
