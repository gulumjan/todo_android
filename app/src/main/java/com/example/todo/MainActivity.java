package com.example.todo;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private DatabaseHelper dbHelper;
    private boolean showingFavorites = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        } else {
            Toast.makeText(this, "Toolbar не найден", Toast.LENGTH_SHORT).show();
        }

        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton addTaskButton = findViewById(R.id.addTaskButton);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

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
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this::updateTask, this::deleteTask, this::editTask);
        recyclerView.setAdapter(taskAdapter);

        dbHelper = new DatabaseHelper(this);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_all) {
                showingFavorites = false;
                loadAllTasks();
                return true;
            } else if (itemId == R.id.nav_favorites) {
                showingFavorites = true;
                loadFavoriteTasks();
                return true;
            }
            return false;
        });

        loadAllTasks();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data); // Fixed line
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadCurrentTasks();
        }
    }

    private void loadAllTasks() {
        new AsyncTask<Void, Void, List<Task>>() {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                try {
                    return dbHelper.getAllTasks();
                } catch (Exception e) {
                    System.out.println("Ошибка при загрузке задач: " + e.getMessage());
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                updateTaskList(tasks);
            }
        }.execute();
    }

    private void loadFavoriteTasks() {
        new AsyncTask<Void, Void, List<Task>>() {
            @Override
            protected List<Task> doInBackground(Void... voids) {
                try {
                    return dbHelper.getFavoriteTasks();
                } catch (Exception e) {
                    System.out.println("Ошибка при загрузке избранных задач: " + e.getMessage());
                    return new ArrayList<>();
                }
            }

            @Override
            protected void onPostExecute(List<Task> tasks) {
                updateTaskList(tasks);
            }
        }.execute();
    }

    private void loadCurrentTasks() {
        if (showingFavorites) {
            loadFavoriteTasks();
        } else {
            loadAllTasks();
        }
    }

    private void updateTaskList(List<Task> tasks) {
        taskList.clear();
        taskList.addAll(tasks != null ? tasks : new ArrayList<>());
        taskAdapter.notifyDataSetChanged();
        Toast.makeText(MainActivity.this, "Загружено задач: " + taskList.size(), Toast.LENGTH_SHORT).show();
    }

    private void updateTask(Task task) {
        try {
            dbHelper.updateTask(task);
            loadCurrentTasks();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка обновления задачи: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void deleteTask(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить задачу")
                .setMessage("Вы уверены, что хотите удалить эту задачу?")
                .setPositiveButton("Да", (dialog, which) -> {
                    try {
                        dbHelper.deleteTask(task.getId());
                        loadCurrentTasks();
                    } catch (Exception e) {
                        Toast.makeText(this, "Ошибка удаления задачи: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void editTask(Task task) {
        Intent intent = new Intent(this, AddTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivityForResult(intent, 1);
    }
}