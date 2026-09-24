package com.example.spendsmartly;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Analytics extends AppCompatActivity {

    private BarChart barChart;
    private PieChart pieChart,pieChart2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_analytics);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            barChart = findViewById(R.id.barChart);
            pieChart = findViewById(R.id.pieChart);
            pieChart2 = findViewById(R.id.pieChart2);
            // Populate the chart
            setupBarChart();
            setupPiechart("Income",pieChart);
            setupPiechart("Expense",pieChart2);
            return insets;
        });

        //Bottom Navigation Code
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.analytics_menu);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int item_id=item.getItemId();

            if(item_id==R.id.analytics_menu)
            {
                return true;
            }
            else if(item_id==R.id.account_menu)
            {
                startActivity(new Intent(getApplicationContext(), Account.class));
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

    private void setupBarChart() {
        // Create entries for the chart
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        DBHelper DB = new DBHelper(this);
        Cursor cursor = DB.getMainTotals();
        if (cursor != null && cursor.moveToFirst()) {
            barEntries.add(new BarEntry(1f, (float) cursor.getDouble(0))); // Example: Income
            barEntries.add(new BarEntry(2f, (float) cursor.getDouble(1))); // Example: Expenses
            barEntries.add(new BarEntry(3f, (float) cursor.getDouble(2))); // Example: Savings
        } else {
            barEntries.add(new BarEntry(1f, 0)); // Example: Income
            barEntries.add(new BarEntry(2f, 0)); // Example: Expenses
            barEntries.add(new BarEntry(3f, 0)); // Example: Savings
        }
        if (cursor != null) {
            cursor.close();
        }

        final String[] labels = new String[]{"","Total Income","Current Balance","Total Expense"};

        // Create a DataSet
        BarDataSet barDataSet = new BarDataSet(barEntries, "Analytics");
        barDataSet.setColors(new int[]{R.color.green, R.color.blue, R.color.red}, this); // Set colors
        barDataSet.setValueTextSize(16f);

        // Bind the DataSet to the Chart
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        // Customize chart (optional)
        barChart.getDescription().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        barChart.animateY(1000);

        // Refresh the chart
        barChart.invalidate();
    }

    private void setupPiechart(String type,PieChart pc)
    {
        DBHelper dbHelper = new DBHelper(this);
        List<Map<String, Object>> categoryTotals = dbHelper.getTopCategoriesForMonths(3,type);

        // Prepare data for the pie chart
        List<PieEntry> pieEntries = new ArrayList<>();
        for (Map<String, Object> entry : categoryTotals) {
            String category = (String) entry.get("category");
            double total = (double) entry.get("total");
            pieEntries.add(new PieEntry((float) total, category));
        }

        // Create the PieDataSet
        PieDataSet pieDataSet = new PieDataSet(pieEntries,null);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Use predefined color templates
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(14f);

        // Create the PieData
        PieData pieData = new PieData(pieDataSet);

        // Configure the PieChart
        pc.setData(pieData);
        //pieChart.setUsePercentValues(true); // Optional: Show percentages
        pc.setDescription(null); // Remove description
        pc.setEntryLabelColor(Color.BLACK);
        pc.setEntryLabelTextSize(6f);
        pc.setCenterText(type+" Analytics");
        pc.setCenterTextSize(15f);
        pc.animateY(1000);

        // Refresh the chart
        pc.invalidate();
    }
}