package com.example.spendsmartly;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.OutputStream;
import java.io.PrintWriter;
public class ExportData {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void exportDataToCSV(Context context, Cursor cursor) {
        try {
            // File Name and Directory
            String fileName = "SpendSmartlyData.csv";

            // Setting up the Download folder path
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            // Get ContentResolver and create file
            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                OutputStream outputStream = resolver.openOutputStream(uri);
                PrintWriter writer = new PrintWriter(outputStream);

                // Writing the CSV header
                writer.println("Amount,Type,Category,Description,Date");

                // Writing the data rows
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int recordId = cursor.getInt(cursor.getColumnIndexOrThrow("Record_id"));
                        double recordAmount = cursor.getDouble(cursor.getColumnIndexOrThrow("Record_amount"));
                        String recordType = cursor.getString(cursor.getColumnIndexOrThrow("Record_type"));
                        String recordCategory = cursor.getString(cursor.getColumnIndexOrThrow("Record_category"));
                        String recordDesc = cursor.getString(cursor.getColumnIndexOrThrow("Record_desc"));
                        String recordDate = cursor.getString(cursor.getColumnIndexOrThrow("Record_date"));

                        // Writing each row into the CSV
                        writer.println(recordAmount + "," + recordType + "," +
                                recordCategory + "," + recordDesc + "," + recordDate);
                    }
                    cursor.close();
                }

                // Closing the writer
                writer.flush();
                writer.close();
                outputStream.close();

                // Success Toast
                Toast.makeText(context, "'SpendSmartlyData.csv' File exported to Downloads", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Failed to create file in Downloads folder", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error exporting data: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}