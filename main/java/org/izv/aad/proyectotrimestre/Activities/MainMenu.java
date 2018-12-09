package org.izv.aad.proyectotrimestre.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.izv.aad.proyectotrimestre.Activities.MenuRecycler.AdapterMenu;
import org.izv.aad.proyectotrimestre.DBConnection.AuthorsManager;
import org.izv.aad.proyectotrimestre.DBConnection.Contract;
import org.izv.aad.proyectotrimestre.DBConnection.DBManager;
import org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection;
import org.izv.aad.proyectotrimestre.DBConnection.ReadingsManager;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.izv.aad.proyectotrimestre.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection.firebaseAuth;

public class MainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        authorsManager = new AuthorsManager(this);
        readingsManager = new ReadingsManager(this);
        fbc = new FireBaseConnection();
        setSupportActionBar(toolbar);

        rvautores = findViewById(R.id.rvautores);
        tvcount1 = findViewById(R.id.tvcount1);
        tvcount2 = findViewById(R.id.tvcount2);
        tvcount3 = findViewById(R.id.tvcount3);

        if(firebaseAuth.getCurrentUser() != null){
            email = firebaseAuth.getCurrentUser().getEmail();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

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
        listaLibrosLeidos = readingsManager.getReadings(Contract.TablaReadings.COLUMN_NAME_FFIN+" IS NOT NULL AND "+Contract.TablaReadings.COLUMN_NAME_FCOMIENZO+" IS NOT NULL",null,null);
        int count1 = listaLibrosLeidos.size();
        listaLibrosLeidos = readingsManager.getReadings(Contract.TablaReadings.COLUMN_NAME_FFIN+" IS NULL AND "+Contract.TablaReadings.COLUMN_NAME_FCOMIENZO+" IS NOT NULL", null, null);
        int count2 = listaLibrosLeidos.size();
        listaLibrosLeidos = readingsManager.getReadings(Contract.TablaReadings.COLUMN_NAME_FFIN+" IS NULL AND "+ Contract.TablaReadings.COLUMN_NAME_FCOMIENZO+" IS NULL", null, null);
        int count3 = listaLibrosLeidos.size();

        tvcount1.setText(String.valueOf(count1) +" libros");
        tvcount2.setText(String.valueOf(count2) +" libros");
        tvcount3.setText(String.valueOf(count3) +" libros");
    }

    public void initRecyclerView(){
        listAuthors.clear();
        readingsList = readingsManager.getReadings(null,null,null);
        for(Readings r : readingsList){
            listAuthors.add(readingsManager.getNombreAutor(r));
        }

        Set<String> hs = new HashSet<>();
        hs.addAll(listAuthors);
        listAuthors.clear();
        listAuthors.addAll(hs);
        listAuthors = ordenarAutores(listAuthors);

        if(listAuthors.isEmpty()){
        }else {
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
            rvautores.setAdapter(mAdapter);

            mLayoutManager = new LinearLayoutManager(this);
            rvautores.setLayoutManager(mLayoutManager);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add) {
            Intent intent = new Intent(this, AniadirEditar.class);
            intent.putExtra("aniadireditar", "add");
            startActivity(intent);
        } else if (id == R.id.cloud) {

        } else if (id == R.id.search_php) {
            Intent intent = new Intent(this, SearchActivityPHP.class);
            startActivity(intent);
        } else if (id == R.id.theme) {

        } else if (id == R.id.help) {

        } else if (id == R.id.signout) {
            firebaseAuth.signOut();
            readingsManager.deleteTodo();
            authorsManager.deleteTodo();
            SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void leidos(View view){
        Intent intent = new Intent(getApplicationContext(), Mostrar.class);
        intent.putExtra("categoria", 2);
        startActivity(intent);
    }

    public void leyendo(View view){
        Intent intent = new Intent(getApplicationContext(), Mostrar.class);
        intent.putExtra("categoria", 1);
        startActivity(intent);
    }

    public void vertodos(View view){
        Intent intent = new Intent(getApplicationContext(), Mostrar.class);
        intent.putExtra("categoria", 4);
        startActivity(intent);
    }

    public void quieroleer(View view){
        Intent intent = new Intent(getApplicationContext(), Mostrar.class);
        intent.putExtra("categoria", 5);
        startActivity(intent);
    }

    public List<String> ordenarAutores(List<String> list){
        Log.v(MainActivity.TAG, "- Ordenando Autores -");
        for(int j=0; j<list.size()-1; j++){
            Log.v(MainActivity.TAG, "j: "+String.valueOf(j));
            for(int i=0; i<list.size()-j-1; i++){
                Log.v(MainActivity.TAG, "i: "+String.valueOf(i));
                if(list.get(i).compareToIgnoreCase(list.get(i+1))>0){
                    String aux = list.get(i);
                    list.set(i, list.get(i+1));
                    list.set(i+1, aux);
                }
            }
        }
        return list;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //firebaseAuth.signOut();
        Log.v(MainActivity.TAG, "On Destroy: "+firebaseAuth.getInstance());
    }
}
