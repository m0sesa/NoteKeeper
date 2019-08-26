package com.m0sesa.notekeeper;

import android.provider.BaseColumns;

public class DatabaseContract {
    private DatabaseContract(){}

    public static final class NotesTable implements BaseColumns {
        public static final String TABLE_NAME = "Notes";
        public static final String NOTE_TITLE = "title";
        public static final String NOTE_BODY = "body";

        public static final String INDEX1 = TABLE_NAME + "_index1";

        public static final String SQL_CREATE_INDEX1 = "CREATE INDEX " + INDEX1 + " ON " +
                TABLE_NAME + " ( " + NOTE_TITLE + " ) ";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "( "+
                        _ID + " INTEGER PRIMARY KEY, "+
                        NOTE_TITLE + " TEXT NOT NULL, "+
                        NOTE_BODY + " TEXT)";
    }
}
