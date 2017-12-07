package com.locationsetup;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sky on 2017-12-06.
 */

public class FirebaseManager {

    private final String TAG = FirebaseManager.class.getSimpleName();

    private static FirebaseManager instance = new FirebaseManager();

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;

    public static FirebaseManager getInstance() {
        return instance;
    }

    private List<OnItemChangedListener> mCallback
            = new ArrayList<>();

    public interface OnItemChangedListener {
        void onItemChanged();
    }

    public void setItemChangedListener(OnItemChangedListener itemChangedListener) {
        mCallback.add(itemChangedListener);
    }

    public void notifyItemChange() {
        for (OnItemChangedListener listener : mCallback)
            listener.onItemChanged();

        if (MainActivity.isSynchronized) {
            saveCurrentItems(mAuth.getCurrentUser());
        }
    }

    private FirebaseManager() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
    }

    public void loadItems(FirebaseUser user) {
        mDatabase.getReference(user.getUid() + "/items")
                .addListenerForSingleValueEvent(postListener);
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            FileManager.items.clear();
            for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                LocationItem item = itemSnapshot.getValue(LocationItem.class);
                FileManager.items.add(item);
            }
            notifyItemChange();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
        }
    };

    public void saveCurrentItems(FirebaseUser user) {
        String userId = user.getUid();
        final Map<String, Object> childUpdates = new HashMap<>();
        DatabaseReference ref = mDatabase.getReference(userId + "/items");
        for (LocationItem item : FileManager.items) {
            String id = ref.push().getKey();
            item.setId(id);
            childUpdates.put(id, item);
        }
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                mutableData.setValue(null);
                mutableData.setValue(childUpdates);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.w(TAG, "transaction complete:" + databaseError.getMessage());
                }
            }
        });
    }

    /*public void onItemUpdated(int position) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            //Toast.makeText(this, R.string.request_auth, Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = user.getUid();
        final Map<String, Object> childUpdates = new HashMap<>();
        DatabaseReference ref = mDatabase.getReference(userId + "/items");
        for (LocationItem item : FileManager.items) {
            String id = ref.push().getKey();
            item.setId(id);
            childUpdates.put(id, item);
        }
    }*/

    public boolean authProcess() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            mAuth.signOut();
            return false;
        }
        return true;
    }

    public FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }


}
