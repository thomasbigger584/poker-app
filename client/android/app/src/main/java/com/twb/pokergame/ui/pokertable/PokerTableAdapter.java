package com.twb.pokergame.ui.pokertable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokergame.R;
import com.twb.pokergame.data.model.PokerTable;

import java.util.List;

public class PokerTableAdapter extends RecyclerView.Adapter<PokerTableAdapter.ViewHolder> {
    private final List<PokerTable> dataset;

    public PokerTableAdapter(List<PokerTable> dataset) {
        this.dataset = dataset;
    }

    @NonNull
    @Override
    public PokerTableAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PokerTableAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.poker_table_item_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(PokerTableAdapter.ViewHolder holder, int position) {
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
        private final TextView nameTextView;
        private final TextView gameTypeTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            gameTypeTextView = itemView.findViewById(R.id.gameTypeTextView);
        }

        public void bind(PokerTable pokerTable) {
            nameTextView.setText(pokerTable.getName());
            gameTypeTextView.setText(pokerTable.getGameType());
        }
    }
}
