package com.twb.pokergame.ui.crypto;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.twb.pokergame.R;
import com.twb.pokergame.data.model.Cryptocurrency;

import java.util.List;

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.ViewHolder> {
    private final List<Cryptocurrency> dataset;

    public CryptoAdapter(List<Cryptocurrency> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public CryptoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CryptoAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.crypto_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(CryptoAdapter.ViewHolder holder, int position) {
        holder.bind(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public long getItemId(int position) {
        return dataset.get(position).hashCode();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView cryptoTextView;
        private final ImageView cryptoImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            cryptoTextView = itemView.findViewById(R.id.cryptoTextView);
            cryptoImageView = itemView.findViewById(R.id.cryptoImageView);
        }

        public void bind(Cryptocurrency cryptocurrency) {
            Glide.with(itemView.getContext())
                    .load(cryptocurrency.getImage()).dontAnimate()
                    .into(cryptoImageView);
            cryptoTextView.setText(cryptocurrency.getName());
        }
    }
}
