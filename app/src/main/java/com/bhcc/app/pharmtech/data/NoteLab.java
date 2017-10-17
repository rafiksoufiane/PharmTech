package com.bhcc.app.pharmtech.data;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bhcc.app.pharmtech.data.model.Medicine;
import com.bhcc.app.pharmtech.data.model.Note;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Singleton + Database
// this notelab is basicly same code as medicine lab,all you need to know is about" check", "insert" "update" 3 functions
//there are some function at medicine lab needed, but notelab doesnot need, just leave it or delete it later on.
// some comments about note of this app
//the note is saved in SAME database but different table, which means when you update( delete old one, and insert a new database).
//the note part will gone.
//however, if you just only update the table,everyone note will be saved and can be continue use if the medicine is matched
//to move the table to new database is pretty simple for progammer by user SQL tools,
// but for those student, we dont know. you may need to do something to save notes
//for students
public class NoteLab {
    private List<Note> notes;  // to hold all notes
    private static NoteLab NoteLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private static String DB_NAME = "TopDrugs.db";

    /**
     *
     * @param context
     * @return
     */
    public static NoteLab get(Context context) {
        if (NoteLab == null) {
           NoteLab = new NoteLab(context);
        }
        return NoteLab;
    }

    /**
     *
     * @param context
     */
    private NoteLab(Context context) {
        mContext = context.getApplicationContext();
        //medicines = new ArrayList<>();


        // get a database helper
        MedicineBaseHelper myDbHelper = new MedicineBaseHelper(mContext);

        // try to create a database
        try {
            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        // try to get a readable database
        try {
            mDatabase = myDbHelper.openDataBase();
            //mDatabase = myDbHelper.getReadableDatabase();
            notes = new ArrayList<>();

            // get all note from a database to an array
            NoteCursorWrapper cursor = queryCrimes(null, null, NoteSchema.NoteTable.Cols.GENERIC_NAME);

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }
            cursor.close();


        }catch(SQLException sqle){

            throw sqle;

        }
    }

    /**
     *
     * @return
     */
    // get all notes
    public List<Note> getNotes() {
        return notes;
    }

    /**
     *
     * @param medicine
     * @param note
     * @return
     */
    public ContentValues getContentValues(Medicine medicine, String note) {
        ContentValues values = new ContentValues();
        values.put(NoteSchema.NoteTable.Cols.GENERIC_NAME, medicine.getGenericName());
        values.put(NoteSchema.NoteTable.Cols.NOTE, note);

        return values;
    }

    /**
     *
     * @param medicine
     * @return
     */
    //it is function that never used, you may need delete it or just leave it for extra use
    //the function was use to debuggin during coding
    public  String checkNote(Medicine medicine) {
        NoteCursorWrapper cursor = rawQueryCrimes(
                "SELECT DISTINCT " + NoteSchema.NoteTable.Cols.GENERIC_NAME + " FROM " +
                        NoteSchema.NoteTable.NAME, null);

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Log.i("database genericname", cursor.getString(cursor.getColumnIndex(NoteSchema.NoteTable.Cols.GENERIC_NAME)));
                Log.i("i want to input",medicine.getGenericName());
                String temp1= cursor.getString(cursor.getColumnIndex(NoteSchema.NoteTable.Cols.GENERIC_NAME));
                String temp2= medicine.getGenericName();
                String temp3= cursor.getString(cursor.getColumnIndex(NoteSchema.NoteTable.Cols.NOTE));
                if(temp1.equals(temp2)){
                    // Log.i("i"," it is same");
                    return temp3;
                }
                if(cursor.isLast() &&!temp1.equals(temp2)){
                    // Log.i("i"," it is not same");
                    return  null;
                }


                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
      return  null;
    }

    /**
     *
     * @param medicine
     * @param note
     */
    //this function is use to check the  database has the record(note) or not
    public void  check(Medicine medicine, String note) {
        NoteCursorWrapper cursor = rawQueryCrimes(
                "SELECT DISTINCT " + NoteSchema.NoteTable.Cols.GENERIC_NAME + " FROM " +
                        NoteSchema.NoteTable.NAME, null);

        try {
            if (cursor.getCount() == 0) {
                cursor.close();
                return;
            }// check the cursor count,if it is 0, that means no note inside

            cursor.moveToFirst();//if corsor have note, move to first(beginning)

            while (!cursor.isAfterLast()) {// while it is not last record
                Log.i("database generic name", cursor.getString(cursor.getColumnIndex(NoteSchema.NoteTable.Cols.GENERIC_NAME)));//log for debugging
                Log.i("i want to input",medicine.getGenericName());//you may check the log so you understand what value are those.
                String temp1 = cursor.getString(cursor.getColumnIndex(NoteSchema.NoteTable.Cols.GENERIC_NAME));//check database geneic name
                String temp2 = medicine.getGenericName();//check the one that was trying to input note or edit note
                if(temp1.equals(temp2)) {//if it is equal, update the note
                    Log.i("i", " it is same");
                    updateMedicine(medicine, note);//the function for update note
                    cursor.close();
                    return;
                }
                if(cursor.isLast() && !temp1.equals(temp2)){// if it is the last one and still not equals, then insert a new one
                    Log.i("i"," it is not same");
                    insertMedicine(medicine, note);// the function for insert new note
                    cursor.close();
                    return;
                }

                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    public void insertMedicine(Medicine medicine, String note){// a simple function for insert new note by insert "values"
        ContentValues values = getContentValues(medicine, note);
        mDatabase.insert(NoteSchema.NoteTable.NAME, null, values);
    }


    public void updateMedicine(Medicine medicine, String note) {//update vaule with same location
        String genericNameString = medicine.getGenericName();
        ContentValues values = getContentValues(medicine, note);
        mDatabase.update(NoteSchema.NoteTable.NAME, values,
                NoteSchema.NoteTable.Cols.GENERIC_NAME + " = ?",
                new String[] { genericNameString });
    }


    // get one note
    public Note getNote(String genericName) {

        NoteCursorWrapper cursor = queryCrimes(
                NoteSchema.NoteTable.Cols.GENERIC_NAME + " = ?",
                new String[] { genericName },
                NoteSchema.NoteTable.Cols.GENERIC_NAME
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getNote();
        } finally {
            cursor.close();
        }
    }

    // get specific note // we dont need this function, it's basicly same as "check"
    public List<Note> getSpecificNote(String whereClause, String[] whereArgs, String orderBy) {
        NoteCursorWrapper cursor = queryCrimes(whereClause, whereArgs, orderBy);
        List<Note> list = new ArrayList<>();

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getNote());
                cursor.moveToNext();
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    // update note lab// we dont need update note lab.ignore for further use
    public void updateMedicineLab(String whereClause, String[] whereArgs, String orderBy) {
        NoteCursorWrapper cursor = queryCrimes(whereClause, whereArgs, orderBy);
        //medicines.clear();

        try {
            if (cursor.getCount() == 0) {
                return;
            }

           notes.clear();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                notes.add(cursor.getNote());
                cursor.moveToNext();
            }

            return;
        } finally {
            cursor.close();
        }
    }

    // get notes with raw sql// no need at this point
    public List<Note> getNotesWithRawSql(String rawSql, String[] whereArgs) {
        NoteCursorWrapper cursor = rawQueryCrimes(rawSql, whereArgs);
        List<Note> list = new ArrayList<>();

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                list.add(cursor.getNote());
                cursor.moveToNext();
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    // get note (distinctly)// no need for this lab
    public List<String> getStudyTopics() {
        NoteCursorWrapper cursor = rawQueryCrimes(
                "SELECT DISTINCT " + NoteSchema.NoteTable.Cols.NOTE + " FROM " +
                        NoteSchema.NoteTable.NAME, null);
        List<String> list = new ArrayList<>();

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                Log.i("test", cursor.getString(cursor.getColumnIndex(NoteSchema.NoteTable.Cols.NOTE)));
                list.add(cursor.getString(cursor.getColumnIndex(NoteSchema.NoteTable.Cols.NOTE)));
                cursor.moveToNext();
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    // get a cursor wrapper with arguments
    private NoteCursorWrapper queryCrimes(String whereClause, String[] whereArgs, String orderBy) {
        Cursor cursor = mDatabase.query(
                NoteSchema.NoteTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                orderBy  // orderBy
        );

        return new NoteCursorWrapper(cursor);
    }

    // get a cursor wrapper with a raw sql
    private NoteCursorWrapper rawQueryCrimes(String rawSql, String[] whereArgs) {
        Cursor cursor = mDatabase.rawQuery(rawSql, whereArgs);

        return new NoteCursorWrapper(cursor);
    }

}
