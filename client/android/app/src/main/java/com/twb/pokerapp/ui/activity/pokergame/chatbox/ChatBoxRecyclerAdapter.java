package com.twb.pokerapp.ui.activity.pokergame.chatbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.twb.pokerapp.R;

import java.util.ArrayList;
import java.util.List;

public class ChatBoxRecyclerAdapter extends RecyclerView.Adapter<ChatBoxRecyclerAdapter.ChatBoxViewHolder> {
    private final List<String> items = new ArrayList<>();
    private final LinearLayoutManager layoutManager;

    public ChatBoxRecyclerAdapter(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @NonNull
    @Override
    public ChatBoxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatBoxItemLayout = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.chat_box_item_layout, parent, false);
        return new ChatBoxViewHolder(chatBoxItemLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatBoxViewHolder holder, int position) {
        String item = items.get(position);
        holder.chatBoxItemTextView.setText(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(String item) {
        int positionStart = items.size() + 1;
        items.add(item);
        notifyItemRangeInserted(positionStart, items.size());
        layoutManager.scrollToPosition(items.size() - 1);
    }

    static class ChatBoxViewHolder extends RecyclerView.ViewHolder {
        final TextView chatBoxItemTextView;

        ChatBoxViewHolder(View view) {
            super(view);
            chatBoxItemTextView = view.findViewById(R.id.chatBoxItemTextView);
        }
    }
}
