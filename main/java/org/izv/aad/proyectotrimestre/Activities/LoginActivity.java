package org.izv.aad.proyectotrimestre.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import org.izv.aad.proyectotrimestre.DBConnection.AuthorsManager;
import org.izv.aad.proyectotrimestre.DBConnection.DBManager;
import org.izv.aad.proyectotrimestre.DBConnection.ReadingsManager;
import org.izv.aad.proyectotrimestre.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection;
import static org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection.firebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //  Declaro variables de clase.
    private Button btlogin, btregister;
    private EditText edcpassword, eduser, edpaassword;
    private ImageView iverror,ivback,ivlogo;
    private ProgressBar pbcircular;
    private TextInputLayout tilcpassword, tilemail, tilpassword;
    private TextView tvback, tvloginerror, tvsignup,tviniciando;
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

        //instanciando elementos del layout
        tviniciando = findViewById(R.id.tv_iniciando_sesion);
        btlogin = findViewById(R.id.btlogin);
        btregister = findViewById(R.id.btregister);
        edcpassword = findViewById(R.id.edcpassword);
        eduser = findViewById(R.id.edemail);
        edpaassword = findViewById(R.id.edpassword);
        ivback = findViewById(R.id.back_border);
        iverror = findViewById(R.id.iverror);
        ivlogo = findViewById(R.id.jpa);
        pbcircular = findViewById(R.id.pbcircular);
        tilcpassword = findViewById(R.id.tilcpassword);
        tilemail = findViewById(R.id.tilemail);
        tilpassword = findViewById(R.id.tilpassword);
        tvback = findViewById(R.id.tvback);
        tvloginerror = findViewById(R.id.tvloginerror);
        tvsignup = findViewById(R.id.tvsignup);
        setEvents();

        //  Comprobar si hay datos de login en las SharedPrefs.
        if (!(leerShared("user", "user").equals("null"))) {
            //  Si hay, iniciar sesión directamente.
            iniciando(); // Muestra y oculta lo necesario del layout.

            //  Iniciar sesión en FireBase
            firebaseAuth.signInWithEmailAndPassword(leerShared("user", "email"), leerShared("user", "pass"))
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        DBManager.syncronize(readingsManager,authorsManager,fbc,fbc.firebaseAuth.getCurrentUser().getUid());
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                Intent i = new Intent(getApplicationContext(), MainMenu.class);
                                                startActivity(i);
                                            }
                                        }, 2000);
                                    } else {
                                        iniciar(1,true);
                                        loginErrors(task.getException().toString());
                                    }
                                }
                            });
        }else {
            //  Si no, mostrar formulario de login.
            iniciar(1,false);

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
            pbcircular.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(getEmail(), getContrasenia())
                    .addOnCompleteListener(
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        DBManager.syncronize(readingsManager,authorsManager,fbc,fbc.firebaseAuth.getCurrentUser().getUid());
                                        escribirShared("user","user",firebaseAuth.getCurrentUser().getUid());
                                        escribirShared("user","email",getEmail());
                                        escribirShared("user","pass",getContrasenia());
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                Intent i = new Intent(getApplicationContext(), MainMenu.class);
                                                startActivity(i);
                                            }
                                        }, 2000);
                                    } else {
                                        eduser.setText("");
                                        edpaassword.setText("");
                                        loginErrors(task.getException().toString());
                                        iniciar(1,true);
                                    }
                                }
                            });
        }else{
            iniciar(1,true);
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
                                        escribirShared("user","user",firebaseAuth.getCurrentUser().getUid());
                                        escribirShared("user","email",getEmail());
                                        escribirShared("user","pass",getContrasenia());
                                        Intent i = new Intent(getApplicationContext(), MainMenu.class);
                                        startActivity(i);

                                    } else {
                                        eduser.setText("");
                                        edpaassword.setText("");
                                        edcpassword.setText("");
                                        loginErrors(task.getException().toString());
                                        iniciar(2,true);
                                    }
                                }
                            });

        }else{
            iniciar(2,true);
        }

        btregister.setClickable(true);
    }

    public void signup(){
        eduser.setText(null);
        edpaassword.setText(null);
        edcpassword.setText(null);
        iniciar(2,false);
    }

    public void back(){
        eduser.setText(null);
        edpaassword.setText(null);
        edcpassword.setText(null);
        iniciar(1,false);
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


    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void iniciando(){
        tviniciando.setVisibility(View.VISIBLE);
        ivback.setVisibility(View.VISIBLE);
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
        ivlogo.setVisibility(View.GONE);
    }


    private void iniciar(int accion, boolean error){
        if (accion == 1){
            edcpassword.setVisibility(View.GONE);
            tilcpassword.setVisibility(View.GONE);
            tvback.setVisibility(View.GONE);
            tvsignup.setVisibility(View.VISIBLE);
            btlogin.setVisibility(View.VISIBLE);
            btlogin.setClickable(true);
            btregister.setVisibility(View.GONE);
            edpaassword.setImeOptions(EditorInfo.IME_ACTION_SEND);
        }else if (accion == 2){
            edcpassword.setVisibility(View.VISIBLE);
            tilcpassword.setVisibility(View.VISIBLE);
            tvback.setVisibility(View.VISIBLE);
            tvsignup.setVisibility(View.GONE);
            btlogin.setVisibility(View.GONE);
            btregister.setVisibility(View.VISIBLE);
            btregister.setClickable(true);
            edpaassword.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            edcpassword.setImeOptions(EditorInfo.IME_ACTION_SEND);
        }

        if (error){
            iverror.setVisibility(View.VISIBLE);
            tvloginerror.setVisibility(View.VISIBLE);
        }else if (!error){
            iverror.setVisibility(View.GONE);
            tvloginerror.setVisibility(View.GONE);
        }
        ivlogo.setVisibility(View.VISIBLE);
        tviniciando.setVisibility(View.GONE);
        ivback.setVisibility(View.GONE);
        pbcircular.setVisibility(View.GONE);
        eduser.setVisibility(View.VISIBLE);
        edpaassword.setVisibility(View.VISIBLE);
        tilemail.setVisibility(View.VISIBLE);
        tilpassword.setVisibility(View.VISIBLE);
        eduser.setImeOptions(EditorInfo.IME_ACTION_NEXT);
    }

    // función que transforma los errores que devuelve la conexión con firebase y los muestra
    private void loginErrors(String task){
        switch (task){
            case "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.":
                tvloginerror.setText("El email está mal formado");
                break;
            case "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.":
                tvloginerror.setText("El usuario no existe");
                break;
            case "com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]":
                tvloginerror.setText("La contraseña debe tener al menos 6 caracteres");
                break;
            case "com.google.firebase.FirebaseNetworkException: A network error (such as timeout, interrupted connection or unreachable host) has occurred.":
                tvloginerror.setText("No hay conexión a internet");
                break;
            case "com.google.firebase.auth.FirebaseAuthUserCollisionException: The email address is already in use by another account.":
                tvloginerror.setText("El usuario ya existe");
                break;


            default:
                tvloginerror.setText("El e-mail o la contraseña son incorrectos");
        }

    }

    private void setEvents(){


        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(LoginActivity.this);
                iniciando();
                btlogin.setClickable(false);
                setEmail(eduser.getText().toString());
                setContrasenia(edpaassword.getText().toString());
                login();
            }
        });
        btregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciando();
                hideKeyboard(LoginActivity.this);
                btregister.setClickable(false);
                setEmail(eduser.getText().toString());
                setContrasenia(edpaassword.getText().toString());
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
        // listener para el imeoption send de contraseña
        edpaassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    hideKeyboard(LoginActivity.this);
                    iniciando();
                    btlogin.setClickable(false);
                    setEmail(eduser.getText().toString());
                    setContrasenia(edpaassword.getText().toString());
                    login();
                    handled = true;
                }
                return handled;
            }
        });

        // listener para el imeoption send de repetir contraseña
        edcpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    hideKeyboard(LoginActivity.this);
                    iniciando();
                    btregister.setClickable(false);
                    setEmail(eduser.getText().toString());
                    setContrasenia(edpaassword.getText().toString());
                    register();
                    handled = true;
                }
                return handled;
            }
        });
    }
}