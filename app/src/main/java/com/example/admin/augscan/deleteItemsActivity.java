package com.example.admin.augscan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class deleteItemsActivity extends AppCompatActivity {
    public static TextView resultDeleteView, item_barcode, item_name;
    private FirebaseAuth firebaseAuth;
    public static Button btnDelete;
    private Spinner spinnerCategory;
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
        item_barcode = findViewById(R.id.viewitembarcode);
        item_name = findViewById(R.id.viewitemname);
        mrecyclerview = findViewById(R.id.recyclerViewsDelete);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mrecyclerview.setLayoutManager(manager);
        mrecyclerview.setHasFixedSize(true);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = spinnerCategory.getSelectedItem().toString();
                resultSearch(searchText);
            }
        });
        String[] spinnerCate = {"Vui lòng chọn", "Thiết Bị", "Hóa Chất"};
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerCate);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);
    }

    public void resultSearch(String searchText) {
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finalUser = users.getEmail();
        String resultEmail = finalUser.replace(".", "");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultEmail).child("Items");
        Query firebaseSearchQuery = databaseReference.orderByChild("itemCategory").startAt(searchText).endAt(searchText + "\uf8ff");
        if (!TextUtils.isEmpty(searchText)) {
            FirebaseRecyclerAdapter<Items, deleteItemsActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, deleteItemsActivity.UsersViewHolder>(Items.class, R.layout.list_item_delete_layout, deleteItemsActivity.UsersViewHolder.class, firebaseSearchQuery) {
                @Override
                protected void populateViewHolder(deleteItemsActivity.UsersViewHolder viewHolder, Items model, int position) {
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
            TextView item_barcode = mView.findViewById(R.id.viewitembarcode);
            TextView item_name = mView.findViewById(R.id.viewitemname);
            TextView item_category = mView.findViewById(R.id.viewitemcategory);
            TextView item_price = mView.findViewById(R.id.viewitemprice);
            TextView item_year = mView.findViewById(R.id.viewItemYear);
            TextView item_origin = mView.findViewById(R.id.viewItemOrigin);
            TextView item_status = mView.findViewById(R.id.viewItemStatus);
            item_barcode.setText(itemBarcode);
            item_category.setText(itemCategory);
            item_name.setText(itemName);
            item_price.setText(itemPrice);
            item_year.setText(itemYear);
            item_origin.setText(itemOrigin);
            item_status.setText(itemStatus);
            btnDelete = mView.findViewById(R.id.buttonDelete);
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
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i;
                    i = new Intent(mView.getContext(), DialogDeleteActivity.class);
                    i.putExtra("itemBarcode", itemList.getItemBarcode());
                    view.getContext().startActivity(i);
                }
            });
        }
    }
}
