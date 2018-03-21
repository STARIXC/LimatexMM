package g3org3.limatexmm;

import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class orders extends AppCompatActivity {

    Button back_button;
    RecyclerView rv_orders;
    MyRecyclerViewAdapterOrders adapter;
    FirebaseFirestore db;

    List<String> documentsIDs;

    ArrayList<listItems> ordersList;

    ArrayList<orderListBig> allOrders;

    userList userSimple;
    additionalList additionalSimple;

    String public_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);


        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        back_button = findViewById(R.id.back_button);
        rv_orders = findViewById(R.id.rv_orders);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //connect to database
        db = FirebaseFirestore.getInstance();


        //initialize documentsIDs list
        documentsIDs = new ArrayList<>();

        allOrders = new ArrayList<>();


        rv_orders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapterOrders(this, allOrders);

        rv_orders.setAdapter(adapter);


        db.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                        documentsIDs.add(document.getId());
                        public_id = document.getId();


                        //initialize the Order list
                        ordersList = new ArrayList<>();

                        Log.e("~~~~~~~~~~~~~~~~", "FOUND DOCS: :" + public_id);

                        //extract
                        //extract ALL ORDER ITEMS
                        db.collection("orders").document(public_id).collection("allOrders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot document : task.getResult()) {
                                        //for each order

                                        Log.e("~~~~~~~~~~~~~~~~", "extracting all orders");

                                        //extract the inside of the document as a listItems
                                        listItems orderSimple = document.toObject(g3org3.limatexmm.listItems.class);

                                        Log.e("~~~~~~~~~~~~~~~~", "Example extracted " + orderSimple.getItemTitle());


                                        //add the current item to the global list
                                    //    ordersList.add(orderSimple);
                                    }

                                    //extract USER
                                    db.collection("orders").document(public_id).collection("userOrder").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    //for each order

                                                    Log.e("~~~~~~~~~~~~~~~~", "extracting userOrder");


                                                    //extract the inside of the document
                                                    userSimple = document.toObject(g3org3.limatexmm.userList.class);

                                                    Log.e("~~~~~~~~~~~~~~~~", "Example extracted " + userSimple.getUserName());

                                                }
                                                //extract ADDITIONAL
                                                db.collection("orders").document(public_id).collection("additionalList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (DocumentSnapshot document : task.getResult()) {
                                                                //for each order

                                                                Log.e("~~~~~~~~~~~~~~~~", "extracting additionalList");
                                                                //extract the inside of the document
                                                                additionalSimple = document.toObject(additionalList.class);

                                                                Log.e("~~~~~~~~~~~~~~~~", "Example extracted " + additionalSimple.getOrderDeliver());

                                                            }

                                                            Log.e("~~~~~~~~~~~~~~~~", "Adding order to list!");
                                                            //extract the inside of the document
                                                            //after all steps

                                                            //build the model for the allOrders List

                                                            orderListBig orderModel = new orderListBig(ordersList, userSimple, additionalSimple,public_id);

                                                            Log.e("~~~~~~~~~~~~~~~~", "Example extracted " + orderModel.getUserSimple().getUserName());

                                                            //add the order to the allOrder List
                                                            allOrders.add(orderModel);

                                                            //refresh the list
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }

                } else {

                }


            }

        });



    }
}
