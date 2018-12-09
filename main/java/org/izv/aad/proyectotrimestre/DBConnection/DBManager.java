package org.izv.aad.proyectotrimestre.DBConnection;

import android.app.WallpaperManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.ProgressBar;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import static org.izv.aad.proyectotrimestre.Activities.MainActivity.TAG;
import static org.izv.aad.proyectotrimestre.Activities.MainMenu.authorsManager;
import static org.izv.aad.proyectotrimestre.Activities.MainMenu.fbc;

public class DBManager {
    public static Bitmap bmp;
    public static ProgressBar progressBar;
    public static Author nombre;
    public static List<String> keys = new ArrayList<>();
    public static List<Author> authors = new ArrayList<>();

    public static void insert(ReadingsManager readingsManager, FireBaseConnection fbc, Readings reading){
        readingsManager.insert(reading);
        fbc.saveReading(reading);
    }

    public static void insert(ReadingsManager readingsManager, FireBaseConnection fbc, Readings readings, Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference imageRef = fbc.storageRef.child(readings.getDrawable_portada());
        Log.v(TAG, readings.getDrawable_portada() + " DRAWABLE PORTADA");

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Handle unsuccessful uploads
            Log.v(TAG, exception.toString());
            exception.printStackTrace();
        }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.v(TAG, "SUCCESS");
            }
        });

        readingsManager.insert(readings);
        fbc.saveReading(readings);
    }

    public static void insert(AuthorsManager authorsManager, FireBaseConnection fbc, Author author){
        Author author1 = author;
        authorsManager.insert(author1);
        author.setId(authorsManager.getAuthor(author1.getNombre()).getId());
        author.setFireBaseKey();
        fbc.saveAuthor(author);
    }

    public static void delete(ReadingsManager readingsManager, FireBaseConnection fbc, Readings reading){
        readingsManager.delete(reading.getId());
        fbc.deleteReading(reading);
    }

    public static void delete(AuthorsManager authorsManager, FireBaseConnection fbc, Author author){
        authorsManager.delete(author.getId());
        fbc.deleteAuthor(author);
    }

    public static Bitmap getImage(Readings readings) {
        new GetImage().execute(readings);
        return bmp;
    }

    public static void update(AuthorsManager authorsManager, FireBaseConnection fbc, Author author){
        authorsManager.delete(author.getId());
        fbc.deleteAuthor(author);
        insert(authorsManager,fbc,author);
    }

    public static void update(ReadingsManager readingsManager, FireBaseConnection fbc, Readings readings, Bitmap bitmap){
        readingsManager.delete(readings.getId());
        fbc.deleteReading(readings);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference imageRef = fbc.storageRef.child(readings.getDrawable_portada());
        Log.v(TAG, readings.getDrawable_portada() + " DRAWABLE PORTADA");

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Handle unsuccessful uploads
            Log.v(TAG, exception.toString());
            exception.printStackTrace();
        }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.v(TAG, "SUCCESS");
            }
        });
        insert(readingsManager,fbc,readings);
    }

    public static void syncronize(final ReadingsManager readingsManager, final AuthorsManager authorsManager, final FireBaseConnection fbc, String user){
        readingsManager.deleteTodo();
        authorsManager.deleteTodo();

        fbc.getDatabaseReference("user/" + user + "/authors/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                DataSnapshot data;
                while(iterator.hasNext()){
                    data = iterator.next();
                    nombre = data.getValue(Author.class);
                    keys.add(data.getKey());
                    authors.add(nombre);
                    Log.v(TAG, "ITERATOR" + data.getKey());
                }
                int index;
                Author aux;
                for(int i = 0; i < keys.size(); ++i){
                    index = Integer.parseInt(String.valueOf(keys.get(i).charAt(keys.get(i).length()-1)));
                    if(index != i-1){
                        aux = authors.get(index-1);
                        authors.set(index-1,authors.get(i));
                        authors.set(i,aux);
                    }
                }

                Log.v(TAG, 174 +"");
                for(int i = 0; i < authors.size(); i++){
                    Author author = new Author(authors.get(i).getNombre());
                    Log.v(TAG, "LISTA AUTORES: " + author.getNombre());
                    authorsManager.insert(author);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.v(TAG, 162+"");


        fbc.getDatabaseReference().child("user/" + user + "/readings/").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String drawable_portada, titulo, fecha_comienzo, fecha_fin, resumen, id_autor, valoracion;
                String firebase_key;
                String[] split;
                Readings reading = new Readings();
                Object object = dataSnapshot.getValue(Object.class);
                String json = new Gson().toJson(object);
                String[] jsonArray = json.split(Pattern.quote("}"));
                String data;
                for(int j = 0; j < jsonArray.length; ++j){
                    data = jsonArray[j];
                    data = data.replace("\"","");
                    Log.v(TAG, "DATA " + data);
                    firebase_key = data.substring(0, data.indexOf(":"));
                    Log.v(TAG, firebase_key);
                    reading.setFireBaseKey(firebase_key);
                    data = data.substring(data.indexOf(":")+2);
                    split = data.split(",");
                    Log.v(TAG, split[0].toString() + "");
                    for(int i = 0; i < split.length; ++i){
                        if(split[i].contains("drawable_portada")){
                            drawable_portada = split[i].substring(split[i].indexOf(":")+1);
                            Log.v(TAG, "drawable " + drawable_portada);
                            reading.setDrawable_portada(drawable_portada);
                        }
                        if(split[i].contains("titulo")){
                            titulo = split[i].substring(split[i].indexOf(":")+1);
                            Log.v(TAG, "titulo" + titulo);
                            reading.setTitulo(titulo);
                        }
                        if(split[i].contains("fecha_comienzo")){
                            fecha_comienzo = split[i].substring(split[i].indexOf(":")+1);
                            Log.v(TAG, "fcom " + fecha_comienzo);
                            reading.setFecha_comienzo(fecha_comienzo);
                        }
                        if(split[i].contains("fecha_fin")){
                            fecha_fin = split[i].substring(split[i].indexOf(":")+1);
                            Log.v(TAG, "ffin " + fecha_fin);
                            reading.setFecha_fin(fecha_fin);
                        }
                        if(split[i].contains("resumen")){
                            resumen = split[i].substring(split[i].indexOf(":")+1);
                            Log.v(TAG, "resumen" + resumen);
                            reading.setResumen(resumen);
                        }
                        if(split[i].contains("id_autor")){
                            id_autor = split[i].substring(split[i].indexOf(":")+1);
                            Log.v(TAG, "id " + id_autor);
                            reading.setId_autor(Integer.parseInt(id_autor));
                        }
                        if(split[i].contains("valoracion")){
                            valoracion = split[i].substring(split[i].indexOf(":")+1);
                            Log.v(TAG, "val " + valoracion);
                            reading.setValoracion(Float.parseFloat(valoracion));
                        }
                    }
                    readingsManager.insert(reading);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class GetImage extends AsyncTask<Readings, Integer, Bitmap>{

        @Override
        protected Bitmap doInBackground(Readings... readings) {
            StorageReference imageRef = fbc.storageRef.child(readings[0].getDrawable_portada());
            Log.v(TAG, readings[0].getDrawable_portada() + " PORTADA PROCESANDOSE");
            final long ONE_MEGABYTE = 1024 * 1024;
            imageRef.getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if(task.isSuccessful()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        bmp = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length, options);
                    }
                }
            });
            //Log.v(TAG, bmp.toString() + " BITMAP");

            return bmp;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            bmp = bitmap;
        }
    }
}
