package com.example.grandmasicecream;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.grandmasicecream.models.CartItem;
import com.example.grandmasicecream.models.Extra;
import com.example.grandmasicecream.models.IceCream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class IceCreamsAtivity extends AppCompatActivity {

    RecyclerView iceCreamsRecyclerView;
    TextView cartPcsText;

    private List<IceCream> iceCreams;
    static List<Extra> extras;
    static List<CartItem> cartItems;
    static List<Long> requiredExtraFirstItemIds;


    IceCreamItemAdapter iceCreamItemAdapter;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ice_creams);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        iceCreams = JSONLoader.loadIceCreamsFromJson(this);
        extras = JSONLoader.loadExtrasFromJson(this);
        cartItems = new ArrayList<>();
        requiredExtraFirstItemIds = new ArrayList<>();

        setRequiredExtras();

        iceCreamsRecyclerView = findViewById(R.id.iceCreamsRecyclerView);

        iceCreamItemAdapter = new IceCreamItemAdapter(this, iceCreams);
        iceCreamsRecyclerView.setAdapter(iceCreamItemAdapter);
        iceCreamsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        iceCreamItemAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        final MenuItem cartMenuItem = menu.findItem(R.id.cartMenu);
        View cartActionView = cartMenuItem.getActionView();

        cartPcsText = cartActionView.findViewById(R.id.cartPcsText);

        cartActionView.setOnClickListener(view -> {
            onOptionsItemSelected(cartMenuItem);
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.cartMenu) {
            Intent cartIntent = new Intent(this, CartActivity.class);
            startActivity(cartIntent);
        }
        return true;
    }

    public static JSONArray getCartItemsInJsonArray() throws JSONException {
        JSONArray cartItemsJson = new JSONArray();
        for (CartItem cartItem : cartItems) {
            JSONObject cartItemJson = new JSONObject();
            cartItemJson.put("id", cartItem.getIceCream().getId());
            cartItemJson.put("extra", cartItem.getExtraItemIds());

            cartItemsJson.put(cartItemJson);
        }
        return cartItemsJson;
    }

    @Override
    protected void onResume() {
        if (cartPcsText != null){
            setCartPcs();
        }
        super.onResume();
    }

    protected void setCartPcs(){
        if (cartItems.size() == 0){
            cartPcsText.setVisibility(View.INVISIBLE);
        } else {
            cartPcsText.setVisibility(View.VISIBLE);
            cartPcsText.setText(String.valueOf(cartItems.size()));
        }

    }

    private void setRequiredExtras() {
        for (Extra extra : extras){
            if (extra.getRequired()){
                requiredExtraFirstItemIds.add(extra.getItems().get(0).getId());
            }
        }
    }
}