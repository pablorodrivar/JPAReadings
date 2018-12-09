package org.izv.aad.proyectotrimestre;

import android.content.ContentValues;

import org.izv.aad.proyectotrimestre.DBConnection.Contract;
import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;

public class Utilities {

    //Transformamos un objeto contacto en un objeto contentvalues.
    public static ContentValues authorValues(Author a){

        ContentValues contentValues = new ContentValues();

        //contentValues.put(Contract.TablaAuthor._ID, c.getId()); Puede dar problemas.

        contentValues.put(Contract.TablaAuthor.COLUMN_NAME_NOMBRE_AUTOR, a.getNombre());
       // contentValues.put(Contract.TablaAuthor.COLUMN_NAME_IDAUTOR, a.getId());
        contentValues.put(Contract.TablaAuthor.COLUMN_NAME_FIREBASEKEY, a.getFireBaseKey());

        return contentValues;
    }

    public static ContentValues readingsValues(Readings r){

        ContentValues contentValues = new ContentValues();

        //contentValues.put(Contract.TablaAuthor._ID, c.getId()); Puede dar problemas.

        contentValues.put(Contract.TablaReadings.COLUMN_NAME_TITULO, r.getTitulo());
        contentValues.put(Contract.TablaReadings.COLUMN_NAME_IDAUTOR, r.getId_autor());
        contentValues.put(Contract.TablaReadings.COLUMN_NAME_FOTO, r.getDrawable_portada());
        contentValues.put(Contract.TablaReadings.COLUMN_NAME_FCOMIENZO, r.getFecha_comienzo());
        contentValues.put(Contract.TablaReadings.COLUMN_NAME_FFIN, r.getFecha_fin());
        contentValues.put(Contract.TablaReadings.COLUMN_NAME_VALORACION, r.getValoracion());
        contentValues.put(Contract.TablaReadings.COLUMN_NAME_RESUMEN, r.getResumen());
        contentValues.put(Contract.TablaReadings.COLUMN_NAME_FIREBASEKEY, r.getFireBaseKey());

        return contentValues;
    }
}


