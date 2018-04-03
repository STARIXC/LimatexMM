package g3org3.limatexmm;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class finish extends AppCompatActivity {

    Button finishOrder;
    TextView dataBase_label;
    AutoCompleteTextView client_phone;
    EditText client_name;
    Button client_address;
    Button cancelOrder;
    TextView client_address_label;
    Button deliverSwitch;
    TextView totalLabel;
    Boolean deliver = true;
    Spinner spinnerAddress;

    boolean connected = false;

    List<userList> clients_extracted;
    List<String> reg_phone_numbers;


    //GOOGLE MAPS KEY:  AIzaSyBioAUEf-XXf_R7PzbbTZPHxHi-IRP9JOY

    public void db_connect() {
        if (!connected) {
            dataBase_label.setText("Nu se poate conecta la baza de date !");
            //   client_phone.setEnabled(false);
            //    client_name.setEnabled(false);
            //      client_address.setEnabled(false);
            //    finishOrder.setEnabled(false);

            client_phone.setBackgroundColor(Color.GRAY);
            client_name.setBackgroundColor(Color.GRAY);
            spinnerAddress.setBackgroundColor(Color.GRAY);
            finishOrder.setBackgroundColor(Color.GRAY);

        } else {
            dataBase_label.setText("Conectat la baza de date! " + reg_phone_numbers.size() + " numere de telefon inregistrate!");
            client_phone.setEnabled(true);
            client_name.setEnabled(true);
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
                spinnerAddress.setBackgroundColor(getThemeAccentColor(this));

            } else {
                finishOrder.setBackgroundColor(Color.GRAY);
                finishOrder.setEnabled(false);
                spinnerAddress.setBackgroundColor(Color.GRAY);
            }
        } else {
            if (temp_name) {
                if (temp_client) {

                    client_name.setBackgroundColor(Color.rgb(14, 165, 87));

                    finishOrder.setEnabled(true);
                    finishOrder.setBackgroundColor(getThemeAccentColor(this));

                }
            }
            spinnerAddress.setBackgroundColor(Color.rgb(14, 165, 87));
        }

    }



    public void customToast(String finalt, Boolean longer) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.item_added, null);

        TextView text = layout.findViewById(R.id.text);
        text.setText(finalt);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        if (longer) {
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.setView(layout);
        toast.show();

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


    public void checkAddress(String fullAddr) {

        if (isPopUp) {
            customToast("Se calculeaza distanta...",false);
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
                                currentLocationTime = last_distTime / 60;
                                if (isPopUp) {
                                    customToast("Distanta catre adresa: " + dist / 1000 + " km, " + String.valueOf(last_distTime / 60) + " minute distanta",false);
                                    doneAddStep = true;
                                    done.setText("Adauga!");
                                }

                            } else {
                                if (isPopUp) {
                                    customToast("Distanta catre adresa selectata este mult prea mare: " + dist / 100 + " km!",false);
                                }
                            }

                        } else {
                            if (isPopUp) {
                                customToast("Strada nu a fost gasita, se considera doar orasul", true);
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isPopUp) {
                                        checkAddress(spinnerCity.getSelectedItem().toString());
                                    } else {
                                        String tempp = spinnerAddress.getSelectedItem().toString();
                                        checkAddress(tempp.substring(0,tempp.indexOf(",,")));
                                    }
                                }
                            }, 1600);

                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customToast("E123: Fara raspuns de la server!",true);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


    public void SendOrder(ArrayList<listItems> list, userList currentUser) {
        customToast("Se trimite ...", false);

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

        String date = SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, 2).format(new Date());

        orderListBig docData = new orderListBig(list, currentUser, adList, date);

        DatabaseReference ref = database.getReference("pizza/orders/" + date);
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
                }
            }, 500);

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


    public static int getThemeAccentColor(final Context context) {
        final TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, value, true);
        return value.data;
    }

    public void calculate_total() {

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
                }
            });

            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

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

        //   Intent intent = getIntent();
        // list_items_intent = getIntent().putParcelableArrayListExtra("final_cart_list");
        list_items_intent = (ArrayList<listItems>) getIntent().getSerializableExtra("final_cart_list");

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
        spinnerAddress = findViewById(R.id.spinnerAddress);


        clientAddr = new ArrayList<>();
        cAddr = new ArrayAdapter<String>(this, R.layout.simple_dropdown_item_finish, clientAddr);


        spinnerAddress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.WHITE);
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);

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
            @Override
            public void onClick(View view) {
                if (deliver) {
                    deliverSwitch.setText("Fara livrare");
                    deliverSwitch.setBackgroundResource(R.drawable.ripple_button_yellow);
                    totalLabel.setBackgroundResource(R.drawable.ripple_button_yellow);
                    deliver = false;
                    client_address.setVisibility(View.GONE);
                    spinnerAddress.setVisibility(View.GONE);
                    client_address_label.setVisibility(View.GONE);
                    check_minimum_city("");

                } else {
                    deliverSwitch.setText("Cu Livrare");
                    deliverSwitch.setBackgroundResource(R.drawable.ripple_button);
                    totalLabel.setBackgroundResource(R.drawable.ripple_button_darker);
                    client_address.setVisibility(View.VISIBLE);
                    spinnerAddress.setVisibility(View.VISIBLE);
                    client_address_label.setVisibility(View.VISIBLE);
                    deliver = true;
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


        cancelOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //create list to store clients
        clients_extracted = new ArrayList<>();

        //create list to store phone numbers
        reg_phone_numbers = new ArrayList<>();

        //Get the database
        db = FirebaseFirestore.getInstance();

        database = FirebaseDatabase.getInstance();


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
                //Reset current user orders when phone number is focused and changed
                current_user_orders = 0;

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
                            clientAddr.clear();
                            clientAddr.addAll(clients_extracted.get(i).getUserAddr());

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


    }


}
