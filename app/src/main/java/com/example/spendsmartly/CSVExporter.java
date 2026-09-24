package com.example.spendsmartly;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.widget.Toast;
import java.io.OutputStream;
import java.io.PrintWriter;
import android.database.Cursor;

public class CSVExporter {

    private static final int CREATE_FILE_REQUEST_CODE = 1;
    private Context context;
    private Cursor cursor;

    public CSVExporter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public void exportCSVFile() {
        // Create a file picker intent
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("text/csv");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        String defaultFileName = "SpendSmartlyData.csv";
        intent.putExtra(Intent.EXTRA_TITLE, defaultFileName);
        ((Activity) context).startActivityForResult(intent, CREATE_FILE_REQUEST_CODE);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                writeCSVToFile(uri);
            } else {
                Toast.makeText(context, "File creation canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void writeCSVToFile(Uri uri) {
        try {
            OutputStream outputStream = context.getContentResolver().openOutputStream(uri);
            PrintWriter writer = new PrintWriter(outputStream);

            // Writing the CSV header
            writer.println("Amount,Type,Category,Description,Date");

            // Writing data rows
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    double recordAmount = cursor.getDouble(1);
                    String recordType = cursor.getString(2);
                    String recordCategory = cursor.getString(3);
                    String recordDesc = cursor.getString(4);
                    String recordDate = cursor.getString(5);

                    String csvRow = recordAmount + "," + recordType + "," +
                            recordCategory + "," + recordDesc + "," + recordDate;

                    writer.println(csvRow);
                } while (cursor.moveToNext());
            }
            writer.flush();
            writer.close();
            if (outputStream != null) {
                outputStream.close();
            }

            Toast.makeText(context, "CSV File Exported Successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to Export: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}