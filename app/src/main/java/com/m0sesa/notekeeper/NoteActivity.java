package com.m0sesa.notekeeper;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "NoteActivity";

    public static final String NOTE_ID = "note_id";
    public static final int NO_NOTE = -1;
    public static final int NOTE_CURSOR_LOADER = 0;
    Cursor cursor;
    private int noteId;
    EditText noteBody, noteTitle;

    NotesDatabaseOpenHelper notesDatabaseOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noteTitle = findViewById(R.id.note_title);
        noteBody = findViewById(R.id.note_body);

        Intent intent = getIntent();
        noteId = intent.getIntExtra(NOTE_ID, NO_NOTE);

        notesDatabaseOpenHelper = new NotesDatabaseOpenHelper(this);

        prepareActivity();
    }

    private void prepareActivity() {
        getSupportLoaderManager().initLoader(NOTE_CURSOR_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        CursorLoader loader = null;
        if (i== NOTE_CURSOR_LOADER){
            loader = loadNote();
        }
        return loader;
    }

    private CursorLoader loadNote() {
        return new CursorLoader(this){
            @Override
            public Cursor loadInBackground() {
                SQLiteDatabase db = notesDatabaseOpenHelper.getReadableDatabase();

                String selection = DatabaseContract.NotesTable._ID + " = ?";
                String[] selectionArgs = {String.valueOf(noteId)};

                return db.query(DatabaseContract.NotesTable.TABLE_NAME, null, selection, selectionArgs,
                        null, null, null);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        this.cursor = cursor;
        cursor.moveToFirst();
        noteTitle.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.NotesTable.NOTE_TITLE)));
        noteBody.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.NotesTable.NOTE_BODY)));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        cursor.close();
    }

    @Override
    protected void onDestroy() {
        notesDatabaseOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (noteTitle.getText().toString().equals("") && noteBody.getText().toString().equals("")){
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    SQLiteDatabase db = notesDatabaseOpenHelper.getWritableDatabase();

                    db.delete(DatabaseContract.NotesTable.TABLE_NAME, DatabaseContract.NotesTable._ID + " = ?",
                            new String[]{String.valueOf(noteId)});

                    return null;
                }
            }.execute();
        }else{
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... voids) {
                    SQLiteDatabase db = notesDatabaseOpenHelper.getWritableDatabase();

                    ContentValues cv = new ContentValues();
                    cv.put(DatabaseContract.NotesTable.NOTE_BODY, noteBody.getText().toString());
                    cv.put(DatabaseContract.NotesTable.NOTE_TITLE, noteTitle.getText().toString());
                    db.update(DatabaseContract.NotesTable.TABLE_NAME, cv, DatabaseContract.NotesTable._ID + " = ?",
                            new String[]{String.valueOf(noteId)});

                    return null;
                }
            }.execute();
        }
    }
}
