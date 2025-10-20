package com.example.todo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> taskList;
    private Consumer<Task> onUpdateListener;
    private Consumer<Task> onDeleteListener;
    private Consumer<Task> onEditListener;

    public TaskAdapter(List<Task> taskList, Consumer<Task> onUpdateListener, Consumer<Task> onDeleteListener, Consumer<Task> onEditListener) {
        this.taskList = taskList;
        this.onUpdateListener = onUpdateListener;
        this.onDeleteListener = onDeleteListener;
        this.onEditListener = onEditListener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.titleTextView.setText(task.getTitle());
        holder.descriptionTextView.setText(task.getDescription());
        holder.completedCheckBox.setChecked(task.isCompleted());
        holder.favoriteIcon.setImageResource(task.isFavorite() ? android.R.drawable.star_big_on : android.R.drawable.star_big_off);

        holder.completedCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            onUpdateListener.accept(task);
        });

        holder.favoriteIcon.setOnClickListener(v -> {
            task.setFavorite(!task.isFavorite());
            onUpdateListener.accept(task);
        });

        holder.taskContent.setOnClickListener(v -> onEditListener.accept(task));

        holder.itemView.setOnLongClickListener(v -> {
            onDeleteListener.accept(task);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        CheckBox completedCheckBox;
        ImageView favoriteIcon;
        LinearLayout taskContent;

        TaskViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            completedCheckBox = itemView.findViewById(R.id.completedCheckBox);
            favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
            taskContent = itemView.findViewById(R.id.taskContent);
        }
    }
}