package g3org3.limatexmm;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//TODO:
//REMOVE SEARCH
//FIX TOTAL CART BUTTON
//FIX dismiss DIALOG with random text
//FIX EXTRA PRICE NOT BEING RECALCULATED


public class casa extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView recyclerView;

    MyRecyclerViewAdapterCateg adapter2;
    RecyclerView recyclerView2;

    MyRecyclerViewAdapterCart adapter3;
    RecyclerView recyclerView3;

    Dialog myDialog;

    ImageButton deleteAll;
    Button nextButton;
    TextView totalPrice;
    List<commentList> commentListList = new ArrayList<>();
    List<SpannableString> commentNameList = new ArrayList<>();

    private List<String> lastSearches;
    // Create SharedPreferences and Editor
    SharedPreferences sharedPref;
    SharedPreferences.Editor sharedEditor;
    private List<list_items> list_itemsList;
    //  int numberOfColumns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casa);

        //History
        sharedPref = getSharedPreferences("history", Context.MODE_PRIVATE);
        sharedEditor = sharedPref.edit();


        // set up the RecyclerView
        recyclerView = findViewById(R.id.rv_items);
        recyclerView2 = findViewById(R.id.rv_categories);
        recyclerView3 = findViewById(R.id.rv_cart);

        totalPrice = findViewById(R.id.totalPrice);
        nextButton = findViewById(R.id.nextButton);
        deleteAll = findViewById(R.id.deleteAll);

        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_itemsList_cart.clear();
                cartUpdate();
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(casa.this, finish.class);
                //   myIntent.putExtra("final_cart_list", list_itemsList_cart); //Optional parameters
                startActivity(myIntent);


            }
        });

        //Register cart list
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));
        adapter3 = new MyRecyclerViewAdapterCart(this, list_itemsList_cart);
        adapter3.setClickListener(new MyRecyclerViewAdapterCart.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // REMOVE THE ITEM FROM THE CART
                list_itemsList_cart.remove(position);
                cartUpdate();
                // Toast.makeText(getApplicationContext(), "test", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView3.setAdapter(adapter3);
        cartUpdate();



        //populate + show categories list
        CreateCategories();


        //populate comments list
        int id = this.getResources().getIdentifier("com_list", "array", this.getPackageName());
        List<String> Lines = Arrays.asList(getResources().getStringArray(id));

        for (int i = 0; i < Lines.size(); i++) {
            //extract the strings
            SpannableString com_name = new SpannableString(Lines.get(i));
            Double com_price = Double.valueOf(Lines.get(i + 1));

            if (com_price.equals(0.0)) {
                //append the text in the correct color
                com_name.setSpan(new BackgroundColorSpan(Color.rgb(140, 200, 240)), 0, com_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (com_price.equals(3.0)) {
                com_name.setSpan(new BackgroundColorSpan(Color.rgb(220, 190, 190)), 0, com_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (com_price.equals(4.0)) {
                com_name.setSpan(new BackgroundColorSpan(Color.rgb(255, 160, 160)), 0, com_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }


            // add item to category list
            commentList temp = new commentList(com_name, com_price);

            //add to list for prices/colors
            commentListList.add(temp);

            //separated list for autocomplet
            commentNameList.add(com_name);

            i++;
        }

    }


    public void cartUpdate() {
        totalPrice.setText("Total:" + adapter3.getTotalPrice() + " lei");
        adapter3.notifyDataSetChanged();
    }


    Button add;
    TextView count;
    Button remove;
    TextView commentValue;
    Button done;
    MultiAutoCompleteTextView comment;

    String current_item_title;
    String current_item_subTitle;
    double current_item_price;
    String current_item_more;
    int current_item_count;
    double comment_value;



    MyArrayAdapter commentAdapter;

    public void ShowPopup(String item_title, String item_subTitle, double item_price) {

        current_item_title = item_title;
        current_item_subTitle = item_subTitle;
        current_item_price = item_price;

        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.item_add);
        myDialog.setTitle("More");


        add = myDialog.findViewById(R.id.add);
        count = myDialog.findViewById(R.id.count);
        remove = myDialog.findViewById(R.id.remove);
        comment = myDialog.findViewById(R.id.comment);
        done = myDialog.findViewById(R.id.done);
        commentValue = myDialog.findViewById(R.id.commentValue);



        List<commentList> cloneCommentListList = new ArrayList<>();
       // cloneCommentListList = commentListList;
        cloneCommentListList.addAll(commentListList);

        Toast.makeText(getApplicationContext(), "clona: " + cloneCommentListList.size() + " originalu: " + commentListList.size(), Toast.LENGTH_SHORT).show();

        commentAdapter = new MyArrayAdapter(this, R.layout.simple_dropdown_item, cloneCommentListList);
        comment.setAdapter(commentAdapter);
        comment.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());


        comment_value = 0;

        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                commentValue.setText(String.valueOf(comment_value) + " lei");

            }
        });

        comment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                comment_value = comment_value + commentAdapter.getItem(i).getComPrice();
                commentValue.setText(String.valueOf(comment_value));
            }
        });


        //TODO: comment list
        //- substract price from total price if type
        //- filter nonList items
        //- color item based on type
        //- create list (internaly) ~~~~~~~~~~~~~~~~~~~~~~~~~~
        //- create list (externaly) + machine learning


        myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                current_item_count = 1;
                count.setText(String.valueOf(current_item_count));
            }
        });


        done.setOnClickListener(doneClickHandler);
        add.setOnClickListener(addClickHandler);
        remove.setOnClickListener(removeClickHandler);

        // myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        myDialog.show();


    }


    View.OnClickListener doneClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            myDialog.dismiss();
            current_item_more = comment.getText().toString();
            AddToCart_current();
        }
    };

    View.OnClickListener addClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            current_item_count++;
            count.setText(String.valueOf(current_item_count));
        }
    };

    View.OnClickListener removeClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (current_item_count > 1) {
                current_item_count--;
                count.setText(String.valueOf(current_item_count));
            }
        }
    };

    // global cart list
    ArrayList<list_items> list_itemsList_cart = new ArrayList<>();


    // ADD CURRENT ITEM (FROM TEMP) to cart list
    public void AddToCart_current() {
        list_items temp = new list_items(current_item_title, current_item_subTitle, current_item_price, current_item_more, comment_value);
        for (int i = 0; i < current_item_count; i++) {
            list_itemsList_cart.add(temp);
        }
        cartUpdate();

    }

    public void CreateCategories() {

        // data to populate the RecyclerView with
        String[] data = {"Pizza", "Sosuri", "Bauturi", "Salate", "Sandwich-uri", "Burgers"};

        // set up the RecyclerView
        RecyclerView recyclerView2 = findViewById(R.id.rv_categories);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new MyRecyclerViewAdapterCateg(this, data);
        adapter2.setClickListener(new MyRecyclerViewAdapterCateg.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ViewList(adapter2.getItem(position));
            }
        });
        recyclerView2.setAdapter(adapter2);

    }


    public void ViewList(String list_name) {

        int id = this.getResources().getIdentifier(list_name, "array", this.getPackageName());
        List<String> Lines = Arrays.asList(getResources().getStringArray(id));

        //crate a list for RecyclerView adapter
        ArrayList<list_items> list_itemsList = new ArrayList<>();

        for (int i = 0; i < Lines.size(); i++) {
            //extract the strings
            String item_title = Lines.get(i);
            String item_subTitle = Lines.get(i + 1);
            Double item_price = Double.valueOf(Lines.get(i + 2));

            // add item to category list
            list_items temp = new list_items(item_title, item_subTitle, item_price, "", 0.0);
            list_itemsList.add(temp);

            i = i + 2;
        }

        //finish RecyclerView
        //recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, list_itemsList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }


    //////// CLICK LISTENER FOR RECYCLER VIEW ITEMS
    @Override
    public void onItemClick(View view, int position) {
        ShowPopup(adapter.getItemTitle(position), adapter.getItemSubTitle(position), adapter.getItemPrice(position));
    }


}
