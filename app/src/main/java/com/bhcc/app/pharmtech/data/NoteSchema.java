package com.bhcc.app.pharmtech.data;

public class NoteSchema {// a schema for note cols. not hard to understand
    public static final class NoteTable {
        public static final String NAME = "Note";

        public static final class Cols {
            public static final String GENERIC_NAME = "GenericName";
            public static final String NOTE = "Note";

        }

        public static final int NUM_COLS = 2;
    }
}
