package org.izv.aad.proyectotrimestre.DBConnection;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.ProgressBar;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class DBManager {
    public static Bitmap bmp;
    public static ProgressBar progressBar;
    public static Author nombre;
    public static List<String> keys = new ArrayList<>();
    public static List<Author> authors = new ArrayList<>();


    public static void insert(ReadingsManager readingsManager, FireBaseConnection fbc, Readings readings, Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] data = baos.toByteArray();
        StorageReference imageRef = fbc.storageRef.child(readings.getDrawable_portada());

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Handle unsuccessful uploads
            exception.printStackTrace();
        }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
        try {
            readingsManager.insert(readings);
        } catch(Exception e){
            e.printStackTrace();
        }
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

    public static void update(AuthorsManager authorsManager, FireBaseConnection fbc, Author author){
        authorsManager.delete(author.getId());
        fbc.deleteAuthor(author);
        insert(authorsManager,fbc,author);
    }

    public static void update(ReadingsManager readingsManager, FireBaseConnection fbc, Readings readings, Bitmap bitmap){
        readingsManager.delete(readings.getFireBaseKey());

        fbc.deleteReading(readings);
        readings.setFireBaseKey();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        StorageReference imageRef = fbc.storageRef.child(readings.getDrawable_portada());

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Handle unsuccessful uploads
            exception.printStackTrace();
        }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
        try {
            readingsManager.insert(readings);
        }catch (Exception e){
            e.printStackTrace();
        }
        fbc.saveReading(readings);

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

                    nombre.setFireBaseKey(data.getKey());
                    nombre.setId(Integer.parseInt(nombre.getIDdesdeFirebasekey()));
                    keys.add(data.getKey());
                    authors.add(nombre);
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

                for(int i = 0; i < authors.size(); i++){
                    authorsManager.insert(authors.get(i));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                String data = "";

                for(int j = 0; j < jsonArray.length; ++j){
                    data = jsonArray[j];
                    data = data.replace("\"","");

                    if(data != null && data.contains(":")){
                        firebase_key = data.substring(0, data.indexOf(":"));
                        reading.setFireBaseKey(firebase_key);
                        reading.setFecha_fin(null);
                        reading.setFecha_comienzo(null);
                        data = data.substring(data.indexOf(":")+2);
                        split = data.split(",");

                            for(int i = 0; i < split.length; ++i){

                                if(split[i].contains("drawable_portada")){
                                    drawable_portada = split[i].substring(split[i].indexOf(":")+1);
                                    reading.setDrawable_portada(drawable_portada);
                                }
                                if(split[i].contains("titulo")){
                                    titulo = split[i].substring(split[i].indexOf(":")+1);
                                    reading.setTitulo(titulo);
                                }
                                if(split[i].contains("fecha_comienzo")){
                                    fecha_comienzo = split[i].substring(split[i].indexOf(":")+1);
                                    reading.setFecha_comienzo(fecha_comienzo);
                                }
                                if(split[i].contains("fecha_fin")){
                                    fecha_fin = split[i].substring(split[i].indexOf(":")+1);
                                    reading.setFecha_fin(fecha_fin);
                                }
                                if(split[i].contains("resumen")){
                                    resumen = split[i].substring(split[i].indexOf(":")+1);
                                    reading.setResumen(resumen);
                                }
                                if(split[i].contains("id_autor")){
                                    id_autor = split[i].substring(split[i].indexOf(":")+1);
                                    reading.setId_autor(Integer.parseInt(id_autor));
                                }
                                if(split[i].contains("valoracion")){
                                    valoracion = split[i].substring(split[i].indexOf(":")+1);
                                    reading.setValoracion(Float.parseFloat(valoracion));
                                }
                            }
                            reading.setFireBaseKey();
                            readingsManager.delete(reading.getFireBaseKey());
                            readingsManager.insert(reading);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
