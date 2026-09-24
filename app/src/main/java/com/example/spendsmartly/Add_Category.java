package com.example.spendsmartly;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
public class Add_Category extends AppCompatActivity {
    DBHelper dbHelper;
    String r_a,r_d;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Dealing with Extras..
        Bundle extras=getIntent().getExtras();
        if(extras != null){
            r_a=extras.getString("amount");
            r_d=extras.getString("desc");
        }

        //Bottom Navigation Code...
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.categories_menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int item_id=item.getItemId();

            if(item_id==R.id.categories_menu)
            {
                Intent i=new Intent(getApplicationContext(), categories.class);
                startActivity(i);
                overridePendingTransition(0,1);
                finish();
                return true;
            }
            else if(item_id==R.id.record_menu)
            {
                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
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

        //Adding A Category
        TextView add_cat_name,add_cat_desc;
        add_cat_name=findViewById(R.id.add_cat_name);
        add_cat_desc=findViewById(R.id.add_cat_desc);
        RadioGroup add_cat_type=findViewById(R.id.add_cat_type);
        dbHelper=new DBHelper(this);

        Button add_cat_btn=findViewById(R.id.add_cat_btn);
        Button cancel_cat_btn=findViewById(R.id.cancel_cat_btn);

        //Adding New Category
        add_cat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected_id=add_cat_type.getCheckedRadioButtonId();
                RadioButton add_cat_type_selected=findViewById(selected_id);
                String newCategoryType=add_cat_type_selected.getText().toString();
                String newCategoryName=add_cat_name.getText().toString();
                String newCategoryDesc=add_cat_desc.getText().toString();

                if(newCategoryName.trim().isEmpty())
                {
                    add_cat_name.requestFocus();
                    add_cat_name.setError("Category Name Is Not Valid");
                }
                else if(newCategoryDesc.trim().isEmpty())
                {
                    add_cat_desc.requestFocus();
                    add_cat_desc.setError("Category Description Is Not Valid");
                }
                else{
                    Boolean category_added=dbHelper.AddCategory(newCategoryName,newCategoryDesc,newCategoryType,"False");
                    if(category_added) {
                        Toast.makeText(Add_Category.this, "Category Added !", Toast.LENGTH_SHORT).show();
                        add_cat_name.setText(null);
                        add_cat_desc.setText(null);
                        //findViewById(R.id.income_newCatType).setSelected(true);
                        add_cat_type.check(R.id.income_newCatType);
                    }
                    else
                        Toast.makeText(Add_Category.this, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
                    }
                }
        });

        //Cancel to add Category
        cancel_cat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}