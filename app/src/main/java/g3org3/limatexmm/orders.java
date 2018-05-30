package g3org3.limatexmm;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.opengl.Visibility;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class orders extends AppCompatActivity {

    //declaring views
    Button driverButton;
    Button deliverButton;
    Button kitchenButton;
    Button readyButton;
    android.support.v7.widget.Toolbar appBar;

    com.getbase.floatingactionbutton.FloatingActionsMenu moreButton;
    com.getbase.floatingactionbutton.FloatingActionButton firstButton;
    com.getbase.floatingactionbutton.FloatingActionButton secondButton;

    RecyclerView rv_orders;
    MyRecyclerViewAdapterOrders adapter;
    ArrayList<orderListBig> allOrders;
    FirebaseDatabase database;
    DatabaseReference ref;
    orderListBigList hashMap;
    Boolean loaded = false;
    Integer currentView = 2;
    Context mContext;
    String currentDate = "";


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        rv_orders = findViewById(R.id.rv_orders);
        driverButton = findViewById(R.id.driverButton);
        kitchenButton = findViewById(R.id.kitchenButton);
        readyButton = findViewById(R.id.readyButton);
        deliverButton = findViewById(R.id.deliverButton);

        moreButton = findViewById(R.id.moreButton);
        firstButton = findViewById(R.id.firstButton);
        secondButton = findViewById(R.id.secondButton);
        //   refreshButton = findViewById(R.id.refreshButton);


        appBar = findViewById(R.id.appBar);
        this.setSupportActionBar(appBar);



        //connect to database
        database = FirebaseDatabase.getInstance();
        currentDate = String.valueOf(android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date()));
        ref = database.getReference("pizza/" + currentDate);

        mContext = this;

        deliverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loaded) {
                    deliverButton.setBackground(getResources().getDrawable(R.drawable.ripple_button_darker));
                    driverButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                    readyButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                    kitchenButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                    currentView = 1;
                    filterList();
                }
            }
        });
        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loaded) {
                deliverButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                driverButton.setBackground(getResources().getDrawable(R.drawable.ripple_button_darker));
                readyButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                kitchenButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                currentView = 2;
                filterList();
                }
            }
        });
        readyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if (loaded) {
                deliverButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                driverButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                readyButton.setBackground(getResources().getDrawable(R.drawable.ripple_button_darker));
                kitchenButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                currentView = 3;
                filterList();
                    }
            }
        });
        kitchenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loaded) {
                deliverButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                driverButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                readyButton.setBackground(getResources().getDrawable(R.drawable.ripple_button));
                kitchenButton.setBackground(getResources().getDrawable(R.drawable.ripple_button_darker));
                currentView = 4;
                filterList();
                }
            }
        });

        allOrders = new ArrayList<>();
        updateList();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hashMap = dataSnapshot.getValue(orderListBigList.class);

                //convert hashmap object to list
                try {
                    allOrders = new ArrayList<>(hashMap.getOrders().values());
                    loaded = true;
                    kitchenButton.performClick();
               } catch (Exception E) {
                    customToast("Nici o comanda gasita azi!", false);
                   //  Toast.makeText(getApplicationContext(), "Eroare: " + E.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItems = new ArrayList<>();
                for (int i = 0; i < allOrders.size(); i++) {
                   if (rv_orders.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.expandButton).isShown()) {
                       selectedItems.add(i);
                    }
                }

                finishSelected();

            }
        });

        secondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItems = new ArrayList<>();
                for (int i = 0; i < allOrders.size(); i++) {
                    if (rv_orders.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.expandButton).isShown()) {
                        selectedItems.add(i);
                    }
                }

                cancelSelected();

            }
        });


        //add refresh timer
        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                           //     filterList();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();


    }

    List<Integer> selectedItems;
    //  FirebaseDatabase databasee;
    private DatabaseReference reff;

    public void cancelSelected() {

        for (int i = 0; i < selectedItems.size(); i++) {
            reff = database.getReference("pizza/" + currentDate + "/orders/" + allOrders.get(i).dateSimple.substring(8, 14) + "/additionalSimple/orderStatus");
            reff.setValue("Anulata");
        }

    }

    public void finishSelected() {
        //connect to database
        //  databasee = FirebaseDatabase.getInstance();
        for (int i = 0; i < selectedItems.size(); i++) {
            reff = database.getReference("pizza/" + currentDate + "/orders/" + allOrders.get(i).dateSimple.substring(8,14) + "/additionalSimple/orderStatus");
            if (allOrders.get(i).getAdditionalSimple().getOrderStatus().equals("De livrat")) {
                reff.setValue("Pe drum");
            } else if (allOrders.get(i).getAdditionalSimple().getOrderStatus().equals("Pe drum")) {
                reff.setValue("Completata");
            } else if (allOrders.get(i).getAdditionalSimple().getOrderStatus().equals("De ridicat")) {
                reff.setValue("Completata");
            } else if (allOrders.get(i).getAdditionalSimple().getOrderStatus().equals("In bucatarie")) {
                if (allOrders.get(i).getAdditionalSimple().getOrderDeliver() > 0) {
                    reff.setValue("De livrat");
                } else {
                    reff.setValue("De ridicat");
                }
            }
        }

    }

    public void hideFABs() {
        if (firstButton.isShown()) {
            moreButton.collapse();
        }
    }


    public void refreshFABs() {
        if (currentView.equals(1)) {
            //De livrat
            firstButton.setTitle("Pe drum");
        } else if (currentView.equals(2)) {
            //Pe drum
            firstButton.setTitle("Livrate");

        } else if (currentView.equals(3)) {
            //De ridicat
            firstButton.setTitle("Ridicate");

        } else if (currentView.equals(4)) {
            //In bucatarie
            firstButton.setTitle("Pregatite!");

        }
    }

    public void updateList() {
        //update UI
        // rv_orders.setLayoutManager(new GridLayoutManager(this, 2));
        rv_orders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapterOrders(this, allOrders, rv_orders);
        rv_orders.setAdapter(adapter);
    }


    public void filterList() {
        if (loaded) {
            //filter & order allOrders List
            //show only in transit orders

            //clear the list
            allOrders.clear();

            //regain all items
            allOrders = new ArrayList<orderListBig>(hashMap.getOrders().values());

            String searchStatus = "";
            if (currentView.equals(1)) {
                searchStatus = "De livrat";
            } else if (currentView.equals(2)) {
                searchStatus = "Pe drum";
            } else if (currentView.equals(3)) {
                searchStatus = "De ridicat";
            } else if (currentView.equals(4)) {
                searchStatus = "In bucatarie";
            }


            for (int i = 0; i < allOrders.size(); i++) {

                if (!allOrders.get(i).getAdditionalSimple().getOrderStatus().toLowerCase().trim().equals(searchStatus.toLowerCase().trim())) {
                    //remove unnecesary items
                    allOrders.remove(i);
                    i--;
                }
            }




            Collections.sort(allOrders, new TimeComparator());

            hideFABs();

            refreshFABs();

            //update UI
            updateList();

        }
    }


    //TODO: filter orders only for non-finalized ones

}

 class TimeComparator implements Comparator<orderListBig> {

    public int compare(orderListBig time1, orderListBig time2) {
try {
    Date temp_date2 = new Date();

    Date item_date = time1.additionalSimple.getOrderDate();
    long diff = temp_date2.getTime() - item_date.getTime();
    String jMins = String.valueOf(diff / 1000 / 60);
    Integer jMinsI = Integer.valueOf(jMins);
    jMinsI = jMinsI + time1.userSimple.getUserAddrCurrentTime();


    Date item_date2 = time2.additionalSimple.getOrderDate();
    long diff2 = temp_date2.getTime() - item_date2.getTime();
    String jMins2 = String.valueOf(diff2 / 1000 / 60);
    Integer jMinsI2 = Integer.valueOf(jMins2);
    jMinsI2 = jMinsI2 + time2.userSimple.getUserAddrCurrentTime();


    return jMinsI2.compareTo(jMinsI);
} catch (Exception E) {
    return 0;
}
    }
}





