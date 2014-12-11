package com.drexel.rephrem.drexelshuttleapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * Author 		: Renjith J Ephrem
 * Email  		: rje49@drexel.edu
 * Subject		: CS530 - Final Project.
 * Instructor 	: Dr. Erin Solovey
 * Layout XML   : N/A
 *
 * This class contains all the methods used to create, open and close the database.
 *
 */

public class DataBaseHelperClass extends SQLiteOpenHelper{

    private static String DB_PATH               = "/data/data/com.drexel.rephrem.drexelshuttleapp/databases/";
    private static final String DATABASE_NAME   = "DrexelShuttleDB";
    private static final int DATABASE_VERSION   = 1;

    public Context context;
    static SQLiteDatabase sqliteDataBase;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     * Parameters of super() are    1. Context
     *                              2. Data Base Name.
     *                              3. Cursor Factory.
     *                              4. Data Base Version.
     */
    public DataBaseHelperClass(Context context) {
        super(context, DATABASE_NAME, null ,DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Creates an empty database on the system and rewrites it with your own database.
     * By calling this method and empty database will be created into the default system path
     * of your application so we can overwrite that database with our database.
     *
     * @throws IOException
     */
    public void createDataBase() throws IOException{

        Log.d("", " In createDataBase(). ");
        boolean databaseExist = checkDataBase();

        Log.d("", " databaseExist : " + databaseExist);

        if(databaseExist){
            // Do Nothing.
        }else{
            this.getWritableDatabase();
            copyDataBase();
        }


    }

    /**
     * Checks if the database already exists, to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     *
     * @return
     */
    public boolean checkDataBase(){

        Log.d("", " Checking DB. ");

        File databaseFile = new File(DB_PATH + DATABASE_NAME);
        return databaseFile.exists();
    }


    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transferring byte stream.
     *
     * @throws IOException
     */
    private void copyDataBase() throws IOException{

        Log.d("", " Copying DB new. ");

        InputStream myInput     = null;
        String outFileName      = "";
        OutputStream myOutput   = null;
        try {
            myInput         = context.getAssets().open(DATABASE_NAME);
            outFileName     = DB_PATH + DATABASE_NAME;
            myOutput        = new FileOutputStream(outFileName);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.d("", " Exception in copyDataBase(). " + e.getMessage());
        }

        Log.d("", " Done Copying. ");
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    /**
     *
     * This method opens the data base connection.
     *
     * @return
     * @throws SQLException
     */
    public SQLiteDatabase openDataBase() throws SQLException{

        Log.d("", " Opening DB. ");
        String myPath   = DB_PATH + DATABASE_NAME;
        sqliteDataBase  = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

        return sqliteDataBase;
    }

    /**
     * This Method is used to close the data base connection.
     *
     * @param sqliteDataBase
     */
    public synchronized void close ( SQLiteDatabase sqliteDataBase) {

        Log.d("", " CLosing DB. ");

        if(sqliteDataBase != null)
            sqliteDataBase.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // No need to write the create table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No need to write the update table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
        // We should not update it as requirements of application.
    }
}