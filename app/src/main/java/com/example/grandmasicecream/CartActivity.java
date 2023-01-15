package com.example.grandmasicecream;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grandmasicecream.models.CartItem;
import com.example.grandmasicecream.models.Extra;
import com.example.grandmasicecream.models.Item;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CartActivity extends AppCompatActivity {

    LinearLayout cartLayout;

    LinearLayout shownExtrasLayout;
    LinearLayout shownExtrasCartItemLayout;

    LinearLayout hiddenExtrasListLayout;
    CartItem hiddenExtrasListCartItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartLayout = findViewById(R.id.cartLayout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showCart();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    public void submit(View view) throws JSONException {
        if (IceCreamsAtivity.cartItems.size() != 0) {
            Thread thread = new Thread() {

                @Override
                public void run() {
                    try {
                        JSONArray cartJsonArray = IceCreamsAtivity.getCartItemsInJsonArray();
                        System.out.println(cartJsonArray.toString());

                        URL url = new URL("http://httpbin.org/post");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestProperty("Content-Type", "application/json");
                        connection.setRequestProperty("Accept", "application/json");
                        connection.setRequestMethod("POST");
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        connection.connect();

                        DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                        os.writeBytes(cartJsonArray.toString());

                        os.flush();
                        os.close();

                        if (Objects.equals(connection.getResponseCode(), 200)) {
                            runOnUiThread(() -> Toast.makeText(CartActivity.this, "Sikeres rendelés!", Toast.LENGTH_SHORT).show());
                            IceCreamsAtivity.cartItems = new ArrayList<>();
                            finish();
                        } else {
                            runOnUiThread(() -> Toast.makeText(CartActivity.this, "A rendelés nem sikerült!", Toast.LENGTH_SHORT).show());
                        }
                        connection.disconnect();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
        } else {
            runOnUiThread(() -> Toast.makeText(CartActivity.this, "Üres a kosara!", Toast.LENGTH_SHORT).show());
        }
    }

    private void showCart() {
        for (CartItem cartItem : IceCreamsAtivity.cartItems) {
            LinearLayout cartItemLayoutWithRemove = new LinearLayout(this);
            cartItemLayoutWithRemove.setOrientation(LinearLayout.HORIZONTAL);

            ImageButton removeButton = new ImageButton(this);
            removeButton.setImageResource(R.drawable.ic_delete);
            cartItemLayoutWithRemove.addView(removeButton);

            LinearLayout cartItemLayout = new LinearLayout(this);
            cartItemLayout.setOrientation(LinearLayout.VERTICAL);
            cartItemLayout.setBackgroundColor(getResources().getColor(R.color.red));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,0,0,10);
            cartItemLayout.setLayoutParams(layoutParams);
            cartItemLayout.setPadding(10,10,10,10);

            TextView nameTextView = new TextView(this);
            nameTextView.setText(cartItem.getIceCream().getName());
            nameTextView.setTextSize(20);

            cartItemLayout.addView(nameTextView);

            LinearLayout extrasListLayout = showExtrasList(cartItem);
            cartItemLayout.addView(extrasListLayout);

            cartItemLayout.setOnClickListener(cartItemLayoutOnClikListener(cartItemLayout, extrasListLayout, cartItem));
            cartItemLayoutWithRemove.addView(cartItemLayout);
            cartLayout.addView(cartItemLayoutWithRemove);

            removeButton.setOnClickListener(removeButtonOnClickListener(cartItemLayout, cartItem, cartItemLayoutWithRemove));
        }
    }

    private View.OnClickListener removeButtonOnClickListener(LinearLayout cartItemLayout, CartItem cartItem, View cartItemLayoutWithRemove) {
        return view -> {
            if ( shownExtrasCartItemLayout == cartItemLayout ) {
                shownExtrasCartItemLayout.removeView(shownExtrasLayout);
                shownExtrasLayout = null;
                shownExtrasCartItemLayout = null;

                LinearLayout newExtrasListLayout = showExtrasList(hiddenExtrasListCartItem);
                hiddenExtrasListLayout.addView(newExtrasListLayout);
                hiddenExtrasListLayout = null;
                hiddenExtrasListCartItem = null;
            }

            IceCreamsAtivity.cartItems.remove(cartItem);
            cartLayout.removeView(cartItemLayoutWithRemove);
        };
    }

    private View.OnClickListener cartItemLayoutOnClikListener(LinearLayout cartItemLayout, LinearLayout extrasListLayout, CartItem cartItem) {
        return view -> {
            if ( shownExtrasCartItemLayout == cartItemLayout ) {
                shownExtrasCartItemLayout.removeView(shownExtrasLayout);
                shownExtrasLayout = null;
                shownExtrasCartItemLayout = null;

                LinearLayout newExtrasListLayout = showExtrasList(hiddenExtrasListCartItem);
                hiddenExtrasListLayout.addView(newExtrasListLayout);
                hiddenExtrasListLayout = null;
                hiddenExtrasListCartItem = null;
            } else {
                if (shownExtrasLayout != null) {
                    shownExtrasCartItemLayout.removeView(shownExtrasLayout);

                    LinearLayout newExtrasListLayout = showExtrasList(hiddenExtrasListCartItem);
                    hiddenExtrasListLayout.addView(newExtrasListLayout);
                    hiddenExtrasListLayout = null;
                    hiddenExtrasListCartItem = null;
                }

                extrasListLayout.removeAllViews();
                hiddenExtrasListLayout = extrasListLayout;
                hiddenExtrasListCartItem = cartItem;

                LinearLayout extrasView = showExtras(cartItem);
                cartItemLayout.addView(extrasView);

                shownExtrasLayout = extrasView;
                shownExtrasCartItemLayout = cartItemLayout;
            }
        };
    }

    @SuppressLint("SetTextI18n")
    private LinearLayout showExtras(CartItem cartItem) {
        LinearLayout extrasLayout = new LinearLayout(this);
        extrasLayout.setOrientation(LinearLayout.VERTICAL);

        List<Extra> extras = IceCreamsAtivity.extras;

        for ( Extra extra : extras) {
            LinearLayout extraLayout = new LinearLayout(this);
            extraLayout.setOrientation(LinearLayout.VERTICAL);
            extraLayout.setBackgroundColor(getResources().getColor(R.color.red));

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,20,0,0);
            extraLayout.setLayoutParams(layoutParams);


            TextView typeText = new TextView(this);
            typeText.setTextColor(getResources().getColor(R.color.white));
            typeText.setPadding(20,20,10,10);
            extraLayout.addView(typeText);

            if (extra.getRequired()) {
                typeText.setText(extra.getType()+" *");
                extraLayout.addView(showRequiredExtras(extra, cartItem));
            } else {
                typeText.setText(extra.getType());

                for (Item item : extra.getItems()) {
                    extraLayout.addView(showOptionalExtra(item, cartItem));
                }
            }

            extrasLayout.addView(extraLayout);
        }
        return extrasLayout;
    }

    @SuppressLint("SetTextI18n")
    private View showOptionalExtra(Item item, CartItem cartItem) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setTextColor(getResources().getColor(R.color.white));
        checkBox.setText(item.getPrice()+"€ "+item.getName());

        if (IceCreamsAtivity.cartItems.get(IceCreamsAtivity.cartItems.indexOf(cartItem)).getExtraItemIds().contains(item.getId())) {
            checkBox.setChecked(true);
        }

        checkBox.setOnCheckedChangeListener((view, isChecked) -> {
            IceCreamsAtivity.cartItems.get(IceCreamsAtivity.cartItems.indexOf(cartItem)).addOrRemoveExtraId(item.getId(), isChecked);
        });
        return checkBox;
    }

    @SuppressLint("SetTextI18n")
    private View showRequiredExtras(Extra extra, CartItem cartItem) {


        RadioGroup radioGroup = new RadioGroup(this);
        for (Item item : extra.getItems()){
            RadioButton radioButton = new RadioButton(this);
            radioButton.setTextColor(getResources().getColor(R.color.white));
            radioButton.setText(item.getPrice()+"€ "+item.getName());
            radioButton.setTag(item.getId());
            radioGroup.addView(radioButton);

            if (IceCreamsAtivity.cartItems.get(IceCreamsAtivity.cartItems.indexOf(cartItem)).getExtraItemIds().contains(item.getId())) {
                radioGroup.check(radioButton.getId());
            }
        }

        radioGroup.setOnCheckedChangeListener((radioGroupView, selectedId) -> {
            int cartItemId = IceCreamsAtivity.cartItems.indexOf(cartItem);
            Long selectedTag = (Long)findViewById(selectedId).getTag();
            for (int j = 0; j < radioGroupView.getChildCount(); j++) {
                if (radioGroupView.getChildAt(j).getTag() != selectedTag) {
                    IceCreamsAtivity.cartItems.get(cartItemId).removeExtraItemId((Long) radioGroupView.getChildAt(j).getTag());
                } else {
                    IceCreamsAtivity.cartItems.get(cartItemId).addExtraItemIds(selectedTag);
                }
            }
        });
        return radioGroup;
    }

    @SuppressLint("SetTextI18n")
    private LinearLayout showExtrasList(CartItem cartItem) {
        LinearLayout extrasListLayout = new LinearLayout(this);
        extrasListLayout.setOrientation(LinearLayout.VERTICAL);
        extrasListLayout.setPadding(40,0,0,0);
        for (Long itemId : cartItem.getExtraItemIds()) {
            Item item = null;
            for (int i = 0; i < IceCreamsAtivity.extras.size(); i++) {
                for (int j = 0; j < IceCreamsAtivity.extras.get(i).getItems().size(); j++) {
                    if (IceCreamsAtivity.extras.get(i).getItems().get(j).getId() == itemId){
                        item = IceCreamsAtivity.extras.get(i).getItems().get(j);
                    }
                }
            }

            if (item != null) {
                TextView itemNameText = new TextView(this);
                itemNameText.setText("- " + item.getName());
                extrasListLayout.addView(itemNameText);
            }
        }
        return extrasListLayout;
    }

}