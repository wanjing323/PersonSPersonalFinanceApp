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

public class TransactionRecyclerAdapter2 extends RecyclerView.Adapter<TransactionRecyclerAdapter.ViewHolder> {

    private DatabaseReference incomeReference;
    private ArrayList<Income> incomeList;
    private TransactionActivity2 activity;
    private StorageReference storageReference;

    public TransactionRecyclerAdapter2(ArrayList<Income> incomeList, TransactionActivity2 activity) {
        this.incomeList = incomeList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public TransactionRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_layout, parent, false);
        return new TransactionRecyclerAdapter.ViewHolder(itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category;
        TextView desc;
        ImageView image;
        TextView amount;
        TextView date;

        ViewHolder(View view) {
            super(view);
            category = view.findViewById(R.id.categoryTrans);
            desc = view.findViewById(R.id.descriptionTrans);
            image = view.findViewById(R.id.imageTrans);
            amount = view.findViewById(R.id.amtTrans);
            date = view.findViewById(R.id.dateTrans);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull final TransactionRecyclerAdapter.ViewHolder holder, int position) {
        Income income=incomeList.get(position);
        final Handler handler = new Handler(getMainLooper());
        final Handler handler2 = new Handler(getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if(income.getImageid().equals("N/A"))
                {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.image.setVisibility(View.GONE);
                            holder.category.setText(income.getIncomeCategory());
                            holder.desc.setText(income.getDescription());
                            holder.amount.setText("+ RM" + income.getIncome());
                            holder.date.setText(income.getDate());

                        }
                    });
                }else {

                    storageReference = FirebaseStorage.getInstance().getReference("IncomeImages").child(income.getImageid());
                    try {

                        final File localFile = File.createTempFile("image", "jpg");
                        storageReference.getFile(localFile)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                                        handler2.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.image.setVisibility(View.VISIBLE);
                                                holder.image.setImageBitmap(bitmap);
                                                holder.category.setText(income.getIncomeCategory());
                                                holder.desc.setText(income.getDescription());
                                                holder.amount.setText("- RM" + income.getIncome());
                                                holder.date.setText(income.getDate());
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

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public Context getContext() {
        return activity;
    }
}
