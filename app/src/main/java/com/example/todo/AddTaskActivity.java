package com.example.todo;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {
    private TextInputEditText titleEditText, descriptionEditText, deadlineEditText;
    private DatabaseHelper dbHelper;
    private int taskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        deadlineEditText = findViewById(R.id.deadlineEditText);
        com.google.android.material.button.MaterialButton saveButton = findViewById(R.id.saveButton);

        dbHelper = new DatabaseHelper(this);

        // Handle edit mode
        taskId = getIntent().getIntExtra("task_id", -1);
        if (taskId != -1) {
            Task task = dbHelper.getTaskById(taskId);
            if (task != null) {
                titleEditText.setText(task.getTitle());
                descriptionEditText.setText(task.getDescription());
                deadlineEditText.setText(task.getDeadline());
                setTitle("Edit Task");
            }
        } else {
            setTitle("Add Task");
        }

        // Show DatePicker on deadline click
        deadlineEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String deadline = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        deadlineEditText.setText(deadline);
                    }, year, month, day);
            datePickerDialog.show();
        });

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String deadline = deadlineEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                boolean isFavorite = (taskId != -1) ? dbHelper.getTaskById(taskId).isFavorite() : false;
                Task task = new Task(taskId != -1 ? taskId : 0, title, description, false, deadline, isFavorite);
                if (taskId == -1) {
                    dbHelper.addTask(task);
                } else {
                    dbHelper.updateTask(task);
                }
                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка сохранения задачи: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}