package org.izv.aad.proyectotrimestre.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.izv.aad.proyectotrimestre.DBConnection.*;
import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.izv.aad.proyectotrimestre.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class AniadirEditar extends AppCompatActivity {
    private static final int LOAD_IMAGE_CODE = 1;
    private static final String TAG = "JAP";
    private String option, key, selectedItem, i_date, e_date, titulo_et, autor_et, resumen_et;
    private Uri imageURI;
    private float rating;
    private Calendar c;
    private ImageButton imageButton;
    private Button fecha_comienzo, fecha_fin;
    private EditText titulo, autor, resumen;
    private Spinner aniadir;
    private RatingBar ratingBar;
    public ReadingsManager rm;
    public AuthorsManager am;
    public FireBaseConnection fbc = new FireBaseConnection();
    private Readings reading;
    private Author author;
    private Bitmap imageBM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aniadir_editar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        init();

        if (key != null) {
            reading = getReadingReference();
            Log.v(TAG, reading.toString());
            author = getAuthorReference(reading.getId_autor());
        } else {
            reading = new Readings();
            author = new Author();
        }

        if (option != null) {
            if (option.equals("edit")) {
                setTitle(R.string.editar);
                setEditElements();
            } else {
                setTitle(R.string.add);
            }
            setEventsHandler();
        }
    }

    private void init() {
        option = getIntent().getStringExtra("aniadireditar");
        key = getIntent().getStringExtra("firebasekey");
        //author = getIntent().getStringExtra()
        rm = new ReadingsManager(this);
        am = new AuthorsManager(this);
        titulo = findViewById(R.id.titulo);
        titulo_et = "";
        autor = findViewById(R.id.autor);
        autor_et = "";
        resumen = findViewById(R.id.resumen);
        resumen_et = "";
        imageButton = findViewById(R.id.imageButton);
        fecha_comienzo = findViewById(R.id.fecha_comienzo);
        fecha_fin = findViewById(R.id.fecha_fin);
        aniadir = findViewById(R.id.aniadir);
        ratingBar = findViewById(R.id.ratingBar);
        selectedItem = aniadir.getSelectedItem().toString();
    }

    private void insertReading() {
        if (option.equals("add")) {
            if(getFormElements()) {

                reading.setFireBaseKey();
                String root = "/" + fbc.firebaseAuth.getCurrentUser().getUid() + "/" + reading.getFireBaseKey() + ".jpg";
                Log.v(TAG + "HOLA", root);
                reading.setDrawable_portada(root);
                if(imageBM != null){
                    DBManager.insert(rm, fbc, reading,imageBM);
                }else{
                    imageBM = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.book);
                    DBManager.insert(rm, fbc, reading,imageBM);
                }

                Log.v(TAG, "insertado");
                Intent i = new Intent(AniadirEditar.this, Mostrar.class);
                Log.v(TAG, "toMAIN");
                startActivity(i);
            }
        }
        else if(option.equals("edit")) {
            if(getFormElements()) {
                if (am.authorExists(autor_et)) {
                    reading.setId_autor(am.getAuthor(autor_et).getId());
                } else {
                    DBManager.update(am, fbc, author);
                    author.setId(am.getAuthor(author.getNombre()).getId());
                    reading.setId_autor(author.getId());
                }
                reading.setFireBaseKey();
                String root = "/" + fbc.firebaseAuth.getCurrentUser().getUid() + "/" + reading.getFireBaseKey() + ".jpg";
                reading.setDrawable_portada(root);
                if(imageBM != null){
                    DBManager.update(rm, fbc, reading,imageBM);
                }else{
                    imageBM = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.book);
                    DBManager.update(rm, fbc, reading,imageBM);
                }
                Log.v(TAG, "insertado");
                Log.v(TAG, "toDISPLAY");
                Intent i = new Intent(AniadirEditar.this, DisplayReading.class);
                i.putExtra("reading", reading.getFireBaseKey());
                startActivity(i);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == LOAD_IMAGE_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;

            if (data != null) {
                uri = data.getData();
                imageURI = uri;
                try {
                    imageBM = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    Log.v(TAG, e.toString());
                }
                imageButton.setImageBitmap(imageBM);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_aniadir_editar, menu);
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
            insertReading();
        }

        return super.onOptionsItemSelected(item);
    }

    private Author getAuthorReference(int id) {
        return am.getAuthor(id);
    }

    private boolean getFormElements() {
        boolean correctlySet = false;
        titulo_et = titulo.getText().toString();
        autor_et = autor.getText().toString();

        if (titulo_et.equals("") || autor_et.equals("")) {
            Toast.makeText(AniadirEditar.this, R.string.rellena, Toast.LENGTH_SHORT).show();
            titulo.setError(getString(R.string.input_error));
            autor.setError(getString(R.string.input_error));
            //Log.v(TAG, "titulo:" + titulo_et + " autor: " + autor_et + " imagen: " + imageURI.toString());
        } else if(!titulo_et.equals("")){
            Log.v(TAG, titulo_et);
            reading.setTitulo(titulo_et);
            if (titulo_et.equals("")) {
                titulo.setError(getString(R.string.input_error));
            }
            author.setNombre(autor_et);
            Log.v(TAG, "NOMBRE DEL AUTOR: " + autor_et);
            if (autor_et.equals("")) {
                autor.setError(getString(R.string.input_error));
            }
            if (am.authorExists(autor_et)) {
                    reading.setId_autor(am.getAuthor(autor_et).getId());
                } else {
                    author.setFireBaseKey();
                    DBManager.insert(am, fbc, author);
                    author.setId(am.getAuthor(author.getNombre()).getId());
                    reading.setId_autor(author.getId());
                }
            //reading.setId_autor(author.getId());
            Log.v(TAG, "ID DEL AUTOR: " + author.getId());
            reading.setValoracion(rating);
            if(imageURI != null){
                reading.setDrawable_portada(imageURI.toString());
            }
            resumen_et = resumen.getText().toString();
            reading.setResumen(resumen_et);
            if (i_date != null) {
                reading.setFecha_comienzo(i_date);
            }
            if (e_date != null) {
                reading.setFecha_fin(e_date);
            }
            correctlySet = true;
        }

        return correctlySet;
    }

        private Readings getReadingReference () {
            List<Readings> readings = rm.getReadings(Contract.TablaReadings.COLUMN_NAME_FIREBASEKEY + "='" + key + "'", null, null);
            return readings.get(0);
        }

        private void setEditElements () {
            setTitle(R.string.editar);
            titulo_et = reading.getTitulo();
            titulo.setText(titulo_et);
            autor_et = author.getNombre();
            autor.setText(autor_et);
            resumen_et = reading.getResumen();
            resumen.setText(resumen_et);
            //imageButton.setImageURI(Uri.parse(reading.getDrawable_portada()));
            imageButton.setImageBitmap(DBManager.getImage(reading));
            imageButton.setAlpha(0.2f);
            fecha_comienzo.setText(reading.getFecha_comienzo());
            fecha_fin.setText(reading.getFecha_fin());
            ratingBar.setRating(reading.getValoracion());
            resumen.setText(reading.getResumen());
        }

        private void setEventsHandler () {
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickImage();
                }
            });

            aniadir.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedItem = aniadir.getSelectedItem().toString();
                    switch (selectedItem) {
                        case "Quiero Leer":
                            fecha_comienzo.setVisibility(View.INVISIBLE);
                            fecha_fin.setVisibility(View.INVISIBLE);
                            break;
                        case "Leyendo":
                            fecha_comienzo.setVisibility(View.VISIBLE);
                            fecha_fin.setVisibility(View.INVISIBLE);
                            break;
                        case "Leidos":
                            fecha_comienzo.setVisibility(View.VISIBLE);
                            fecha_fin.setVisibility(View.VISIBLE);
                            break;
                        default:
                            fecha_comienzo.setVisibility(View.VISIBLE);
                            fecha_fin.setVisibility(View.VISIBLE);
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            fecha_comienzo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    c = Calendar.getInstance();
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AniadirEditar.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                            i_date = mDay + "/" + (mMonth + 1) + "/" + mYear;
                            fecha_comienzo.setText(i_date);
                        }
                    }, day, month, year);
                    datePickerDialog.show();
                }
            });

            fecha_fin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    c = Calendar.getInstance(TimeZone.getDefault());
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int month = c.get(Calendar.MONTH);
                    int year = c.get(Calendar.YEAR);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AniadirEditar.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                            e_date = mDay + "/" + (mMonth + 1) + "/" + mYear;
                            fecha_fin.setText(e_date);

                        }
                    }, day, month, year);
                    datePickerDialog.show();
                }
            });

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    rating = ratingBar.getRating();
                }
            });
        }

        private void pickImage () {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, LOAD_IMAGE_CODE);
        }
    }
