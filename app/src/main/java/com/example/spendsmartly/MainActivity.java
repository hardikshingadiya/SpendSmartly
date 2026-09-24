package com.example.spendsmartly;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Transition;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addfloatbtn;
    TextView no_record_msg,totalIncomes,totalExpenses,mainBalance;
    RecyclerView rcv;
    ArrayList<Model> dataHolder;

    Spinner filterSpinner;

    private DBHelper DB;
    private SharedPreferences sharedPreferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            LoadRecords(this);
            setAlarm();
            setMainTotals();
            //viewOfWarning();
            return insets;
        });

        DB=new DBHelper(this);
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        if(!sharedPreferences.getBoolean("firstRun",false) && !DB.isTotalSet())
        {
            showInputDialog();
            //sharedPreferences.edit().putBoolean("firstRun",false).apply();
        }

        //Filtering Data
        TextView filterHeading=findViewById(R.id.recordsHeading);
        filterSpinner=findViewById(R.id.filter_spinner);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected=adapterView.getItemAtPosition(i).toString();
                if(selected.equals("Newest")){
                    setFilteredData(DB.getAllRecords());
                    filterHeading.setText("All Records");
                } else if (selected.equals("Oldest")) {
                    setFilteredData(DB.FilteredRecords("SELECT * FROM Records ORDER BY Record_id"));
                    filterHeading.setText("Filtered : Oldest");
                }
                else if(selected.equals("Incomes")){
                    setFilteredData(DB.FilteredRecords("SELECT * FROM Records WHERE Record_type='Income' ORDER BY Record_id"));
                    filterHeading.setText("Filtered : Incomes");
                }
                else{
                    setFilteredData(DB.FilteredRecords("SELECT * FROM Records WHERE Record_type='Expense' ORDER BY Record_id"));
                    filterHeading.setText("Filtered : Expenses");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        //Setting Main Totals
        totalIncomes=findViewById(R.id.totalIncomes);
        mainBalance=findViewById(R.id.mainBalance);
        totalExpenses=findViewById(R.id.totalExpenses);

        //Floating Button To Add Records
        addfloatbtn=findViewById(R.id.addrecordFloatBtn);
        addfloatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, Add_Record.class);
                startActivity(i);
            }
        });

        //Filters Spinner Adapter
        Spinner filter=findViewById(R.id.filter_spinner);
        String[] filterArr={"Newest","Oldest","Incomes","Expenses"};
        ArrayAdapter<String> spinnerAdapter=new ArrayAdapter<>(this, R.layout.spinner_layout,filterArr);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        filter.setAdapter(spinnerAdapter);

        //Bottom Navigation Code...
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.record_menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int item_id=item.getItemId();

            if(item_id==R.id.record_menu)
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
    
    //Handling Bundle Extras..
    public void handleExtras()
    {
        Bundle extras=getIntent().getExtras();
        if(extras!=null){
            if(extras.getInt("Updated")==1){
                Toast.makeText(this, "Record Edited", Toast.LENGTH_SHORT).show();
            }
        }
        getIntent().removeExtra("Updated");
    }

    public void LoadRecords(Context context){
        rcv=findViewById(R.id.records_recycler);
        no_record_msg=findViewById(R.id.no_record_msg);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        dataHolder=new ArrayList<>();
        try {
            Cursor cursor=new DBHelper(this).getAllRecords();
            if(cursor.getCount()==0){
                no_record_msg.setVisibility(View.VISIBLE);
                rcv.setVisibility(View.INVISIBLE);
            }
            else{
                no_record_msg.setVisibility(View.INVISIBLE);
                rcv.setVisibility(View.VISIBLE);

                while (cursor.moveToNext()){
                    Model obj=new Model(cursor.getInt(0),cursor.getDouble(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),no_record_msg);
                    dataHolder.add(obj);
                }
                MyAdapter myAdapter=new MyAdapter(dataHolder,this,no_record_msg);
                rcv.setAdapter(myAdapter);
            }
        }catch (Exception e){
            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }

    public void setFilteredData(Cursor cursor) {
        rcv = findViewById(R.id.records_recycler);
        no_record_msg = findViewById(R.id.no_record_msg);
        rcv.setLayoutManager(new LinearLayoutManager(this));

        try {
            // Initialize dataHolder if not already initialized
            if (dataHolder == null) {
                dataHolder = new ArrayList<>();
            } else {
                dataHolder.clear(); // Clear existing data if reusing the method
            }

            if (cursor.getCount() == 0) {
                no_record_msg.setText("No Records");
                no_record_msg.setVisibility(View.VISIBLE);
                rcv.setVisibility(View.INVISIBLE);
            } else {
                no_record_msg.setVisibility(View.INVISIBLE);
                rcv.setVisibility(View.VISIBLE);

                // Move to first record in the cursor
                while (cursor.moveToNext()) {
                    Model obj = new Model(
                            cursor.getInt(0),
                            cursor.getDouble(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6),
                            no_record_msg
                    );
                    dataHolder.add(obj);  // Add data to the list
                }

                MyAdapter myAdapter = new MyAdapter(dataHolder, this, no_record_msg);
                rcv.setAdapter(myAdapter);
            }

        } catch (Exception e) {
            // Print the exception to Logcat for debugging
            e.printStackTrace();
            Toast.makeText(this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
        } finally {
            // Always close the cursor to avoid memory leaks
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        LoadRecords(this);
        handleExtras();
    }

    private void setAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationService.class); // Your existing BroadcastReceiver
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        // Set alarm time to 7 PM today or next day if it's already past 7 PM
        if (calendar.get(Calendar.HOUR_OF_DAY) >= 21) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    public void showInputDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this,androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle("Enter Total Initial Amount");
        builder.setMessage("Please Add Current Amount You Have.. This Amount Will Not Be Edited !");
        builder.setIcon(R.drawable.spendsmartly);
        builder.setCancelable(false);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Numeric input
        input.requestFocus();
        input.setHint("Your Current Amount");
        input.setTextColor(Color.parseColor("Black"));
        
        builder.setView(input);

// Prevent dialog from closing if input is empty
        builder.setPositiveButton("OK", null); // Set null for custom behavior

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String inputValue = input.getText().toString().trim();

                if (inputValue.isEmpty()) {
                    input.setError("Amount cannot be empty");
                } else {
                    // Perform actions with the input value
                    double totalAmount = Double.parseDouble(inputValue);
                    sharedPreferences.edit().putBoolean("firstRun",true).apply();
                    // Save the total amount in the database
                    DBHelper dbHelper = new DBHelper(MainActivity.this);
                    dbHelper.insertInitialTotal(totalAmount);
                    viewOfWarning();
                    //Toast.makeText(MainActivity.this, "Initial Amount Set: " + totalAmount, Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Close the dialog only if input is valid
                }
            });
        });

        dialog.show();

    }

    public void setMainTotals() {
        DBHelper DB = new DBHelper(this);
        Cursor cursor = DB.getMainTotals();
        if (cursor != null && cursor.moveToFirst()) {
            // Ensure you access the cursor after moving it to the first position
            totalIncomes.setText(String.valueOf(cursor.getDouble(0))); // Column 0
            mainBalance.setText(String.valueOf(cursor.getDouble(1))); // Column 1
            totalExpenses.setText(String.valueOf(cursor.getDouble(2))); // Column 2
        } else {
            // Handle case where no data exists in the table
            totalIncomes.setText("0");
            mainBalance.setText("0");
            totalExpenses.setText("0");
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public void viewOfWarning()
    {
        try{
            Cursor cursor=new DBHelper(this).getAllRecords();
            if(cursor.getCount()==0){
                Intent i=new Intent(this, Account.class);
                i.putExtra("showWarning",1);
                startActivity(i);
            }
        }
        catch (Exception e){
            Toast.makeText(this, "Something Went Wrong ! Processing..", Toast.LENGTH_SHORT).show();
        }
    }

}