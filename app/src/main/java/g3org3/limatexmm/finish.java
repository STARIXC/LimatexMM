package g3org3.limatexmm;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class finish extends AppCompatActivity {

    Button finishOrder;
    TextView dataBase_label;

    TextInputLayout client_phone_c;
    AutoCompleteTextView client_phone;

    TextInputLayout client_name_c;
    EditText client_name;

    TextInputLayout client_address_c;
    Spinner spinnerAddress;
    MaterialButton client_address;

    MaterialButton deliverSwitch;
    MaterialButton paidSwitch;

    TextView totalLabel;
    Boolean deliver = true;
    Boolean paid = false;

    android.support.v7.widget.Toolbar appBar;

    boolean connected = false;

    List<userList> clients_extracted;
    List<String> reg_phone_numbers;


    //GOOGLE MAPS KEY:  AIzaSyBioAUEf-XXf_R7PzbbTZPHxHi-IRP9JOY


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);


        cart_value = 0.0;
        last_city = "";
        meet_minimum = false;
        minimum_current = 0.0;

        // Get the Intent that started this activity and extract the string
        cart_value = Double.valueOf(getIntent().getStringExtra(casa.CART_VALUE_EX));

        // get the cart list
        list_items_intent = (ArrayList<listItems>) getIntent().getSerializableExtra("final_cart_list");

        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        dataBase_label = findViewById(R.id.dataBase_label);

        client_phone = findViewById(R.id.client_phone);
        client_name = findViewById(R.id.client_name);
        client_address = findViewById(R.id.client_address);
        client_phone_c = findViewById(R.id.client_phone_c);
        client_name_c = findViewById(R.id.client_name_c);
        client_address_c = findViewById(R.id.client_address_c);
        finishOrder = findViewById(R.id.finishOrder);
        deliverSwitch = findViewById(R.id.deliverSwitch);
        paidSwitch = findViewById(R.id.paidSwitch);
        totalLabel = findViewById(R.id.totalLabel);
        spinnerAddress = findViewById(R.id.spinnerAddress);


        appBar = findViewById(R.id.appBar);
        this.setSupportActionBar(appBar);
        appBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        clientAddr = new ArrayList<>();
        cAddr = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, clientAddr);// R.layout.simple_dropdown_item_finish, clientAddr);


        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                //  ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(15);
                ((TextView) adapterView.getChildAt(0)).setBackgroundColor(Color.rgb(239, 239, 239));

                String curAddr = "";
                if (spinnerAddress != null && spinnerAddress.getSelectedItem() != null) {
                    curAddr = (String) spinnerAddress.getSelectedItem();
                }

                checkAddress(curAddr);

                check_forms();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerAddress.setAdapter(cAddr);

        client_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addressPopUp();
            }
        });


        deliverSwitch.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (deliver) {
                    deliverSwitch.setText("Fara livrare");
                    //    deliverSwitch.setSupportBackgroundTintList(null);

                    deliver = false;
                    client_address.setVisibility(View.GONE);
                    spinnerAddress.setVisibility(View.GONE);
                    check_minimum_city("");

                } else {
                    deliverSwitch.setText("Cu Livrare");
                    //   deliverSwitch.setBackgroundTintList(getResources().getColorStateList(R.color.colorYellow));
                    client_address.setVisibility(View.VISIBLE);
                    spinnerAddress.setVisibility(View.VISIBLE);
                    deliver = true;
                }
                check_forms();
            }
        });

        paidSwitch.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                if (paid) {
                    paidSwitch.setText("Neplatita!");
                    //  paidSwitch.setBackgroundTintList(getColorStateList(R.color.colorPrimary));
                    paid = false;
                    check_minimum_city("");

                } else {
                    paidSwitch.setText("Deja platita");
                    //   paidSwitch.setBackgroundTintList(getColorStateList(R.color.colorYellow));
                    paid = true;
                }
                check_forms();
            }
        });


        finishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String curAddr = "";
                if (spinnerAddress != null && spinnerAddress.getSelectedItem() != null) {
                    curAddr = (String) spinnerAddress.getSelectedItem();
                }

                userList curUser = new userList(clientAddr, curAddr, currentLocationTime, client_name.getText().toString(), client_phone.getText().toString(), current_user_orders, new Date(), 0);
                SendOrder(list_items_intent, curUser);
            }
        });


        //create list to store clients
        clients_extracted = new ArrayList<>();

        //create list to store phone numbers
        reg_phone_numbers = new ArrayList<>();

        //Get the database
        db = FirebaseFirestore.getInstance();

        database = FirebaseDatabase.getInstance();


        String date = String.valueOf(android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date()));
        DatabaseReference ref = database.getReference("pizza/" + date + "/orders");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentCounts = Integer.valueOf(String.valueOf(dataSnapshot.getChildrenCount())) + 1;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        connected = false;
        db_connect();


        //enter the collection clients // get every document
        db.collection("clients").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        Log.e("Working Orders id", ":" + document.getData());

                        //get values inside document
                        userList userList = document.toObject(g3org3.limatexmm.userList.class);

                        //add each client to internal basedata
                        clients_extracted.add(userList);

                        //add every phone number extracted to AutoComplet phone number TextView
                        reg_phone_numbers.add(userList.getUserPhone());

                        connected = true;
                    }
                    //show message about basedata
                    db_connect();

                } else {
                    connected = false;
                    //show message about basedata
                    db_connect();
                }
            }
        });


        check_forms();

        check_minimum_city("");

        calc_init();


        client_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                check_client_name(String.valueOf(editable));
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
                check_client_phone(String.valueOf(editable));

                //Reset current user orders when phone number is focused and changed
                current_user_orders = 0;


                //Autocomplete Client name in UI
                client_name.setText("");
                clientAddr.clear();
                cAddr.notifyDataSetChanged();

                //Lose focus// hide keyboard when the number is completed
                if (client_phone.getText().length() == 10) {
                    client_phone.clearFocus();
                    // Close keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(client_phone.getWindowToken(), 0);

                }

                //Valide other forms/inputs
                check_forms();

            }
        });

        //after textbox lose focus
        client_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                //if focus is on phonenumber
                if (b) {
                    // Open keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(client_phone, InputMethodManager.SHOW_FORCED);
                } else {
                    // Close keyboard
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(client_phone.getWindowToken(), 0);
                }


                //if number is not valid
                if (client_phone.getText().length() < 10) {
                    if (client_phone.getText().length() > 0) {
                        customToast("Numar de telefon", "incorect!", true);
                    }
                    client_phone.setText("");
                } else {
                    //if number IS VALID
                    //Search it in extracted DB

                    //Enable user UI in case there is no phone detected
                    client_name.setEnabled(true);

                    Boolean userFound = false;

                    //Search for each client in basedata for the current phone number inserted
                    for (int i = 0; i < clients_extracted.size(); i++) {

                        //NEED TO add mod for not stressing UI too much
                        dataBase_label.setText(i + "/" + clients_extracted.size());

                        if (clients_extracted.get(i).getUserPhone().contains(client_phone.getText().toString())) {

                            userFound = true;

                            //Autocomplete Client name in UI
                            client_name.setText(clients_extracted.get(i).getUserName());

                            //Autocomplete Client address in UI

                            //get addresses list from extracted data
                            // clientAddr = new ArrayList<>();

                            try {
                                clientAddr.clear();
                                clientAddr.addAll(clients_extracted.get(i).getUserAddr());
                            } catch (Exception e) {
                                Log.e("EROARE!!!!!!!", e.getMessage());
                            }

                            //set addresses to spinner
                            cAddr.notifyDataSetChanged();

                            //Get client join date
                            Date temp_date = clients_extracted.get(i).getUserJoin();

                            //Get current date
                            Date temp_date2 = new Date();

                            //Calculate the days from when he joined
                            long diff = temp_date2.getTime() - temp_date.getTime();
                            String jDays = String.valueOf(diff / 1000 / 60 / 60 / 24);

                            //Get client orders count
                            Integer client_orders_ex = clients_extracted.get(i).getUserOrders();

                            //Set infotaiment label text
                            dataBase_label.setText("Finalizeaza comanda, Stiu pe " + clients_extracted.get(i).getUserName() + " de " + jDays + " zile, are " + String.valueOf(client_orders_ex) + " comenzi la noi.");

                            //DEBUG
                            orderOrders(clients_extracted.get(i).getUserAddr());

                            //save current user oders
                            current_user_orders = client_orders_ex;

                            //Get current user docID
                            db.collection("clients").whereEqualTo("userPhone", client_phone.getText().toString())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    current_user_docId = document.getId();
                                                }
                                            } else {
                                                dataBase_label.setText("Eroare fatala, id-ul clientului nu se poate gasi");
                                            }
                                        }
                                    });


                            //disable user actions to changing client name,address when the number is already in database;
                            client_name.setEnabled(false);

                            // Close keyboard
                            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(client_phone.getWindowToken(), 0);


                            break; //stop searching
                        }
                    }
                    if (!userFound) {
                        //Set infotaiment label text
                        dataBase_label.setText(":(  Clientul nu exista, adauga datele si finalizeaza comanda pentru a-l adauga!");
                        //if client doens't exist and you want deliver
                        if (deliver) {
                            //open address enterer
                            if (clientAddr.size() < 1) {
                                addressPopUp();
                            }
                        }
                    }
                }
            }
        });

        //pre-create the icons
        errorIcon = this.getResources().getDrawable(R.drawable.ic_error_black_24dp);
        errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());
        errorIcon.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        checkIcon = this.getResources().getDrawable(R.drawable.ic_check_circle_black_24dp);
        checkIcon.setBounds(0, 0, checkIcon.getIntrinsicWidth(), checkIcon.getIntrinsicHeight());
        checkIcon.setColorFilter(Color.rgb(108, 193, 42), PorterDuff.Mode.SRC_ATOP);

    }

    Drawable errorIcon;
    Drawable checkIcon;

    public void check_client_name(String name) {
        if (name.length() <= 2) {
            client_name.setCompoundDrawables(null, null, errorIcon, null);
        } else {
            client_name.setCompoundDrawables(null, null, checkIcon, null);
        }
    }

    public void check_client_phone(String phone) {
        if (phone.length() <= 9) {
            client_phone.setCompoundDrawables(null, null, errorIcon, null);
        } else {
            client_phone.setCompoundDrawables(null, null, checkIcon, null);
        }
    }


    public void db_connect() {
        if (!connected) {
            dataBase_label.setText("Nu se poate conecta la baza de date !");
            //TODO: show NOT connected to db at ActionBar
            appBar.setBackgroundColor(Color.RED);
            finishOrder.setEnabled(false);

        } else {
            dataBase_label.setText("Conectat la baza de date! " + reg_phone_numbers.size() + " numere de telefon inregistrate!");
            //TODO: show connected to database at Actionbar
            appBar.setBackgroundColor(Color.TRANSPARENT);
            client_phone.setEnabled(true);
            client_name.setEnabled(true);

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

        meet_minimum = false;
        minimum_current = 150.0;

        if (deliver) {
            //populate comments list
            int id = this.getResources().getIdentifier("citys", "array", this.getPackageName());
            List<String> Lines = Arrays.asList(getResources().getStringArray(id));

            for (int i = 0; i < Lines.size(); i++) {
                //search for city name
                if (address.toLowerCase().contains(Lines.get(i).toLowerCase())) {
                    last_city = Lines.get(i);
                    minimum_current = Double.valueOf(Lines.get(i + 1));
                    if (cart_value > minimum_current) {
                        meet_minimum = true;
                    }
                    break;
                }

                //jump over minimum order value
                i++;
            }
        } else {
            meet_minimum = true;
        }

        calculate_total();

    }


    public void check_forms() {

        String name = "";
        if (spinnerAddress != null && spinnerAddress.getSelectedItem() != null) {
            name = (String) spinnerAddress.getSelectedItem();
        }

        check_minimum_city(name);


        boolean temp_client = false;
        if (client_phone.length() == 10) {
            temp_client = true;
        }

        boolean temp_name = false;
        if (client_name.length() > 2) {
            temp_name = true;
        }


        if (!meet_minimum) {
            if (last_city.length() > 2) {
                finishOrder.setEnabled(false);
            } else {
                finishOrder.setEnabled(false);
            }
        } else {
            if (temp_name) {
                if (temp_client) {
                    //if address finished checked
                    if (addressChecked || !deliver) {
                        finishOrder.setEnabled(true);
                    } else {
                        finishOrder.setEnabled(false);
                    }

                }
            }
        }

    }


    Boolean order_placed;
    //Integer steps;
    Integer errors;


    public void orderOrders(List<String> allAddrList) {
        // https://maps.googleapis.com/maps/api/directions/json?origin=New+York,+NY&destination=Boston,+MA&waypoints=optimize:true|Providence,+RI|Hartford,+CT&key=YOUR_API_KEY

        //build data link
        String allAddr = "";
        for (int i = 0; i < allAddrList.size(); i++) {
            //filtrer address for spaces dots etc...
            allAddr = allAddr + allAddrList.get(i).replace(" ", "+").replace(".", "+").replace("-", "+").trim();
            if (i < allAddrList.size() - 1) {
                //add separator between middle addreses
                allAddr = allAddr + "|";
            }
        }

        String curLocation = "44.447915,25.755464";
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + curLocation + "&destination=" + curLocation + "&waypoints=optimize:true|" + allAddr + "&key=AIzaSyBioAUEf-XXf_R7PzbbTZPHxHi-IRP9JOY";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        Log.e("!!!!!!!!!!!!!", response);

                        String[] liness = response.split("\n");

                        if (liness[2].contains("OK")) {


                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    Button calc_1;
    Button calc_2;
    Button calc_3;
    Button calc_4;
    Button calc_5;
    Button calc_6;
    Button calc_7;
    Button calc_8;
    Button calc_9;
    Button calc_0;
    TextView calc_tot;
    TextView calc_tot_label;
    Button calc_dot;
    Button calc_clear;

    String cur_sum_calc = "";

    public void calc_init() {

        calc_1 = findViewById(R.id.calc_1);
        calc_2 = findViewById(R.id.calc_2);
        calc_3 = findViewById(R.id.calc_3);
        calc_4 = findViewById(R.id.calc_4);
        calc_5 = findViewById(R.id.calc_5);
        calc_6 = findViewById(R.id.calc_6);
        calc_7 = findViewById(R.id.calc_7);
        calc_8 = findViewById(R.id.calc_8);
        calc_9 = findViewById(R.id.calc_9);
        calc_0 = findViewById(R.id.calc_0);
        calc_tot = findViewById(R.id.calc_tot);
        calc_tot_label = findViewById(R.id.calc_tot_label);
        calc_dot = findViewById(R.id.calc_dot);
        calc_clear = findViewById(R.id.calc_clear);

        calc_tot_label.setText("De platit:");
        calc_tot.setText(String.valueOf(cart_value));

        calc_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 1;
                calc_refresh();
            }
        });
        calc_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 2;
                calc_refresh();
            }
        });
        calc_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 3;
                calc_refresh();
            }
        });
        calc_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 4;
                calc_refresh();
            }
        });
        calc_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 5;
                calc_refresh();
            }
        });
        calc_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 6;
                calc_refresh();
            }
        });
        calc_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 7;
                calc_refresh();
            }
        });
        calc_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 8;
                calc_refresh();
            }
        });
        calc_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 9;
                calc_refresh();
            }
        });
        calc_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + 0;
                calc_refresh();
            }
        });
        calc_dot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = cur_sum_calc + ".";
                calc_refresh();
            }
        });
        calc_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cur_sum_calc = "";
                calc_refresh();
            }
        });

    }


    public void calc_refresh() {
        if (!paid) {
            paidSwitch.performClick();
        }


        Double temp_sum = 0.0;

        try {
            temp_sum = Double.valueOf(cur_sum_calc);

            if (cur_sum_calc.length() <= 0) {
                temp_sum = 0.0;
            }

        } catch (Exception e) {

        }

        calc_tot.setText(String.valueOf(cart_value - temp_sum));

        calc_tot_label.setText(String.valueOf(temp_sum));


    }


    Boolean addressChecked = false;

    public void checkAddress(String fullAddr) {
        //default
        addressChecked = false;

        if (isPopUp) {
            customToast("Se calculeaza", " distanta...", false);
        }

        String temp_addr = fullAddr.replace(" ", "+").trim();


        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://maps.googleapis.com/maps/api/distancematrix/xml?units=metric&origins=" + "44.447915,25.755464" + "&destinations=" + temp_addr + "&key=AIzaSyBioAUEf-XXf_R7PzbbTZPHxHi-IRP9JOY";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.

                        Log.e("!!!!!!!!!!!", response);

                        String[] liness = response.split("\n");

                        String last_distance = "";

                        Integer last_distTime = 0;

                        if (liness[7].contains("OK")) {
                            last_distance = liness[13];
                            last_distance = last_distance.replace("<value>", "");
                            last_distance = last_distance.replace("</value>", "");

                            last_distTime = Integer.valueOf((liness[9].replace("<value>", "").replace("</value>", "")).trim());

                            Double dist = Double.valueOf(last_distance.trim());
                            if (dist < 40000) {
                                addressChecked = true;
                                check_forms();
                                currentLocationTime = last_distTime / 60;
                                if (isPopUp) {
                                    customToast("Distanta catre adresa: ", dist / 1000 + " km, " + String.valueOf(last_distTime / 60) + " minute distanta", false);
                                    doneAddStep = true;
                                    done.setText("Adauga!");
                                    done.setEnabled(true);
                                }

                            } else {
                                if (isPopUp) {
                                    customToast("Distanta catre adresa selectata este mult prea mare: ", dist / 100 + " km!", false);
                                }
                            }

                        } else {
                            if (isPopUp) {
                                customToast("Strada nu a fost gasita,", "se considera doar orasul", true);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isPopUp) {
                                        checkAddress(spinnerCity.getSelectedItem().toString());
                                    } else {
                                        String tempp = spinnerAddress.getSelectedItem().toString();
                                        checkAddress(tempp.substring(0, tempp.indexOf(",,")));
                                    }
                                }
                            }, 1600);

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customToast("Server ERROR", "E123: Fara raspuns de la server!", true);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public void SendOrder(ArrayList<listItems> list, userList currentUser) {


        customToast("Comanda", "se inregistreaza", false);

        //disable button durring the process
        finishOrder.setEnabled(false);

        Integer curDeliver = 0;
        if (deliver) {
            curDeliver = 1;
        }

        errors = 0;

        //set order status by searching for Need to be prepared variable
        String orderStatus = "De ridicat";
        Boolean containsPrepare = false;
        for (int ai = 0; ai < list.size(); ai++) {
            if (list.get(ai).getItemPrepare() == 1) {
                containsPrepare = true;
            }
        }
        if (containsPrepare) {
            orderStatus = "In bucatarie";
        }


        additionalList adList = new additionalList(curDeliver, orderStatus, new Date());

        String date = String.valueOf(android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date()));
        String time = String.valueOf(android.text.format.DateFormat.format("hhmmss", new java.util.Date()));

        orderListBig docData = new orderListBig(list, currentUser, adList, date + time, currentCounts, paid);

        DatabaseReference ref = database.getReference("pizza/" + date + "/orders/" + time);
        ref.setValue(docData);


        //checking if the order has been placed:

        order_placed = false;
        //order has been placed
        if (errors < 1) {

            dataBase_label.setText("Comanda adaugata ! :" + date);

            //enable button after the process completed
            finishOrder.setEnabled(true);

            //set order_placed as true
            order_placed = true;

            //Wait a bit
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //exit activity
                    casa.justFinished = true;
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }, 1500);

        } else {
            dataBase_label.setText("EROARE!");
        }


        //Register new client
        //if the order have been placed
        if (order_placed) {


            //if the client is NEW
            if (currentUser.getUserOrders().equals(0)) {

                // Write user to basedata

                currentUser.setUserOrders(1);

                String docIdCl = db.collection("clients").document().getId();
                // Add a new document with a generated ID
                db.collection("clients").document(docIdCl).set(currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dataBase_label.append("\n Noul client a fost adaugat in baza de date !");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dataBase_label.append("\n Eroare: Noul client nu a fost salvat in baza de date!");
                            }
                        });


            } else {

                //check if user id is found!
                if (current_user_docId.length() < 1) {
                    dataBase_label.append(" \n " + current_user_docId);
                    dataBase_label.append(" \n Eroare fatala, user existent dar fara ID!");
                    finishOrder.setEnabled(false);
                    return;
                }

                DocumentReference user_profile = db.collection("clients").document(current_user_docId);
                user_profile.update("userOrders", current_user_orders + 1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dataBase_label.append("\n Profilul clientului a fost actualizat!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dataBase_label.append("\n Eroare: nu sa putut modifica profilul clientului!");
                            }
                        });

            }

        }
    }



    public void calculate_total() {

        SpannableStringBuilder temp_span = new SpannableStringBuilder();
        temp_span.append("Produse: " + cart_value + " lei \n", new UnderlineSpan(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

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

    ArrayList<listItems> list_items_intent = new ArrayList<>();
    Integer current_user_orders = 0;
    String current_user_docId = "";
    FirebaseFirestore db;


    List<String> clientAddr;
    ArrayAdapter cAddr;


    //CUSTOM POPUP FOR CITY ADD

    Dialog myDialog;
    Spinner spinnerCity;
    EditText address;
    Button done;
    ImageButton cancel;
    Boolean isPopUp = false;

    Boolean doneAddStep = false;


    public void addressPopUp() {
        if (!isPopUp) {
            isPopUp = true;
            myDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
            myDialog.setContentView(R.layout.city_select);
            myDialog.setTitle("More");

            doneAddStep = false;

            spinnerCity = myDialog.findViewById(R.id.spinnerCity);
            address = myDialog.findViewById(R.id.address);
            done = myDialog.findViewById(R.id.done);
            cancel = myDialog.findViewById(R.id.cancel);

            myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    isPopUp = false;
                    dialogInterface.dismiss();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });


            address.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {

                    doneAddStep = false;
                    done.setText("Inainte");
                    done.setEnabled(true);
                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    done.setEnabled(false);
                    //formulate full address;
                    String tempS = spinnerCity.getSelectedItem().toString() + ", " + address.getText().toString();
                    if (!clientAddr.contains(address.getText().toString())) {
                        if (tempS.length() > 10) {
                            if (!doneAddStep) {
                                checkAddress(tempS);
                            } else {
                                myDialog.dismiss();
                                clientAddr.add(tempS);
                                //apply changes
                                cAddr.notifyDataSetChanged();

                                db.collection("clients").whereEqualTo("userPhone", client_phone.getText().toString())
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (DocumentSnapshot document : task.getResult()) {

                                                        DocumentReference user_profile = db.collection("clients").document(document.getId());
                                                        user_profile.update("userAddr", clientAddr)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(getApplicationContext(), "Noua adresa a clientului adaugata!", Toast.LENGTH_SHORT).show();
                                                                        //    try {
                                                                        //          spinnerAddress.setSelection(clientAddr.size(), true);
                                                                        //    } catch (Exception e) {
                                                                        //        Log.e("~~~~~", String.valueOf(clientAddr.size()));
                                                                        //    }
                                                                    }
                                                                });

                                                    }
                                                }
                                            }
                                        });
                            }

                        } //item too short
                    } //item already exists
                }
            });


            myDialog.show();

        }
    }

    Integer currentLocationTime = 0;
    FirebaseDatabase database;
    Integer currentCounts = 0;


    public void customToast(String messageTitle, String message, Boolean longer) {
        //show custom TOAST
        LayoutInflater inflater = getLayoutInflater();
        View layout; // = inflater.inflate(R.layout.popup, null);
        layout = inflater.inflate(R.layout.popup, null);

        TextView text = layout.findViewById(R.id.messageTitle);
        TextView text2 = layout.findViewById(R.id.message);
        text.setText(messageTitle);
        text2.setText(message);

        Toast toast = new Toast(getApplicationContext());
        //  toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setGravity(Gravity.FILL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        if (longer) {
            toast.setDuration(Toast.LENGTH_LONG);
        }

        toast.setView(layout);

        toast.show();
    }


}
