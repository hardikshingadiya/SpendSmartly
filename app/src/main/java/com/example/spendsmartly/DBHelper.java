package com.example.spendsmartly;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context) {
        super(context, "SpendSmartly.db", null, 1);
        if (isTableEmpty()){
            addDefaultCategories();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        DB.execSQL("CREATE TABLE Categories (" + "Category_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "Category_name TEXT UNIQUE, " + "Category_type TEXT, " + "Category_desc TEXT, " + "Category_isBudgetActivate TEXT DEFAULT 'False', " + "Category_default TEXT DEFAULT 'False');");
        DB.execSQL("CREATE TABLE Records (" + "Record_id INTEGER PRIMARY KEY AUTOINCREMENT, " + "Record_amount REAL, " + "Record_type TEXT, " + "Record_category TEXT," + "Record_desc TEXT," + "Record_date TEXT," + "Record_time TEXT"+");");
        DB.execSQL("CREATE TABLE Budgets (" + "Budget_Id INTEGER PRIMARY KEY AUTOINCREMENT, " + "B_Category_Id INTEGER NOT NULL, " + "Budget_Amount REAL NOT NULL, " + "Used_Amount REAL DEFAULT 0, " + "Start_Date DATE NOT NULL, " + "End_Date DATE NOT NULL, " + "FOREIGN KEY (B_Category_Id) REFERENCES Categories(Category_Id)" + ");");
        DB.execSQL("CREATE TABLE MainTotals(" + "TotalIncome REAL DEFAULT 0, " + "TotalBalance REAL DEFAULT 0, " + "TotalExpense REAL DEFAULT 0," + "IsTotalSet TEXT DEFAULT 'False' " +");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("Drop Table If Exists Categories");
        DB.execSQL("Drop Table If Exists Records");
        DB.execSQL("Drop Table If Exists Budgets");
        onCreate(DB);
    }

    public boolean addRecord(double record_amount,String record_type,String record_cat,String desc,String record_date,String record_time){
        SQLiteDatabase DB=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Record_amount",record_amount);
        contentValues.put("Record_type",record_type);
        contentValues.put("Record_category",record_cat);
        contentValues.put("Record_desc",desc);
        contentValues.put("Record_date",record_date);
        contentValues.put("Record_time",record_time);

        long result=DB.insert("Records",null,contentValues);
        //updateTotalAmountTable();
        DB.close();
        return result != -1;
    }

    public boolean deleteRecord(int record_id){
        SQLiteDatabase DB=this.getWritableDatabase();
        int row_deleted=DB.delete("Records","Record_id=?",new String[]{String.valueOf(record_id)});
        //updateTotalAmountTable();
        DB.close();
        return (row_deleted>0);
    }

    public boolean deleteCategory(int cat_id){
        SQLiteDatabase DB=this.getWritableDatabase();
        int row_deleted=DB.delete("Categories","Category_id=?",new String[]{String.valueOf(cat_id)});
        //updateTotalAmountTable();
        DB.close();
        return (row_deleted>0);
    }

    public Cursor getAllRecords(){
        SQLiteDatabase DB=this.getWritableDatabase();
        String query="SELECT * FROM Records ORDER BY Record_id DESC";
        Cursor cursor=DB.rawQuery(query,null);
        return cursor;
    }

    public boolean updateRecord(int id,double newAmount,String newType,String newDesc,String newCat,String newDate,String newTime){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("Record_amount", newAmount);
        values.put("Record_type", newType);
        values.put("Record_category", newCat);
        values.put("Record_desc", newDesc);
        values.put("Record_date", newDate);
        values.put("Record_time", newTime);
        int rowsAffected = db.update("Records", values, "Record_id=?",new String[]{String.valueOf(id)});
        //updateTotalAmountTable();
        return (rowsAffected>0);
    }

    public Cursor FilteredRecords(String query)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        return cursor;
    }

    public boolean isTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Categories", null);
        boolean isEmpty = false;
        if (cursor.moveToFirst()) {
            isEmpty = cursor.getInt(0) == 0; // Table is empty if the count is 0
        }
        cursor.close();
        return isEmpty;
    }

    public void addDefaultCategories() {
        // Adding default categories - Alphabetically sorted
        AddCategory("Business", "Business profits", "Income", "True");
        AddCategory("Gifts", "Cash Gifts and Other", "Income", "True");
        AddCategory("Investments", "Investments Income Like Share, IPO", "Income", "True");
        AddCategory("Salary", "Monthly salary", "Income", "True");
        AddCategory("Side Income", "Any Side Income Except Salary or Business", "Income", "True");

        AddCategory("Bills", "Bills like Rent, Tax etc", "Expense", "True");
        AddCategory("Entertainment", "Entertainment like Movies, Picnic", "Expense", "True");
        AddCategory("Groceries", "Monthly groceries", "Expense", "True");
        AddCategory("Health", "Health Expense like Doctor Fees, Medicines", "Expense", "True");
        AddCategory("Travel", "Expense for Travel like Bus Ticket etc", "Expense", "True");

    }

    public String categoryNameById(int CategoryId){
        SQLiteDatabase DB = this.getReadableDatabase();
        String query = "SELECT Category_name FROM Categories WHERE Category_id = ?";
        Cursor cursor = DB.rawQuery(query, new String[]{String.valueOf(CategoryId)});
        String categoryName = null;

        if (cursor.moveToFirst()) {
            categoryName = cursor.getString(0);
        }
        cursor.close();
        return categoryName;
    }
    public int categoryIdByName(String CategoryName){
        SQLiteDatabase DB = this.getReadableDatabase();
        String query = "SELECT Category_id FROM Categories WHERE Category_name = ?";
        Cursor cursor = DB.rawQuery(query, new String[]{CategoryName});
        int categoryId = -1;

        if (cursor.moveToFirst()) {
            categoryId = cursor.getInt(0);
        }
        cursor.close();
        return categoryId;
    }

    public boolean AddCategory(String catName,String catDesc,String catType,String isDefault)
    {
        SQLiteDatabase DB=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put("Category_name",catName);
        contentValues.put("Category_type",catType);
        contentValues.put("Category_desc",catDesc);
        contentValues.put("Category_default",isDefault);

        long result=DB.insert("Categories",null,contentValues);
        DB.close();
        return result != -1;
    }

    public void updateBudget(int CategoryId,double usedAmount)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query="Update Budgets Set Used_Amount = Used_Amount + ? Where B_Category_id=?";
        SQLiteStatement statement=db.compileStatement(query);
        statement.bindDouble(1,usedAmount);
        statement.bindLong(2,CategoryId);
        statement.executeUpdateDelete();
        db.close();
        //db.update("Categories", values, "Category_name=?",new String[]{String.valueOf(usedAmount)});
    }

    public void updateBudgetDeduct(int CategoryId,double usedAmount)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String query="Update Budgets Set Used_Amount = Used_Amount - ? Where B_Category_id=?";
        SQLiteStatement statement=db.compileStatement(query);
        statement.bindDouble(1,usedAmount);
        statement.bindLong(2,CategoryId);
        statement.executeUpdateDelete();
        db.close();
        //db.update("Categories", values, "Category_name=?",new String[]{String.valueOf(usedAmount)});
    }

    public ArrayList<String> getCategoriesByType(String type) {
        ArrayList<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Category_name FROM Categories WHERE Category_type=? ORDER BY Category_name", new String[]{type});
        if (cursor.moveToFirst()) {
            do {
                String UniqueCat=cursor.getString(0); // Column Index 0 for Category_name
                if (!categories.contains(UniqueCat))
                    categories.add(UniqueCat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categories;
    }

    public ArrayList<HashMap<String, String>> getAllCategories() {
        ArrayList<HashMap<String, String>> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT Category_name, Category_type, Category_desc , Category_default , Category_id FROM " + "Categories ORDER BY Category_name", null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> category = new HashMap<>();
                category.put("Category_name", cursor.getString(0));
                category.put("Category_type", cursor.getString(1));
                category.put("Category_desc", cursor.getString(2));
                category.put("Category_default", cursor.getString(3));
                category.put("Category_id", cursor.getString(4));
                categoryList.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categoryList;
    }

    public Cursor ExpenseCategoriesWithId() {
        SQLiteDatabase DB=this.getWritableDatabase();
        String query="SELECT * FROM Categories WHERE Category_type='Expense' and Category_isBudgetActivate='False' ORDER BY Category_name";
        Cursor cursor=DB.rawQuery(query,null);
        return cursor;
    }


    public boolean isBudgetSet(int categoryId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Budgets WHERE B_Category_Id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(categoryId)});
        boolean isSet = cursor.getCount() > 0;
        cursor.close();
        return isSet;
    }

    public boolean setBudget(int categoryId, double amount, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("B_Category_Id", categoryId);
        values.put("Budget_Amount", amount);
        values.put("Used_Amount", 0); // Initially, 0 is used
        values.put("Start_Date", startDate);
        values.put("End_Date", endDate);

        long no=db.insert("Budgets", null, values);
        db.close();
        return no != -1;
    }

    public void activateBudget(int Category_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Category_isBudgetActivate", "True");
        db.update("Categories", values, "Category_id=?",new String[]{String.valueOf(Category_id)});
    }

    public Cursor getBudgets()
    {
        SQLiteDatabase DB=this.getWritableDatabase();
        String query="SELECT * FROM Budgets ORDER BY Budget_Id DESC";
        Cursor cursor=DB.rawQuery(query,null);
        return cursor;
    }

    public void DeActivateBudget(int Category_id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("Category_isBudgetActivate", "False");
        db.update("Categories",values, "Category_id=?",new String[]{String.valueOf(Category_id)});
        db.delete("Budgets","B_Category_Id=?",new String[]{String.valueOf(Category_id)});
    }

    public List<Map<String, Object>> getTopCategoriesForMonths(int months,String type) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Calculate start date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -months);
        Date startDate = calendar.getTime();
        //String r_type=type;

        // Format start date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String startDateString = dateFormat.format(startDate);

        // Query for category totals
        String query = "SELECT Record_category, SUM(Record_amount) AS total_amount " +
                "FROM Records " +
                "WHERE Record_type=? and strftime('%Y-%m-%d', substr(Record_date, 7, 4) || '-' || substr(Record_date, 4, 2) || '-' || substr(Record_date, 1, 2)) >= ? " +
                "GROUP BY Record_category " +
                "ORDER BY total_amount DESC";

        Cursor cursor = db.rawQuery(query, new String[]{type,startDateString});

        // Process results
        List<Map<String, Object>> results = new ArrayList<>();
        double othersTotal = 0;
        int count = 0;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                double totalAmount = cursor.getDouble(1);

                if (count < 4) {
                    // Top 4 categories
                    Map<String, Object> item = new HashMap<>();
                    item.put("category", category);
                    item.put("total", totalAmount);
                    results.add(item);
                } else {
                    // Sum remaining totals for "Others"
                    othersTotal += totalAmount;
                }

                count++;
            } while (cursor.moveToNext());

            cursor.close();
        }

        // Add "Others" category if applicable
        if (othersTotal > 0) {
            Map<String, Object> othersItem = new HashMap<>();
            othersItem.put("category", "Others");
            othersItem.put("total", othersTotal);
            results.add(othersItem);
        }

        db.close();
        return results;
    }

    public void insertInitialTotal(double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Check if the table is empty
        String query = "SELECT COUNT(*) FROM MainTotals";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst() && cursor.getInt(0) == 0) {
            // Insert total if no record exists
            ContentValues values = new ContentValues();
            values.put("TotalBalance", totalAmount);
            values.put("IsTotalSet","True");
            db.insert("MainTotals", null, values);
        }
        cursor.close();
        db.close();
    }


    public boolean isTotalSet() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT IsTotalSet FROM MainTotals";
        Cursor cursor = db.rawQuery(query, null);
        boolean isSet = false;

        if (cursor.moveToFirst()) {
            isSet = cursor.getString(0).equals("True"); // Check if count is greater than 0
        }
        cursor.close();
        db.close();
        return isSet;
    }
    public Cursor getMainTotals()
    {
        SQLiteDatabase DB=this.getWritableDatabase();
        String query="SELECT * FROM MainTotals";
        Cursor cursor=DB.rawQuery(query,null);
        return cursor;
    }

    public void updateTotalAmountTable(boolean isDeducting,double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        // SQL query to update TotalAmount table with calculated totals
        String query=null;
        if (!isDeducting) {
            query = "UPDATE MainTotals " +
                    "SET " +
                    "TotalIncome = (SELECT COALESCE(SUM(Record_amount), 0) FROM Records WHERE Record_type = 'Income'), " +
                    "TotalExpense = (SELECT COALESCE(SUM(Record_amount), 0) FROM Records WHERE Record_type = 'Expense'), " +
                    "TotalBalance = TotalBalance + " + amount;
        } else {
            query = "UPDATE MainTotals " +
                    "SET " +
                    "TotalIncome = (SELECT COALESCE(SUM(Record_amount), 0) FROM Records WHERE Record_type = 'Income'), " +
                    "TotalExpense = (SELECT COALESCE(SUM(Record_amount), 0) FROM Records WHERE Record_type = 'Expense'), " +
                    "TotalBalance = TotalBalance - " + amount;
        }
        try {
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void deleteTotalAmountTable(String type, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = null;

        if (type.equals("Income")) {
            // Correctly subtract the income amount and update the balance
            query = "UPDATE MainTotals " +
                    "SET " +
                    "TotalIncome = TotalIncome - " + amount + ", " +
                    "TotalBalance = TotalBalance - " + amount;
        } else if (type.equals("Expense")) {
            // Correctly subtract the expense amount and update the balance
            query = "UPDATE MainTotals " +
                    "SET " +
                    "TotalExpense = TotalExpense - " + amount + ", " +
                    "TotalBalance = TotalBalance + " + amount;
        }

        try {
            Log.d("SQLQuery", "Executing query: " + query); // Debugging log to verify query
            db.execSQL(query);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }



}