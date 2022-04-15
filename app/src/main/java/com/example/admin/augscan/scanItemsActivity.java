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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class scanItemsActivity extends AppCompatActivity {
    public static EditText resultSearchView;
    private FirebaseAuth firebaseAuth;
    private Spinner spinnerCategory;
    ImageButton scanToSearch;
    Button searchBtn;
    RecyclerView mrecyclerview;
    DatabaseReference mdatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_items);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finalUser = users.getEmail();
        String resultEmail = finalUser.replace(".", "");
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultEmail).child("Items");
        resultSearchView = findViewById(R.id.searchfield);
        scanToSearch = findViewById(R.id.imageButtonsearch);
        searchBtn = findViewById(R.id.searchbtnn);
        mrecyclerview = findViewById(R.id.recyclerViews);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        scanToSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivitysearch.class));
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = resultSearchView.getText().toString();
                firebaseSearch(searchText);
            }
        });
    }

    public void firebaseSearch(String searchText) {
        Query firebaseSearchQuery = mdatabaseReference.orderByChild("itemBarcode").startAt(searchText).endAt(searchText + "\uf8ff");
        if (!TextUtils.isEmpty(searchText)) {
            FirebaseRecyclerAdapter<Items, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, UsersViewHolder>(Items.class, R.layout.list_layout, UsersViewHolder.class, firebaseSearchQuery) {
                @Override
                protected void populateViewHolder(UsersViewHolder viewHolder, Items model, int position) {
                    viewHolder.setDetails(
                            getApplicationContext(),
                            model.getItemBarcode(),
                            model.getItemCategory(),
                            model.getItemName(),
                            model.getItemPrice(),
                            model.getItemImg(),
                            model.getItemYear(),
                            model.getItemOrigin(),
                            model.getItemStatus()
                    );
                }
            };
            mrecyclerview.setAdapter(firebaseRecyclerAdapter);
            Toast.makeText(scanItemsActivity.this, "Wait a minute", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(scanItemsActivity.this, "Please scan Barcode", Toast.LENGTH_SHORT).show();
        }
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        private final Items itemList = new Items();
        public void setDetails(
                Context ctx,
                final String itemBarcode,
                String itemCategory,
                String itemName,
                String itemPrice,
                String itemImg,
                String itemYear,
                String itemOrigin,
                String itemStatus
        ) {
            TextView item_barcode = (TextView) mView.findViewById(R.id.viewitembarcode);
            TextView item_name = (TextView) mView.findViewById(R.id.viewitemname);
            TextView item_category = (TextView) mView.findViewById(R.id.viewitemcategory);
            TextView item_price = (TextView) mView.findViewById(R.id.viewitemprice);
            TextView item_year = (TextView) mView.findViewById(R.id.viewItemYear);
            TextView item_origin = (TextView) mView.findViewById(R.id.viewItemOrigin);
            TextView item_status = (TextView) mView.findViewById(R.id.viewItemStatus);
            item_barcode.setText(itemBarcode);
            item_category.setText(itemCategory);
            item_name.setText(itemName);
            item_price.setText(itemPrice);
            item_year.setText(itemYear);
            item_origin.setText(itemOrigin);
            item_status.setText(itemStatus);
            this.itemList.setItem(
                    itemName,
                    itemCategory,
                    itemPrice,
                    itemBarcode,
                    itemImg,
                    itemYear,
                    itemOrigin,
                    itemStatus
            );
            Button editItem = mView.findViewById(R.id.buttonedit);
            editItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    i = new Intent(mView.getContext(), edititemActivity.class);
                    i.putExtra("itemBarcode", itemList.getItemBarcode());
                    i.putExtra("itemName", itemList.getItemName());
                    i.putExtra("itemCategory", itemList.getItemCategory());
                    i.putExtra("itemPrice", itemList.getItemPrice());
                    i.putExtra("itemYear", itemList.getItemYear());
                    i.putExtra("itemOrigin", itemList.getItemOrigin());
                    i.putExtra("itemStatus", itemList.getItemStatus());
                    view.getContext().startActivity(i);
                }
            });
            Button btnView = mView.findViewById(R.id.buttonView);
            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    i = new Intent(mView.getContext(), ViewImagePopUp.class);
                    i.putExtra("itemBarcode", itemList.getItemBarcode());
                    view.getContext().startActivity(i);
                }

            });
        }
    }
}
