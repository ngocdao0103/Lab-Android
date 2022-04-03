package com.example.admin.augscan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

public class attendancetrackingActivity extends AppCompatActivity {
//    private EditText itemname,itemcategory,itemprice;
    private EditText idname,idmssv,idclass;
    private TextView itembarcode;
//    private Spinner spin;
//    private FirebaseAuth firebaseAuth;
    private String idText,idStudentCode,idName,idClass;
//    private String finaluser;
//    private String resultemail;
    public static TextView resulttextview;
    Button updatesv,scanbutton;
//    DatabaseReference databaseReference;
//    DatabaseReference databaseReferencecat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_tracking);

        resulttextview = findViewById(R.id.barcodeview);
        updatesv = findViewById(R.id.updateattendance);
        idname = findViewById(R.id.namestudent);
        idmssv= findViewById(R.id.studentcode);
        idclass = findViewById(R.id.idclass);
        itembarcode= findViewById(R.id.barcodeview);
        scanbutton = findViewById(R.id.buttonscan);

        ///
        scanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanCodeUsed.class));
            }
        });
        //
        updatesv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 additem();
            }
        });
    }
    // addding item to databse
    public  void additem(){
        //TODO
        idText = itembarcode.getText().toString();
        idStudentCode = idmssv.getText().toString();
        idName = idname.getText().toString();
        idClass = idclass.getText().toString();
        if (idStudentCode.isEmpty()) {
            idmssv.setError("It's Empty");
            idmssv.requestFocus();
            return;
        }
        if (idText.isEmpty()) {
            itembarcode.setError("It's Empty");
            itembarcode.requestFocus();
            return;
        }
        {
            new sendRequest().execute();
            Toast.makeText(attendancetrackingActivity.this,"Success",Toast.LENGTH_SHORT).show();
            itembarcode.setText("");//
            idname.setText("");//
            idmssv.setText("");//
            idclass.setText("");//

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
//                URL url = new URL("https://script.google.com/macros/s/AKfycbxY9bRwEC-KCUJa4QRdtD2Aj3Utsf04ct0l76G_P76Fx32Xscs/exec");
                URL url = new URL("https://script.google.com/macros/s/AKfycbyb76Hmpfn_XeYbdIe4hHO4uq8oSbNXw7qzbCpjLPhIjfOB5FIXiNL5l-sUTfWyiUNu/exec");
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("Time_Update", currentDateandTime);

                postDataParams.put("Code_Id_Student", idStudentCode);

                postDataParams.put("Name", idName);
                postDataParams.put("Class", idClass);

                postDataParams.put("Item_Code_Used", idText);

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
