package com.twb.pokerapp.ui.activity.transaction;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokerapp.R;
import com.twb.pokerapp.data.model.dto.transaction.TransactionHistoryDTO;
import com.twb.pokerapp.databinding.ItemTransactionHistoryBinding;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionHistoryAdapter extends ListAdapter<TransactionHistoryDTO, TransactionHistoryAdapter.ViewHolder> {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());

    private static final DiffUtil.ItemCallback<TransactionHistoryDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull TransactionHistoryDTO oldItem, @NonNull TransactionHistoryDTO newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TransactionHistoryDTO oldItem, @NonNull TransactionHistoryDTO newItem) {
            return oldItem.getDescription().equals(newItem.getDescription()) &&
                    oldItem.getAmount().equals(newItem.getAmount()) &&
                    oldItem.getDateCreated().equals(newItem.getDateCreated());
        }
    };

    public TransactionHistoryAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ItemTransactionHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemTransactionHistoryBinding binding;

        public ViewHolder(ItemTransactionHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TransactionHistoryDTO transaction) {
            var context = binding.getRoot().getContext();
            binding.transactionTitle.setText(transaction.getDescription());
            binding.transactionTimestamp.setText(DATE_FORMAT.format(transaction.getDateCreated()));

            var amount = transaction.getAmount();
            var amountText = String.format(Locale.getDefault(), "%s$%.2f", amount >= 0 ? "+" : "-", Math.abs(amount));
            binding.transactionAmount.setText(amountText);

            if (amount >= 0) {
                binding.transactionAmount.setTextColor(ContextCompat.getColor(context, R.color.transaction_positive_gain));
                binding.chipIcon.setImageResource(R.drawable.ic_positive_green);
            } else {
                binding.transactionAmount.setTextColor(ContextCompat.getColor(context, R.color.transaction_negative_cost));
                binding.chipIcon.setImageResource(R.drawable.ic_negative_red);
            }
        }
    }
}
