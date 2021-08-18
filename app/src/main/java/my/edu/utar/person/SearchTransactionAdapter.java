package my.edu.utar.person;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class SearchTransactionAdapter extends FirebaseRecyclerAdapter<Expenses,SearchTransactionAdapter.myViewHolder> {

    //    /**
//     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
//     * {@link FirebaseRecyclerOptions} for configuration options.
//     *
//     * @param options
//     */
    public SearchTransactionAdapter(FirebaseRecyclerOptions<Expenses> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(myViewHolder holder, int position, Expenses expenses) {
        if(expenses.getImageId().equals("N/A")) {
            holder.image.setVisibility(View.GONE);
            holder.category.setText(expenses.getExpensesCategory());
            holder.desc.setText(expenses.getDescription());
            holder.amount.setText("- RM" + expenses.getAmount());
            holder.date.setText(expenses.getExpDate());
        }
    }

    @Override
    public myViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_layout, parent, false);
        return new myViewHolder(view);
    }

    class myViewHolder extends RecyclerView.ViewHolder {
        TextView category;
        TextView desc;
        ImageView image;
        TextView amount;
        TextView date;

        public myViewHolder(View itemView) {
            super(itemView);
            category = (TextView)itemView.findViewById(R.id.categoryTrans);
            desc = (TextView)itemView.findViewById(R.id.descriptionTrans);
            image = (ImageView)itemView.findViewById(R.id.imageTrans);
            amount = (TextView)itemView.findViewById(R.id.amtTrans);
            date = (TextView)itemView.findViewById(R.id.dateTrans);
        }
    }
}