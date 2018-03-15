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
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class casa extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    MyRecyclerViewAdapter adapter;
    in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView recyclerView;

    MyRecyclerViewAdapterCateg adapter2;
    RecyclerView recyclerView2;

    MyRecyclerViewAdapterCart adapter3;
    RecyclerView recyclerView3;

    Dialog myDialog;

    TextView deleteAll;
    Button nextButton;
    TextView totalPrice;

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
            String com_name = Lines.get(i);
            int com_price = Integer.valueOf(Lines.get(i + 1));

            // add item to category list
            commentList temp = new commentList(com_name, com_price);

            //add to list for prices/colors
            commentListList.add(temp);

            //separated list for autocomplet
            commentNameList.add(temp.getComName());

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
    Button done;
    MultiAutoCompleteTextView comment;

    String current_item_title;
    String current_item_subTitle;
    String current_item_price;
    String current_item_more;
    int current_item_count;


    List<commentList> commentListList = new ArrayList<>();
    List<String> commentNameList = new ArrayList<>();


    public void comment_color_text() {

        //save comment text for later
        String comment_all = comment.getText().toString().toLowerCase();

        //split text for every space " "
        String[] comment_split = comment_all.split(" ");

        //save the user here words for later
        List<String> used_words = new ArrayList<>();

        //declaring the new string that will replace the old one
        SpannableStringBuilder temp_new = new SpannableStringBuilder();

        //declare the phase which will be analized
        StringBuilder temp_phase = new StringBuilder();
        ;

        //for each word in the text
        for (int i = 0; i < comment_split.length; i++) {

            //reset the analizing phase
            temp_phase.delete(0, temp_phase.length());


            //here the for is returned after the first word + i*words was found
            phase:
            //build phase using the following 4 words
            for (int i2 = 0; i2 < 5; i2++) {

                //get the word id
                int word_id = i + i2;

                //check for used words, jump to the last used word + 1
                if (used_words.contains(String.valueOf(word_id))) {
                    //get back to each word
                    break phase;
                }

                //check if the word id exists
                if (word_id < comment_split.length) {

                    //add word to analized phase
                    temp_phase.append(comment_split[word_id]);

                    //filtrer phase for commas
                    String text_to_compare = temp_phase.toString().replaceAll(",", "");
                    Integer text_to_compare_w_count = text_to_compare.split(" ").length;

                    if (text_to_compare_w_count > 1) {


                        //check in the comment phase list for the current analized phase
                        for (int i3 = 0; i3 < commentListList.size(); i3++) {

                            //if the phase exists
                            if (commentListList.get(i3).getComName().equals(text_to_compare)) {

                                for (int pw = 0; pw < text_to_compare_w_count; pw++) {
                                    used_words.add(String.valueOf(word_id - pw));
                                }


                                //get the color of the phase by checking the price
                                Integer temp_price = commentListList.get(i3).getComPrice();
                                if (temp_price.equals(0)) {
                                    //append the text in the correct color
                                    temp_new.append(commentListList.get(i3).getComName(), new BackgroundColorSpan(Color.rgb(255, 170, 170)), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                } else {
                                    temp_new.append(commentListList.get(i3).getComName(), new BackgroundColorSpan(Color.rgb(140, 200, 240)), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                }
                                temp_new.append(", ");


                                //get back to each word
                                break phase;
                            }

                        }
                    }

                    //if phase not detected, add space between the next word
                    temp_phase.append(" ");

                }
            }
        }

        //for each word
        for (int ia = 0; ia < comment_split.length; ia++) {
            boolean used = false;
            //for each used word
            for (int ib = 0; ib < used_words.size(); ib++) {
                if (used_words.get(ib).equals(String.valueOf(ia))) {
                    //mark word as used;
                    used = true;
                }
            }
            //if word is not used, write it!
            if (!used) {
                temp_new.append(comment_split[ia]);
            }
        }


        comment.setText(temp_new);
        comment.setSelection(comment.length());

        //get the focus back
        //  comment.requestFocus();

    }

    private Handler mHandler;


    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mStatusChecker);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                comment_color_text(); //this function can change value of mInterval.
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, 2500);
            }
        }
    };


    public void ShowPopup(String item_title, String item_subTitle, String item_price) {

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


        ArrayAdapter<String> adapterText = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, commentNameList);
        comment.setAdapter(adapterText);
        comment.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        comment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    comment.showDropDown();
                }
            }
        });


        mHandler = new Handler();
        mStatusChecker.run();


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

        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //remove text color timer
                mHandler.removeCallbacks(mStatusChecker);
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

            //remove text color timer
            mHandler.removeCallbacks(mStatusChecker);
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
        list_items temp = new list_items(current_item_title, current_item_subTitle, current_item_price, current_item_more);
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
            String item_price = Lines.get(i + 2);

            // add item to category list
            list_items temp = new list_items(item_title, item_subTitle, item_price, "");
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
