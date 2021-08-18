package my.edu.utar.person;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recycleAdapter extends RecyclerView.Adapter<recycleAdapter.viewHolder> {

    private Context context;
    private ArrayList<TimeNotification> dataList;
    private SQLiteAdapter sqLiteDatabase;

    public recycleAdapter(Context context, ArrayList<TimeNotification> dataList){
        this.context = context;
        this.dataList = dataList;
        sqLiteDatabase = new SQLiteAdapter(context);
    }

    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list, null, false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {

        TimeNotification notifications = dataList.get(position);
        holder.tvTitle.setText(notifications.getTitle());
        holder.tvContent.setText(notifications.getContent());
        if (notifications.getMin() < 10) {
            holder.tvTime.setText(Integer.toString(notifications.getHour()) + ":0" + Integer.toString(notifications.getMin()));
        }else{
            holder.tvTime.setText(Integer.toString(notifications.getHour()) + ":" + Integer.toString(notifications.getMin()));
        }
        //holder.tvTime.setText(Integer.toString(notifications.getHour()) + ":" + Integer.toString(notifications.getMin()));
        holder.tvDate.setText(Integer.toString(notifications.getYear()) + "/" + Integer.toString(notifications.getMonth()) + "/" + Integer.toString(notifications.getDay()));
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDatabase.deleteNotificationList(notifications.getId());
                ((Activity) context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime, tvDate;
        ImageView imgDelete, img;

        public viewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.l_title);
            tvContent = itemView.findViewById(R.id.l_content);
            tvTime = itemView.findViewById(R.id.l_time);
            tvDate = itemView.findViewById(R.id.l_date);
            imgDelete = itemView.findViewById(R.id.delete_btn);
            img = itemView.findViewById(R.id.n_pic);
        }
    }

}
