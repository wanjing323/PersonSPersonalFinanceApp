package my.edu.utar.person;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {

    private DatabaseReference expensesReference;
    private ArrayList<Expenses> expensesList;
    private CategoryActivity activity;

    public CategoryRecyclerAdapter(ArrayList<Expenses> expensesList, CategoryActivity activity) {
        this.expensesList = expensesList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView desc;
        ImageView image;
        TextView amount;
        TextView datetv;

        ViewHolder(View view) {
            super(view);
            datetv=view.findViewById(R.id.dateCatSpend);
            desc=view.findViewById(R.id.descriptionCatSpend);
            //image=view.findViewById(R.id.imageTrans);
            amount=view.findViewById(R.id.amtCatSpend);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Expenses expenses=expensesList.get(position);
        String text1=expenses.getExpDate();
        holder.datetv.setText(expenses.getExpDate());
        holder.desc.setText(expenses.getDescription());
        holder.amount.setText("- RM "+expenses.getAmount());
    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public Context getContext() {
        return activity;
    }



}
