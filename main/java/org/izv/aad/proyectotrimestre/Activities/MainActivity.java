package org.izv.aad.proyectotrimestre.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.izv.aad.proyectotrimestre.DBConnection.AuthorsManager;
import org.izv.aad.proyectotrimestre.DBConnection.DBManager;
import org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection;
import org.izv.aad.proyectotrimestre.DBConnection.ReadingsManager;
import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.izv.aad.proyectotrimestre.R;

import static org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection.firebaseAuth;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "JAP";
    private Button button;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FireBaseConnection fbc = new FireBaseConnection();
        final FirebaseUser fbUser;
        firebaseAuth.signOut();
        ReadingsManager readingsManager = new ReadingsManager(this);
        AuthorsManager authorsManager = new AuthorsManager(this);
        // BORRA TODOS LOS DATOS PREVIOS INSERTA DATOS DE MUESTRA EN SQLITE
        datosDeMuestra(readingsManager,authorsManager);
        // HAY DOS OPCIONES, O PONEMOS LA FUNCION SIGN IN COMO STRING ASI DEVUELVA UID, POR TANTO HASTA QUE NO TERMINE
        // EL LOGIN NO SIGUE EL PROGRAMA, CUIDADO CUANDO TARDA MUCHO QUE DIJO RUT QUE ANDROID CIERRA LA APP, POR TANTO
        // NO ES RECOMENDABLE
        //user = fbc.signIn("lapassesprueba@prueba.com","prueba");
         //user = firebaseAuth.getCurrentUser();
        //mAuth=FirebaseAuth.getInstance();
        //mAuth.signInWithEmailAndPassword("lapassesprueba@prueba.com","prueba");

        /*SharedPreferences prefs = getSharedPreferences("user",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user", firebaseAuth.getCurrentUser().getUid());
        editor.apply();*/

        /*Intent i = new Intent(MainActivity.this, MainMenu.class);
        startActivity(i);*/

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                i.putExtra("categoria",2);
                startActivity(i);
            }
        });
        //readingsManager.cerrar();
        //authorsManager.cerrar();
    }
    private void datosDeMuestra(ReadingsManager readingsManager, AuthorsManager authorsManager){
        readingsManager.deleteTodo();
        authorsManager.deleteTodo();
        Author lorca = new Author("Federico García Lorca");
        authorsManager.insert(lorca);
        lorca.setId(authorsManager.getAuthor(lorca.getNombre()).getId());

        Author cervantes = new Author("Miguel de Cervantes");
        authorsManager.insert(cervantes);
        cervantes.setId(authorsManager.getAuthor(cervantes.getNombre()).getId());

        Author lope = new Author("Lope de Vega");
        authorsManager.insert(lope);
        lope.setId(authorsManager.getAuthor(lope.getNombre()).getId());

        Readings quijote = new Readings("Don Quijote de la Mancha", cervantes.getId(), "https://estaticos.muyhistoria.es/media/cache/400x300_thumb/uploads/images/pyr/57036af75cafe801f93c0be9/c-don-quijote-mancha_0.jpg", "11-17-2018", "11-19-2018", 3, "Resumen Don Quijote");
        readingsManager.insert(quijote);

        Readings fuenteovejuna = new Readings("Fuenteovejuna", lope.getId(), "https://estaticos.muyhistoria.es/media/cache/400x300_thumb/uploads/images/pyr/57036af75cafe801f93c0be9/c-don-quijote-mancha_0.jpg", "11-15-2018", "11-17-2018", 4, "Resumen Fuenteovejuna");
        readingsManager.insert(fuenteovejuna);

        //Readings romancero = new Readings("Romancero Gitano", lorca.getId(), R.drawable.romancero, "11-18-2018", "11-20-2018", 2, "Resumen Romancero Gitano");
        //readingsManager.insert(romancero);

        //Readings prueba = new Readings();
        //readingsManager.insert(prueba);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //firebaseAuth.signOut();
        Log.v(TAG, "On Destroy: ");
    }

}

