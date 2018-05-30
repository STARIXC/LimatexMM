package g3org3.limatexmm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.widget.LinearLayout.HORIZONTAL;

//TODO:
//FILL MENU_ADD DESIGN NOT BEEING SYMETRIC
//FIX EXTRA PRICE NOT BEING RECALCULATED in MENU_ADD
//FIX APP CRASH when exiting MENU_ADD without any comment (dismiss)


public class casa extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {


    public static final String CART_VALUE_EX = "";
    public static boolean justFinished = false;

    MyRecyclerViewAdapter adapter;
    RecyclerView recyclerView;
    MyRecyclerViewAdapterCateg adapter2;
    RecyclerView recyclerView2;
    MyRecyclerViewAdapterCart adapter3;
    RecyclerView recyclerView3;
    Dialog myDialog;
    ImageButton deleteAll;
    Button nextButton;
    TextView totalPrice;
    List<SpannableString> commentListList = new ArrayList<>();

    TextView profileName;
    android.support.v7.widget.Toolbar appBar;

    //when resuming from finish order
    @Override
    protected void onResume() {
        super.onResume();
        //if the order was finished
        if (justFinished) {
            //clean the cart list
            deleteAll.performClick();
            justFinished = false;
            customToast("Comanda finalizata !", true);

            deleteAll.performClick();
        }

    }

    //when entering activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casa);

        recyclerView = findViewById(R.id.rv_items);
        recyclerView2 = findViewById(R.id.rv_categories);
        recyclerView3 = findViewById(R.id.rv_cart);
        totalPrice = findViewById(R.id.totalPrice);
        nextButton = findViewById(R.id.nextButton);
        deleteAll = findViewById(R.id.deleteAll);
        profileName = findViewById(R.id.profileName);
        appBar = findViewById(R.id.appBar);
        this.setSupportActionBar(appBar);



        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Register Delete all cart button
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list_itemsList_cart.clear();
                adapter3.notifyDataSetChanged();
            }
        });


        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        //Register Finish order button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TotalValue = adapter3.getTotalPrice();

                if (Double.valueOf(TotalValue) > 1.0) {
                    Intent myIntent = new Intent(casa.this, finish.class);

                    myIntent.putExtra(CART_VALUE_EX, TotalValue);

                    myIntent.putExtra("final_cart_list", list_itemsList_cart);

                    startActivity(myIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    customToast("Cosul este gol!", false);
                }

            }
        });

        //Register cart list
        recyclerView3.setLayoutManager(new LinearLayoutManager(this));
        adapter3 = new MyRecyclerViewAdapterCart(this, list_itemsList_cart, recyclerView3);
        recyclerView3.setAdapter(adapter3);
        adapter3.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                cartUpdate();
            }
        });
        adapter3.notifyDataSetChanged();


        //populate + show categories list
        CreateCategories();


        //populate comments list
        int id = this.getResources().getIdentifier("com_list", "array", this.getPackageName());
        List<String> Lines = Arrays.asList(getResources().getStringArray(id));

        for (int i = 0; i < Lines.size(); i++) {
            //extract the strings
            SpannableString com_name = new SpannableString(Lines.get(i));
            Double com_price = getPriceCom(com_name.toString());

            if (com_price.equals(0.0)) {
                //append the text in the correct color
                com_name.setSpan(new BackgroundColorSpan(Color.rgb(140, 200, 240)), 0, com_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (com_price.equals(3.0)) {
                com_name.setSpan(new BackgroundColorSpan(Color.rgb(220, 190, 190)), 0, com_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if (com_price.equals(4.0)) {
                com_name.setSpan(new BackgroundColorSpan(Color.rgb(255, 160, 160)), 0, com_name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            //add to list
            commentListList.add(com_name);

        }

    }


    public void cartFeedback() {

        ObjectAnimator colorFade = ObjectAnimator.ofObject(recyclerView3, "backgroundColor", new ArgbEvaluator(), 0x80ef3b59, 0xffffffff);
        colorFade.setDuration(500);
        colorFade.start();

    }


    public void cartUpdate() {
        if (list_itemsList_cart.size() > 0) {
            totalPrice.setText("Total: " + adapter3.getTotalPrice() + " lei");
            nextButton.setEnabled(true);
            nextButton.setBackground(this.getResources().getDrawable(R.drawable.ripple_button_white_dark));
        } else {
            totalPrice.setText("Cosul e gol!");
            nextButton.setEnabled(false);
            nextButton.setBackground(this.getResources().getDrawable(R.drawable.ripple_button_white));
        }
        cartFeedback();
    }


    ImageButton add;
    TextView count;
    ImageButton remove;
    TextView commentValue;
    Button done;
    Button dont;
    MultiAutoCompleteTextView comment;

    String current_item_title;
    String current_item_subTitle;
    Double current_item_price;
    String current_item_more;
    int current_item_count;
    FirebaseFirestore db;
    Double comment_value;

    MyArrayAdapter commentAdapter;

    public Double getPriceCom(String item_name) {
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(item_name);
        Double temp_price = 0.0;
        try {
            while (m.find()) {
                temp_price = temp_price + Double.valueOf(m.group(1));
            }
        } catch (NumberFormatException e) {

        }
        return temp_price;
    }


    public void ShowPopup(String item_title, String item_subTitle, double item_price) {

        current_item_title = item_title;
        current_item_subTitle = item_subTitle;
        current_item_price = item_price;

        myDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        myDialog.setContentView(R.layout.item_add);
        myDialog.setTitle("More");

        add = myDialog.findViewById(R.id.add);
        count = myDialog.findViewById(R.id.count);
        remove = myDialog.findViewById(R.id.remove);
        comment = myDialog.findViewById(R.id.comment);
        done = myDialog.findViewById(R.id.done);
        dont = myDialog.findViewById(R.id.dont);
        commentValue = myDialog.findViewById(R.id.commentValue);

        List<SpannableString> cloneCommentListList = new ArrayList<>();
        cloneCommentListList.addAll(commentListList);

        commentAdapter = new MyArrayAdapter(this, R.layout.simple_dropdown_item, cloneCommentListList);
        comment.setAdapter(commentAdapter);
        comment.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        dont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        comment_value = 0.0;
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                comment_value = getPriceCom(editable.toString());
                String final_price = String.valueOf(comment_value) + " lei";
                commentValue.setText(final_price);
            }
        });


        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                commentAdapter.clear();
                comment.setAdapter(null);
                current_item_count = 0;
                comment.setText("");
                comment.clearFocus();
                dialogInterface.dismiss();

            }
        });


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
    ArrayList<listItems> list_itemsList_cart = new ArrayList<>();


    // ADD CURRENT ITEM (FROM TEMP) to cart list
    public void AddToCart_current() {

        //add items
        listItems temp = new listItems(current_item_title, current_item_subTitle, current_item_price, current_item_count, current_item_more, comment_value, current_list_dialog,0);

        boolean found = false;
        for (int i = 0; i < list_itemsList_cart.size(); i++) {
            if (list_itemsList_cart.get(i).getItemTitle().equals(temp.getItemTitle())) {
                if (list_itemsList_cart.get(i).getItemSubtitle().equals(temp.getItemSubtitle())) {
                    if (list_itemsList_cart.get(i).getItemMore().equals(temp.getItemMore())) {
                        Integer newQuantity = list_itemsList_cart.get(i).getItemQuantity() + current_item_count;
                        list_itemsList_cart.get(i).setItemQuantity(newQuantity);
                        found = true;
                    }
                }
            }
        }
        if (!found) {
            list_itemsList_cart.add(temp);
        }


        //update cart
       adapter3.notifyDataSetChanged();

    }


    public void customToast(String finalt, Boolean longer) {
        //show custom TOAST
        LayoutInflater inflater = getLayoutInflater();
        View layout; // = inflater.inflate(R.layout.item_added, null);
            layout = inflater.inflate(R.layout.item_added, null);

        TextView text = layout.findViewById(R.id.text);
        text.setText(finalt);

        Toast toast = new Toast(getApplicationContext());
      //  toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setGravity(Gravity.FILL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        if (longer) {
            toast.setDuration(Toast.LENGTH_LONG);
        }

        toast.setView(layout);

        toast.show();
    }

boolean oneTimeInit = false;
    public void CreateCategories() {

        // data to populate the RecyclerView with
        String[] data = {"Pizza", "Sosuri", "Bauturi", "Grill", "Altele", "Meniuri", "Burgers", "Salate", "Focaccia"};

        // set up the RecyclerView
        recyclerView2.setLayoutManager(new LinearLayoutManager(this,HORIZONTAL,false));
        adapter2 = new MyRecyclerViewAdapterCateg(this, data,recyclerView2);
        adapter2.setClickListener(new MyRecyclerViewAdapterCateg.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ViewList(adapter2.getItem(position));
            }
        });
        recyclerView2.setAdapter(adapter2);

        recyclerView2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!oneTimeInit) {
                    recyclerView2.findViewHolderForAdapterPosition(0).itemView.performClick();
                    oneTimeInit = true;
                }
            }
        });

    }


    Integer current_list_dialog;

    public void ViewList(String list_name) {

        int id = this.getResources().getIdentifier(list_name, "array", this.getPackageName());
        List<String> Lines = Arrays.asList(getResources().getStringArray(id));

        //crate a list for RecyclerView adapter
        ArrayList<listItems> list_itemsList = new ArrayList<>();

        //read dialog boolean
        current_list_dialog = Integer.valueOf(Lines.get(0));


        for (int i = 1; i < Lines.size() - 1; i++) {
            //extract the strings
            String item_title = Lines.get(i);
            String item_subTitle = Lines.get(i + 1);
            Double item_price = Double.valueOf(Lines.get(i + 2));
            String item_photo_name = Lines.get(i + 3);

            //extract the image from resources
            int idd = this.getResources().getIdentifier(item_photo_name, "drawable", this.getPackageName());

            // add item to category list
            listItems temp = new listItems(item_title, item_subTitle, item_price, 0, "", 0.0, current_list_dialog, idd);
            list_itemsList.add(temp);

            i = i + 3;
        }


        //sort items
        Collections.sort(list_itemsList, new NameComp());


        //finish RecyclerView
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) ((dpWidth*0.65) / 150);
        recyclerView.setLayoutManager(new GridLayoutManager(this,noOfColumns));
        adapter = new MyRecyclerViewAdapter(this, list_itemsList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }


    //////// CLICK LISTENER FOR RECYCLER VIEW ITEMS
    @Override
    public void onItemClick(View view, int position) {
        if (current_list_dialog.equals(1)) {
            ShowPopup(adapter.getItemTitle(position), adapter.getItemSubTitle(position), adapter.getItemPrice(position));
        } else {
            current_item_title = adapter.getItemTitle(position);
            current_item_subTitle = adapter.getItemSubTitle(position);
            current_item_price = adapter.getItemPrice(position);
            current_item_more = "";
            current_item_count = 1;
            //current_list_dialog = 0; is already 0
            comment_value = 0.0;
            AddToCart_current();
        }
    }


}

class NameComp implements Comparator<listItems> {

    public int compare(listItems time1, listItems time2) {

        //split the item title
        String[] words = time1.getItemTitle().split(" ");
        String section = "";
        // if there are 2 words
        if (words.length > 1) {
            // get char from the second word
            section = String.valueOf(words[1].toUpperCase().charAt(0));
        } else {
            section = String.valueOf(words[0].toUpperCase().charAt(0));
        }

        String[] words2 = time2.getItemTitle().split(" ");
        String section2 = "";
        // if there are 2 words
        if (words2.length > 1) {
            // get char from the second word
            section2 = String.valueOf(words2[1].toUpperCase().charAt(0));
        } else {
            section2 = String.valueOf(words2[0].toUpperCase().charAt(0));
        }


        return section.compareTo(section2);
    }
}
