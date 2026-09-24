package com.example.spendsmartly;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.myviewHolder>{
    ArrayList<Model> dataHolder;
    Context context;
    TextView msgTextview;

    public MyAdapter(ArrayList<Model> dataHolder,Context context,TextView msgTextview) {
        this.dataHolder = dataHolder;
        this.context = context;
        this.msgTextview = msgTextview;
    }

    @NonNull
    @Override
    public myviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_recyler_item,parent,false);
        return new myviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewHolder holder, int position) {
        int id;
        double amount=dataHolder.get(position).getAmount();
        holder.amount.setText(String.valueOf(amount));
        holder.type.setText(dataHolder.get(position).getType());
        holder.cat.setText(dataHolder.get(position).getCategory());
        holder.desc.setText(dataHolder.get(position).getDesc());
        holder.date.setText(dataHolder.get(position).getDate());
        holder.time.setText(dataHolder.get(position).getTime());

        id=dataHolder.get(position).getId();

        if ("Income".equals(dataHolder.get(position).getType())) {
            holder.type.setTextColor(Color.parseColor("#016E05"));
        } else if ("Expense".equals(dataHolder.get(position).getType())) {
            holder.type.setTextColor(Color.parseColor("#BB2212"));
        }
        if(position%2==1){
            holder.itemView.setBackgroundColor(Color.parseColor("#EDEDED"));
        }else{
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
        //Code To Edit Record By Sending Values Through Intent
        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), Add_Record.class);
                intent.putExtra("uId",dataHolder.get(holder.getAdapterPosition()).getId());
                intent.putExtra("uAmount",dataHolder.get(holder.getAdapterPosition()).getAmount());
                intent.putExtra("uType",dataHolder.get(holder.getAdapterPosition()).getType());
                intent.putExtra("uDesc",dataHolder.get(holder.getAdapterPosition()).getDesc());
                intent.putExtra("uCat",dataHolder.get(holder.getAdapterPosition()).getCategory());
                intent.putExtra("uDate",dataHolder.get(holder.getAdapterPosition()).getDate());
                intent.putExtra("uTime",dataHolder.get(holder.getAdapterPosition()).getTime());
                context.startActivity(intent);
            }
        });

        //Code To DELETE A Record
        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, String.valueOf(id), Toast.LENGTH_SHORT).show();
                SpannableString title = new SpannableString("Delete Confirmation");
                title.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.blue)), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                title.setSpan(new StyleSpan(Typeface.BOLD), 0, title.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                SpannableString message = new SpannableString("Records Cannot Be Restored !");
                message.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.orange)), 0, message.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                Drawable icon = ContextCompat.getDrawable(context, R.drawable.warning);
                if (icon != null) {
                    icon = DrawableCompat.wrap(icon);
                    DrawableCompat.setTint(icon, ContextCompat.getColor(context, R.color.blue)); // Change to your desired color
                }

                new AlertDialog.Builder(context, androidx.appcompat.R.style.Theme_AppCompat_Light_Dialog_Alert).setTitle(title).setMessage(message).setIcon(icon).setPositiveButton("OK",(dialogInterface, i) -> {
                    try {
                        DBHelper DB=new DBHelper(context);
                        String cat=dataHolder.get(holder.getAdapterPosition()).getType();
                        boolean isDeleted=DB.deleteRecord(id);
                        if (isDeleted){
                            int CatID=DB.categoryIdByName(cat);
                            DB.deleteTotalAmountTable(cat,dataHolder.get(holder.getAdapterPosition()).getAmount());
                            DB.updateBudgetDeduct(CatID,dataHolder.get(holder.getAdapterPosition()).getAmount());
                            dataHolder.remove(holder.getAdapterPosition());
                            refreshData(dataHolder);
                            //notifyItemRemoved(holder.getAdapterPosition());
                            //notifyItemRangeChanged(holder.getAdapterPosition(),dataHolder.size());
                            
                            if(dataHolder.isEmpty()){
                                //Toast.makeText(context, "All Deleted", Toast.LENGTH_SHORT).show();
                                msgTextview.setText("No Records At The Moment");
                                msgTextview.setVisibility(View.VISIBLE);
                            }
                        }else{
                            Toast.makeText(context, "Not Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception ex) {
                        //Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        Log.e("Message Setting","Error : "+ ex.getMessage());
                    }
                }).setNegativeButton("Cancle",(dialogInterface, i) -> {
                    dialogInterface.dismiss();}).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }

    class myviewHolder extends RecyclerView.ViewHolder
    {
        TextView amount,type,cat,desc,date,time,editBtn,deleteBtn;
        public myviewHolder(@NonNull View itemView){
            super(itemView);
            amount=itemView.findViewById(R.id.rcv_amount);
            type=itemView.findViewById(R.id.rcv_type);
            cat=itemView.findViewById(R.id.rcv_cat);
            desc=itemView.findViewById(R.id.rcv_desc);
            date=itemView.findViewById(R.id.rcv_date);
            time=itemView.findViewById(R.id.rcv_time);
            editBtn=itemView.findViewById(R.id.recordEditBtn);
            deleteBtn=itemView.findViewById(R.id.recordDeleteBtn);
        }
    }
    public void refreshData(ArrayList<Model> dataHolder) {
        this.dataHolder = dataHolder;
        notifyDataSetChanged(); // Forces the adapter to rebind all items
    }

}