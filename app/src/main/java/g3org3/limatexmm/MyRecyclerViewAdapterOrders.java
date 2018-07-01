package g3org3.limatexmm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Date;
import java.util.List;


public class MyRecyclerViewAdapterOrders extends RecyclerView.Adapter<MyRecyclerViewAdapterOrders.ViewHolder> {
    private static final int UNSELECTED = -1;

    private RecyclerView recyclerView;
    private List<orderListBig> orderListBigList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int selectedItem = UNSELECTED;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    private Boolean more = false;
    public Boolean batchSelected = false;

    public MyRecyclerViewAdapterOrders(Context context, List<orderListBig> orderListBigList, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.orderListBigList = orderListBigList;
        this.mContext = context;
        // this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_model_order, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind();
        final orderListBig item = orderListBigList.get(position);

        //Write the delivery Address (if needed)
        if (item.getAdditionalSimple().getOrderDeliver().equals(0)) {
            holder.deliveryAddress.setText("Fara livrare!");
        } else {
            holder.deliveryAddress.setText(item.getUserSimple().getUserAddrCurrent());
        }

        //Write client informations
        holder.deliveryName.setText(item.getUserSimple().getUserName());
        holder.deliveryPhone.setText(item.getUserSimple().getUserPhone());

        //Write order
        Integer maxi = item.getOrderList().size();
        if (maxi > 2) {
            maxi = 2;
        }
        StringBuilder finalItems = new StringBuilder();
        for (int i = 0; i < maxi; i++) {
            finalItems.append("x");
            finalItems.append(item.getOrderList().get(i).getItemQuantity());
            finalItems.append(" ");
            finalItems.append(item.getOrderList().get(i).getItemTitle());
            finalItems.append(" ");
            finalItems.append(item.getOrderList().get(i).getItemSubtitle());
            finalItems.append("\n");
        }

        if (item.getOrderList().size() > 2) {
            finalItems.append("... inca " + String.valueOf(item.getOrderList().size()-2) + " produse");
        }

        holder.orderLabel.setText(finalItems);


        //Write Total Order Value
        Double totalPrice = 0.0;
        for (int i2 = 0; i2 < item.getOrderList().size(); i2++) {
            totalPrice = totalPrice + ((item.getOrderList().get(i2).getItemPrice() + item.getOrderList().get(i2).getItemMorevalue()) * item.getOrderList().get(i2).getItemQuantity());
        }

        holder.totalLabel.setText("Total: " + String.valueOf(totalPrice) + " lei");
        if (item.getPaid()) {
            holder.paidLabel.setText("Platita");
            holder.paidLabel.setTextColor(Color.rgb(49, 158, 34));
        } else {
            holder.paidLabel.setText("Neplatita!");
            holder.paidLabel.setTextColor(Color.rgb(244, 66, 66));
        }

        //Get current date
        Date temp_date2 = new Date();

        Date item_date = item.additionalSimple.getOrderDate();


        holder.idLabel.setText("Nr.Comanda: " + String.valueOf(item.countSimple));

        //Write time passed
        long diff = temp_date2.getTime() - item_date.getTime();
        String jMins = String.valueOf(diff / 1000 / 60);
        Integer jMinsI = Integer.valueOf(jMins);

        //if item require deliver
        if (item.getAdditionalSimple().getOrderDeliver().equals(1)) {
            //display the time passed + time to deliver
            holder.timeLabel.setText(String.valueOf(jMinsI)); // + item.getUserSimple().getUserAddrCurrentTime()));
            holder.timeLabelS.setVisibility(View.VISIBLE);
            holder.timeLabelS.setText("+" + item.getUserSimple().getUserAddrCurrentTime());
        } else {
            //display only the time passed
            holder.timeLabel.setText(String.valueOf(jMinsI));
            holder.timeLabelS.setVisibility(View.GONE);
        }


        Drawable clone = mContext.getResources().getDrawable(R.drawable.simple_circle).getConstantState().newDrawable();
        Integer finalVal = Integer.valueOf(holder.timeLabel.getText().toString());

        if (finalVal < 10) {
            clone.setColorFilter(Color.rgb(114, 160, 16), PorterDuff.Mode.MULTIPLY);
        } else if (finalVal >= 10 && finalVal < 25) {
            clone.setColorFilter(Color.rgb(206, 192, 0), PorterDuff.Mode.MULTIPLY);
        } else if (finalVal >= 25 && finalVal < 40) {
            clone.setColorFilter(Color.rgb(221, 111, 37), PorterDuff.Mode.MULTIPLY);
        } else if (finalVal >= 40 && finalVal < 60) {
            clone.setColorFilter(Color.rgb(219, 65, 65), PorterDuff.Mode.MULTIPLY);
        } else if (finalVal >= 60 && finalVal < 2000) {
            clone.setColorFilter(Color.rgb(96, 29, 87), PorterDuff.Mode.MULTIPLY);
        }

        holder.timeLabel.setBackground(clone);


        //connect to database
        database = FirebaseDatabase.getInstance();
        String currentDate = String.valueOf(android.text.format.DateFormat.format("yyyyMMdd", new java.util.Date()));
        ref = database.getReference("pizza/" + currentDate + "/orders/" + item.dateSimple.substring(8, 14) + "/additionalSimple/orderStatus");

        holder.denyButton.setText("Anuleaza comanda!");
        holder.denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ref.setValue("Anulata");
            }
        });

        //Different every order
        if (item.getAdditionalSimple().getOrderStatus().equals("De livrat")) {
            holder.barColor.setBackgroundColor(Color.rgb(110, 159, 239));
            holder.completeButton.setText("Comanda e in masina!");
            holder.completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ref.setValue("Pe drum");
                }
            });
        } else if (item.getAdditionalSimple().getOrderStatus().equals("Pe drum")) {
            holder.barColor.setBackgroundColor(Color.rgb(249, 160, 104));
            holder.completeButton.setText("Livrat la client!");
            holder.completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ref.setValue("Completata");
                }
            });

        } else if (item.getAdditionalSimple().getOrderStatus().equals("De ridicat")) {
            holder.barColor.setBackgroundColor(Color.rgb(236, 104, 249));
            holder.completeButton.setText("Ridicat de client !");
            holder.completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ref.setValue("Completata");
                }
            });
        } else if (item.getAdditionalSimple().getOrderStatus().equals("In bucatarie")) {
            if (item.getAdditionalSimple().getOrderDeliver() > 0) {
                holder.barColor.setBackgroundColor(Color.rgb(94, 219, 173));
                holder.completeButton.setText("Gata de Livrat!");
                holder.completeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ref.setValue("De livrat");
                    }
                });
            } else {
                holder.barColor.setBackgroundColor(Color.rgb(94, 214, 219));
                holder.completeButton.setText("Gata de Ridicat!");
                holder.completeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ref.setValue("De ridicat");
                    }
                });
            }
        }

        final StringBuilder allOrder = new StringBuilder();
        for (int i = 0; i < item.getOrderList().size(); i++) {
            String nr = String.valueOf((i + 1));
            allOrder.append(nr + ") ");
            allOrder.append(item.getOrderList().get(i).getItemTitle());
            allOrder.append(", ");
            allOrder.append(item.getOrderList().get(i).getItemSubtitle());
            allOrder.append(" x");
            allOrder.append(item.getOrderList().get(i).getItemQuantity());
            allOrder.append("      ");
        }


        holder.expandOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!more) {
                    more = true;
                    holder.moreView.setVisibility(View.VISIBLE);
                    holder.moreView.setText(allOrder);
                } else {
                    more = false;
                    holder.moreView.setVisibility(View.GONE);

                }
            }
        });


        holder.aboveButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!batchSelected) {
                    batchSelected = true;
                    holder.expandButton.setVisibility(View.VISIBLE);
                } else {
                    batchSelected = false;
                    holder.expandButton.setVisibility(View.INVISIBLE);
                }
                return true;
            }
        });

        holder.aboveButtonS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.aboveButton.performLongClick();
            }
        });


    }


    // total number of cells
    @Override
    public int getItemCount() {
        return orderListBigList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        private ExpandableLayout expandableLayout;
        private ImageButton expandButton;
        Button completeButton;
        Button denyButton;
        TextView deliveryAddress;
        TextView totalLabel;
        TextView orderLabel;
        TextView idLabel;
        TextView deliveryName;
        TextView timeLabel;
        TextView deliveryPhone;
        View barColor;
        TextView timeLabelS;
        Button expandOrderButton;
        TextView moreView;
        View aboveButtonS;
        View aboveButton;
        TextView paidLabel;
        android.support.constraint.ConstraintLayout topView;


        public ViewHolder(View itemView) {
            super(itemView);

            completeButton = itemView.findViewById(R.id.completeButton);
            denyButton = itemView.findViewById(R.id.denyButton);
            deliveryAddress = itemView.findViewById(R.id.deliveryAddress);
            totalLabel = itemView.findViewById(R.id.totalLabel);
            orderLabel = itemView.findViewById(R.id.orderLabel);
            idLabel = itemView.findViewById(R.id.idLabel);
            deliveryName = itemView.findViewById(R.id.deliveryName);
            timeLabel = itemView.findViewById(R.id.timeLabel);
            deliveryPhone = itemView.findViewById(R.id.deliveryPhone);
            barColor = itemView.findViewById(R.id.barColor);
            topView = itemView.findViewById(R.id.topView);
            expandOrderButton = itemView.findViewById(R.id.expandOrderButton);
            timeLabelS = itemView.findViewById(R.id.timeLabelS);
            moreView = itemView.findViewById(R.id.moreView);
            aboveButton = itemView.findViewById(R.id.aboveButton);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            expandableLayout.setInterpolator(new DecelerateInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);
            expandButton = itemView.findViewById(R.id.expandButton);
            aboveButtonS = itemView.findViewById(R.id.aboveButtonS);
            paidLabel = itemView.findViewById(R.id.paidLabel);


            aboveButton.setOnClickListener(this);
        }

        public void bind() {
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;
            aboveButton.setSelected(isSelected);
            expandableLayout.setExpanded(isSelected, false);
        }

        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                holder.aboveButton.setSelected(false);
                holder.expandableLayout.collapse();

                // WhiteDark
                holder.topView.setBackgroundColor(Color.rgb(244, 244, 244));
                holder.completeButton.setBackgroundColor(Color.rgb(244, 244, 244));
                holder.denyButton.setBackgroundColor(Color.rgb(244, 244, 244));
                holder.expandOrderButton.setBackgroundColor(Color.rgb(244, 244, 244));

                if (more) {
                    holder.expandOrderButton.performClick();
                }

                if (batchSelected) {
                    holder.aboveButton.performLongClick();
                }

            }

            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;


            } else {
                aboveButton.setSelected(true);
                expandableLayout.expand();

                if (batchSelected) {
                    aboveButton.performLongClick();
                }

                // White
                topView.setBackgroundColor(Color.rgb(255, 255, 255));
                completeButton.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_white));
                denyButton.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_white));
                expandOrderButton.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_white));

                selectedItem = position;
            }
        }

        @Override
        public void onExpansionUpdate(float expansionFraction, int state) {
            if (state == ExpandableLayout.State.EXPANDING) {
                recyclerView.smoothScrollToPosition(getAdapterPosition());
            }
        }
    }
}


