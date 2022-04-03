package com.example.admin.augscan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class deleteItemsActivity extends AppCompatActivity {
    public static EditText resultSearchView;
    public static TextView resultdeleteview, item_barcode, item_name;
    private FirebaseAuth firebaseAuth;
    public static Button btnDelete;
    Button searchBtn;
    DatabaseReference databaseReference;
    RecyclerView mrecyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_items);
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        searchBtn = findViewById(R.id.searchBtn);
        resultSearchView = findViewById(R.id.searchField);
        item_barcode = (TextView) findViewById(R.id.viewitembarcode);
        item_name = (TextView) findViewById(R.id.viewitemname);
        mrecyclerview = findViewById(R.id.recyclerViewsDelete);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = resultSearchView.getText().toString();
                resultSearch(searchText);
            }
        });
    }

    public void resultSearch(String searchtext) {

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finaluser = users.getEmail();
        String resultemail = finaluser.replace(".", "");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultemail).child("Items");
        Query firebaseSearchQuery = databaseReference.orderByChild("itembarcode").startAt(searchtext).endAt(searchtext + "\uf8ff");
        if (!TextUtils.isEmpty(searchtext)) {
            FirebaseRecyclerAdapter<Items, deleteItemsActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, deleteItemsActivity.UsersViewHolder>

                    (Items.class,
                            R.layout.list_item_delete_layout,
                            deleteItemsActivity.UsersViewHolder.class,
                            firebaseSearchQuery) {
                @Override
                protected void populateViewHolder(deleteItemsActivity.UsersViewHolder viewHolder, Items model, int position) {

                    viewHolder.setDetails(getApplicationContext(), model.getItembarcode(), model.getItemcategory(), model.getItemname(), model.getItemprice(), model.getItemimg());

                }
            };
            mrecyclerview.setAdapter(firebaseRecyclerAdapter);
            Toast.makeText(deleteItemsActivity.this, "Wait a minute", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(deleteItemsActivity.this, "Please scan Barcode", Toast.LENGTH_SHORT).show();
        }
    }


    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        private Items itemList = new Items();

        public void setDetails(Context ctx, final String itembarcode, String itemcategory, String itemname, String itemprice, String itemimg) {
            TextView item_barcode = (TextView) mView.findViewById(R.id.viewitembarcode);
            TextView item_name = (TextView) mView.findViewById(R.id.viewitemname);
            TextView item_category = (TextView) mView.findViewById(R.id.viewitemcategory);
            TextView item_price = (TextView) mView.findViewById(R.id.viewitemprice);
            item_barcode.setText(itembarcode);
            item_category.setText(itemcategory);
            item_name.setText(itemname);
            item_price.setText(itemprice);
            btnDelete = mView.findViewById(R.id.buttonDelete);
            this.itemList.setItem(itemname, itemcategory, itemprice, itembarcode, itemimg);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    i = new Intent(mView.getContext(), DialogDeleteActivity.class);
                    i.putExtra("itembarcode", itemList.getItembarcode());
                    view.getContext().startActivity(i);
                }
            });
        }
    }
}
