package com.example.admin.augscan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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
    private EditText idname,idmssv,idclass;
    private TextView itemBarcode;
    private String idText,idStudentCode,idName,idClass;
    public static TextView resultTextView;
    Button updateSV, scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_tracking);
        resultTextView = findViewById(R.id.barcodeview);
        updateSV = findViewById(R.id.updateattendance);
        idname = findViewById(R.id.namestudent);
        idmssv= findViewById(R.id.studentcode);
        idclass = findViewById(R.id.idclass);
        itemBarcode = findViewById(R.id.barcodeview);
        scanButton = findViewById(R.id.buttonscan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ScanCodeUsed.class));
            }
        });
        updateSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 addItem();
            }
        });
    }

    public  void addItem(){
        idText = itemBarcode.getText().toString();
        idStudentCode = idmssv.getText().toString();
        idName = idname.getText().toString();
        idClass = idclass.getText().toString();
        if (idStudentCode.isEmpty()) {
            idmssv.setError("It's Empty");
            idmssv.requestFocus();
            return;
        }
        if (idText.isEmpty()) {
            itemBarcode.setError("It's Empty");
            itemBarcode.requestFocus();
            return;
        }
        {
            new sendRequest().execute();
            Toast.makeText(attendancetrackingActivity.this,"Success",Toast.LENGTH_SHORT).show();
            itemBarcode.setText("");
            idname.setText("");
            idmssv.setText("");
            idclass.setText("");
        }
    }
    public class sendRequest extends AsyncTask<String, Void, String> {
        protected void onPreExecute() { }
        @RequiresApi(api = Build.VERSION_CODES.N)
        protected String doInBackground(String... arg0) {
            try {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
                String currentDateAndTime = sdf.format(new Date());
                URL url = new URL("https://script.google.com/macros/s/AKfycbyb76Hmpfn_XeYbdIe4hHO4uq8oSbNXw7qzbCpjLPhIjfOB5FIXiNL5l-sUTfWyiUNu/exec");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("Time_Update", currentDateAndTime);
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
                    StringBuffer stringBuffer = new StringBuffer("");
                    String line = "";
                    while((line = in.readLine()) != null) {
                        stringBuffer.append(line);
                        break;
                    }
                    in.close();
                    return stringBuffer.toString();
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
