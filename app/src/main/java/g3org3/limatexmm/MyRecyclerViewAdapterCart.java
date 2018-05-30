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
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Date;
import java.util.List;


public class MyRecyclerViewAdapterCart extends RecyclerView.Adapter<MyRecyclerViewAdapterCart.ViewHolder> {
    private static final int UNSELECTED = -1;
    private int selectedItem = UNSELECTED;

    private RecyclerView recyclerView;
    private List<listItems> list_itemsList;
    private Context mContext;


    public MyRecyclerViewAdapterCart(Context context, List<listItems> list_itemsList, RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.list_itemsList = list_itemsList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_model_cart, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.bind();
        final listItems item = list_itemsList.get(position);

        //set title
        holder.cart_title.setText((position + 1) + ". " + item.getItemTitle() + " " + item.getItemSubtitle());

        String extraPrice = String.valueOf(item.getItemMorevalue());
        String prodPrice = String.valueOf((item.getItemPrice() + item.getItemMorevalue()) * item.getItemQuantity());

        String temp_pharse = "";
      //  if (Double.valueOf(extraPrice) > 0) {
       //     temp_pharse = "(" + extraPrice.replace(".0", "") + ")";
      //  }
        temp_pharse = temp_pharse + prodPrice + " Lei";

        holder.cart_price.setText(temp_pharse);

        String finalS = "";
        if (item.getItemMore().length() > 1) {
            finalS = item.getItemMore() + finalS + " ";
        }

        final String finalSS = finalS;

        holder.cart_count.setText("x" + String.valueOf(item.getItemQuantity()));

        holder.cart_more.setText(finalS);


        holder.cart_more_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Notite: " + finalSS , Toast.LENGTH_LONG).show();
            }
        });

        holder.cart_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer curQ = list_itemsList.get(position).getItemQuantity();
                if (curQ < 100) {
                    list_itemsList.get(position).setItemQuantity(curQ + 1);
                }
                notifyDataSetChanged();
            }
        });

        holder.cart_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer curQ = list_itemsList.get(position).getItemQuantity();
                if (curQ > 1) {
                    list_itemsList.get(position).setItemQuantity(curQ - 1);
                } else {
                    list_itemsList.remove(position);
                }

                notifyDataSetChanged();
            }
        });

        holder.expandButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                list_itemsList.remove(position);
                notifyDataSetChanged();

                return true;
            }
        });


    }


    // total number of cells
    @Override
    public int getItemCount() {
        return list_itemsList.size();
    }

    String getTotalPrice() {
        double tempTotalPrice = 0;
        // for each item
        for (int i = 0, size = list_itemsList.size(); i < size; i++) {
            //get each item
            final listItems item = list_itemsList.get(i);
            tempTotalPrice = tempTotalPrice + ((item.getItemPrice() + item.getItemMorevalue()) * item.getItemQuantity());
        }

        return String.valueOf(tempTotalPrice);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ExpandableLayout.OnExpansionUpdateListener {
        private ExpandableLayout expandableLayout;
        private ImageButton expandButton;
        TextView cart_count;
        TextView cart_title;
        TextView cart_price;
        TextView cart_more;
        ImageButton cart_more_button;
        ImageButton cart_add;
        ImageButton cart_remove;


        public ViewHolder(View itemView) {
            super(itemView);

            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            expandButton = itemView.findViewById(R.id.expandButton);
            cart_count = itemView.findViewById(R.id.cart_count);
            cart_title = itemView.findViewById(R.id.cart_title);
            cart_price = itemView.findViewById(R.id.cart_price);
            cart_more = itemView.findViewById(R.id.cart_more);
            cart_more_button = itemView.findViewById(R.id.cart_more_button);
            cart_add = itemView.findViewById(R.id.cart_add);
            cart_remove = itemView.findViewById(R.id.cart_remove);
            expandableLayout.setInterpolator(new DecelerateInterpolator());
            expandableLayout.setOnExpansionUpdateListener(this);

            expandButton.setOnClickListener(this);
        }

        public void bind() {
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;
            expandButton.setSelected(isSelected);
            expandableLayout.setExpanded(isSelected, false);
        }

        @Override
        public void onClick(View view) {
            MyRecyclerViewAdapterCart.ViewHolder holder = (MyRecyclerViewAdapterCart.ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
                holder.expandButton.setSelected(false);
                holder.expandableLayout.collapse();

                // WhiteDark
                holder.cart_count.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_white));
                holder.cart_title.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_white));
                holder.cart_price.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_white));

                holder.expandButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_expand_more_black_24dp));


            }

            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;


            } else {
                expandButton.setSelected(true);
                expandableLayout.expand();

                // White
                cart_count.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_white_dark));
                cart_title.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_white_dark));
                cart_price.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_white_dark));

               expandButton.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_expand_less_black_24dp));

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


