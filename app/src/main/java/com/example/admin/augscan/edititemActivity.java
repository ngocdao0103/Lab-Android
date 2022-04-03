package com.example.admin.augscan;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class edititemActivity extends AppCompatActivity {
    private EditText itemname, itemcategory, itemprice;
    private TextView itembarcode;
    private ImageView img;
    private Button btnChoose,btndelete;
    private FirebaseAuth firebaseAuth;
    public static TextView resulttextview;
    private Spinner spin;
    Button edititembuttontodatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferencecat;
    private String itemNameBefore, itemCategoryBefore, itemPriceBefore, itemBarcode;
    public String itemNameAfter, itemCategoryAfter, itemPriceAfter;
    private String unitText;
    Bitmap selectedBitmap;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edititem);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
       //  databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        // databaseReferencecat = FirebaseDatabase.getInstance().getReference("Users");
        String finaluser = users.getEmail();
        String resultemail = finaluser.replace(".","");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultemail);
        databaseReferencecat = FirebaseDatabase.getInstance().getReference("Users").child(resultemail);

        resulttextview = findViewById(R.id.barcodeview);
        edititembuttontodatabase = findViewById(R.id.edititembuttontodatabase);
        itemname = findViewById(R.id.edititemname);
        itemcategory = findViewById(R.id.editcategory);
        itemprice = findViewById(R.id.editprice);
        itembarcode = findViewById(R.id.barcodeview);
        spin = findViewById(R.id.spinnerUnitEdit);
        img = findViewById(R.id.chooseImg);
        btnChoose = findViewById(R.id.btnChoose);
        btndelete = findViewById(R.id.btnDelete);

        Intent n = getIntent();
        // TODO get value
        String getItemBarCode = n.getStringExtra("itembarcode");
        String getItemName = n.getStringExtra("itemname");
        String getItemCategory = n.getStringExtra("itemcategory");
        String getItemPrice = n.getStringExtra("itemprice");
        String[] parts = getItemPrice.split("\\s", 0);
        unitText = parts[1];
        itembarcode.setText(getItemBarCode);
        itemname.setText(getItemName);
        itemcategory.setText(getItemCategory);
        itemprice.setText(parts[0]);

        Query firebaseSearchQuery = databaseReference.child("Items").child(getItemBarCode).child("itemimg");
        firebaseSearchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Img = dataSnapshot.getValue(String.class);
                if(dataSnapshot.exists()) {
                    byte[] decodedString = Base64.decode(Img, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    img.setImageBitmap(decodedByte);
                    url = Img;
                } else if (!dataSnapshot.exists()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setImageBitmap(null);
                selectedBitmap = null;
                unitText = null;
            }
        });


        itemNameBefore = itemname.getText().toString();
        itemCategoryBefore = itemcategory.getText().toString();
        itemPriceBefore = itemprice.getText().toString() + " " + unitText;
        itemBarcode = itembarcode.getText().toString();

        // TODO load spinner
        String[] spinnerUnit = { "g","ml", "ống", "đĩa", "bình", "cái", "chai", "Khác" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerUnit);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        if (unitText != null) {
            int spinnerPosition = adapter.getPosition(unitText);
            spin.setSelection(spinnerPosition);
        }
        edititembuttontodatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                additem();
            }
        });
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    // TODO add img
    private void chooseImage() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , 200);//one can be replaced with any action code
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100&& resultCode == RESULT_OK) {
            selectedBitmap = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(selectedBitmap);
        } else if(requestCode == 200&& resultCode == RESULT_OK) {
            try {
                Uri imageUri = data.getData();
                selectedBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                img.setImageBitmap(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void additem() {
        //TODO add log history
        itemNameAfter  = itemname.getText().toString();
        itemCategoryAfter = itemcategory.getText().toString();
        itemPriceAfter = itemprice.getText().toString() + " " + spin.getSelectedItem().toString();

        String itemnameValue = itemname.getText().toString();
        String itemcategoryValue = itemcategory.getText().toString();
        String itempriceValue = itemprice.getText().toString() + " " + spin.getSelectedItem().toString();
        String itembarcodeValue = itembarcode.getText().toString();

        if (itembarcodeValue.isEmpty()) {
            itembarcode.setError("It's Empty");
            itembarcode.requestFocus();
            return;
        }

        String imgeEncoded;
        if (selectedBitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            imgeEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            if (unitText != null) {
                imgeEncoded = url;
            } else {
                imgeEncoded = null;
            }
        }

        if (!TextUtils.isEmpty(itemnameValue) && !TextUtils.isEmpty(itemcategoryValue) && !TextUtils.isEmpty(itempriceValue)) {

            // TODO
            Items items = new Items(itemnameValue, itemcategoryValue, itempriceValue, itembarcodeValue, imgeEncoded);
            databaseReference.child("Items").child(itembarcodeValue).setValue(items);
            databaseReferencecat.child("ItemByCategory").child(itemcategoryValue).child(itembarcodeValue).setValue(items);

            itemname.setText("");
            itembarcode.setText("");
            itemprice.setText("");
            itembarcode.setText("");
            Toast.makeText(edititemActivity.this, "Edited", Toast.LENGTH_SHORT).show();
            new sendRequest().execute();
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(edititemActivity.this, "Please Fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    public class sendRequest extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        protected String doInBackground(String... arg0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                String currentDateandTime = sdf.format(new Date());

                URL url = new URL("https://script.google.com/macros/s/AKfycbxKix31zwEInK1w6J-QbnkPSTqcSYAbS0jwJCcoBrpeXKEYss2Oyi1WcpztEksuvwHw/exec");
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("Time", currentDateandTime);

                postDataParams.put("Number_Barcode", itemBarcode);

                postDataParams.put("Product_Before", itemNameBefore);
                postDataParams.put("Product_After", itemNameAfter);

                postDataParams.put("Category_Before", itemCategoryBefore);
                postDataParams.put("Category_After", itemCategoryAfter);

                postDataParams.put("Quantity_Before", itemPriceBefore);
                postDataParams.put("Quantity_After", itemPriceAfter);

                postDataParams.put("Note", "Note");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, StandardCharsets.UTF_8));
                writer.write(getPostDataString(postDataParams));
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";
                    while((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                } else {
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }



        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(getApplicationContext(), result,
//                    Toast.LENGTH_LONG).show();
        }
    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}

