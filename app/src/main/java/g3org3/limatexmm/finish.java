package g3org3.limatexmm;

import android.content.Context;
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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
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

    boolean connected = false;

    List<userList> clients_extracted;
    List<String> reg_phone_numbers;


    public void db_connect() {
        if (!connected) {
            dataBase_label.setText("Nu se poate conecta la baza de date !");
            //   client_phone.setEnabled(false);
            //    client_name.setEnabled(false);
            //      client_address.setEnabled(false);
            //    finishOrder.setEnabled(false);

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
                minimum_current = Double.valueOf(Lines.get(i + 1));
                if (cart_value > minimum_current) {
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


    public void customToast(String finalt) {
        //show custom TOAST
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.item_added, null);

        TextView text = layout.findViewById(R.id.text);
        text.setText(finalt);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }


    public void clear_form() {
        client_phone.setText("");
        client_name.setText("");
        client_address.setText("");
    }

    Boolean order_placed;
    //Integer steps;
    Integer errors;

    // usage: SendOrder(
    public void SendOrder(ArrayList<listItems> list, userList currentUser) {
        customToast("Se trimite ...");


        //disable button durring the process
        finishOrder.setEnabled(false);

        Date curDate = new Date();

        Integer curDeliver = 0;
        if (deliverSwitch.getText().toString().contains("DA")) {
            curDeliver = 1;
        }

        //    orderList orderList = new orderList(list, currentUser, curDate, curDeliver);


        final String docId = db.collection("orders").document().getId();


        order_placed = false;
       // steps = 0;
        errors = 0;

        //ADD ORDER ITEMS to Collection "allOrders" in individual document ids
        //for each Order Item
        for (int it = 0; it < list.size(); it++) {

            //convert item from list to indivitual item
            listItems orderItem = new listItems(list.get(it).getItemTitle(), list.get(it).getItemSubtitle(), list.get(it).getItemPrice(), list.get(it).getItemMore(), list.get(it).getItemMorevalue(), list.get(it).getItemPrepare());

            final String docIdFi = db.collection("orders").document(docId).collection("allOrders").document().getId();
            db.collection("orders").document(docId).collection("allOrders").document(docIdFi).set(orderItem)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            errors++;
                        }
                    });

        }

        //ADD User to Collection "userOrder" in one single document

        final String docIdSe = db.collection("orders").document(docId).collection("userOrder").document().getId();
        db.collection("orders").document(docId).collection("userOrder").document(docIdSe).set(currentUser)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errors++;
                    }
                });


        //Add Additional infos about the order in Collection Additional
        additionalList adList = new additionalList(curDate, curDeliver, "In desfasurare");

        final String docIdTh = db.collection("orders").document(docId).collection("additionalList").document().getId();
        db.collection("orders").document(docId).collection("additionalList").document(docIdTh).set(adList)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        errors++;
                    }
                });


        //Add filling null in order for the document to exist
        Map<String, Object> fakeData = new HashMap<>();
        fakeData.put("filling", "null");
        db.collection("orders").document(docId).set(fakeData);


        //order has been placed
        if (errors < 1) {

                dataBase_label.setText("Comanda adaugata ! :" + docId);

                //enable button after the process completed
                finishOrder.setEnabled(true);

                //clear the form
                clear_form();

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

                //get current date
                Date userJoin = new Date();

                userList userList = new userList(currentUser.getUserAddr(), currentUser.getUserName(), currentUser.getUserPhone(), 1, userJoin, 0);
                String docIdCl = db.collection("clients").document().getId();
                // Add a new document with a generated ID
                db.collection("clients").document(docIdCl).set(userList)
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


        finishOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userList curUser = new userList(client_address.getText().toString(), client_name.getText().toString(), client_phone.getText().toString(), current_user_orders, new Date(), 0);
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
                    client_address.setEnabled(true);

                    //Search for each client in basedata for the current phone number inserted
                    for (int i = 0; i < clients_extracted.size(); i++) {

                        //NEED TO add mod for not stressing UI too much
                        dataBase_label.setText(i + "/" + clients_extracted.size());

                        if (clients_extracted.get(i).getUserPhone().contains(client_phone.getText().toString())) {

                            //Autocomplete Client name in UI
                            client_name.setText(clients_extracted.get(i).getUserName());

                            //Autocomplete Client address in UI
                            client_address.setText(clients_extracted.get(i).getUserAddr());

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
                            client_address.setEnabled(false);

                            // Close keyboard
                            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(client_phone.getWindowToken(), 0);


                            break; //stop searching
                        }
                        if (i == clients_extracted.size() - 1) {
                            //Set infotaiment label text
                            dataBase_label.setText(":(  Clientul nu exista, adauga datele si finalizeaza comanda pentru a-l adauga!");
                        }
                    }
                }
            }
        });


    }


}
