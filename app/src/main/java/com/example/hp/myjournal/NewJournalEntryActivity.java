package com.example.hp.myjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewJournalEntryActivity extends AppCompatActivity {

    // Extra for the entry ID to be received in the intent
    public static final String EXTRA_ENTRY_ID = "extraEntryId";

    //UI Elements
    private EditText mTitleEditText;
    private EditText mBodyEditText;

    private String mDateModified;
    private String mEntryTitle;
    private String mEntryBody;

    private JournalEntry mEntry;
    private String key;
    private boolean updateEntry = false;

    //Firebase elements
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEntriesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_journal_entry);

        mTitleEditText = findViewById(R.id.title_edit_text);
        mBodyEditText = findViewById(R.id.body_edit_text);

        mEntryTitle = mTitleEditText.getText().toString();
        mEntryBody = mBodyEditText.getText().toString();

        mDateModified = "Modified: " + new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                .format(new Date());

        //Initialize Firebase elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEntriesDatabaseReference = mFirebaseDatabase.getReference().child("entries");

        //Handle existing entry
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ENTRY_ID)) {
            updateEntry = true;
            mEntry = (JournalEntry) getIntent().getSerializableExtra(EXTRA_ENTRY_ID);
            populateUI(mEntry);
            key = mEntry.getKey();
        }
    }

    private JournalEntry getData() {
        mEntry = new JournalEntry(mEntryTitle, mEntryBody, mDateModified, key);
        mEntry.setTitle(mTitleEditText.getText().toString());
        mEntry.setBody(mBodyEditText.getText().toString());
        mEntry.setDateModified(mDateModified);
        mEntry.setKey(key);
        return mEntry;
    }

    private void updateJournalEntry() {
        mEntry = getData();
        mEntriesDatabaseReference.child(mEntry.getKey()).setValue(mEntry);
        saveEntry();
    }

    private void saveEntry() {
        Intent intent = new Intent(NewJournalEntryActivity.this, MainActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    private void addEntry() {
        key = mEntriesDatabaseReference.child("entries").push().getKey();
        mEntry = getData();
        mEntriesDatabaseReference.child(key).setValue(mEntry);
        saveEntry();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_new_journal_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.save_menu) {
            if (updateEntry) {
                updateJournalEntry();
            } else {
                addEntry();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param entry the journalEntry to populate the UI
     */
    private void populateUI(JournalEntry entry) {
        if (entry == null) {
            return;
        }
        mTitleEditText.setText(entry.getTitle());
        mBodyEditText.setText(entry.getBody());
    }
}