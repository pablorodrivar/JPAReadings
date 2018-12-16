package org.izv.aad.proyectotrimestre.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.izv.aad.proyectotrimestre.Activities.MenuRecycler.AdapterMenu;
import org.izv.aad.proyectotrimestre.DBConnection.AuthorsManager;
import org.izv.aad.proyectotrimestre.DBConnection.Contract;
import org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection;
import org.izv.aad.proyectotrimestre.DBConnection.ReadingsManager;
import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.izv.aad.proyectotrimestre.R;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection.firebaseAuth;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Declaración de variables
    public static String email;
    private RecyclerView rvautores;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static AuthorsManager authorsManager;
    public static ReadingsManager readingsManager;
    public static FireBaseConnection fbc;
    private static List<String> listAuthors = new ArrayList<>();
    private static List<Readings> readingsList = new ArrayList<>();
    private TextView tvcount1, tvcount2, tvcount3, nvemail;
    private List<Readings> listaLibrosLeidos = new ArrayList<>();
    private int menu_color;
    private LinearLayout headerMenu;
    private int librosLeidos; private void setLibrosLeidos(int l){this.librosLeidos = l;} private int getLibrosLeidos(){return this.librosLeidos;}
    private int librosLeyendo; private void setlibrosLeyendo(int l){this.librosLeyendo = l;} private int getlibrosLeyendo(){return this.librosLeyendo;}
    private int librosQuieroLeer; private void setlibrosQuieroLeer(int l){this.librosQuieroLeer = l;} private int getlibrosQuieroLeer(){return this.librosQuieroLeer;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeActivity.leerSharedTheme(this);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        authorsManager = new AuthorsManager(this);
        readingsManager = new ReadingsManager(this);
        fbc = new FireBaseConnection();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menú Principal");

        menu_color = ThemeActivity.leerSharedThemeDrawer(getApplicationContext(), menu_color);

        rvautores = findViewById(R.id.rvautores);
        tvcount1 = findViewById(R.id.tvcount1);
        tvcount2 = findViewById(R.id.tvcount2);
        tvcount3 = findViewById(R.id.tvcount3);

        if(firebaseAuth.getCurrentUser() != null){
            email = firebaseAuth.getCurrentUser().getEmail();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        // se inicia cuando abrimos el navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // se ajusta el color con el tema
                headerMenu = findViewById(R.id.nav_header_menu);
                headerMenu.setBackgroundColor(menu_color);
                // pone el correo de la sesión actual
                nvemail = findViewById(R.id.nvemail);
                nvemail.setText(email);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AniadirEditar.class);
                intent.putExtra("aniadireditar", "add");
                startActivity(intent);
            }
        });

        initRecyclerView();
        actualizaContadorClase(readingsManager);
        actualizarContadorLibros();
    }

    public void initRecyclerView(){
        // reinicia la lista de autores
        listAuthors.clear();
        // recoge los autores de la base de datos
        readingsList = readingsManager.getReadings(null,null,null);
        for(Readings r : readingsList){
            listAuthors.add(readingsManager.getNombreAutor(r));
        }

        // elimina las repeticiones
        Set<String> hs = new HashSet<>();
        hs.addAll(listAuthors);
        listAuthors.clear();
        listAuthors.addAll(hs);
        // ordena los autores por orden alfabético
        listAuthors = ordenarAutores(listAuthors);

        if(listAuthors.isEmpty()){

        }else {
            // si la lista de autores no está vacía llama al adapter para llenar el listview con los autores
            mAdapter = new AdapterMenu(listAuthors);
            ((AdapterMenu) mAdapter).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), Mostrar.class);
                    intent.putExtra("categoria", 3);
                    intent.putExtra("autor", readingsManager.getIdAutor(listAuthors.get(rvautores.getChildAdapterPosition(view))));
                    startActivity(intent);
                }
            });

            //DBManager.syncronize(readingsManager,authorsManager,fbc,fbc.firebaseAuth.getCurrentUser().getUid());
            mAdapter.notifyDataSetChanged();
            rvautores.setAdapter(mAdapter);
            mLayoutManager = new LinearLayoutManager(this);
            rvautores.setLayoutManager(mLayoutManager);
            actualizaContadorClase(readingsManager);
            actualizarContadorLibros();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Aquí manejamos cuando hacemos click en alguno de los elementos del navigation drawer
        int id = item.getItemId();
        Intent intent;

        switch(id){
            case R.id.add:
                // lanza un intent a la actividad de añadir libro
                intent = new Intent(this, AniadirEditar.class);
                intent.putExtra("aniadireditar", "add");
                startActivity(intent);
                return true;
            case R.id.search_php:
                // lanza un intent a la búsqueda en php
                intent = new Intent(this, SearchActivityPHP.class);
                startActivity(intent);
                return true;
            case R.id.theme:
                // lanza un intent a la actividad de cambiar de tema
                intent = new Intent(this, ThemeActivity.class);
                startActivity(intent);
                return true;
            case R.id.help:
                // lanza un intent a la pantalla de ayuda
                intent = new Intent(this, HelpActivity.class);
                startActivity(intent);
                return true;
            case R.id.signout:
                // cierra la sesión y reinicia la base de datos
                firebaseAuth.signOut();
                readingsManager.deleteTodo();
                authorsManager.deleteTodo();
                SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.clear();
                editor.apply();
                SharedPreferences prefs2 = getSharedPreferences("Themes", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = prefs2.edit();
                editor2.clear();
                editor2.apply();
                // lanza un intent al login
                intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            //case R.id.export:
            //importarDesdeArchivo(exportar());
            //return true;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // función para mostrar en otra actividad los libros leídos
    public void leidos(View view){
        Intent intent = new Intent(getApplicationContext(), Mostrar.class);
        intent.putExtra("categoria", 2);
        startActivity(intent);
    }

    // función para mostrar en otra actividad los libros que se están leyendo
    public void leyendo(View view){
        Intent intent = new Intent(getApplicationContext(), Mostrar.class);
        intent.putExtra("categoria", 1);
        startActivity(intent);
    }

    // función para mostrar en otra actividad todos los libros
    public void vertodos(View view){
        Intent intent = new Intent(getApplicationContext(), Mostrar.class);
        intent.putExtra("categoria", 4);
        startActivity(intent);
    }

    // función para mostrar en otra actividad los libros que se quieren leer
    public void quieroleer(View view){
        Intent intent = new Intent(getApplicationContext(), Mostrar.class);
        intent.putExtra("categoria", 5);
        startActivity(intent);
    }

    // función para ordenar los autores por orden alfabético
    public List<String> ordenarAutores(List<String> list){
        for(int j=0; j<list.size()-1; j++){
            for(int i=0; i<list.size()-j-1; i++){
                if(list.get(i).compareToIgnoreCase(list.get(i+1))>0){
                    String aux = list.get(i);
                    list.set(i, list.get(i+1));
                    list.set(i+1, aux);
                }
            }
        }
        return list;
    }

    private String exportar(){
        AuthorsManager am = new AuthorsManager(this);
        ReadingsManager rm = new ReadingsManager(this);
        String autores = am.getAuthorsFile();
        String readings = rm.getReadingsArchivo();
        String separador = "|||||";
        String archivo = autores+separador+readings;
        return archivo;
    }

    private void importarDesdeArchivo(String archivo) {
        AuthorsManager am = new AuthorsManager(MainMenu.this);
        ReadingsManager rm = new ReadingsManager(MainMenu.this);
        String[] dos = archivo.split("|||||");
        String[] authors = dos[0].split("\n");
        String[] readings = dos[1].split("\n");


        for (int i = 0; i < authors.length; i++) {
            String[] datos = authors[i].split(";");
            String idSinComillas = datos[0].replace("\'", "");
            String nombreSinComillas = datos[1].replace("\'", "");
            String firebasekeySinComillas = datos[2].replace("\'", "");
            int id = Integer.parseInt(idSinComillas);
            Author autor = new Author(id,nombreSinComillas,firebasekeySinComillas);
            //am.insert(autor);
        }
        for (int i = 0; i < readings.length; i++) {
            String[] datos = readings[i].split(";");
            // id   titulo  idautor portada comienzo    fin valoracion  resumen firebasekey
            String idSinComillas = datos[0].replace("\'", "");
            String tituloSinComillas = datos[1].replace("\'", "");
            String idAutor = datos[2].replace("\'", "");
            String portada = datos[3].replace("\'", "");
            String fComienzo = datos[4].replace("\'", "");
            String fFin = datos[5].replace("\'", "");
            String val = datos[6].replace("\'", "");
            String res = datos[7].replace("\'", "");
            String firekey = datos[8].replace("\'", "");
            int id = Integer.parseInt(idSinComillas);
            int idAutorInt = Integer.parseInt(idAutor);
            float valoracion = Float.parseFloat(val);
            Readings r = new Readings(id,tituloSinComillas,idAutorInt,portada,fComienzo,fFin,valoracion,res,firekey);
            //rm.insert(r);
        }
    }
    private void actualizaContadorClase(ReadingsManager readingsManager){
        List<Readings> lista = new ArrayList<>();
        lista = readingsManager.getReadings(Contract.TablaReadings.COLUMN_NAME_FFIN+" IS NOT NULL AND "+Contract.TablaReadings.COLUMN_NAME_FCOMIENZO+" IS NOT NULL",null,null);
        setLibrosLeidos(lista.size());
        lista.clear();
        lista = readingsManager.getReadings(Contract.TablaReadings.COLUMN_NAME_FFIN+" IS NULL AND "+Contract.TablaReadings.COLUMN_NAME_FCOMIENZO+" IS NOT NULL", null, null);
        setlibrosLeyendo(lista.size());
        lista.clear();
        lista = readingsManager.getReadings(Contract.TablaReadings.COLUMN_NAME_FFIN+" IS NULL AND "+ Contract.TablaReadings.COLUMN_NAME_FCOMIENZO+" IS NULL", null, null);
        setlibrosQuieroLeer(lista.size());
        lista.clear();
        lista = readingsManager.getReadings(null,null,null);
        lista.clear();
    }
    private void actualizarContadorLibros(){
        tvcount1.setText(String.valueOf(getLibrosLeidos()) +" libros");
        tvcount2.setText(String.valueOf(getlibrosLeyendo()) +" libros");
        tvcount3.setText(String.valueOf(getlibrosQuieroLeer()) +" libros");
    }
}
