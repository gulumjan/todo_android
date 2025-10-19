package com.example.todo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar); // Работает только с темой без ActionBar
        } else {
            Toast.makeText(this, "Toolbar не найден", Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.recyclerView);
        Button addTaskButton = findViewById(R.id.addTaskButton);

        if (recyclerView == null) {
            Toast.makeText(this, "RecyclerView не найден", Toast.LENGTH_SHORT).show();
        }
        if (addTaskButton == null) {
            Toast.makeText(this, "Кнопка addTaskButton не найдена", Toast.LENGTH_SHORT).show();
            return;
        } else {
            addTaskButton.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
                startActivityForResult(intent, 1);
            });
            Toast.makeText(this, "Кнопка найдена и инициализирована", Toast.LENGTH_SHORT).show();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this::updateTaskStatus, this::deleteTask);
        recyclerView.setAdapter(taskAdapter);

        dbHelper = new DatabaseHelper(this);
        loadTasks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadTasks();
        }
    }

    private void loadTasks() {
        new AsyncTask<Void, Void, List<Task>>() {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                try {
                    List<Task> tasks = dbHelper.getAllTasks();
                    System.out.println("Получено задач: " + tasks.size());
                    return tasks;
                } catch (Exception e) {
                    System.out.println("Ошибка при загрузке задач: " + e.getMessage());
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                taskList.clear();
                taskList.addAll(tasks);
                if (taskAdapter != null) {
                    taskAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Загружено задач: " + tasks.size(), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateTaskStatus(Task task) {
        try {
            dbHelper.updateTask(task);
            loadTasks();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка обновления задачи: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteTask(Task task) {
        try {
            dbHelper.deleteTask(task.getId());
            loadTasks();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка удаления задачи: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}