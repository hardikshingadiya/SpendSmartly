package com.example.spendsmartly;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Account extends AppCompatActivity {
    CheckBox showPwd;
    Button exportDataBtn;
    EditText userEdittext, passwordEdittext;
    Cursor data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            handleExtras();
            return insets;
        });

        //Exporting Data
        exportDataBtn=findViewById(R.id.exportDataBtn);
        exportDataBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onClick(View view) {
                DBHelper DB=new DBHelper(Account.this);
                data=DB.getAllRecords();
                CSVExporter csvExporter = new CSVExporter(Account.this, data); // Pass `this` as context and your cursor
                csvExporter.exportCSVFile();
            }
        });

        //Bottom Navigation Code
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.account_menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int item_id=item.getItemId();

            if(item_id==R.id.account_menu)
            {
                return true;
            }
            else if(item_id==R.id.analytics_menu)
            {
                startActivity(new Intent(getApplicationContext(), Analytics.class));
                overridePendingTransition(0,1);
                finish();
                return true;
            }
            else if(item_id==R.id.record_menu)
            {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0,1);
                finish();
                return true;
            }
            else if(item_id==R.id.budget_menu)
            {
                startActivity(new Intent(getApplicationContext(), Budget.class));
                overridePendingTransition(0,1);
                finish();
                return true;
            }
            else if(item_id==R.id.categories_menu)
            {
                startActivity(new Intent(getApplicationContext(), categories.class));
                overridePendingTransition(0,1);
                finish();
                return true;
            }
            else{
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent idata) {
        super.onActivityResult(requestCode, resultCode, idata);

        // Pass the result to the CSVExporter class
        if (requestCode == 1) { // 1 matches CREATE_FILE_REQUEST_CODE in CSVExporter
            CSVExporter csvExporter = new CSVExporter(Account.this, data); // cursor
            csvExporter.handleActivityResult(requestCode, resultCode, idata);
        }
    }

    public void handleExtras(){
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            if(extras.getInt("showWarning")==1){
                Toast.makeText(this, "Please have a look at 'THINGS TO BE TAKEN CARE' for better Calculations ", Toast.LENGTH_SHORT).show();
            }
        }
        getIntent().removeExtra("showWarning");
    }

}