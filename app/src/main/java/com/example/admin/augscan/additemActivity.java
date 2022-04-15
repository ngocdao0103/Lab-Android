package com.example.admin.augscan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class additemActivity extends AppCompatActivity {
    private Button btnChoose;
    private ImageView imageView;
    private EditText itemName, itemPrice, itemYear, itemOrigin, itemStatus;
    private TextView itemBarcode;
    private Spinner spin, spinnerCategory, spinnerHc;
    private FirebaseAuth firebaseAuth;
    private String idText;
    private String finalUser;
    private String resultEmail;
    private  String imgEncoded;
    public static TextView resulttextview;
    Button scanButton, addItemToDatabase, btnDelete;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceCat;
    ProgressDialog pd;
    Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferenceCat = FirebaseDatabase.getInstance().getReference("Users");
        resulttextview = findViewById(R.id.barcodeview);
        addItemToDatabase = findViewById(R.id.additembuttontodatabase);
        scanButton = findViewById(R.id.buttonscan);
        itemName = findViewById(R.id.edititemname);
        itemPrice = findViewById(R.id.editprice);
        itemYear = findViewById(R.id.textYear);
        itemOrigin = findViewById(R.id.textOrigin);
        itemStatus = findViewById(R.id.textStatus);
        itemBarcode = findViewById(R.id.barcodeview);
        if (users != null) {
            finalUser =users.getEmail();
            resultEmail = finalUser.replace(".","");
        }
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
            }
        });
        addItemToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        String[] spinnerUnit = { "g","ml", "ống", "đĩa", "bình", "cái", "chai", "Khác" };
        String[] spinnerCate = {"Thiết Bị", "Hóa Chất"};
        String[] spinnerHC = {"Rắn", "Lỏng", "Khí"};
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerHc = findViewById(R.id.spinnerHC);
        spin = findViewById(R.id.spinnerUnit);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerUnit);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerCate);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);

        ArrayAdapter<String> adapterHC = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerHC);
        adapterHC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHc.setAdapter(adapterHC);

        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");
        btnChoose = findViewById(R.id.btnChoose);
        imageView = findViewById(R.id.chooseImg);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
                selectedBitmap = null;
            }
        });
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                System.out.println(selectedItem);
                if(selectedItem.equals("Hóa Chất")) {
                    spinnerHc.setVisibility(View.VISIBLE);
                } else {
                    spinnerHc.setVisibility(View.GONE);
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {
                spinnerHc.setVisibility(View.GONE);
            }
        });
    }

    private void chooseImage() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 200);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100&& resultCode == RESULT_OK) {
            selectedBitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(selectedBitmap);
        } else if(requestCode == 200&& resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                imageView.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public  void addItem(){
        idText = itemBarcode.getText().toString();
        Query firebaseSearchQuery = databaseReference.child(resultEmail).child("Items").child(idText);
        if (idText.isEmpty()) {
            itemBarcode.setError("It's Empty");
            itemBarcode.requestFocus();
        } else {
            firebaseSearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        saveItem(false);
                    } else if (!dataSnapshot.exists()){
                        saveItem(true);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }
    }

    private void Logout()
    {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(additemActivity.this,LoginActivity.class));
        Toast.makeText(additemActivity.this,"LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            Logout();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveItem(boolean isRun) {
        if (isRun) {
            String itemNameValue = itemName.getText().toString();
            String itemCategoryValue;
            String itemYearValue = itemYear.getText().toString();
            String itemStatusValue = itemStatus.getText().toString();
            String itemOriginValue = itemOrigin.getText().toString();
            String itemPriceValue = itemPrice.getText().toString() + " " + spin.getSelectedItem().toString();
            if (spinnerCategory.getSelectedItem().toString().equals("Hóa Chất")){
                itemCategoryValue = spinnerCategory.getSelectedItem().toString() + "-" + spinnerHc.getSelectedItem().toString();
            } else {
                itemCategoryValue = spinnerCategory.getSelectedItem().toString();
            }
            if (selectedBitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                imgEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } else {
                imgEncoded = null;
            }
            Items items = new Items(
                    itemNameValue,
                    itemCategoryValue,
                    itemPriceValue,
                    idText,
                    imgEncoded,
                    itemYearValue,
                    itemOriginValue,
                    itemStatusValue
            );
            databaseReference.child(resultEmail).child("Items").child(idText).setValue(items);
            databaseReferenceCat.child(resultEmail).child("ItemByCategory").child(itemCategoryValue).child(idText).setValue(items);
            itemName.setText("");
            itemBarcode.setText("");
            itemPrice.setText("");
            itemBarcode.setText("");
            itemYear.setText("");
            itemOrigin.setText("");
            itemStatus.setText("");
            imageView.setImageBitmap(null);
            Toast.makeText(additemActivity.this,itemNameValue+"---Added",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(additemActivity.this,"Bar/Qr already exists",Toast.LENGTH_SHORT).show();
        }
    }
}
