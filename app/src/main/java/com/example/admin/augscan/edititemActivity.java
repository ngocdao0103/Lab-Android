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
import android.util.Base64;
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
    private EditText itemName, itemCategory, itemPrice, itemYear, itemOrigin, itemStatus;
    private TextView itembarcode;
    private ImageView img;
    private Button btnChoose, btnDelete;
    private FirebaseAuth firebaseAuth;
    public static TextView resultTextView;
    private Spinner spin, spinnerCategory, spinnerHc;
    Button editItemButtonToDatabase;
    DatabaseReference databaseReference;
    DatabaseReference databaseReferenceCat;
    private String itemNameBefore, itemCategoryBefore, itemPriceBefore, itemBarcode;
    public String itemNameAfter, itemCategoryAfter, itemPriceAfter;
    private String unitText, categoryText, hcText;
    Bitmap selectedBitmap;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edititem);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finalUser = users.getEmail();
        String resultEmail = finalUser.replace(".","");
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultEmail);
        databaseReferenceCat = FirebaseDatabase.getInstance().getReference("Users").child(resultEmail);

        resultTextView = findViewById(R.id.barcodeview);
        editItemButtonToDatabase = findViewById(R.id.edititembuttontodatabase);
        itemName = findViewById(R.id.edititemname);
        itemPrice = findViewById(R.id.editprice);
        itemYear = findViewById(R.id.textYear);
        itemOrigin = findViewById(R.id.textOrigin);
        itemStatus = findViewById(R.id.textStatus);
        itembarcode = findViewById(R.id.barcodeview);
        spin = findViewById(R.id.spinnerUnitEdit);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        spinnerHc = findViewById(R.id.spinnerHC);
        img = findViewById(R.id.chooseImg);
        btnChoose = findViewById(R.id.btnChoose);
        btnDelete = findViewById(R.id.btnDelete);

        Intent n = getIntent();
        String getItemBarCode = n.getStringExtra("itemBarcode");
        String getItemName = n.getStringExtra("itemName");
        String getItemCategory = n.getStringExtra("itemCategory");
        String getItemPrice = n.getStringExtra("itemPrice");
        String getItemYear = n.getStringExtra("itemYear");
        String getItemOrigin = n.getStringExtra("itemOrigin");
        String getItemStatus = n.getStringExtra("itemStatus");
        String[] parts = getItemPrice.split("\\s", 0);
        String[] cateText = getItemCategory.split("-");
        if (cateText.length == 2 || parts.length == 2) {
            categoryText = cateText[0];
            hcText = cateText[1];
            itemPrice.setText(parts[0]);
            unitText = parts[1];
            spinnerHc.setVisibility(View.GONE);

        } else {
            categoryText = cateText[0];
            unitText = parts[0].trim();
            itemPrice.setText(null);
        }
        itembarcode.setText(getItemBarCode);
        itemName.setText(getItemName);
        itemYear.setText(getItemYear);
        itemOrigin.setText(getItemOrigin);
        itemStatus.setText(getItemStatus);

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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setImageBitmap(null);
                selectedBitmap = null;
                unitText = null;
            }
        });

        itemNameBefore = getItemName;
        itemCategoryBefore = getItemCategory;
        itemPriceBefore = itemPrice.getText().toString() + " " + unitText;
        itemBarcode = getItemBarCode;

        String[] spinnerUnit = { "g","ml", "ống", "đĩa", "bình", "cái", "chai", "Khác" };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerUnit);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        if (unitText != null) {
            int spinnerPosition = adapter.getPosition(unitText);
            spin.setSelection(spinnerPosition);
        }

        String[] spinnerCate = {"Thiết Bị", "Hóa Chất"};
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerCate);
        adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);
        if (categoryText != null) {
            int spinnerPosition = adapterCategory.getPosition(categoryText);
            spinnerCategory.setSelection(spinnerPosition);
        }

        String[] spinnerHC = {"Rắn", "Lỏng", "Khí"};
        ArrayAdapter<String> adapterHC = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerHC);
        adapterHC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHc.setAdapter(adapterHC);
        if(hcText != null) {
            int spinnerPosition = adapterHC.getPosition(hcText);
            spinnerHc.setSelection(spinnerPosition);
        }

        editItemButtonToDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem();
            }
        });
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
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

    public void addItem() {
        String itemNameValue = itemName.getText().toString();
        String itemCategoryValue;
        if (spinnerCategory.getSelectedItem().toString().equals("Hóa Chất")){
            itemCategoryValue = spinnerCategory.getSelectedItem().toString() + "-" + spinnerHc.getSelectedItem().toString();
        } else {
            itemCategoryValue = spinnerCategory.getSelectedItem().toString();
        }
        String itemPriceValue = itemPrice.getText().toString() + " " + spin.getSelectedItem().toString();
        String itemBarcodeValue = itembarcode.getText().toString();
        String itemYearValue = itemYear.getText().toString();
        String itemStatusValue = itemStatus.getText().toString();
        String itemOriginValue = itemOrigin.getText().toString();

        itemNameAfter  = itemNameValue;
        itemCategoryAfter = itemCategoryValue;
        itemPriceAfter = itemPriceValue;

        if (itemBarcodeValue.isEmpty()) {
            itembarcode.setError("It's Empty");
            itembarcode.requestFocus();
            return;
        }

        String imgEncoded;
        if (selectedBitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            imgEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            if (unitText != null) {
                imgEncoded = url;
            } else {
                imgEncoded = null;
            }
        }
        Items items = new Items(
                itemNameValue,
                itemCategoryValue,
                itemPriceValue,
                itemBarcodeValue,
                imgEncoded,
                itemYearValue,
                itemOriginValue,
                itemStatusValue
        );
        databaseReference.child("Items").child(itemBarcodeValue).setValue(items);
        databaseReferenceCat.child("ItemByCategory").child(itemCategoryValue).child(itemBarcodeValue).setValue(items);

        itemName.setText("");
        itembarcode.setText("");
        itemPrice.setText("");
        itembarcode.setText("");
        itemYear.setText("");
        itemOrigin.setText("");
        itemStatus.setText("");
        Toast.makeText(edititemActivity.this, "Edited", Toast.LENGTH_SHORT).show();
        new sendRequest().execute();
        super.onBackPressed();
        finish();
    }

    public class sendRequest extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
        }
        @RequiresApi(api = Build.VERSION_CODES.N)
        protected String doInBackground(String... arg0) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                String currentDateAndTime = sdf.format(new Date());

                URL url = new URL("https://script.google.com/macros/s/AKfycbxKix31zwEInK1w6J-QbnkPSTqcSYAbS0jwJCcoBrpeXKEYss2Oyi1WcpztEksuvwHw/exec");
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("Time", currentDateAndTime);

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

