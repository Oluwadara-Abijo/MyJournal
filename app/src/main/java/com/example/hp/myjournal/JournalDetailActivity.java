package com.example.hp.myjournal;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JournalDetailActivity extends AppCompatActivity {

    // Extra for the entry ID to be received in the intent
    public static final String EXTRA_ENTRY_ID = "extraEntryId";

    private JournalEntry mEntry;

    private String key;

    //UI elements
    private TextView mTitleTextView;
    private TextView mBodyTextView;

    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_detail);

        //UI elements
        mTitleTextView = findViewById(R.id.title_tv);
        mBodyTextView = findViewById(R.id.body_tv);
        FloatingActionButton mEditFAB = findViewById(R.id.edit_fab);

        mEntry = (JournalEntry) getIntent().getSerializableExtra(EXTRA_ENTRY_ID);

        populateUI(mEntry);

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("entries");

        mEditFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch NewJournalEntryActivity to update entry
                Intent intent = new Intent(JournalDetailActivity.this, NewJournalEntryActivity.class);
                intent.putExtra(NewJournalEntryActivity.EXTRA_ENTRY_ID, mEntry);
                startActivity(intent);
            }
        });

    }

    void populateUI(JournalEntry entry) {
        mTitleTextView.setText(entry.getTitle());
        mBodyTextView.setText(entry.getBody());
        key = entry.getKey();
    }

    private void DeleteEntry(String entry) {
        mDatabaseReference.child(entry).removeValue();
        Intent intent = new Intent(JournalDetailActivity.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_journal_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_delete) {
            DeleteEntry(key);
        }
        return super.onOptionsItemSelected(item);
    }
}
