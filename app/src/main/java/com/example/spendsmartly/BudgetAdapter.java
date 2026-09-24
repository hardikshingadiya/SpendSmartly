package com.example.spendsmartly;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BudgetAdapter extends BaseAdapter {
    private Context context;
    private Cursor cursor;
    private DBHelper dbHelper;

    // Constructor
    public BudgetAdapter(Context context, Cursor cursor) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.budget_display, parent, false);
        }
        TextView categoryName=convertView.findViewById(R.id.categoryName);
        TextView startDate=convertView.findViewById(R.id.startDate);
        TextView endDate=convertView.findViewById(R.id.endDate);
        TextView usedAmount=convertView.findViewById(R.id.usedAmount);
        TextView totalamount=convertView.findViewById(R.id.totalAmount);
        TextView remainedAmount=convertView.findViewById(R.id.remainingAmount);
        ProgressBar progressBar=convertView.findViewById(R.id.progress_bar);
        //progressBar.getProgressDrawable().setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);

        cursor.moveToPosition(position);
        int CategoryId=cursor.getInt(1);
        Double totalAmount=cursor.getDouble(2);
        Double UsedAmount=cursor.getDouble(3);
        String StartDate=cursor.getString(4);
        String EndDate=cursor.getString(5);

        double per=UsedAmount*100/totalAmount;
        int progress = (int) Math.round(per); // Convert to int

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date endDateParsed = sdf.parse(EndDate); // Parse endDate string into Date object
            if (endDateParsed != null) {
                // Get the current date (set time to 00:00:00 for accurate comparison)
                Calendar currentDate = Calendar.getInstance();
                currentDate.set(Calendar.HOUR_OF_DAY, 0);
                currentDate.set(Calendar.MINUTE, 0);
                currentDate.set(Calendar.SECOND, 0);
                currentDate.set(Calendar.MILLISECOND, 0);

                // Compare current date with end date
                if (endDateParsed.before(currentDate.getTime()) || endDateParsed.equals(currentDate.getTime())) {
                    // Call DeActivateBudget if EndDate is today or before
                    dbHelper.DeActivateBudget(CategoryId);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String cat_name=dbHelper.categoryNameById(CategoryId);
        categoryName.setText(cat_name);

        startDate.setText(StartDate);
        endDate.setText(EndDate);
        usedAmount.setText("Used "+UsedAmount.toString());
        totalamount.setText("Limit "+totalAmount.toString());
        Double remainAmount=totalAmount-UsedAmount;
        remainedAmount.setText("Balance "+remainAmount.toString());
        progressBar.setProgress(progress); // Set progress

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, cat_name, Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
