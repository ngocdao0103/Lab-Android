package com.example.admin.augscan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private EditText itemname,itemcategory,itemprice;
    private TextView itembarcode;
    private Spinner spin;
    private FirebaseAuth firebaseAuth;
    private String idText;
    private String finaluser;
    private String resultemail;
    private  String imgeEncoded;
    public static TextView resulttextview;
    Button scanbutton, additemtodatabase,btndelete;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferencecat;

    // TODO add img
    private Button btnChoose;
    private ImageView imageView;
    ProgressDialog pd;
    Bitmap selectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);
        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReferencecat = FirebaseDatabase.getInstance().getReference("Users");

        resulttextview = findViewById(R.id.barcodeview);
        additemtodatabase = findViewById(R.id.additembuttontodatabase);
        scanbutton = findViewById(R.id.buttonscan);
        itemname = findViewById(R.id.edititemname);
        itemcategory= findViewById(R.id.editcategory);
        itemprice = findViewById(R.id.editprice);
        itembarcode= findViewById(R.id.barcodeview);
        finaluser=users.getEmail();
        resultemail = finaluser.replace(".","");
        scanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivity.class));
            }
        });

        additemtodatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });

        String[] spinnerUnit = { "Pcs","l", "ml", "Kg", "g", "Other" };
        spin = findViewById(R.id.spinnerUnit);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerUnit);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

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

        btndelete = findViewById(R.id.btnDelete);
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(null);
                selectedBitmap = null;
            }
        });


    }

    // TODO add img
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

    // adding item to database
    public  void addItem(){
        //TODO
        idText = itembarcode.getText().toString();
        Query firebaseSearchQuery = databaseReference.child(resultemail).child("Items").child(idText);

        if (idText.isEmpty()) {
            itembarcode.setError("It's Empty");
            itembarcode.requestFocus();
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
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    // logout below
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
        switch (item.getItemId()){
            case  R.id.logoutMenu:{
                Logout();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveItem(boolean isRun) {
        if (isRun) {
            String itemnameValue = itemname.getText().toString();
            String itemcategoryValue = itemcategory.getText().toString();
            String itempriceValue = itemprice.getText().toString() + " " + spin.getSelectedItem().toString();
            if (selectedBitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                imgeEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            } else {
                imgeEncoded = null;
            }
            Items items = new Items(itemnameValue,itemcategoryValue,itempriceValue,idText, imgeEncoded);
            databaseReference.child(resultemail).child("Items").child(idText).setValue(items);
            databaseReferencecat.child(resultemail).child("ItemByCategory").child(itemcategoryValue).child(idText).setValue(items);
            itemname.setText("");
            itembarcode.setText("");
            itemprice.setText("");
            itembarcode.setText("");
            imageView.setImageBitmap(null);
            Toast.makeText(additemActivity.this,itemnameValue+"---Added",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(additemActivity.this,"Bar/Qr already exists",Toast.LENGTH_SHORT).show();
        }
    }
}
