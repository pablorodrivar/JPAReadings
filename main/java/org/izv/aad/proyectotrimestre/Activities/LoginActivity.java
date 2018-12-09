package org.izv.aad.proyectotrimestre.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import org.izv.aad.proyectotrimestre.DBConnection.AuthorsManager;
import org.izv.aad.proyectotrimestre.DBConnection.DBManager;
import org.izv.aad.proyectotrimestre.DBConnection.ReadingsManager;
import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.izv.aad.proyectotrimestre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection;
import static org.izv.aad.proyectotrimestre.Activities.MainActivity.TAG;
import static org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection.firebaseAuth;
public class LoginActivity extends AppCompatActivity {
    //  Declaro variables de clase.
    private Button btlogin, btregister;
    private EditText edcpassword, eduser, edpaassword;
    private ImageView iverror;
    private ProgressBar pbcircular;
    private TextInputLayout tilcpassword, tilemail, tilpassword;
    private TextView tvback, tvloginerror, tvsignup;
    private FireBaseConnection fbc;
    private ReadingsManager readingsManager;
    private AuthorsManager authorsManager;
    private VideoView vw;
    //  Algunas tienen sus Getters y Setters, incluidos a continuación.
    private String email,contrasenia;
    private void setEmail(String email){
        this.email = email;
    } private String getEmail(){
        return this.email;
    }
    private void setContrasenia(String contrasenia){ this.contrasenia = contrasenia; } private String getContrasenia(){return this.contrasenia;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  Orientación bloqueada a vertical.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("Login");
        //  Instanciar ReadingsManager, AuthorsManager y conexión a Firebase.
        readingsManager = new ReadingsManager(this);
        authorsManager = new AuthorsManager(this);
        fbc = new FireBaseConnection();
        //  Cargar el vídeo de fondo en bucle.
        vw = findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.vid);
        vw.setVideoURI(uri);
        vw.start();
        vw.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                vw.start();
            }
        });

        btlogin = findViewById(R.id.btlogin);
        btregister = findViewById(R.id.btregister);
        edcpassword = findViewById(R.id.edcpassword);
        eduser = findViewById(R.id.edemail);
        edpaassword = findViewById(R.id.edpassword);
        iverror = findViewById(R.id.iverror);
        pbcircular = findViewById(R.id.pbcircular);
        tilcpassword = findViewById(R.id.tilcpassword);
        tilemail = findViewById(R.id.tilemail);
        tilpassword = findViewById(R.id.tilpassword);
        tvback = findViewById(R.id.tvback);
        tvloginerror = findViewById(R.id.tvloginerror);
        tvsignup = findViewById(R.id.tvsignup);
        //  Comprobar si hay datos de login en las SharedPrefs.
        if (!(leerShared("user", "user").equals("null"))) {
            //  Si hay, iniciar sesión directamente.
            pbcircular.setVisibility(View.VISIBLE);
            eduser.setVisibility(View.GONE);
            edpaassword.setVisibility(View.GONE);
            tilemail.setVisibility(View.GONE);
            edcpassword.setVisibility(View.GONE);
            tilpassword.setVisibility(View.GONE);
            tilcpassword.setVisibility(View.GONE);
            tvsignup.setVisibility(View.GONE);
            tvback.setVisibility(View.GONE);
            tvloginerror.setVisibility(View.GONE);
            iverror.setVisibility(View.GONE);
            btlogin.setVisibility(View.GONE);
            btregister.setVisibility(View.GONE);

            //DBManager.syncronize(readingsManager,authorsManager,fbc,fbc.firebaseAuth.getCurrentUser().getUid());

            //  Iniciar sesión en FireBase
            firebaseAuth.signInWithEmailAndPassword(leerShared("user", "email"), leerShared("user", "pass"))
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.v(TAG, "46");
                                    if (task.isSuccessful()) {
                                        pbcircular.setVisibility(View.GONE);
                                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                                        startActivity(i);
                                        //new Syncronize().execute(firebaseAuth.getCurrentUser().getUid());
                                    } else {
                                        pbcircular.setVisibility(View.GONE);
                                        Log.v(MainActivity.TAG, "no logueado");

                                    }
                                }
                            });
        }else {
        //  Si no, mostrar formulario de login.
        iverror.setVisibility(View.INVISIBLE);
        tvloginerror.setVisibility(View.INVISIBLE);
        btregister.setVisibility(View.GONE);
        pbcircular.setVisibility(View.GONE);
        tilcpassword.setVisibility(View.GONE);
        tvback.setVisibility(View.GONE);

        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btlogin.setClickable(false);
                setEmail(eduser.getText().toString());
                setContrasenia(edpaassword.getText().toString());
                Log.v(TAG, "EMAIL: " + getEmail() + " CONTRASEÑA: " + getContrasenia());
                login();
            }
        });
        btregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btregister.setClickable(false);
                setEmail(eduser.getText().toString());
                setContrasenia(edpaassword.getText().toString());
                Log.v(TAG, "EMAIL: " + getEmail() + " CONTRASEÑA: " + getContrasenia());
                register();
            }
        });
        tvsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        tvback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }
    }

    public void login(){

        boolean login = true;
        if(getEmail().isEmpty()){
            tilemail.setError("You need to enter your email");
            login = false;
        }else{
            tilemail.setError(null);
        }

        if(getContrasenia().isEmpty()){
            tilpassword.setError("You need to enter your password");
            login = false;
        }else{
            tilpassword.setError(null);
        }

        if(login == true) {
            Log.v(TAG, "LOGEADOOOO");
            pbcircular.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(getEmail(), getContrasenia())
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.v(TAG, "46");
                                        DBManager.syncronize(readingsManager,authorsManager,fbc,fbc.firebaseAuth.getCurrentUser().getUid());
                                        Log.v(MainActivity.TAG, "logueado"+firebaseAuth.getCurrentUser().getEmail());
                                        tvloginerror.setVisibility(View.INVISIBLE);
                                        iverror.setVisibility(View.INVISIBLE);
                                        escribirShared("user","user",firebaseAuth.getCurrentUser().getUid());
                                        escribirShared("user","email",getEmail());
                                        escribirShared("user","pass",getContrasenia());
                                        pbcircular.setVisibility(View.GONE);
                                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                                        startActivity(i);
                                    } else {
                                        Log.v(MainActivity.TAG, "no logueado");
                                        eduser.setText("");
                                        edpaassword.setText("");
                                        tvloginerror.setText("El e-mail o la contraseña son incorrectos.");
                                        iverror.setVisibility(View.VISIBLE);
                                        tvloginerror.setVisibility(View.VISIBLE);
                                        btlogin.setClickable(true);
                                        pbcircular.setVisibility(View.GONE);
                                    }
                                }
                            });
        }
        btlogin.setClickable(true);
    }

    public void register(){

        boolean register = true;

        if(getEmail().isEmpty()){
            tilemail.setError("You need to enter your email");
            register = false;
        }else{
            tilemail.setError(null);
        }

        if(getContrasenia().isEmpty()){
            tilpassword.setError("You need to enter your password");
            register = false;
        }else{
            tilpassword.setError(null);
        }

        if(edcpassword.getText().toString().isEmpty()){
            tilcpassword.setError("You need to repeat your password");
            register = false;
        } else if(edcpassword.getText().toString().compareTo(edpaassword.getText().toString())!=0){
            tilcpassword.setError("Your password doesn't match");
            register = false;
        }else{
            tilcpassword.setError(null);
        }

        if(register == true) {

            pbcircular.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(eduser.getText().toString(), edpaassword.getText().toString()).
                    addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.v(MainActivity.TAG, "registrado y logueado"+firebaseAuth.getCurrentUser().getEmail());
                                        tvloginerror.setVisibility(View.INVISIBLE);
                                        iverror.setVisibility(View.INVISIBLE);
                                        escribirShared("user","user",firebaseAuth.getCurrentUser().getUid());
                                        escribirShared("user","email",getEmail());
                                        escribirShared("user","pass",getContrasenia());
                                        pbcircular.setVisibility(View.GONE);
                                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                                        startActivity(i);

                                    } else {
                                        Log.v(MainActivity.TAG, "no registrado");
                                        eduser.setText("");
                                        edpaassword.setText("");
                                        edcpassword.setText("");
                                        tvloginerror.setText("El usuario es incorrecto o ya existe");
                                        iverror.setVisibility(View.VISIBLE);
                                        tvloginerror.setVisibility(View.VISIBLE);
                                        btregister.setClickable(true);
                                        pbcircular.setVisibility(View.GONE);
                                    }
                                }
                            });

        }
        btregister.setClickable(true);
    }

    public void signup(){
        eduser.setText(null);
        edpaassword.setText(null);
        edcpassword.setText(null);
        btlogin.setVisibility(View.GONE);
        tvsignup.setVisibility(View.GONE);
        tilcpassword.setVisibility(View.VISIBLE);
        btregister.setVisibility(View.VISIBLE);
        tvback.setVisibility(View.VISIBLE);
        tvloginerror.setVisibility(View.INVISIBLE);
        iverror.setVisibility(View.INVISIBLE);
    }

    public void back(){
        eduser.setText(null);
        edpaassword.setText(null);
        edcpassword.setText(null);
        tilcpassword.setVisibility(View.INVISIBLE);
        btregister.setVisibility(View.INVISIBLE);
        tvback.setVisibility(View.INVISIBLE);
        btlogin.setVisibility(View.VISIBLE);
        tvsignup.setVisibility(View.VISIBLE);
        tvloginerror.setVisibility(View.INVISIBLE);
        iverror.setVisibility(View.INVISIBLE);
    }

    private String leerShared(String shared,String key){
        SharedPreferences prefs = getSharedPreferences(shared, Context.MODE_PRIVATE);
        return prefs.getString(key, "null");
    }

    private void escribirShared(String shared, String key, String value){
        SharedPreferences prefs = getSharedPreferences(shared, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = prefs.edit();
                                        editor.putString(key, value);
                                        editor.apply();
    }

    /*private class Syncronize extends AsyncTask<String, Void, Void>{
        ReadingsManager readingsManager = new ReadingsManager(LoginActivity.this);
        AuthorsManager authorsManager = new AuthorsManager(LoginActivity.this);
        FireBaseConnection fbc = new FireBaseConnection();

        @Override
        protected Void doInBackground(String... strings) {
            DBManager.syncronize(readingsManager,authorsManager,fbc,strings[0]);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

        }
    }*/
}