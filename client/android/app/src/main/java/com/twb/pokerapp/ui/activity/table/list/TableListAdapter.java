package com.twb.pokerapp.ui.activity.table.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.table.AvailableTableDTO;
import com.twb.pokerapp.data.model.dto.table.TableDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TableListAdapter extends RecyclerView.Adapter<TableListAdapter.ViewHolder> {
    private final TableClickListener clickListener;
    private final List<AvailableTableDTO> dataset = new ArrayList<>();

    public TableListAdapter(TableClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TableListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        var viewHolder = new TableListAdapter.ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.table_list_item, parent, false));

        viewHolder.connectButton.setOnClickListener(view -> {
            var position = viewHolder.getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                AvailableTableDTO availableTable = dataset.get(position);
                clickListener.onTableClicked(availableTable.getTable());
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TableListAdapter.ViewHolder holder, int position) {
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

    public void setData(List<AvailableTableDTO> list) {
        dataset.clear();
        dataset.addAll(list);
        notifyDataSetChanged();
    }

    public interface TableClickListener {
        void onTableClicked(TableDTO table);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView gameTypeTextView;
        private final TextView playersTextView;
        final Button connectButton;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            gameTypeTextView = itemView.findViewById(R.id.gameTypeTextView);
            playersTextView = itemView.findViewById(R.id.playersTextView);
            connectButton = itemView.findViewById(R.id.connectButton);
        }

        public void bind(AvailableTableDTO availableTable) {
            var table = availableTable.getTable();
            nameTextView.setText(table.getName());
            gameTypeTextView.setText(table.getGameType());
            playersTextView.setText(String.format(Locale.getDefault(), "%d/%d", availableTable.getPlayersConnected(), table.getMaxPlayers()));
        }
    }
}
