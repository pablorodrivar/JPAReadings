package org.izv.aad.proyectotrimestre.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import org.izv.aad.proyectotrimestre.Activities.ActivityMostrar.Adapter;
import org.izv.aad.proyectotrimestre.DBConnection.Contract;
import org.izv.aad.proyectotrimestre.DBConnection.ReadingsManager;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.izv.aad.proyectotrimestre.R;
import java.util.List;

public class Mostrar extends AppCompatActivity {
    // Declaro variables de clase.
    private RecyclerView rv;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayout;
    private ReadingsManager rm;
    private Spinner sp;
    private ImageView inv;
    // Algunas tienen Getters y Setters, los incluyo a continuación.
    private boolean cargado = false; private void setCargado(boolean car){this.cargado = car;} private boolean getCargado(){return this.cargado;}
    private int categoria; private void setCategoria(int cate){this.categoria = cate;} private int getCategoria(){return this.categoria;}
    private String condicion;private void setCondicion(String con){this.condicion = con;}private String getCondicion(){return this.condicion;}
    private String condicionInicial; private void setCondicionInicial(String con){this.condicionInicial = con;} private String getCondicionInicial(){return this.condicionInicial;}
    private String ordenar;private void setOrdenar(String ord){this.ordenar = ord;}private String getOrdenar(){return this.ordenar;}
    private String orden;private void setOrden(String or){this.orden = or;}private String getOrden(){return this.orden;}
    // Listas de Readings y Strings para el RecyclerView.
    private List<Readings> listaFinal;private void setListaFinal(List<Readings> ls){this.listaFinal = ls;}private List<Readings> getListaFinal(){return this.listaFinal;}
    private List<String> nombresFinal;private void setNombresFinal(List<Readings> nf){this.nombresFinal = rm.getListaNombres(nf);}private List<String> getNombresFinal(){return this.nombresFinal;}
    private int autor; private void setAutor(int a){this.autor = a;} private int getAutor(){return this.autor;}
    // Función para invertir el orden de ambas listas, listaFinal y nombresFinal
    private void invOrden(){
        if (getOrden().equals("asc")){
            setOrden("desc");
        }else setOrden("asc");
    }
    // Función para generar las listas según la condición, ordenar por y orden.
    private void setListas(){
        setListaFinal(rm.getReadings(getCondicion(),null,getOrdenar()+" "+getOrden()));
        setNombresFinal(getListaFinal());
    }
    // Función para ir a la Activity DisplayReading, enviando los Extras necesarios.
    private void irA(Readings item){
        Intent i = new Intent(Mostrar.this, DisplayReading.class);
        i.putExtra("tipo", categoria);
        i.putExtra("reading", item.getFireBaseKey());
        startActivityForResult(i,0);
    }

    // Crear el Adapter con la funcion personalizada para onClick.
    private Adapter newAdapter(){
        return new Adapter(getListaFinal(), new Adapter.OnItemClickListener(){@Override public void onItemClick(Readings item){irA(item);}},getNombresFinal());
    }
    // Generar las listas, crear el Adapter, e inflar el RecyclerView.
    private void setYCargar(){
        setListas();
        mAdapter = newAdapter();
        rv.setLayoutManager(this.mLayout);
        rv.setAdapter(mAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeActivity.leerSharedTheme(this,true);
        setContentView(R.layout.activity_mostrar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  Instancio ReadingsManager.
        this.rm = new ReadingsManager(this);
        //  Instancio LayoutManager para el RecyclerView.
        this.mLayout = new LinearLayoutManager(this);
        rv = findViewById(R.id.recycler_mostrar);
        sp = findViewById(R.id.spinner_mostrar);
        inv = findViewById(R.id.iv_invertir_orden);
        // Ordenar Readings por título.
        setOrdenar(Contract.TablaReadings.COLUMN_NAME_TITULO);
        // En orden ascendente.
        setOrden("asc");
        // getIntent para saber qué mostrar.
        Intent traer = getIntent();
        setCategoria(traer.getIntExtra("categoria",0));
        //  Según lo que diga el Intent...
        switch(getCategoria()){
            case 1:
                //  Condición de la consulta SQL... Libros que se estén leyendo.
                setCondicion(Contract.TablaReadings.COLUMN_NAME_FFIN+" IS NULL AND "+Contract.TablaReadings.COLUMN_NAME_FCOMIENZO+" IS NOT NULL");
                getSupportActionBar().setTitle("Leyendo");
                break;
            case 2:
                //  Condición de la consulta SQL... Libros que leídos.
                setCondicion(Contract.TablaReadings.COLUMN_NAME_FFIN+" IS NOT NULL AND "+Contract.TablaReadings.COLUMN_NAME_FCOMIENZO+" IS NOT NULL");
                getSupportActionBar().setTitle("Leidos");
                break;
            case 3:
                //  Condición de la consulta SQL... Libros según autor.
                setAutor(traer.getIntExtra("autor",0));
                getSupportActionBar().setTitle("Libros de");
                getSupportActionBar().setSubtitle(rm.getNombreAutor(getAutor()));
                setCondicion(Contract.TablaReadings.COLUMN_NAME_IDAUTOR + " ="+getAutor()+"");
                break;
            case 4:
                //  Condición de la consulta SQL... Ninguna, todos los libros.
                getSupportActionBar().setTitle("Todas las lecturas");
                setCondicion(null);
                break;
            case 5:
                //  Condición de la consulta SQL... Libros que se quieran leer.
                getSupportActionBar().setTitle("Quiero leer");
                setCondicion(Contract.TablaReadings.COLUMN_NAME_FFIN+" IS NULL AND "+ Contract.TablaReadings.COLUMN_NAME_FCOMIENZO+" IS NULL");
                break;
            default:
                break;
        }
        //  Guardar la condición original en otra variable.
        setCondicionInicial(getCondicion());
        // Listener del Spinner para elegir por qué ordenar.
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String opcion = sp.getSelectedItem().toString();
                switch (opcion){
                    case "Nombre":
                        //  En caso de nombre, set Ordenar, Orden, y generar listas y adaptador.
                        setOrdenar(Contract.TablaReadings.COLUMN_NAME_TITULO);
                        setOrden("asc");
                        setYCargar();
                        break;
                    case "Autor":
                        setOrdenar(Contract.TablaReadings.COLUMN_NAME_IDAUTOR);
                        setOrden("asc");
                        setYCargar();
                        break;
                    case "Fecha":
                        setOrdenar(Contract.TablaReadings.COLUMN_NAME_FCOMIENZO);
                        setOrden("desc");
                        setYCargar();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setYCargar();
            }
        });
        // Botón invertir. Invierte orden, genera listas y carga Adapter.
        inv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invOrden();setYCargar();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mostrar, menu);
        // Barra de búsqueda.
        SearchView search = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // No hacer nada al enviar, no es necesario.
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                // Listener al tipear en la barra de búsqueda.
                if (s.equals("")){
                    //  Si está vacío el cuadro de búsqueda, entonces generar
                    //  listas y cargar Adapter con la condición inicial.
                    setCondicion(getCondicionInicial());
                }else {
                    if (getCondicion() == null){
                        // Actualizar condición en base a lo que se va escribiendo.
                        setCondicion( Contract.TablaReadings.COLUMN_NAME_TITULO + " LIKE '%" + s + "%'");
                    }else setCondicion(getCondicion() + " AND " + Contract.TablaReadings.COLUMN_NAME_TITULO + " LIKE '%" + s + "%'");
                }
                setYCargar();
                return false;
            }
        });
        return true;
    }
}