package g3org3.limatexmm;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by meg3o on 3/15/2018.
 */

public class MyArrayAdapter extends ArrayAdapter<SpannableString> {

    private LayoutInflater layoutInflater;
    List<SpannableString> mcommentLists;

    private Filter mFilter = new Filter() {
        @Override
        public SpannableString convertResultToString(Object resultValue) {
            return ((SpannableString) resultValue);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null) {
                ArrayList<SpannableString> suggestions = new ArrayList<SpannableString>();
                for (SpannableString comment : mcommentLists) {
                    // Note: change the "contains" to "startsWith" if you only want starting matches
                    if (comment.toString().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(comment);
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll((ArrayList<SpannableString>) results.values);
            } else {
                // no filter, add entire original list back in
              //  addAll(mcommentLists);
            }
            notifyDataSetChanged();
        }
    };

    public MyArrayAdapter(Context context, int textViewResourceId, List<SpannableString> commentLists) {
        super(context, textViewResourceId, commentLists);
        // copy all the commentLists into a master list
        mcommentLists = new ArrayList<SpannableString>(commentLists.size());
        mcommentLists.addAll(commentLists);
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Integer getSize() {
        return mcommentLists.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.simple_dropdown_item, null);
        }

        SpannableString comment = getItem(position);

        TextView name = view.findViewById(R.id.textDD);
        name.setText(comment);

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}



