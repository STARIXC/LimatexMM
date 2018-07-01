package g3org3.limatexmm;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TabLayout;
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
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class casa extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener, MyRecyclerViewAdapterSearch.ItemClickListener {

    public static final String CART_VALUE_EX = "";
    public static boolean justFinished = false;

    MyRecyclerViewAdapter rv_items_adapter;
    RecyclerView rv_items;
    MyRecyclerViewAdapterCart rv_cart_adapter;
    RecyclerView rv_cart;
    Dialog myDialog;
    MaterialButton deleteAll;
    MaterialButton nextButton;
    MaterialButton search;
    TextView totalPrice;
    List<SpannableString> commentListList = new ArrayList<>();
    Button clock;
    TabLayout tabMenu;
    ConstraintLayout rv_items_c;
    ConstraintLayout rv_items_c2;
    SearchView search_bar;
    RecyclerView search_result;
    MyRecyclerViewAdapterSearch search_result_adapter;
    TextView search_hint;

    SharedPreferences sharedPref;
    TextView profileName;
    android.support.v7.widget.Toolbar appBar;

    //dialog items START
    ImageButton add;
    TextView count;
    ImageButton remove;
    TextView commentValue;
    Button done;
    Button dont;
    MultiAutoCompleteTextView comment;
    //dialog items END
/*
    String current_item_title;
    String current_item_subTitle;
    Double current_item_price;
    String current_item_more;
    int current_item_imgURL;
    int current_item_count;
    FirebaseFirestore db;
    Double comment_value;
*/

    MyArrayAdapter commentAdapter;

    // global cart list
    ArrayList<listItems> list_itemsList_cart = new ArrayList<>();
    String[] data = {"Pizza", "Sosuri", "Bauturi", "Grill", "Altele", "Meniuri", "Burgers", "Salate", "Focaccia"};
    Integer current_list_dialog;
    ArrayList<listItems> list_itemsList;

    //when resuming from finish order
    @Override
    protected void onResume() {
        super.onResume();
        //if the order was finished
        if (justFinished) {
            //clean the cart list
            deleteAll.performClick();
            justFinished = false;
            customToast("Comanda", "finalizata !", true);

            deleteAll.performLongClick();
        }


        //load preferences
        loadPreferences();


    }


    public void loadPreferences() {
        //load Preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //Search On Preference
        if (sharedPref.getBoolean("search_on", false)) { //if search_on was TRUE
            if (rv_items_c2.getVisibility() != View.VISIBLE) { //if search_view is NOT visible
                swapView(false, false);             //silent swap search view
            }
        } else {                                              //if search_on was FALSE
            if (rv_items_c.getVisibility() != View.VISIBLE) { //if search_view is visible
                swapView(false, false);            //silent swap list view
            }
        }
    }

    public void swapView(boolean anim, boolean save) {
        if (rv_items_c2.getVisibility() != View.VISIBLE) {    //determine which view to switch
            if (anim) {
                anim(rv_items_c, rv_items_c2, 500);      //Animate the switch if necessary
            } else {
                rv_items_c.setVisibility(View.GONE);
                rv_items_c2.setVisibility(View.VISIBLE);
            }
            search.setText("Vezi lista");                     //change the button text acordingly
            search.setIcon(getResources().getDrawable(R.drawable.ic_menu_black_24dp));
            //save the preference
            if (save) sharedPref.edit().putBoolean("search_on", true).apply();
        } else {
            if (anim) {
                anim(rv_items_c2, rv_items_c, 500);
            } else {
                rv_items_c.setVisibility(View.VISIBLE);
                rv_items_c2.setVisibility(View.GONE);
            }
            search.setText("Cauta");
            search.setIcon(getResources().getDrawable(R.drawable.ic_search_black_24dp));
            //save the preference
            if (save) sharedPref.edit().putBoolean("search_on", false).apply();
        }
    }

    //when entering activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_casa);

        rv_items = findViewById(R.id.rv_items);
        rv_cart = findViewById(R.id.rv_cart);
        totalPrice = findViewById(R.id.totalPrice);
        nextButton = findViewById(R.id.nextButton);
        deleteAll = findViewById(R.id.deleteAll);
        profileName = findViewById(R.id.profileName);
        appBar = findViewById(R.id.appBar);
        tabMenu = findViewById(R.id.tabMenu);
        search = findViewById(R.id.search);
        rv_items_c = findViewById(R.id.rv_items_c);
        rv_items_c2 = findViewById(R.id.rv_items_c2);
        search_bar = findViewById(R.id.search_bar);
        search_result = findViewById(R.id.search_result);
        search_hint = findViewById(R.id.search_hint);

        //search_bar click listener
        search_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: open keyboard/focus search bar here
            }
        });

        //fill search result
        LoadList("", true);
        search_result.setLayoutManager(new LinearLayoutManager(this));
        search_result_adapter = new MyRecyclerViewAdapterSearch(this, list_itemsList);
        search_result.setAdapter(search_result_adapter);

        search_bar.setIconified(false);

        search_bar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search_result_adapter.getFilter().filter(s);
                return true;
            }
        });


        search_result_adapter.setClickListener(this);

        //load action bar
        this.setSupportActionBar(appBar);

        //load preferences
        loadPreferences();

        //Force screen Landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        deleteAll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                list_itemsList_cart.clear();
                rv_cart_adapter.notifyDataSetChanged();
                return true;
            }
        });

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //DEFAULT view
        rv_items_c.setVisibility(View.VISIBLE);
        rv_items_c2.setVisibility(View.GONE);

        //register search toggler button
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapView(true, true);
            }
        });

        //Register Finish order button
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TotalValue = rv_cart_adapter.getTotalPrice();

                if (Double.valueOf(TotalValue) > 1.0) {
                    Intent myIntent = new Intent(casa.this, finish.class);

                    myIntent.putExtra(CART_VALUE_EX, TotalValue);

                    myIntent.putExtra("final_cart_list", list_itemsList_cart);

                    startActivity(myIntent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    customToast("Cosul", "este gol", false);
                }

            }
        });

        //Register cart list
        loadCart();

        //Load Categories
        LoadCategories();

        //Load commends intro temp list
        loadCommends();

    }


    public void anim(final View oldScreen, final View newScreen, final int dur) {

        newScreen.setElevation(1);
        oldScreen.setElevation(0);

        newScreen.setVisibility(View.VISIBLE);


        int x = search.getLeft() + (search.getWidth() / 2);
        int y = search.getTop() + (search.getHeight() / 2);

        //int endRadius = (int) Math.hypot(oldScreen.getWidth(), oldScreen.getHeight());
        float endRadius = (float) Math.hypot(x, y);
        int startRadius = 1;


        final Animator anim = ViewAnimationUtils.createCircularReveal(newScreen, x, y, startRadius, endRadius);

        //disable touch interface
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                //enable touch interface
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                oldScreen.setVisibility(View.GONE);
            }
        });
        anim.setDuration(dur);
        anim.start();

    }


    public void loadCart() {
        rv_cart.setLayoutManager(new LinearLayoutManager(this));
        rv_cart_adapter = new MyRecyclerViewAdapterCart(this, list_itemsList_cart, rv_cart);
        rv_cart.setAdapter(rv_cart_adapter);
        rv_cart_adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                cartUpdate();
            }
        });
        rv_cart_adapter.notifyDataSetChanged();
    }


    public void loadCommends() {
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
        ObjectAnimator colorFade = ObjectAnimator.ofObject(rv_cart, "backgroundColor", new ArgbEvaluator(), 0x80ef3b59, 0xffffffff);
        colorFade.setDuration(500);
        colorFade.start();
    }


    public void cartUpdate() {
        if (list_itemsList_cart.size() > 0) {
            totalPrice.setText(rv_cart_adapter.getTotalPrice() + " lei");
            nextButton.setEnabled(true);
        } else {
            totalPrice.setText("Cosul e gol!");
            nextButton.setEnabled(false);
        }
        cartFeedback();
    }

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


    listItems modifiedCurItem;

    public void ShowPopup(listItems curItem) {

        modifiedCurItem = curItem;

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

        modifiedCurItem.setItemMorevalue(0.0);
        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                modifiedCurItem.setItemMorevalue(getPriceCom(editable.toString()));
                String final_price = String.valueOf(modifiedCurItem.getItemMorevalue()) + " lei";
                commentValue.setText(final_price);
            }
        });


        myDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //commentAdapter.clear();
                //comment.setAdapter(null);
                //modifiedCurItem.setItemQuantity(0);
                //comment.setText("");
                //comment.clearFocus();
                dialogInterface.dismiss();

            }
        });


        myDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                modifiedCurItem.setItemQuantity(1);
                updateDialogCount();

            }
        });


        done.setOnClickListener(doneClickHandler);
        add.setOnClickListener(addClickHandler);
        remove.setOnClickListener(removeClickHandler);


        myDialog.show();

    }


    View.OnClickListener doneClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            myDialog.dismiss();
            modifiedCurItem.setItemMore(comment.getText().toString());
            AddToCart(modifiedCurItem);
        }
    };

    View.OnClickListener addClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            modifiedCurItem.setItemQuantity(modifiedCurItem.getItemQuantity() + 1);
            updateDialogCount();
        }
    };

    View.OnClickListener removeClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (modifiedCurItem.getItemQuantity() > 1) {
                modifiedCurItem.setItemQuantity(modifiedCurItem.getItemQuantity() - 1);
                updateDialogCount();
            }
        }
    };

    public void updateDialogCount() {
        count.setText(String.valueOf(modifiedCurItem.getItemQuantity()));
    }


    // ADD CURRENT ITEM (FROM TEMP) to cart list
    public void AddToCart(listItems curItem) {

        //modify quantity if the item already exists, with the same paramaters
        boolean found = false;
        for (int i = 0; i < list_itemsList_cart.size(); i++) {
            if (list_itemsList_cart.get(i).getItemTitle().equals(curItem.getItemTitle())) {
                if (list_itemsList_cart.get(i).getItemSubtitle().equals(curItem.getItemSubtitle())) {
                    if (list_itemsList_cart.get(i).getItemMore().equals(curItem.getItemMore())) {
                        Integer newQuantity = list_itemsList_cart.get(i).getItemQuantity() + modifiedCurItem.getItemQuantity();
                        list_itemsList_cart.get(i).setItemQuantity(newQuantity); //modify the item
                        found = true;
                    }
                }
            }
        }
        if (!found) {
            list_itemsList_cart.add(curItem); //add new item
        }

        //update cart
        rv_cart_adapter.notifyDataSetChanged();

    }


    public void customToast(String messageTitle, String message, Boolean longer) {
        //show custom TOAST
        LayoutInflater inflater = getLayoutInflater();
        View layout; // = inflater.inflate(R.layout.popup, null);
        layout = inflater.inflate(R.layout.popup, null);

        TextView text = layout.findViewById(R.id.messageTitle);
        TextView text2 = layout.findViewById(R.id.message);
        text.setText(messageTitle);
        text2.setText(message);

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


    public void LoadCategories() {

        //clear all tab layout tabs
        tabMenu.removeAllTabs();

        //add all the necesare tabs
        for (int i = 0; i < data.length; i++) {
            tabMenu.addTab(tabMenu.newTab().setText(data[i]));
        }

        //add listener for every tab
        tabMenu.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ViewList((String) tab.getText());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //open the first menu
        tabMenu.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                tabMenu.getTabAt(1).select();
                tabMenu.getTabAt(0).select();
                tabMenu.removeOnLayoutChangeListener(this);
            }
        });


    }


    public void LoadList(String list_name, boolean all) {

        //Create list which contains the items(categories) to be loaded from
        ArrayList<String> toLoad = new ArrayList<String>();
        if (!all) {
            toLoad.add(list_name);
        } else {
            for (int i = 0; i < data.length; i++) { //
                toLoad.add(data[i]);
                Log.e("Debuging", "TOLOAD: Added: " + data[i]);
            }
        }

        //Initialize the list which stores all the items loaded
        list_itemsList = new ArrayList<>();

        for (int i = 0; i < toLoad.size(); i++) {

            int id = this.getResources().getIdentifier(toLoad.get(i), "array", this.getPackageName());
            List<String> Lines = Arrays.asList(getResources().getStringArray(id));

            //read the first line which contains 'dialog_need' boolean
            current_list_dialog = Integer.valueOf(Lines.get(0));


            for (int i2 = 1; i2 < Lines.size() - 1; i2++) {
                //extract the strings
                String item_title = Lines.get(i2);
                String item_subTitle = Lines.get(i2 + 1);
                Double item_price = Double.valueOf(Lines.get(i2 + 2));
                String item_photo_name = Lines.get(i2 + 3);

                //extract the image from resources
                int idd = this.getResources().getIdentifier(item_photo_name, "drawable", this.getPackageName());

                // add item to category list
                listItems temp = new listItems(item_title, item_subTitle, item_price, 0, "", 0.0, current_list_dialog, idd);
                list_itemsList.add(temp);

                i2 = i2 + 3;
            }

        }
    }


    public void ViewList(String list_name) {

        //load items to temp list (list_itemsList)
        LoadList(list_name, false);

        //sort items
        Collections.sort(list_itemsList, new NameComp());

        //finish RecyclerView
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) ((dpWidth * 0.65) / 120);
        rv_items.setLayoutManager(new GridLayoutManager(this, noOfColumns));
        rv_items_adapter = new MyRecyclerViewAdapter(this, list_itemsList);
        rv_items_adapter.setClickListener(this);
        rv_items.setAdapter(rv_items_adapter);


    }


    //////// CLICK LISTENER FOR RECYCLER VIEW ITEMS
    @Override
    public void onItemClick(View view, int position) {
        AddItem(rv_items_adapter.getItem(position));
    }

    @Override
    public void onItemClick2(View view, int position) {
        AddItem(search_result_adapter.getItem(position));
    }

    public void AddItem(listItems curItem) {
        if (curItem.getItemPrepare() == 0) { //if the item doesn't need dialog
            curItem.setItemQuantity(1);
            modifiedCurItem = curItem;
            AddToCart(modifiedCurItem);
        } else {
            ShowPopup(curItem);              //or open dialog if needed
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
