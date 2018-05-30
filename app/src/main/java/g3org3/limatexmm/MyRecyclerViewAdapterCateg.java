package g3org3.limatexmm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by meg3o on 3/8/2018.
 */

//public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
public class MyRecyclerViewAdapterCateg extends RecyclerView.Adapter<MyRecyclerViewAdapterCateg.ViewHolder> {

    private String[] mData = new String[0];
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;
    private RecyclerView recyclerView;

    private static final int UNSELECTED = -1;
    private int selectedItem = UNSELECTED;

    // data is passed into the constructor
    MyRecyclerViewAdapterCateg(Context context, String[] data,RecyclerView recyclerView) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        mContext = context;
        this.recyclerView = recyclerView;
    }

    // inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_model_categ, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind();
        String animal = mData[position];
        holder.categ.setText(animal);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.length;
    }



    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Button categ;

        ViewHolder(View itemView) {
            super(itemView);
            categ = itemView.findViewById(R.id.title);
            categ.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind() {
            int position = getAdapterPosition();
            boolean isSelected = position == selectedItem;
          //  expandButton.setSelected(isSelected);
        //    expandableLayout.setExpanded(isSelected, false);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

            MyRecyclerViewAdapterCateg.ViewHolder holder = (MyRecyclerViewAdapterCateg.ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedItem);
            if (holder != null) {
             //   holder.expandButton.setSelected(false);
            //    holder.expandableLayout.collapse();

                holder.categ.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button));


            }
            int position = getAdapterPosition();
            if (position == selectedItem) {
                selectedItem = UNSELECTED;


            } else {
             //   expandButton.setSelected(true);
             //   expandableLayout.expand();

                categ.setBackground(mContext.getResources().getDrawable(R.drawable.ripple_button_darker));

                selectedItem = position;
            }

        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData[id];
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