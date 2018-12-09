package org.izv.aad.proyectotrimestre.DBConnection;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Helper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final String DATABASE_NAME = "readingmanager.sqlite"; //Nombre del archivo en el que vamos a guardar la bd, cualquier extensión vale.
    public static final int DATABASE_VERSION = 2; //Siempre se empieza por la versión uno, va incrementando.

    public Helper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    //Cuando corre por primera vez llama a esto.
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Contract.TablaAuthor.SQL_CREATE_AUTHOR);
        db.execSQL(Contract.TablaReadings.SQL_CREATE_READINGS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        if(oldVersion == 1 && newVersion == 2) {
            db.execSQL(Contract.TablaReadings.SQL_DROP_READINGS);
            db.execSQL(Contract.TablaAuthor.SQL_DROP_AUTHOR);
        }
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        onUpgrade(db, oldVersion, newVersion);
    }

}

