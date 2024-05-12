package com.example.webshopgyakorlas;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.MenuItemCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG = ShopListActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private FirebaseUser user;

    private FrameLayout redCircle;
    private TextView contentTextView;
    private int cartItems = 0;
    private int gridNumber = 1;

    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemData;
    private ShoppingItemAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private NotificationHandler mNotificatiohandler;

    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null){
            Log.d(LOG_TAG, "Authenticated user");
        }else {
            Log.d(LOG_TAG, "Unuthenticated user");
            finish();
        }


        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemData = new ArrayList<>();

        mAdapter = new ShoppingItemAdapter(this, mItemData);

        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        mItems = mFirestore.collection("Items");

        queryData();

        mNotificatiohandler = new NotificationHandler(this);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        setAlarmManager();

    }
    private void intializeData(){
        String[] itemsList = getResources()
                .getStringArray(R.array.shopping_item_names);
        String[] itemsInfo = getResources()
                .getStringArray(R.array.shopping_item_desc);
        String[] itemsPrice = getResources()
                .getStringArray(R.array.shopping_item_price);
        TypedArray itemsImageResource =
                getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemsRate =
                getResources().obtainTypedArray(R.array.shopping_item_rates);

        /*Create the ArrayList of objects with the titles and
        * information about each*/
        int cartedCount;
        for (int i = 0; i < itemsList.length ; i++)
            mItems.add(new ShoppingItem(
                    itemsList[i],
                    itemsInfo[i],
                    itemsPrice[i],
                    itemsRate.getFloat(i, 0),
                    itemsImageResource.getResourceId(i,0),
                    cartedCount = 0));

    }
    private void queryData(){
        mItemData.clear();

        mItems.orderBy("cartedCount", Query.Direction.DESCENDING).limit(10).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                ShoppingItem item = document.toObject(ShoppingItem.class);
                item.setId(document.getId());
                mItemData.add(item);
            }
            if (mItemData.size() == 0){
                intializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();

        });

    }
    public void deleteItems(ShoppingItem item){
        DocumentReference ref = mItems.document(item._getID());

        ref.delete().addOnSuccessListener(success -> {
           Log.d(LOG_TAG, "Item is succesfully deleted: " + item._getID());
        })
        .addOnFailureListener(failure ->{
            Toast.makeText(this, "item " + item._getID() + "cannot be deleted.", Toast.LENGTH_LONG).show();
        });

        queryData();
        mNotificatiohandler.cancel();
    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG, "Search: " + newText);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.log_out_btn) {
            Log.d(LOG_TAG, "Log out clicked");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (itemId == R.id.settings_btn) {
            Log.d(LOG_TAG, "Settings clicked");
            return true;
        } else if (itemId == R.id.cart_btn) {
            Log.d(LOG_TAG, "Cart clicked");
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean  onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem = menu.findItem(R.id.cart_btn);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    onOptionsItemSelected(alertMenuItem);
                }
        });

        return super.onPrepareOptionsMenu(menu);
    }


    public void updateAlertIcon(ShoppingItem item){
        cartItems = (cartItems + 1);
        if (0 < cartItems) {
            contentTextView.setText(String.valueOf(cartItems));
        }else {
            contentTextView.setText("");
        }

        redCircle.setVisibility((cartItems > 0) ? View.VISIBLE : View.GONE);


        mItems.document(item._getID()).update("cartedCount", item.getCartedCount() + 1)
                //nézd meg mit jelent a lambda kifejezés
                .addOnFailureListener(failure ->{
                    Toast.makeText(this, "item " + item._getID() + "cannot be changed.", Toast.LENGTH_LONG).show();
                });
        mNotificatiohandler.send(item.getName());
        queryData();
        mNotificatiohandler.cancel();
        }

    private void setAlarmManager() {
        long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        long triggertime = SystemClock.elapsedRealtime() + repeatInterval;
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);

    }
    public void updateAlertIcon() {
        cartItems = (cartItems + 1);
        if (0 < cartItems){
            contentTextView.setText(String.valueOf(cartItems));
        }else{
            contentTextView.setText("");
        }
    }
}





