package g3org3.limatexmm;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
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

import java.util.ArrayList;
import java.util.Collections;

public class bucatarie extends AppCompatActivity {

    android.support.v7.widget.Toolbar appBar;
    RecyclerView rv_bucatarie;

    MyRecyclerViewAdapterBucatarie adapter;
    ArrayList<orderListBig> allOrders;
    FirebaseDatabase database;
    DatabaseReference ref;
    orderListBigList hashMap;
    String currentDate = "";
    Boolean loaded = false;
    int noOfColumns = 1;

    Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bucatarie);


        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        rv_bucatarie = findViewById(R.id.rv_bucatarie);
        appBar = findViewById(R.id.appBar);
        this.setSupportActionBar(appBar);

        database = FirebaseDatabase.getInstance();
        currentDate = String.valueOf(android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date()));
        ref = database.getReference("pizza/" + currentDate);

        allOrders = new ArrayList<>();
        myContext = this;

        //calculate maximum colums
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        noOfColumns = (int) (dpWidth / 250);

        updateList();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hashMap = dataSnapshot.getValue(orderListBigList.class);

                //convert hashmap object to list
                try {
                    allOrders = new ArrayList<>(hashMap.getOrders().values());
                    loaded = true;
                    filterList();
                } catch (Exception E) {
                    Toast.makeText(getApplicationContext(), "Eroare: " + E.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //listen for data from adapter
     //   LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("showDialog"));

    }
/*
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String personName = intent.getStringExtra("personName");
            String phone = intent.getStringExtra("phone");

            //show the dialog
            expandItem(personName, phone, "", "", "");

        }
    };
*/

    public void filterList() {
        if (loaded) {
            //filter & order allOrders List
            //show only in transit orders

            //clear the list
            allOrders.clear();

            //regain all items
            allOrders = new ArrayList<orderListBig>(hashMap.getOrders().values());

            String searchStatus = "In bucatarie";

            for (int i = 0; i < allOrders.size(); i++) {

                if (!allOrders.get(i).getAdditionalSimple().getOrderStatus().toLowerCase().trim().equals(searchStatus.toLowerCase().trim())) {
                    //remove unnecesary items
                    allOrders.remove(i);
                    i--;
                }
            }

            //Collections.sort(allOrders, new TimeComparator());

            //update UI
            updateList();

        }
    }


    public void updateList() {

        //update the adapter
        rv_bucatarie.setLayoutManager(new StaggeredGridLayoutManager(noOfColumns, StaggeredGridLayoutManager.VERTICAL));
        adapter = new MyRecyclerViewAdapterBucatarie(this, allOrders);
        rv_bucatarie.setAdapter(adapter);

    }


    /*






    Dialog myDialog;
    ImageButton back;
    ImageButton ok;
    TextView deliverMore;
    Button personName;
    Button phone;
    Button time;
    Button deliver;




    //send data to activity
    String personName = item.getUserSimple().getUserName();
    String phone = item.getUserSimple().getUserPhone();
    String data;
    String delivery;
    String moreDelivery;
    Intent intent = new Intent("showDialog");
                intent.putExtra("personName",personName);
                intent.putExtra("phone",phone);
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);


    public void expandItem(String personNameData, String phoneData, String timeData, String deliveryData, String moreDeliveryData) {

        myDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        myDialog.setContentView(R.layout.bucatarie_meniu);
        myDialog.setTitle("More");


        back = myDialog.findViewById(R.id.back);
        ok = myDialog.findViewById(R.id.ok);
        deliverMore = myDialog.findViewById(R.id.deliverMore);
        personName = myDialog.findViewById(R.id.personName);
        phone = myDialog.findViewById(R.id.phone);
        time = myDialog.findViewById(R.id.time);
        deliver = myDialog.findViewById(R.id.deliver);

        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDialog.dismiss();
            }
        });

        personName.setText(personNameData);
        phone.setText(phoneData);

        //  deliver.setText(item.additionalSimple.getOrderDeliver());



        myDialog.show();

    }
*/

}
