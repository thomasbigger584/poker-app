package com.twb.pokerapp.ui.activity.transaction;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokerapp.R;
import com.twb.pokerapp.proto.TransactionHistoryDTO;
import com.twb.pokerapp.databinding.ItemTransactionHistoryBinding;
import com.twb.pokerapp.util.Protos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TransactionHistoryAdapter extends ListAdapter<TransactionHistoryDTO, TransactionHistoryAdapter.ViewHolder> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, HH:mm", Locale.getDefault());

    private static final DiffUtil.ItemCallback<TransactionHistoryDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull TransactionHistoryDTO oldItem, @NonNull TransactionHistoryDTO newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TransactionHistoryDTO oldItem, @NonNull TransactionHistoryDTO newItem) {
            return oldItem.getType().equals(newItem.getType()) &&
                    oldItem.getAmount().equals(newItem.getAmount()) &&
                    oldItem.getCreatedDateTime().equals(newItem.getCreatedDateTime());
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
            var type = transaction.getType();
            binding.transactionTitle.setText(Protos.shortName(type));
            binding.transactionTimestamp.setText(
                    LocalDateTime.parse(transaction.getCreatedDateTime()).format(DATE_FORMAT));

            var amount = Protos.money(transaction.getAmount());

            var sign = amount >= 0 ? "+" : "-";
            var amountText = String.format(Locale.getDefault(), "%s$%.2f", sign, Math.abs(amount));
            binding.transactionAmount.setText(amountText);

            int iconRes;
            int colorRes;

            switch (type) {
                case TRANSACTION_HISTORY_TYPE_DEPOSIT:
                    iconRes = R.drawable.ic_credit_card;
                    colorRes = R.color.transaction_positive_gain;
                    break;
                case TRANSACTION_HISTORY_TYPE_WITHDRAW:
                    iconRes = R.drawable.ic_credit_card;
                    colorRes = R.color.transaction_negative_cost;
                    break;
                case TRANSACTION_HISTORY_TYPE_BUYIN:
                    iconRes = R.drawable.ic_poker_chip;
                    colorRes = R.color.transaction_negative_cost;
                    break;
                case TRANSACTION_HISTORY_TYPE_CASHOUT:
                    iconRes = R.drawable.ic_poker_chip;
                    colorRes = R.color.transaction_positive_gain;
                    break;
                case TRANSACTION_HISTORY_TYPE_RESET:
                    iconRes = R.drawable.ic_refresh;
                    colorRes = (amount >= 0) ? R.color.transaction_positive_gain : R.color.transaction_negative_cost;
                    break;
                default:
                    iconRes = R.drawable.ic_info;
                    colorRes = R.color.lightGrey;
                    break;
            }

            var color = ContextCompat.getColor(context, colorRes);
            binding.transactionAmount.setTextColor(color);
            binding.chipIcon.setImageResource(iconRes);
            binding.chipIcon.setColorFilter(color);

        }
    }
}
