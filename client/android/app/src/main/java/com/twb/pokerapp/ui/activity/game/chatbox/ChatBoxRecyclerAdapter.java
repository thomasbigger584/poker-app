package com.twb.pokerapp.ui.activity.game.chatbox;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokerapp.databinding.ChatBoxListItemBinding;

import java.util.ArrayList;

public class ChatBoxRecyclerAdapter extends ListAdapter<String, ChatBoxRecyclerAdapter.ChatBoxViewHolder> {

    private static final DiffUtil.ItemCallback<String> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };

    public ChatBoxRecyclerAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ChatBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = ChatBoxListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatBoxViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBoxViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void add(String item) {
        if (item == null) return;
        var currentList = new ArrayList<>(getCurrentList());
        currentList.add(item);
        submitList(currentList);
    }

    public static class ChatBoxViewHolder extends RecyclerView.ViewHolder {
        private final ChatBoxListItemBinding binding;

        ChatBoxViewHolder(ChatBoxListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String item) {
            binding.chatBoxItemTextView.setText(item);
        }
    }
}
