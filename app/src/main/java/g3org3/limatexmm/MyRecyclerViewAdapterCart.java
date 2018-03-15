package g3org3.limatexmm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by meg3o on 3/8/2018.
 */

//public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
public class MyRecyclerViewAdapterCart extends RecyclerView.Adapter<MyRecyclerViewAdapterCart.ViewHolder> {

  //  private String[] mData = new String[0];
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private List<list_items> list_itemsList;

    // data is passed into the constructor
    MyRecyclerViewAdapterCart(Context context, List<list_items> list_itemsList) {
        this.list_itemsList = list_itemsList;
        this.mInflater = LayoutInflater.from(context);
    }



    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_model_cart, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set string for button
        final list_items item = list_itemsList.get(position);

        holder.cart_title.setText(item.getTitle());
        holder.cart_subTitle.setText(item.getSubTitle());
        holder.cart_price.setText(item.getPrice() + " Lei");
        holder.cart_more.setText(item.getMore());

    }

    // total number of cells
    @Override
    public int getItemCount() {
        return list_itemsList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cart_title;
        TextView cart_subTitle;
        TextView cart_more;
        TextView cart_price;
        ImageButton cart_remove;

        ViewHolder(View itemView) {
            super(itemView);

            cart_title = itemView.findViewById(R.id.cart_title);
            cart_subTitle = itemView.findViewById(R.id.cart_subTitle);
            cart_more = itemView.findViewById(R.id.cart_more);
            cart_price = itemView.findViewById(R.id.cart_price);
            cart_remove = itemView.findViewById(R.id.cart_remove);

            cart_remove.setOnClickListener(this);
         //   itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }


    String getTotalPrice() {
        int tempTotalPrice = 0;
        // for each item
        for (int i = 0, size = list_itemsList.size(); i < size; i++) {
            //get each item
            final list_items item = list_itemsList.get(i);
            tempTotalPrice = tempTotalPrice + Integer.valueOf(item.getPrice());
        }

        return String.valueOf(tempTotalPrice);
    }

    // convenience method for getting data at click position
    String getItemTitle(int id) {
        return list_itemsList.get(id).getTitle();
    }

    String getItemSubTitle(int id) {
        return list_itemsList.get(id).getSubTitle();
    }

    String getItemPrice(int id) {
        return list_itemsList.get(id).getPrice();
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
