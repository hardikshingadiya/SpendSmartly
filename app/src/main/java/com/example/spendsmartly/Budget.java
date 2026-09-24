package com.example.spendsmartly;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashMap;

public class Budget extends AppCompatActivity {

    ListView budgetLessList,budgetList;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_budget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            //Get Updated BudgetLess Categories
            getLatestBudgetsLess();
            getLatestBudgetsList();
            return insets;
        });

        //Bottom Navigation Code...
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.budget_menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int item_id=item.getItemId();

            if(item_id==R.id.budget_menu)
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
            else if(item_id==R.id.categories_menu)
            {
                startActivity(new Intent(getApplicationContext(),categories.class));
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

    public void getLatestBudgetsLess(){
        Cursor cursor=new DBHelper(this).ExpenseCategoriesWithId();
        budgetLessList=findViewById(R.id.category_list);
        BudgetCategoryAdapter budgetCategoryAdapter=new BudgetCategoryAdapter(this, cursor);
        budgetLessList.setAdapter(budgetCategoryAdapter);
    }

    public void getLatestBudgetsList(){
        Cursor cursor=new DBHelper(this).getBudgets();
        budgetList=findViewById(R.id.budget_list);
        BudgetAdapter budgetAdapter=new BudgetAdapter(this,cursor);
        budgetList.setAdapter(budgetAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLatestBudgetsLess();
        getLatestBudgetsList();
    }
}