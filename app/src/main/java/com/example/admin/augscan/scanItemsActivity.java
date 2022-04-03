package com.example.admin.augscan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class scanItemsActivity extends AppCompatActivity {
    public static EditText resultsearcheview;
    private FirebaseAuth firebaseAuth;
    ImageButton scantosearch;
    Button searchbtn;
    public Number mPageEndOffset = 0;
    public Number mPageLimit = 10;
    RecyclerView mrecyclerview;
    DatabaseReference mdatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_items);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finaluser=users.getEmail();
        String resultemail = finaluser.replace(".","");
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultemail).child("Items");
        resultsearcheview = findViewById(R.id.searchfield);
        scantosearch = findViewById(R.id.imageButtonsearch);
        searchbtn = findViewById(R.id.searchbtnn);

        mrecyclerview = findViewById(R.id.recyclerViews);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(true);


        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));


        scantosearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivitysearch.class));
            }
        });

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchtext = resultsearcheview.getText().toString();
                firebasesearch(searchtext);
            }
        });
    }

    public void firebasesearch(String searchtext){

        Query firebaseSearchQuery = mdatabaseReference.orderByChild("itembarcode").startAt(searchtext).endAt(searchtext+"\uf8ff");
        if(!TextUtils.isEmpty(searchtext)){
            FirebaseRecyclerAdapter<Items, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, UsersViewHolder>

                    (  Items.class,
                            R.layout.list_layout,
                            UsersViewHolder.class,
                            firebaseSearchQuery)

            {
                @Override
                protected void populateViewHolder(UsersViewHolder viewHolder, Items model,int position){

                    viewHolder.setDetails(getApplicationContext(),model.getItembarcode(),model.getItemcategory(),model.getItemname(),model.getItemprice(), model.getItemimg());

                }
            };

            mrecyclerview.setAdapter(firebaseRecyclerAdapter);

            Toast.makeText(scanItemsActivity.this,"Wait a minute",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(scanItemsActivity.this,"Please scan Barcode",Toast.LENGTH_SHORT).show();

        }

    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public UsersViewHolder(View itemView){
            super(itemView);
            mView =itemView;
        }
        private Items itemList = new Items();
        public void setDetails(Context ctx, final String itembarcode, String itemcategory, String itemname, String itemprice, String itemimg){
            TextView item_barcode = (TextView) mView.findViewById(R.id.viewitembarcode);
            TextView item_name = (TextView) mView.findViewById(R.id.viewitemname);
            TextView item_category = (TextView) mView.findViewById(R.id.viewitemcategory);
            TextView item_price = (TextView) mView.findViewById(R.id.viewitemprice);
            item_barcode.setText(itembarcode);
            item_category.setText(itemcategory);
            item_name.setText(itemname);
            item_price.setText(itemprice);
            this.itemList.setItem(itemname, itemcategory, itemprice, itembarcode, itemimg);
            Button editItem = mView.findViewById(R.id.buttonedit);
            editItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    i = new Intent(mView.getContext(), edititemActivity.class);
                    i.putExtra("itembarcode", itemList.getItembarcode());
                    i.putExtra("itemname", itemList.getItemname());
                    i.putExtra("itemcategory", itemList.getItemcategory());
                    i.putExtra("itemprice", itemList.getItemprice());
                    view.getContext().startActivity(i);
                }
            });
            Button btnView = mView.findViewById(R.id.buttonView);
            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    i = new Intent(mView.getContext(), ViewImagePopUp.class);
                    i.putExtra("itembarcode", itemList.getItembarcode());
                    view.getContext().startActivity(i);
                }

            });


        }
    }
}
