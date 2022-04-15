package com.example.admin.augscan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class dashboardActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    TextView firebaseNameView;
    private CardView addItems, deleteItems, scanItems, viewInventory;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        firebaseNameView = findViewById(R.id.firebasename);
        Button ButtonAttendance = findViewById(R.id.BtnAttendance);
        ButtonAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl("https://docs.google.com/spreadsheets/d/15FiDxwkwonTdD4JCLjKhNo8hkQl0LOkyqe07dHS7Bjk/edit#gid=0");
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finalUser = users.getEmail();
        String result = finalUser.substring(0, finalUser.indexOf("@"));
        String resultEmail = result.replace(".", "");
        firebaseNameView.setText("Welcome, " + resultEmail);
        addItems = findViewById(R.id.addItems);
        deleteItems = findViewById(R.id.deleteItems);
        scanItems = findViewById(R.id.scanItems);
        viewInventory = findViewById(R.id.viewInventory);

        addItems.setOnClickListener(this);
        deleteItems.setOnClickListener(this);
        scanItems.setOnClickListener(this);
        viewInventory.setOnClickListener(this);
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.addItems:
                i = new Intent(this, additemActivity.class);
                startActivity(i);
                break;
            case R.id.deleteItems:
                i = new Intent(this, deleteItemsActivity.class);
                startActivity(i);
                break;
            case R.id.scanItems:
                i = new Intent(this, scanItemsActivity.class);
                startActivity(i);
                break;
            case R.id.viewInventory:
                i = new Intent(this, viewInventoryActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    private void Logout() {
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(dashboardActivity.this, MainActivity.class));
        Toast.makeText(dashboardActivity.this, "LOGOUT SUCCESSFUL", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            Logout();
        }
        return super.onOptionsItemSelected(item);
    }
}
