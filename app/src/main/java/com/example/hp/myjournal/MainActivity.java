package com.example.hp.myjournal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements JournalEntryAdapter.ItemClickListener {

    //Enable offline capabilities
    static {FirebaseDatabase.getInstance().setPersistenceEnabled(true);}

    private static final int RC_SIGN_IN = 123;

    private List<JournalEntry> mList = new ArrayList<>();
    private JournalEntry mEntry;

    //UI elements
    private RecyclerView mRecyclerView;
    private JournalEntryAdapter mAdapter;

    //Firebase elements
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mEntriesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);

        //Connect the recycler view to a layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //Attach an adapter for the data to be displayed
        mAdapter = new JournalEntryAdapter(mList, this);
        mRecyclerView.setAdapter(mAdapter);

        //Instantiate Firebase elements
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mEntriesDatabaseReference = mFirebaseDatabase.getReference().child("entries");
        mEntriesDatabaseReference.keepSynced(true);
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Firebase authentication and UI
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User is signed in
                    onSignedInInitialize();
                } else {
                    //User is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .setTheme(R.style.GreenTheme)
                                    .setLogo(R.drawable.logo)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent
                        (MainActivity.this, NewJournalEntryActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed In!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Signing in cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void getAllEntries(DataSnapshot dataSnapshot) {
        mEntry = dataSnapshot.getValue(JournalEntry.class);
        mList.add(0, mEntry);
        mAdapter.notifyDataSetChanged();
    }

    private void attachDatabaseListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    getAllEntries(dataSnapshot);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    getAllEntries(dataSnapshot);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                    mEntry = dataSnapshot.getValue(JournalEntry.class);
                    mList.remove(mEntry);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }

            };

            mEntriesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mEntriesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    private void onSignedInInitialize() {
        attachDatabaseListener();
    }

    private void onSignedOutCleanup() {
        mList.clear();
        detachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
        mList.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                //Sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onItemClickListener(JournalEntry entry) {
        // Launch NewJournalEntryActivity putting the journal entry as an extra in the intent
        Intent intent = new Intent(MainActivity.this, JournalDetailActivity.class);
        intent.putExtra(NewJournalEntryActivity.EXTRA_ENTRY_ID, entry);
        startActivity(intent);
    }
}
