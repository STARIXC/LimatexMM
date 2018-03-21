package g3org3.limatexmm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.util.Date;
import java.util.List;


//public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
public class MyRecyclerViewAdapterOrders extends RecyclerView.Adapter<MyRecyclerViewAdapterOrders.ViewHolder> {

    //  private String[] mData = new String[0];
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private List<orderListBig> orderListBigList;
    Context mContext;

    // data is passed into the constructor
    MyRecyclerViewAdapterOrders(Context context, List<orderListBig> orderListBigList) {
        this.orderListBigList = orderListBigList;
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }


    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set string for button
        final orderListBig item = orderListBigList.get(position);


        holder.delivery_label.setText(item.getAdditionalSimple().getOrderStatus());

        if (item.getAdditionalSimple().getOrderDeliver().equals(1)) {
            holder.delivery_info_label.setText("Fara livrare!");
        } else {
            holder.delivery_info_label.setText("Livrare la: ");
            holder.delivery_info_label.append(item.getUserSimple().getUserName() + "(" + item.getUserSimple().getUserOrders() + ") " + item.getUserSimple().getUserAddr());
        }


        //Get current date
        Date temp_date2 = new Date();


        StringBuilder finalDate = new StringBuilder();
        finalDate.append("Data: ");
        finalDate.append(item.getAdditionalSimple().getOrderDate().getDay());
        finalDate.append(".");
        finalDate.append(item.getAdditionalSimple().getOrderDate().getMonth());
        finalDate.append(".");
        finalDate.append(item.getAdditionalSimple().getOrderDate().getYear());
        finalDate.append("  ");
        finalDate.append(item.getAdditionalSimple().getOrderDate().getHours());
        finalDate.append(":");
        finalDate.append(item.getAdditionalSimple().getOrderDate().getMinutes());

        //Calculate the days from when he joined
        long diff = temp_date2.getTime() - item.getAdditionalSimple().getOrderDate().getTime();
        String jMins = String.valueOf(diff / 1000 / 60);

        if (item.getAdditionalSimple().getOrderStatus().equals("In desfasurare!")){
            finalDate.append("\n Au trecut " + jMins + " minute");
        } else {
            finalDate.append("...");
        }

        holder.data_label.setText(finalDate);

        Integer maxi = item.getOrderList().size();
        if (maxi > 2) {
            maxi = 2;
        }
        StringBuilder finalItems = new StringBuilder();
        for (int i = 0; i < maxi; i++) {
            finalItems.append(item.getOrderList().get(i).getItemTitle());
            finalItems.append("\n");
        }
        finalItems.append("(click pentru toata lista)");

        holder.order_label.setText(finalItems);

        Double totalPrice = 0.0;
        for (int i2 = 0; i2 < item.getOrderList().size(); i2++) {
            totalPrice = totalPrice + item.getOrderList().get(i2).getItemPrice() + item.getOrderList().get(i2).getItemMorevalue();
        }

        holder.total_label.setText(String.valueOf(totalPrice) + " lei");

        additionalId = "";

        holder.order_label.setClickable(true);
        holder.order_label.setFocusable(true);
        holder.order_label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 Toast.makeText(mContext, "Comanda: " + item.getOrderList().toString(), Toast.LENGTH_LONG).show();
            }
        });


        holder.complete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Se finalizeaza comanda...", Toast.LENGTH_LONG).show();
               ModifyOrder("Finalizata", item.getDocID());
            }
        });

        holder.deny_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Se marcheaza ca nefinalizeaza ...", Toast.LENGTH_LONG).show();
                ModifyOrder("Nefinalizata!", item.getDocID());
            }
        });
    }

    String additionalId;

    // total number of cells
    @Override
    public int getItemCount() {
        return orderListBigList.size();
    }

    public void ModifyOrder(final String endText, final String itemID){
        db.collection("orders").document(itemID).collection("additionalList").whereEqualTo("orderStatus", "In desfasurare")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {

                                additionalId = document.getId();


                            }

                            DocumentReference user_profile = db.collection("orders").document(itemID).collection("additionalList").document(additionalId);
                            user_profile.update("orderStatus", endText)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //refresh me
                                        }
                                    });
                        }
                    }
                });
    }

    FirebaseFirestore db;

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button complete_button;
        Button deny_button;
        TextView delivery_label;
        TextView delivery_info_label;
        TextView data_label;
        TextView total_label;
        TextView order_label;


        ViewHolder(View itemView) {
            super(itemView);

            //Get the database
            db = FirebaseFirestore.getInstance();

            complete_button = itemView.findViewById(R.id.complete_button);
            deny_button = itemView.findViewById(R.id.deny_button);
            delivery_label = itemView.findViewById(R.id.delivery_label);
            delivery_info_label = itemView.findViewById(R.id.delivery_info_label);
            data_label = itemView.findViewById(R.id.data_label);
            total_label = itemView.findViewById(R.id.total_label);
            order_label = itemView.findViewById(R.id.order_label);


            //  cart_remove.setOnClickListener(this);
            //   itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    // allows clicks events to be caught

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
