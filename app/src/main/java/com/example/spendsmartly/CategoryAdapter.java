package com.example.spendsmartly;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.spendsmartly.R;

        import java.util.ArrayList;
        import java.util.HashMap;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> categoryList;

    public CategoryAdapter(Context context, ArrayList<HashMap<String, String>> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        TextView tvCategoryName = convertView.findViewById(R.id.cat_name_layout);
        TextView tvCategoryType = convertView.findViewById(R.id.cat_type_layout);
        TextView tvCategoryDesc = convertView.findViewById(R.id.cat_desc_layout);
        TextView delOption = convertView.findViewById(R.id.deleteBtn);

        HashMap<String, String> category = categoryList.get(position);

        tvCategoryName.setText(category.get("Category_name"));
        tvCategoryType.setText(category.get("Category_type"));
        tvCategoryDesc.setText(category.get("Category_desc"));
        String CatId = category.get("Category_id");
        String isDefault= category.get("Category_default");
        int CatIdInt = Integer.parseInt(CatId);

        if (isDefault.equals("True")) {
            delOption.setVisibility(View.GONE);
        } else {
            delOption.setVisibility(View.VISIBLE);
            delOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SpannableString title = new SpannableString("Delete Confirmation");
                    title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue)), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    SpannableString message = new SpannableString("Category Cannot Be Restored !");
                    message.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.orange)), 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    Drawable icon = ContextCompat.getDrawable(context, R.drawable.warning);
                    if (icon != null) {
                        icon = DrawableCompat.wrap(icon);
                        DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.blue)); // Change to your desired color
                    }

                    new AlertDialog.Builder(context, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert).setTitle(title).setMessage(message).setIcon(icon).setPositiveButton("OK", (dialogInterface, i) -> {
                        try {
                            DBHelper DB = new DBHelper(context);
                            boolean isDeleted = DB.deleteCategory(CatIdInt);
                            if (isDeleted) {
                                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Not Deleted", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            //Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                            Log.e("Message Setting", "Error : " + ex.getMessage());
                        }
                    }).setNegativeButton("Cancle", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    }).show();
                }
            });
        }
            return convertView;
        }
}
