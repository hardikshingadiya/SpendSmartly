package com.example.spendsmartly;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Calendar;

public class Add_Record extends AppCompatActivity {
    Spinner spinner;
    Boolean spinnerRefreshed;
    Button saveRecordBtn,cancleRecordBtn;
    EditText record_date,record_amount,record_desc,record_time;
    DBHelper DB;
    DatePickerDialog.OnDateSetListener setListener;
    int u_id;
    double u_amount;
    String u_type,u_cat,u_desc,u_date,u_time;
    ArrayAdapter<String> adpater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_record);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            setCategoriesToSpinner();
            return insets;
        });

        record_amount=findViewById(R.id.record_amount);
        record_date=findViewById(R.id.record_date);
        record_time=findViewById(R.id.record_time);
        RadioGroup newRecordRG=findViewById(R.id.newRecord_Radiogroup);
        record_desc=findViewById(R.id.record_desc);
        spinner=findViewById(R.id.record_category);
        DB=new DBHelper(Add_Record.this);
        spinnerRefreshed=true;


        //Setting Categories According to Income or Expense..
        newRecordRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton selectedType=findViewById(i);
                String CategoryType=selectedType.getText().toString();
                //Toast.makeText(Add_Record.this, CategoryType, Toast.LENGTH_SHORT).show();
                spinner=findViewById(R.id.record_category);
                ArrayList<String> cat=DB.getCategoriesByType(CategoryType);
                adpater=new ArrayAdapter<>(Add_Record.this, R.layout.spinner_layout,cat);
                adpater.setDropDownViewResource(R.layout.spinner_dropdown_layout);
                spinner.setAdapter(adpater);
            }
        });

        //Getting Date Chosen By User
        record_date.setFocusable(false); //Avoiding user to type date manually..

        Calendar calendar=Calendar.getInstance();
        final int year=calendar.get(Calendar.YEAR);
        final int month=calendar.get(Calendar.MONTH)+1;
        final int day=calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        //String d=day+"/"+month+"/"+year;
        String date=String.format("%02d/%02d/%d",day,month,year);
        record_date.setText(date);
        String time = String.format("%02d:%02d", hour, minute);
        record_time.setText(time);


        record_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass original month (no +1) to DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(Add_Record.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month-1 , day);
                datePickerDialog.getWindow();
                datePickerDialog.show();
            }
        });
        setListener= (datePicker, year1, month1, day1) -> {
            // Format for display (increment month here)
            String date1 = String.format("%02d/%02d/%d", day1, month1 + 1, year1);
            record_date.setText(date1);
        };

        //Getting time
        record_time=findViewById(R.id.record_time);
        record_time.setOnClickListener(v -> showTimePickerDialog());

        //Checking if Updating or not
        Bundle ex=getIntent().getExtras();
        if(ex != null)
        {
            u_id=ex.getInt("uId");
            u_amount=ex.getDouble("uAmount");
            u_type=ex.getString("uType");
            u_desc=ex.getString("uDesc");
            u_cat=ex.getString("uCat");
            u_date=ex.getString("uDate");
            u_time=ex.getString("uTime");
            SpinnerAdapter ad=spinner.getAdapter();
            if(u_id>0)
            {
                TextView t=findViewById(R.id.newrecordHeading);
                t.setText("Edit Record");
                Button b=findViewById(R.id.save_record_btn);
                b.setText("Save Edit");
                record_amount.setText(String.valueOf(u_amount));
                record_desc.setText(u_desc);
                record_date.setText(u_date);
                record_time.setText(u_time);

                //setting radiobutton
                for (int i=0;i<newRecordRG.getChildCount();i++){
                    View view=newRecordRG.getChildAt(i);
                    if(view instanceof RadioButton){
                        RadioButton radioButton=(RadioButton) view;
                        if(radioButton.getText().toString().equals(u_type)){
                            radioButton.setChecked(true);
                        }
                    }
                }

                //Setting Spinner
                spinner.post(()-> {
                    for (int i = 0; i < spinner.getAdapter().getCount(); i++) {
                        String item=spinner.getAdapter().getItem(i).toString();
                        if(item.equals(u_cat)) {
                            spinner.setSelection(i);
                            break;
                        }
                    }
                });
            }
        }

        //Inserting A Record..
        saveRecordBtn=findViewById(R.id.save_record_btn);
        saveRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount=record_amount.getText().toString();
                int type=newRecordRG.getCheckedRadioButtonId();
                RadioButton type_radiobutton=findViewById(type);
                String r_type=type_radiobutton.getText().toString();
                String r_desc=record_desc.getText().toString();
                Object obj=spinner.getSelectedItem();
                String r_category=obj.toString();
                String r_date=record_date.getText().toString();
                String r_time=record_time.getText().toString();
                double r_amount= Double.parseDouble(amount);
                
                if(amount.trim().isEmpty() || r_amount <= 0){
                    record_amount.requestFocus();
                    record_amount.setError("Amount is not Valid");
                }
                else if(r_desc.trim().isEmpty()) {
                    record_desc.requestFocus();
                    record_desc.setError("Description is not Valid");
                }
                else{
                    try {
                        double newAmount;
                        int id=DB.categoryIdByName(r_category);
                        //Updating if Updation Request
                        if(saveRecordBtn.getText().equals("Save Edit")){
                            boolean isUpdated=DB.updateRecord(u_id,r_amount,r_type,r_desc,r_category,r_date,r_time);
                            if(isUpdated){
                                if(r_amount>u_amount){
                                    newAmount=r_amount-u_amount;
                                    DB.updateBudget(id,newAmount);
                                    if(r_type.equals("Income")) {
                                        DB.updateTotalAmountTable(false,newAmount);
                                    }
                                    else{
                                        DB.updateTotalAmountTable(true,newAmount);
                                    }
                                }
                                else if(r_amount<u_amount){
                                    newAmount=u_amount-r_amount;
                                    DB.updateBudgetDeduct(id,newAmount);
                                    if(r_type.equals("Income")) {
                                        DB.updateTotalAmountTable(true,newAmount);
                                    }
                                    else{
                                        DB.updateTotalAmountTable(false,newAmount);
                                    }
                                }
                                else{}
                                Intent i=new Intent(Add_Record.this, MainActivity.class);
                                i.putExtra("Updated",1);
                                startActivity(i);
                            }
                            else
                                Toast.makeText(Add_Record.this, "Record NOT Edited !", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            boolean isRecordAdded=DB.addRecord(r_amount,r_type,r_category,r_desc,r_date,r_time);
                            if(isRecordAdded){
                                if(r_type.equals("Income")){
                                    DB.updateTotalAmountTable(false,r_amount);
                                }
                                else{
                                    DB.updateTotalAmountTable(true,r_amount);
                                }
                                record_amount.setText("");
                                record_desc.setText("");
                                setCategoriesToSpinner();
                                int category=DB.categoryIdByName(r_category);
                                DB.updateBudget(category,r_amount);
                                Toast.makeText(Add_Record.this, "Record Saved Successfully !", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(Add_Record.this, "Record NOT Saved !", Toast.LENGTH_SHORT).show();
                        }

                    }
                    catch (Exception e){
                        Toast.makeText(Add_Record.this, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Cancle Adding A Record..
        cancleRecordBtn=findViewById(R.id.cancel_record_btn);
        cancleRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //New Category From Records..
        TextView nCat=findViewById(R.id.textView3);
        nCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Add_Record.this, Add_Category.class);
                startActivity(intent);
            }
        });

        //Canceling REFRESH of Spinner
        record_desc.setOnFocusChangeListener((view, b) -> {
            if(b)
                spinnerRefreshed=false;
            else
                spinnerRefreshed=true;
        });

        record_amount.setOnFocusChangeListener((view, b) -> {
            if(b)
                spinnerRefreshed=false;
            else
                spinnerRefreshed=true;
        });

        //Bottom Navigation Code...
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.record_menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int item_id=item.getItemId();

            if(item_id==R.id.record_menu)
            {
                startActivity(new Intent(this, MainActivity.class));
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

    public void setCategoriesToSpinner(){
        //ByDefault Categories..
        if(!spinnerRefreshed){
            return;
        }
        RadioGroup rg=findViewById(R.id.newRecord_Radiogroup);
        int selected_id=rg.getCheckedRadioButtonId();
        RadioButton selectedType=findViewById(selected_id);
        String CategoryType=selectedType.getText().toString();
        //Toast.makeText(Add_Record.this, CategoryType, Toast.LENGTH_SHORT).show();
        spinner=findViewById(R.id.record_category);
        ArrayList<String> cat=DB.getCategoriesByType(CategoryType);
        ArrayAdapter<String> adpater=new ArrayAdapter<>(Add_Record.this, R.layout.spinner_layout,cat);
        adpater.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spinner.setAdapter(adpater);
    }

    @Override
    protected void onResume() {
        super.onResume();
        spinnerRefreshed=true;
        setCategoriesToSpinner();
    }

    private void showTimePickerDialog() {
        // Get current time
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (TimePicker view, int selectedHour, int selectedMinute) -> {
                    // Format the time as "HH:mm" and set it in the EditText
                    String time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    record_time.setText(time);
                }, hour, minute, true); // Use true for 24-hour format, false for AM/PM

        timePickerDialog.show();
    }
}