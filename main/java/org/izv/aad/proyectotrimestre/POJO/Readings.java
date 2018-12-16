package org.izv.aad.proyectotrimestre.POJO;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.firebase.database.DataSnapshot;
import java.util.HashMap;
import java.util.Map;

public class Readings implements Parcelable{

    private int id,id_autor;
    private String titulo,fecha_comienzo,fecha_fin,resumen,fireBaseKey,drawable_portada;
    private float valoracion;

    public Readings(){
        this.id_autor = 0;
        this.drawable_portada = "132226-200-png";
        this.valoracion = 0;
        this.titulo = "Título Vacío";
        this.fecha_comienzo = null;
        this.fecha_fin = null;
        this.resumen = "Resumen Vacío";
        this.fireBaseKey = titulo.toLowerCase().replace(" ", "") + id_autor;
    }

    public Readings(Readings readings){
        this.setTitulo(readings.getTitulo());
        this.setId_autor(readings.getId_autor());
        this.setDrawable_portada(readings.getDrawable_portada());
        this.setFecha_comienzo(readings.getFecha_comienzo());
        this.setFecha_fin(readings.getFecha_fin());
        this.setValoracion(readings.getValoracion());
        this.setResumen(readings.getResumen());
    }

    public Readings(DataSnapshot dataSnapshot){
    }

    public Readings(String titulo, int id_autor, String drawable_portada, String fecha_comienzo, String fecha_fin, float valoracion, String resumen) {
        this.id_autor = id_autor;
        this.drawable_portada = drawable_portada;
        this.valoracion = valoracion;
        this.titulo = titulo;
        this.fecha_comienzo = fecha_comienzo;
        this.fecha_fin = fecha_fin;
        this.resumen = resumen;
        this.fireBaseKey = titulo.toLowerCase().replace(" ", "").replace(".","")
                .replace("#", "").replace("$", "").replace("[", "").replace("]","") + id_autor;
    }

    public Readings(int id, String titulo, int id_autor, String drawable_portada, String fecha_comienzo, String fecha_fin, float valoracion, String resumen, String fireBaseKey) {
        this.id = id;
        this.id_autor = id_autor;
        this.drawable_portada = drawable_portada;
        this.valoracion = valoracion;
        this.titulo = titulo;
        this.fecha_comienzo = fecha_comienzo;
        this.fecha_fin = fecha_fin;
        this.resumen = resumen;
        this.fireBaseKey = fireBaseKey;
    }

    protected Readings(Parcel in) {
        id = in.readInt();
        id_autor = in.readInt();
        drawable_portada = in.readString();
        valoracion = in.readFloat();
        titulo = in.readString();
        fecha_comienzo = in.readString();
        fecha_fin = in.readString();
        resumen = in.readString();
        fireBaseKey = in.readString();
    }

    public static final Creator<Readings> CREATOR = new Creator<Readings>() {
        @Override
        public Readings createFromParcel(Parcel in) {
            return new Readings(in);
        }

        @Override
        public Readings[] newArray(int size) {
            return new Readings[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_autor() {
        return id_autor;
    }

    public void setId_autor(int id_autor) {
        this.id_autor = id_autor;
    }

    public String getDrawable_portada() {
        return drawable_portada;
    }

    public void setDrawable_portada(String drawable_portada) {
        this.drawable_portada = drawable_portada;
    }

    public float getValoracion() {
        return valoracion;
    }

    public void setValoracion(float valoracion) {
        this.valoracion = valoracion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFecha_comienzo() {
        return fecha_comienzo;
    }

    public void setFecha_comienzo(String fecha_comienzo) {
        this.fecha_comienzo = fecha_comienzo;
    }

    public String getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(String fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getFireBaseKey() {
        return fireBaseKey;
    }

    public void setFireBaseKey(String fireBaseKey) {
        this.fireBaseKey = fireBaseKey;
    }

    public void setFireBaseKey(){this.fireBaseKey = titulo.toLowerCase().replace(" ", "").replace(".","")
            .replace("#", "").replace("$", "").replace("[", "").replace("]","") + id_autor;}

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id_autor", id_autor);
        result.put("drawable_portada", drawable_portada);
        result.put("valoracion", valoracion);
        result.put("titulo", titulo);
        result.put("fecha_comienzo", fecha_comienzo);
        result.put("fecha_fin", fecha_fin);
        result.put("resumen", resumen);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(id_autor);
        dest.writeString(drawable_portada);
        dest.writeFloat(valoracion);
        dest.writeString(titulo);
        dest.writeString(fecha_comienzo);
        dest.writeString(fecha_fin);
        dest.writeString(resumen);
        dest.writeString(fireBaseKey);
    }

    @Override
    public String toString() {
        return "Readings{" +
                "id=" + id +
                ", id_autor=" + id_autor +
                ", titulo='" + titulo + '\'' +
                ", fecha_comienzo='" + fecha_comienzo + '\'' +
                ", fecha_fin='" + fecha_fin + '\'' +
                ", resumen='" + resumen + '\'' +
                ", fireBaseKey='" + fireBaseKey + '\'' +
                ", drawable_portada='" + drawable_portada + '\'' +
                ", valoracion=" + valoracion +
                '}';
    }
}
