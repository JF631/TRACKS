package com.example.goy;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder>{

    private List<Pair<Course, LocalDate>> courseList;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

    public DepartmentAdapter(List<Pair<Course, LocalDate>> courseList){
        this.courseList = courseList;
    }

    public interface OnItemClickListener{
        void onItemClick(int pos);
    }
    private DepartmentAdapter.OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(DepartmentAdapter.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(int pos);
    }
    private DepartmentAdapter.OnItemLongClickListener onItemLongClickListener;
    public void setOnItemLongClickListener(DepartmentAdapter.OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.simple_date_item, parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(view1 -> {
           if(onItemClickListener != null){
               int pos = viewHolder.getAdapterPosition();
               onItemClickListener.onItemClick(pos);
           }
        });

        view.setOnLongClickListener(view1 -> {
            if(onItemLongClickListener != null){
                int pos = viewHolder.getAdapterPosition();
                onItemLongClickListener.onItemLongClick(pos);
                return true;
            }
            return false;
        });

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        Course course = courseList.get(position).getFirst();
        LocalDate date = courseList.get(position).getSecond();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(holder.ctx);
        String duration = dataBaseHelper.getDuration(course, date.getDayOfWeek());
        holder.dateView.setText(date.format(formatter));
        holder.durationView.setText(duration);
        holder.courseView.setText(course.getGroup());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public void switchList(List<Pair<Course, LocalDate>> courseList){
        this.courseList = courseList;
        notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteItem(int pos, Context ctx){
        LocalDate date = courseList.get(pos).getSecond();
        String course = courseList.get(pos).getFirst().getGroup();
        DataBaseHelper dataBaseHelper = new DataBaseHelper(ctx);
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ctx)
                .setTitle("Datum löschen?")
                .setMessage("Möchten Sie den " + date.format(formatter) + " vom Kurs " + course + " löschen?")
                .setCancelable(false)
                .setPositiveButton("Löschen", (dialogInterface, i) -> {
                    if(!dataBaseHelper.deleteDate(courseList.get(pos).getFirst(), date)){
                        Toast.makeText(ctx, "Es ist ein Fehler aufgetreten", Toast.LENGTH_SHORT).show();
                    }else {
                        courseList.remove(pos);
                        notifyItemRemoved(pos);
                    }
                    dialogInterface.dismiss();

                })
                .setNegativeButton("Abbrechen", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                });
        AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView dateView, durationView, courseView;
        Context ctx;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.simple_date_row);
            durationView = itemView.findViewById(R.id.simple_duration_row);
            courseView = itemView.findViewById(R.id.simple_course_row);
            ctx = itemView.getContext();
        }
    }
}
