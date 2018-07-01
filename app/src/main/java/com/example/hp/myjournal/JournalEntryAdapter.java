package com.example.hp.myjournal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class JournalEntryAdapter extends RecyclerView.Adapter<JournalEntryAdapter.EntryViewHolder> {

    public interface ItemClickListener {
        void onItemClickListener(JournalEntry entry);
    }

    private List<JournalEntry> mJournalEntries;

    // Member variable to handle item clicks
    private final ItemClickListener mItemClickListener;

    //Constructor for JournalEntryAdapter that initializes the context
    JournalEntryAdapter(List<JournalEntry> mList, ItemClickListener listener) {
        mJournalEntries = mList;
        mItemClickListener = listener;

    }

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int listItemLayoutId = R.layout.item_list;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(listItemLayoutId, viewGroup, attachToParentImmediately);

        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {

        holder.bind(mJournalEntries.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (mJournalEntries == null) {
            return 0;
        }
        return mJournalEntries.size();
    }

    public void replaceData(List<JournalEntry> entries) {
        this.mJournalEntries = entries;
        this.notifyDataSetChanged();
    }

    //Inner class for creating view holders
    class EntryViewHolder extends RecyclerView.ViewHolder {

        //Class variables for journal entry title, preview and date modified text views
        TextView mTitleTextView;
        TextView mPreviewTextView;
        TextView mDateTextView;

        //Constructor for EntryViewHolder
        EntryViewHolder(View itemView) {
            super(itemView);

            mTitleTextView = itemView.findViewById(R.id.title_edit_text);
            mPreviewTextView = itemView.findViewById(R.id.preview_text_view);
            mDateTextView = itemView.findViewById(R.id.date_text_view);
        }

        void bind(final JournalEntry journalEntry, final int position) {
            //Set values
            mTitleTextView.setText(journalEntry.getTitle());
            mPreviewTextView.setText(journalEntry.getBody());
            mDateTextView.setText(journalEntry.getDateModified());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClickListener(mJournalEntries.get(position));
                }
            });
        }

    }

}