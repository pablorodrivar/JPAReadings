package org.izv.aad.proyectotrimestre.POJO;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class Author implements Parcelable{
    private int id;
    private String nombre, fireBaseKey;

    public Author(String nombre){
        this.nombre = nombre;
        String key = nombre.replace(" ", "").replace(".","")
                .replace("#", "").replace("$", "").replace("[", "").replace("]","");
        this.fireBaseKey = key.toLowerCase() + id;
    }

    public Author(int id, String nombre, String fireBaseKey) {
        this.id = id;
        this.nombre = nombre;
        this.fireBaseKey = fireBaseKey;
    }

    public Author(){
        this.nombre = "Vacío";
        String key = nombre.replace(" ", "").replace(".","")
                .replace("#", "").replace("$", "").replace("[", "").replace("]","");
        this.fireBaseKey = key.toLowerCase() + id;
    }

    protected Author(Parcel in) {
        id = in.readInt();
        nombre = in.readString();
        fireBaseKey = in.readString();
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };

    public String getIDdesdeFirebasekey(){
        String fireSinID = this.nombre.toLowerCase().replace(" ", "").replace(".","")
                .replace("#", "").replace("$", "").replace("[", "").replace("]","");
        int sub = fireSinID.length();
        return this.fireBaseKey.substring(sub);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFireBaseKey() {
        return fireBaseKey;
    }

    public void setFireBaseKey(String fireBaseKey) {
        this.fireBaseKey = fireBaseKey;
    }

    public void setFireBaseKey(){this.fireBaseKey = nombre.toLowerCase().replace(" ", "").replace(".","")
            .replace("#", "").replace("$", "").replace("[", "").replace("]","") + id;}

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", fireBaseKey='" + fireBaseKey + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nombre", nombre);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nombre);
        dest.writeString(fireBaseKey);
    }
}
