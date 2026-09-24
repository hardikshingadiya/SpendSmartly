package com.example.spendsmartly;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Assuming DBHelper is your database helper class for database operations
public class BudgetCategoryAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private DBHelper dbHelper;

    // Constructor
    public BudgetCategoryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        this.dbHelper = new DBHelper(context); // Assuming DBHelper is implemented
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public Object getItem(int position) {
        cursor.moveToPosition(position);
        return cursor;
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getInt(0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.budget_item, parent, false);
        }

        // Find views inside the layout
        TextView categoryName = convertView.findViewById(R.id.budget_CatName);
        Button setBudgetButton = convertView.findViewById(R.id.budget_SetBtn);

        // Move cursor to the current position
        cursor.moveToPosition(position);

        // Retrieve data from cursor
        String name = cursor.getString(1);
        int categoryId = cursor.getInt(0);

        // Set category name
        categoryName.setText(name);

        // Check if the budget is already set
        if (dbHelper.isBudgetSet(categoryId)) { // Implement this method in DBHelper
            convertView.setVisibility(View.GONE);
        } else {
            convertView.setVisibility(View.VISIBLE);
        }

        // Handle "Set Budget" button click
        setBudgetButton.setOnClickListener(v -> {
                showSetBudgetDialog(categoryId,name);
        });

        return convertView;
    }
    /*
    private void showSetBudgetDialog(int categoryId,String categoryName) {
        // Create an AlertDialog builder
        SpannableString title = new SpannableString("Setting "+ categoryName + " Budget");
        title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue)), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        Drawable icon = ContextCompat.getDrawable(context, R.drawable.add_budget);
        if (icon != null) {
            icon = DrawableCompat.wrap(icon);
            DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.blue)); // Change to your desired color
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context,androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);

        // Inflate a custom layout for the dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_budget, null);

        // Get references to input fields in the custom layout
        EditText amountInput = view.findViewById(R.id.amount_input);
        EditText startDateInput = view.findViewById(R.id.start_date_input);
        EditText endDateInput = view.findViewById(R.id.end_date_input);

        // Add date pickers for Start Date and End Date inputs
        startDateInput.setOnClickListener(v -> showDatePickerDialog(startDateInput, true, null));
        endDateInput.setOnClickListener(v -> showDatePickerDialog(endDateInput, false, startDateInput));

        // Set the custom layout to the dialog
        builder.setView(view);
        builder.setTitle(title);
        builder.setIcon(icon);
        // Set buttons for the dialog
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Retrieve input values
            String amountText = amountInput.getText().toString().trim();
            String startDate = startDateInput.getText().toString().trim();
            String endDate = endDateInput.getText().toString().trim();
            // Validation for empty fields
            if (amountText.isEmpty() || startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(context, "Budget NOT Set.. Details Were Missing", Toast.LENGTH_SHORT).show();
                return;
            }
            // Parse the amount
            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid Amount Entered !", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                // Insert the data into the Budgets table
                DBHelper dbHelper = new DBHelper(context);
                boolean setBudget=dbHelper.setBudget(categoryId, amount, startDate, endDate);
                if(setBudget){
                    Toast.makeText(context, "Budget Set Successfully!", Toast.LENGTH_SHORT).show();
                    dbHelper.activateBudget(categoryId);
                }
            }
            catch (Exception e){
                Toast.makeText(context, "Something Went Wrong !", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        // Show the dialog
        builder.create().show();
    }
    */

    private void showSetBudgetDialog(int categoryId, String categoryName) {
        // Create an AlertDialog builder
        SpannableString title = new SpannableString("Setting " + categoryName + " Budget");
        title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue)), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        Drawable icon = ContextCompat.getDrawable(context, R.drawable.add_budget);
        if (icon != null) {
            icon = DrawableCompat.wrap(icon);
            DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.blue)); // Change to your desired color
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert);

        // Inflate a custom layout for the dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_set_budget, null);

        // Get references to input fields in the custom layout
        EditText amountInput = view.findViewById(R.id.amount_input);
        EditText startDateInput = view.findViewById(R.id.start_date_input);
        EditText endDateInput = view.findViewById(R.id.end_date_input);

        // Set start date to today's date and make it read-only
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());
        startDateInput.setText(todayDate);
        startDateInput.setEnabled(false); // Make the field read-only

        // Add a date picker for End Date only
        endDateInput.setOnClickListener(v -> showDatePickerDialog(endDateInput, false, startDateInput));

        // Set the custom layout to the dialog
        builder.setView(view);
        builder.setTitle(title);
        builder.setIcon(icon);

        // Set buttons for the dialog
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Retrieve input values
            String amountText = amountInput.getText().toString().trim();
            String startDate = startDateInput.getText().toString().trim(); // Fixed as today's date
            String endDate = endDateInput.getText().toString().trim();

            // Validation for empty fields
            if (amountText.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(context, "Budget NOT Set.. Details Were Missing", Toast.LENGTH_SHORT).show();
                return;
            }

            // Parse the amount
            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid Amount Entered!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate that End Date is after Start Date
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);

                if (end == null || end.before(start)) {
                    Toast.makeText(context, "Ending date must be in the future of today's date!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(context, "Invalid Date Format!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Insert the data into the Budgets table
                DBHelper dbHelper = new DBHelper(context);
                boolean setBudget = dbHelper.setBudget(categoryId, amount, startDate, endDate);
                if (setBudget) {
                    Toast.makeText(context, "Budget Set Successfully!", Toast.LENGTH_SHORT).show();
                    dbHelper.activateBudget(categoryId);
                }
            } catch (Exception e) {
                Toast.makeText(context, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }



    private void showDatePickerDialog(EditText dateInput, boolean isStartDate, EditText startDateInput) {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Format the selected date as dd/MM/yyyy
            String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);

            // Check for start date or end date logic
            if (isStartDate) {
                // Ensure the selected date is not in the past
                Calendar selectedDateCalendar = Calendar.getInstance();
                selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay);

                if (selectedDateCalendar.before(Calendar.getInstance())) {
                    Toast.makeText(context, "Starting Date Cannot Be Past!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Set the selected start date in the EditText
                dateInput.setText(selectedDate);

            } else {
                // Ensure end date is not before the selected start date
                String startDateText = startDateInput.getText().toString().trim();

                if (startDateText.isEmpty()) {
                    Toast.makeText(context, "Please select the start date first!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Parse the start date
                String[] startDateParts = startDateText.split("/");
                Calendar startDateCalendar = Calendar.getInstance();
                startDateCalendar.set(
                        Integer.parseInt(startDateParts[2]),
                        Integer.parseInt(startDateParts[1]) - 1,
                        Integer.parseInt(startDateParts[0])
                );

                Calendar selectedEndDateCalendar = Calendar.getInstance();
                selectedEndDateCalendar.set(selectedYear, selectedMonth, selectedDay);

                if (selectedEndDateCalendar.before(startDateCalendar)) {
                    Toast.makeText(context, "Ending Date Must Be In Future Of Starting", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Set the selected end date in the EditText
                dateInput.setText(selectedDate);
            }
        }, year, month, day);

        datePickerDialog.show();
    }


}
