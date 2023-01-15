package com.example.grandmasicecream;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.grandmasicecream.models.CartItem;
import com.example.grandmasicecream.models.IceCream;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IceCreamItemAdapter extends RecyclerView.Adapter<IceCreamItemAdapter.ViewHolder> {

    private Context context;
    private List<IceCream> iceCreams;

    public IceCreamItemAdapter(Context context, List<IceCream> iceCreams) {
        this.context = context;
        this.iceCreams = iceCreams;
    }

    @NonNull
    @Override
    public IceCreamItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IceCreamItemAdapter.ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.ice_cream_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IceCreamItemAdapter.ViewHolder holder, int position) {
        holder.bindTo(iceCreams.get(position));
    }

    @Override
    public int getItemCount() {
        return iceCreams.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView iceCreamName;
        TextView iceCreamPrice;
        ImageView iceCreamImage;
        Button toCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iceCreamName = itemView.findViewById(R.id.iceCreamName);
            iceCreamPrice = itemView.findViewById(R.id.iceCreamPrice);
            iceCreamImage = itemView.findViewById(R.id.iceCreamImage);
            toCartButton = itemView.findViewById(R.id.toCart);
        }

        @SuppressLint("SetTextI18n")
        private void bindTo(IceCream iceCream){
            iceCreamName.setText(iceCream.getName());

            if (iceCream.getStatus() == IceCream.Status.MELTED) {
                iceCreamPrice.setText("Kifogyott");
                toCartButton.setEnabled(false);
                toCartButton.setTextColor(itemView.getResources().getColor(R.color.grey));
            } else if (iceCream.getStatus() == IceCream.Status.UNAVAILABLE){
                iceCreamPrice.setText("Nem is volt");
                toCartButton.setEnabled(false);
                toCartButton.setTextColor(itemView.getResources().getColor(R.color.grey));
            }

            Thread loadImageThread = new Thread() {
                @Override
                public void run() {
                    if (iceCream.getImageUrl() != null){
                        try {
                            URL newurl = new URL(iceCream.getImageUrl());
                            Bitmap mIcon_val = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
                            iceCreamImage.setImageBitmap(mIcon_val);
                        } catch (Exception e){
                            System.out.println("ERROR: " + e);
                        }
                    }
                }

            };
            loadImageThread.start();

            toCartButton.setOnClickListener(view -> {
                IceCreamsAtivity.cartItems.add(new CartItem(iceCream, new ArrayList<>(IceCreamsAtivity.requiredExtraFirstItemIds)));
                ((IceCreamsAtivity)context).setCartPcs();
            });
        }
    }
}

