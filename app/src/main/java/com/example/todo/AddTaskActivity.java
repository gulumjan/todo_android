package com.example.todo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class AddTaskActivity extends AppCompatActivity {
    private TextInputEditText titleEditText, descriptionEditText;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        com.google.android.material.button.MaterialButton saveButton = findViewById(R.id.saveButton);

        dbHelper = new DatabaseHelper(this);

        saveButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Task task = new Task(0, title, description, false);
                dbHelper.addTask(task);
                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка сохранения задачи: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}