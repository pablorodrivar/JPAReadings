package org.izv.aad.proyectotrimestre.DBConnection;

import android.provider.BaseColumns;

public class Contract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Contract() {}

    /* Inner class that defines the table contents */
    public static class TablaReadings implements BaseColumns {
        public static final String TABLE_NAME = "readings";
        public static final String COLUMN_NAME_TITULO= "titulo";
        public static final String COLUMN_NAME_IDAUTOR = "idAutor";
        public static final String COLUMN_NAME_FOTO = "foto";
        public static final String COLUMN_NAME_FCOMIENZO = "fComienzo";
        public static final String COLUMN_NAME_FFIN = "fFinalizacion";
        public static final String COLUMN_NAME_VALORACION = "valoracion";
        public static final String COLUMN_NAME_RESUMEN = "resumen";
        public static final String COLUMN_NAME_FIREBASEKEY = "firebasekey";

        public static final String SQL_CREATE_READINGS =    "create table " + TABLE_NAME + " (" +
                _ID + " integer primary key," + //No es necesario ponerle autoincrement por lo visto.
                COLUMN_NAME_TITULO + " text not null," +
                COLUMN_NAME_IDAUTOR + " text,"+COLUMN_NAME_FOTO+" text,"+COLUMN_NAME_FCOMIENZO+" date,"+
                COLUMN_NAME_FFIN+" date,"+COLUMN_NAME_VALORACION+" real,"+COLUMN_NAME_RESUMEN+" text, " +
                COLUMN_NAME_FIREBASEKEY+" text unique, "+
                "foreign key("+COLUMN_NAME_IDAUTOR+") references artist)";

        public static final String SQL_DROP_READINGS = "drop table if exists " + TABLE_NAME;

    }

    public static class TablaAuthor implements BaseColumns{
        public static final String TABLE_NAME = "author";
        public static final String COLUMN_NAME_NOMBRE_AUTOR = "nombre";
        public static final String COLUMN_NAME_IDAUTOR = "idAutor";
        public static final String COLUMN_NAME_FIREBASEKEY = "firebasekey";


        public static final String SQL_CREATE_AUTHOR =    "create table " + TABLE_NAME + " (" +
                COLUMN_NAME_IDAUTOR + " integer primary key, " + //No es necesario ponerle autoincrement por lo visto.
                COLUMN_NAME_NOMBRE_AUTOR + " text not null, "+
                COLUMN_NAME_FIREBASEKEY+" text unique)";

        public static final String SQL_DROP_AUTHOR = "drop table if exists " + TABLE_NAME;
    }

}
