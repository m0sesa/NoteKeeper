package com.m0sesa.notekeeper;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class NoteRecyclerViewAdapter extends RecyclerView.Adapter<NoteRecyclerViewAdapter.NoteViewHolder> {

    private Context context;
    private Cursor cursor;
    private LayoutInflater inflater;

    public NoteRecyclerViewAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = inflater.inflate(R.layout.note_recyclerview_item_layout, viewGroup, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i) {
        cursor.moveToPosition(i);

        String noteTitle = cursor.getString(cursor.getColumnIndex(DatabaseContract.NotesTable.NOTE_TITLE));
        String noteBody = cursor.getString(cursor.getColumnIndex(DatabaseContract.NotesTable.NOTE_BODY));
        final int noteId = (int) cursor.getLong(cursor.getColumnIndex(DatabaseContract.NotesTable._ID));

        noteViewHolder.noteTitle.setText(noteTitle);
        noteViewHolder.noteBody.setText(noteBody);

        noteViewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NoteActivity.class);
                intent.putExtra(NoteActivity.NOTE_ID, noteId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (cursor != null){
            return cursor.getCount();
        }else{
            return 0;
        }
    }

    public void changeCursor(Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView noteTitle, noteBody;
        CardView layout;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.recycler_item_layout);
            noteTitle = itemView.findViewById(R.id.note_title);
            noteBody = itemView.findViewById(R.id.note_body);
        }
    }
}
