package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2; // Bumped version for new columns
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_COMPLETED = "completed";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String COLUMN_FAVORITE = "favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER, " +
                COLUMN_DEADLINE + " TEXT, " +
                COLUMN_FAVORITE + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public void addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_DEADLINE, task.getDeadline());
        values.put(COLUMN_FAVORITE, task.isFavorite() ? 1 : 0);
        db.insert(TABLE_TASKS, null, values);
        db.close();
    }

    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, null, null, null, null, COLUMN_DEADLINE + " ASC");
        if (cursor.moveToFirst()) {
            do {
                tasks.add(extractTaskFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        System.out.println("Retrieved " + tasks.size() + " tasks");
        return tasks;
    }

    public List<Task> getFavoriteTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_FAVORITE + " = 1";
        Cursor cursor = db.query(TABLE_TASKS, null, selection, null, null, null, COLUMN_DEADLINE + " ASC");
        if (cursor.moveToFirst()) {
            do {
                tasks.add(extractTaskFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        System.out.println("Retrieved " + tasks.size() + " favorite tasks");
        return tasks;
    }

    private Task extractTaskFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
        boolean completed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1;
        String deadline = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE));
        boolean favorite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE)) == 1;
        return new Task(id, title, description, completed, deadline, favorite);
    }

    public void updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_DEADLINE, task.getDeadline());
        values.put(COLUMN_FAVORITE, task.isFavorite() ? 1 : 0);
        db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public Task getTaskById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        Task task = null;
        if (cursor.moveToFirst()) {
            task = extractTaskFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return task;
    }
}