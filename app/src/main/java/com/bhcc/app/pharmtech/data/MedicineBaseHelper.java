package com.bhcc.app.pharmtech.data;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MedicineBaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "TopDrugs.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;


    /**
     * Constructor
     * @param context
     */
    public MedicineBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * To create the database
     * @throws IOException
     */
    public void createDataBase() throws IOException
    {
        //If database not exists copy it from the assets
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist)
        {
            try
            {
                //Copy the database from assets
                copyDataBase();

                System.out.println("createDatabase database created");
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    /**
     * To check if the database already exists
     * @return
     */
    private boolean checkDataBase()
    {
        File dbFile = new File(Environment.getDataDirectory() + "/data/" + myContext.getPackageName() + "/databases/" + DB_NAME);
        Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //Copy the database from assets

    /**
     * To copy the database file from assets to the application database directory
     * @throws IOException
     */
    private void copyDataBase() throws IOException
    {
        // open database from assets
        InputStream mInput = myContext.getAssets().open(DB_NAME);
        Log.v("dbFile", "get Asset");

        // get the database directory path
        String outFileName = Environment.getDataDirectory() + "/data/" + myContext.getPackageName() + "/databases";
        File file = new File(outFileName);
        // make a folder for the database
        file.mkdirs();
        Log.v("dbFile", outFileName);

        Log.v("dbFile", "get assets database");
        outFileName = Environment.getDataDirectory() + "/data/" + myContext.getPackageName() + "/databases/" + DB_NAME;

        // set output stream to the path
        OutputStream mOutput = new FileOutputStream(outFileName);
        Log.v("dbFile", "get output");

        // copy the database
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        Log.v("dbFile", "done copying");

        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    /**
     * To open the database
     * @return
     * @throws SQLException
     */
    public SQLiteDatabase openDataBase() throws SQLException {
        //Open the database
        String myPath = myContext.getDatabasePath(DB_NAME).getAbsolutePath();
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
        return myDataBase;
    }


    /**
     * to close the database
     */
    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
