package g3org3.limatexmm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by meg3o on 3/8/2018.
 */

//public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

  //  private String[] mData = new String[0];
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

  //  private List<String> mDataArray;
    private ArrayList<Integer> mSectionPositions;

    private Context mContext;
    private List<listItems> list_itemsList;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<listItems> list_itemsList) {
        this.list_itemsList = list_itemsList;
        this.mInflater = LayoutInflater.from(context);
        mContext =  context;
    }


    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_model, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // set string for button
        final listItems item = list_itemsList.get(position);

        Picasso.with(mContext).load(item.getImgURL())
                .fit()
                .centerCrop()
                .into(holder.imageView);

        holder.myTextView.setText(item.getItemTitle() + " " + item.getItemSubtitle());
        holder.myTextView3.setText(item.getItemPrice() + " Lei");

    }

    // total number of cells
    @Override
    public int getItemCount() {
        return list_itemsList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        TextView myTextView3;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.title);
            myTextView3 = itemView.findViewById(R.id.price);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItemTitle(int id) {
        return list_itemsList.get(id).getItemTitle();
    }

    String getItemSubTitle(int id) {
        return list_itemsList.get(id).getItemSubtitle();
    }

    double getItemPrice(int id) {
        return list_itemsList.get(id).getItemPrice();
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
