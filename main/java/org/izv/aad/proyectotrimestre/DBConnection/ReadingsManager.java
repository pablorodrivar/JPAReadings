package org.izv.aad.proyectotrimestre.DBConnection;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.izv.aad.proyectotrimestre.Utilities;

import java.util.ArrayList;
import java.util.List;

import static org.izv.aad.proyectotrimestre.Activities.MainActivity.TAG;

public class ReadingsManager {

    private Helper ayudante;
    private SQLiteDatabase bd;

    public ReadingsManager(Context c) {
        this(c, true);
    }

    public ReadingsManager(Context c, boolean write) {
        this.ayudante = new Helper(c);

        if(write){
            bd = this.ayudante.getWritableDatabase();

        }else if(!write){
            bd = this.ayudante.getReadableDatabase();

        }
    }

    /*  Recomienda cerrar la conexión con la bd a nivel de actividad.
        Ver pdf donde se habla al respecto.
        Cerrar: en el onPause() Reabrir: onResume()
    */
    public void cerrar(){
        this.ayudante.close(); //Al cerrar el ayudante se cierra la conexión con la bd.
    }

    public long insert(Readings r){

        //El long es el id con el que el objeto se ha insertado.
        //El nullColumnHack:
        return bd.insert(Contract.TablaReadings.TABLE_NAME, null, Utilities.readingsValues(r));
    }

    public int deleteTodo(){
        int delete = bd.delete(Contract.TablaReadings.TABLE_NAME, null,null);

        return delete;
    }

    public int delete(long id){

        String condicion = Contract.TablaReadings._ID + " = ?";

        String[] argumentos = { id + "" };

        int cuenta = bd.delete(Contract.TablaReadings.TABLE_NAME, condicion,argumentos);

        return cuenta;
    }

    public int delete(String titulo){

        String condicion = Contract.TablaReadings.COLUMN_NAME_TITULO + " = ?";

        String[] argumentos = { titulo };

        int cuenta = bd.delete(Contract.TablaReadings.TABLE_NAME, condicion,argumentos);

        return cuenta;
    }

    public int update(Readings r) {

        return bd.update(   Contract.TablaReadings.TABLE_NAME,
                Utilities.readingsValues(r),
                Contract.TablaReadings._ID + " = ?",
                new String[]{r.getId() + ""});
    }

    //Objeto que representa una esstructura de datos mediante el cual puedo recorrer la consulta.
    public Cursor getCursor(String condicion, String[] argumentos,String order) {
        Log.v(TAG, bd.query(Contract.TablaReadings.TABLE_NAME,
                null, //null = *
                condicion, //where
                argumentos, //Array de los argumentos del select
                null, //Lo que va después del group by
                null, //Lo que va después del having (va solo con group by)
                order).toString());
        return bd.query(    Contract.TablaReadings.TABLE_NAME,
                null, //null = *
                condicion, //where
                argumentos, //Array de los argumentos del select
                null, //Lo que va después del group by
                null, //Lo que va después del having (va solo con group by)
                order); //Ordenar
    }

    public Cursor getCursor() {

        return getCursor(null, null,Contract.TablaReadings.COLUMN_NAME_TITULO + " asc");
    }

    //Nos devuelve la fila del cursor.
    public Readings getRow(Cursor c) {
        Readings lectura = new Readings();

        //Vemos la importancia del contrato.
        lectura.setId(c.getInt(c.getColumnIndex(Contract.TablaReadings._ID)));
        lectura.setTitulo(c.getString(c.getColumnIndex(Contract.TablaReadings.COLUMN_NAME_TITULO)));
        lectura.setId_autor(c.getInt(c.getColumnIndex(Contract.TablaReadings.COLUMN_NAME_IDAUTOR)));
        lectura.setDrawable_portada(c.getString(c.getColumnIndex(Contract.TablaReadings.COLUMN_NAME_FOTO)));
        lectura.setFecha_comienzo(c.getString(c.getColumnIndex(Contract.TablaReadings.COLUMN_NAME_FCOMIENZO)));
        lectura.setFecha_fin(c.getString(c.getColumnIndex(Contract.TablaReadings.COLUMN_NAME_FFIN)));
        lectura.setValoracion(c.getInt(c.getColumnIndex(Contract.TablaReadings.COLUMN_NAME_VALORACION)));
        lectura.setResumen(c.getString(c.getColumnIndex(Contract.TablaReadings.COLUMN_NAME_RESUMEN)));
        lectura.setFireBaseKey(c.getString(c.getColumnIndex(Contract.TablaReadings.COLUMN_NAME_FIREBASEKEY)));

        return lectura;
    }

    public List<String> getListaNombres(List<Readings> readings){
        List<String> nombres = new ArrayList<>();
        for(Readings r : readings){
            nombres.add(getNombreAutor(r));
        }
        return nombres;
    }

    public List<Readings> getReadings(String condicion, String[] argumentos,String order) {

        List<Readings> listaReadings = new ArrayList<>();

        Cursor cursor = getCursor(condicion,argumentos,order);

        while (cursor.moveToNext ()) {

            listaReadings.add(getRow(cursor));
        }
        cursor.close();
        return listaReadings;
    }

    public String getReadingsArchivo() {

        // Esto lo unico que hace es separar cada objeto por un \n
        // Y cada atributo entre comillas simples, y separado por ; entre sí.
        // Era para lo de contactos de Carmelo de guardar en un archivo .csv
        // No creo que se use aquí, pero podemos usarlo como Backup a archivo.

        String archivo = "";
        List<Readings> listaContactos = new ArrayList<>();

        Cursor cursor = getCursor(null,null,Contract.TablaReadings.COLUMN_NAME_TITULO + " asc");

        while (cursor.moveToNext ()) {
            archivo += "'" + getRow(cursor).getId() + "';'"+ getRow(cursor).getTitulo() + "';'"
                    +getRow(cursor).getId_autor()+"';'"+ getRow(cursor).getDrawable_portada() + "';'"+
                    getRow(cursor).getFecha_comienzo() + "';'"+ getRow(cursor).getFecha_fin() + "';'"+
                    getRow(cursor).getValoracion() + "';'"+ getRow(cursor).getResumen()+ "';"+ getRow(cursor).getFireBaseKey()+ "';\n";

        }
        cursor.close();

        return archivo;
    }

    public Cursor getAutor(String condicion, String[] argumentos) {

        return bd.query(    Contract.TablaAuthor.TABLE_NAME,
                null, //null = *
                condicion, //where
                argumentos, //Array de los argumentos del select
                null, //Lo que va después del group by
                null, //Lo que va después del having (va solo con group by)
                Contract.TablaAuthor.COLUMN_NAME_NOMBRE_AUTOR + " asc"); //Ordenar

    }

    public Author getRowAuthor(Cursor c) {

        Author autor = new Author();

        while (c.moveToNext ()) {
            //Vemos la importancia del contrato.
            autor.setId(c.getInt(c.getColumnIndex(Contract.TablaAuthor.COLUMN_NAME_IDAUTOR)));
            autor.setNombre(c.getString(c.getColumnIndex(Contract.TablaAuthor.COLUMN_NAME_NOMBRE_AUTOR)));
            autor.setFireBaseKey(c.getString(c.getColumnIndex(Contract.TablaAuthor.COLUMN_NAME_FIREBASEKEY)));
        }
        return autor;
    }

    public Author getAuthor(Readings r){
        int id = r.getId_autor();
        Cursor c = getAutor(Contract.TablaAuthor.COLUMN_NAME_IDAUTOR+" ="+id,null);
        return getRowAuthor(c);
    }

    public String getNombreAutor(Readings r){
        int id = r.getId_autor();
        Cursor c = getAutor(Contract.TablaAuthor.COLUMN_NAME_IDAUTOR+" ='"+id+"'",null);
        Author a = getRowAuthor(c);
        return a.getNombre();
    }

    public String getNombreAutor(int id){
        Cursor c = getAutor(Contract.TablaAuthor.COLUMN_NAME_IDAUTOR+" ='"+id+"'",null);
        Author a = getRowAuthor(c);
        return a.getNombre();
    }

    public int getIdAutor(String name){
        Cursor c = getAutor(Contract.TablaAuthor.COLUMN_NAME_NOMBRE_AUTOR+" ='"+name+"'",null);
        Author a = getRowAuthor(c);
        return a.getId();
    }
}