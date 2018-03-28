package g3org3.limatexmm;

import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
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


    public void customToast(String finalt, Boolean longer) {
        //show custom TOAST
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

        //getting the maximum colloms for screen

        rv_orders.setLayoutManager(new GridLayoutManager(this,2));

       // rv_orders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapterOrders(this, allOrders);

        rv_orders.setAdapter(adapter);


        customToast("Se incarca...", false);



        db.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {

                           orderListBig orderModel = document.toObject(orderListBig.class);

                        if (orderModel.getAdditionalSimple().getOrderStatus().toLowerCase().contains("desfasurare")) {
                            //add the order to the allOrder List
                            allOrders.add(0, orderModel);

                        } else {
                            //add the order to the allOrder List
                            allOrders.add(orderModel);
                        }


                           documentsIDs.add(document.getId());

                    }
                    Integer allIdsD = task.getResult().size();

                    if (allIdsD.equals(documentsIDs.size())) {

                        back_button.setText("Inapoi la casa | " + String.valueOf(allIdsD) + " comenzi incarcate!");

                        adapter.notifyDataSetChanged();

                    }
                }
            }
        });

    }

    /*

    Integer allIds = 0;

    public void readUsers() {
        //extract USER




        db.collection("orders").document(public_id).collection("userOrder").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        //for each order

                        //extract the inside of the document
                        userSimple = document.toObject(g3org3.limatexmm.userList.class);

                    }
                    //extract ADDITIONAL
                    db.collection("orders").document(public_id).collection("additionalList").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    //for each order

                                    //extract the inside of the document
                                    additionalSimple = document.toObject(additionalList.class);


                                }
                                //extract the inside of the document
                                //after all steps

                                //build the model for the allOrders List

                                orderListBig orderModel = new orderListBig(ordersList, userSimple, additionalSimple, public_id);


                                if (orderModel.getAdditionalSimple().getOrderStatus().toLowerCase().contains("desfasurare")) {
                                    //add the order to the allOrder List
                                    allOrders.add(0, orderModel);

                                } else {
                                    //add the order to the allOrder List
                                    allOrders.add(orderModel);
                                }

                                //refresh the list
                                back_button.setText("Inapoi la casa | Se incarca: " + String.valueOf(allIds + 1) + " din " + documentsIDs.size());

                                //if current ids scanned are not all the documents
                                if (!allIds.equals(documentsIDs.size() - 1)) {
                                    allIds++;
                                    readDocs();
                                } else {
                                    back_button.setText("Inapoi la casa | " + String.valueOf(allIds + 1) + " comenzi incarcate!");
                                    adapter.notifyDataSetChanged();
                                    //ending
                                }

                            }
                        }
                    });
                }
            }
        });
    }


    public void readDocs() {

        //get document id
        public_id = documentsIDs.get(allIds);

        //initialize the Order list
        ordersList = new ArrayList<>();

        //extract
        //extract ALL ORDER ITEMS

        db.collection("orders").document(public_id).collection("allOrders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        //for each order

                        //extract the inside of the document as a listItems
                        listItems orderSimple = document.toObject(g3org3.limatexmm.listItems.class);

                        //add the current item to the global list
                        ordersList.add(orderSimple);
                    }

                    Integer allOrd = task.getResult().size();

                    if (allOrd.equals(ordersList.size())) {
                        readUsers();
                    }
                }
            }
        });
    }


    */

}





