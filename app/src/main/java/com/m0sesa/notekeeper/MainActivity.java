package com.m0sesa.notekeeper;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final int NOTE_LOADER_ID = 0;
    private NotesDatabaseOpenHelper notesDatabaseOpenHelper;
    private Cursor noteCursor;
    private NoteRecyclerViewAdapter noteRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTask<Void, Void, Integer> newNoteTask = new AsyncTask<Void, Void, Integer>() {
                    @Override
                    protected Integer doInBackground(Void... voids) {
                        SQLiteDatabase db = notesDatabaseOpenHelper.getWritableDatabase();

                        ContentValues cv = new ContentValues();
                        cv.put(DatabaseContract.NotesTable.NOTE_TITLE, "");
                        cv.put(DatabaseContract.NotesTable.NOTE_BODY, "");

                        int id = (int) db.insert(DatabaseContract.NotesTable.TABLE_NAME, null, cv);
                        return id;
                    }

                    @Override
                    protected void onPostExecute(Integer integer) {
                        super.onPostExecute(integer);
                        Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                        intent.putExtra(NoteActivity.NOTE_ID, integer.intValue());
                        startActivity(intent);
                    }
                };

                newNoteTask.execute();
            }
        });

        notesDatabaseOpenHelper = new NotesDatabaseOpenHelper(this);

        noteRecyclerViewAdapter = new NoteRecyclerViewAdapter(this, noteCursor);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        RecyclerView notesRecyclerView = findViewById(R.id.note_recyclerview);
        notesRecyclerView.setLayoutManager(linearLayoutManager);
        notesRecyclerView.setAdapter(noteRecyclerViewAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportLoaderManager().restartLoader(NOTE_LOADER_ID, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        notesDatabaseOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        CursorLoader loader = null;
        if (i == NOTE_LOADER_ID){
            loader = createLoaderNotes();
        }
        return loader;
    }

    private CursorLoader createLoaderNotes() {
        String orderBy = DatabaseContract.NotesTable._ID + " DESC";
        return new CursorLoader(this, Uri.parse("content://com.m0sesa.notekeeper.provider"), null,
                null, null, orderBy);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == NOTE_LOADER_ID){
            loadFinishedNotes(cursor);
        }
    }

    private void loadFinishedNotes(Cursor cursor) {
        noteCursor = cursor;
        noteRecyclerViewAdapter.changeCursor(noteCursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (loader.getId() == NOTE_LOADER_ID){
            noteRecyclerViewAdapter.changeCursor(null);
        }
    }
}
