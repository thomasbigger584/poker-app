package com.twb.pokerapp.ui.activity.table.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokerapp.R;
import com.twb.pokerapp.databinding.TableListItemBinding;
import com.twb.pokerapp.data.model.dto.table.AvailableTableDTO;
import com.twb.pokerapp.data.model.dto.table.TableDTO;

public class TableListAdapter extends ListAdapter<AvailableTableDTO, TableListAdapter.ViewHolder> {
    private final TableClickListener clickListener;

    private static final DiffUtil.ItemCallback<AvailableTableDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull AvailableTableDTO oldItem, @NonNull AvailableTableDTO newItem) {
            return oldItem.getTable().getId().equals(newItem.getTable().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AvailableTableDTO oldItem, @NonNull AvailableTableDTO newItem) {
            return oldItem.equals(newItem);
        }
    };

    public TableListAdapter(TableClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TableListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = TableListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TableListAdapter.ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public interface TableClickListener {
        void onTableClicked(TableDTO table);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TableListItemBinding binding;
        private final TableClickListener clickListener;

        public ViewHolder(TableListItemBinding binding, TableClickListener clickListener) {
            super(binding.getRoot());
            this.binding = binding;
            this.clickListener = clickListener;
        }

        public void bind(AvailableTableDTO availableTable) {
            var table = availableTable.getTable();
            var context = binding.getRoot().getContext();
            binding.nameTextView.setText(table.getName());
            binding.gameTypeTextView.setText(table.getGameType());
            binding.playersTextView.setText(context.getString(R.string.player_count_format, availableTable.getPlayersConnected(), table.getMaxPlayers()));
            binding.connectButton.setOnClickListener(v -> clickListener.onTableClicked(table));
        }
    }
}
