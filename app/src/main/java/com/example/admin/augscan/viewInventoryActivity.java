package com.example.admin.augscan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class viewInventoryActivity extends AppCompatActivity {
    Button EditHs;

    private FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    DatabaseReference mdatabaseReference;
   private TextView totalnoofitem, totalnoofsum;
   private Button buttonedit;
   private int counttotalnoofitem =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);

        EditHs= findViewById(R.id.EditHistory);
        EditHs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://docs.google.com/spreadsheets/d/1Sx9jiB_dyDh43RQzbYyKerKMG0An72GYFJCF13znWJ4/edit#gid=0");
            }
        });
        totalnoofitem= findViewById(R.id.totalnoitem);
//        totalnoofsum = findViewById(R.id.totalsum);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finaluser=users.getEmail();
        String resultemail = finaluser.replace(".","");
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultemail).child("Items");
        mrecyclerview = findViewById(R.id.recyclerViews);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));


        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    counttotalnoofitem = (int) dataSnapshot.getChildrenCount();
                    totalnoofitem.setText(Integer.toString(counttotalnoofitem));
                }else{
                    totalnoofitem.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

    protected  void  onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder>
                (  Items.class,
                        R.layout.list_layout,
                        scanItemsActivity.UsersViewHolder.class,
                        mdatabaseReference

                )
        {
            @Override
            protected void populateViewHolder(scanItemsActivity.UsersViewHolder viewHolder, Items model, int position){

                viewHolder.setDetails(getApplicationContext(),model.getItembarcode(),model.getItemcategory(),model.getItemname(),model.getItemprice(), model.getItemimg());
            }
        };

        mrecyclerview.setAdapter(firebaseRecyclerAdapter);
    }


}
