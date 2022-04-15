package com.example.admin.augscan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DialogDeleteActivity extends Activity {
    private TextView text;
    DatabaseReference mdatabaseReference;
    private FirebaseAuth firebaseAuth;
    public String getItemBarcode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_delete);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finalUser = users.getEmail();
        String resultEmail = finalUser.replace(".","");
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultEmail);
        text = findViewById(R.id.textView);
        Intent n = getIntent();
        getItemBarcode = n.getStringExtra("itemBarcode");
        Query firebaseSearchQuery = mdatabaseReference.child("Items").child(getItemBarcode);
        firebaseSearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mdatabaseReference.child("Items").child(getItemBarcode).removeValue();
                    text.setText( "'" + getItemBarcode + "'" +" item has been successfully deleted");
                } else {
                    text.setText("delete failed");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }
}
