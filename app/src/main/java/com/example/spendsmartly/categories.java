package com.example.spendsmartly;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;

public class categories extends AppCompatActivity {
    DBHelper DB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_categories);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            getCategoriesList();
            return insets;
        });
        Button catTonewAdd=findViewById(R.id.categoryToaddcategory);
        catTonewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(categories.this, Add_Category.class);
                startActivity(i);
            }
        });

        //Bottom Navigation Code...
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.categories_menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int item_id=item.getItemId();

            if(item_id==R.id.categories_menu)
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
            else if(item_id==R.id.account_menu)
            {
                startActivity(new Intent(getApplicationContext(), Account.class));
                overridePendingTransition(0,1);
                finish();
                return true;
            }
            else{
                return false;
            }
        });
    }

    protected void getCategoriesList()
    {
        DBHelper DB=new DBHelper(categories.this);
        //ListView Categories Setting..
        ListView listView=findViewById(R.id.Categories_List);
        ArrayList<HashMap<String, String>> categoryList = DB.getAllCategories();
        // Set the adapter
        CategoryAdapter adapter=new CategoryAdapter(categories.this,categoryList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCategoriesList();
    }
}