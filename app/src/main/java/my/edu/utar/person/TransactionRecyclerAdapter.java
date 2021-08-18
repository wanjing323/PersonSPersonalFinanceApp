package my.edu.utar.person;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.os.Looper.getMainLooper;

public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder>{
    private DatabaseReference expensesReference;
    private StorageReference storageReference;
    private ArrayList<Expenses> expensesList;
    private TransactionActivity activity;


    public TransactionRecyclerAdapter(ArrayList<Expenses> expensesList, TransactionActivity activity) {
        //this.targetDate=targetDate;
        this.expensesList = expensesList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category;
        TextView desc;
        ImageView image;
        TextView amount;
        TextView date;

        ViewHolder(View view) {
            super(view);
            category=view.findViewById(R.id.categoryTrans);
            desc=view.findViewById(R.id.descriptionTrans);
            image=view.findViewById(R.id.imageTrans);
            amount=view.findViewById(R.id.amtTrans);
            date=view.findViewById(R.id.dateTrans);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Expenses expenses=expensesList.get(position);
        final Handler handler = new Handler(getMainLooper());
        if(expenses.getImageId().equals("N/A"))
        {
                    holder.image.setVisibility(View.GONE);
                    holder.category.setText(expenses.getExpensesCategory());
                    holder.desc.setText(expenses.getDescription());
                    holder.amount.setText("- RM" + expenses.getAmount());
                    holder.date.setText(expenses.getExpDate());
        }else {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    {
                        storageReference = FirebaseStorage.getInstance().getReference("ExpensesImages").child(expenses.getImageId());
                        try {
                            final File localFile = File.createTempFile("image", "jpg");
                            storageReference.getFile(localFile)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    holder.image.setVisibility(View.VISIBLE);
                                                    holder.image.setImageBitmap(bitmap);
                                                    holder.category.setText(expenses.getExpensesCategory());
                                                    holder.desc.setText(expenses.getDescription());
                                                    holder.amount.setText("- RM" + expenses.getAmount());
                                                    holder.date.setText(expenses.getExpDate());
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(activity, "Error Occured", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            };
            Thread myThread = new Thread(runnable);
            myThread.start();
        }
    }

    @Override
    public int getItemCount() {
        return expensesList.size();
    }

    public Context getContext() {
        return activity;
    }

}
