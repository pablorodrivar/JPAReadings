package org.izv.aad.proyectotrimestre.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import org.izv.aad.proyectotrimestre.DBConnection.AuthorsManager;
import org.izv.aad.proyectotrimestre.DBConnection.Contract;
import org.izv.aad.proyectotrimestre.DBConnection.DBManager;
import org.izv.aad.proyectotrimestre.DBConnection.FireBaseConnection;
import org.izv.aad.proyectotrimestre.DBConnection.ReadingsManager;
import org.izv.aad.proyectotrimestre.POJO.Author;
import org.izv.aad.proyectotrimestre.POJO.Readings;
import org.izv.aad.proyectotrimestre.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static org.izv.aad.proyectotrimestre.Activities.MainActivity.TAG;
import static org.izv.aad.proyectotrimestre.Activities.MainMenu.fbc;

public class DisplayReading extends AppCompatActivity {
    public final String TAG = "JAP";
    private ImageView imageView;
    private TextView titulo, autor, resumen, f_inicio, f_fin;
    private RatingBar ratingBar;
    private List<Readings> readings;
    private Readings reading;
    private Author author;
    private ReadingsManager readingsManager;
    private AuthorsManager authorsManager;
    private FireBaseConnection fbc;
    private String reading_id;

    private Bitmap imagebm;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void init(){
        imageView = findViewById(R.id.imageView);
        titulo = findViewById(R.id.titulo);
        autor = findViewById(R.id.autor_e);
        resumen = findViewById(R.id.resumen);
        ratingBar = findViewById(R.id.ratingBar);
        f_inicio = findViewById(R.id.fecha_inicio);
        f_fin = findViewById(R.id.fecha_fin);

        readingsManager = new ReadingsManager(this);
        authorsManager = new AuthorsManager(this);
        fbc = new FireBaseConnection();
        reading_id = getIntent().getStringExtra("reading");
        //Cursor c = readingsManager.getCursor(Contract.TablaReadings.COLUMN_NAME_FIREBASEKEY + "='" + reading_id + "'",  null ,null);
        //Log.v(TAG, c.toString());
        readings = readingsManager.getReadings(Contract.TablaReadings.COLUMN_NAME_FIREBASEKEY + "='" + reading_id + "'",  null ,null);
        reading = readings.get(0);
        Log.v(TAG,reading.toString());
        author = authorsManager.getRow(authorsManager.getCursor(Contract.TablaAuthor.COLUMN_NAME_IDAUTOR + "='" + String.valueOf(reading.getId_autor()) + "'",null));

        //Bitmap bmp = DBManager.getImage(reading);
        StorageReference imageRef = fbc.storageRef.child(reading.getDrawable_portada());
            Log.v(TAG, reading.getDrawable_portada() + " PORTADA PROCESANDOSE");
            final long ONE_MEGABYTE = 1024 * 1024;
            imageRef.getBytes(ONE_MEGABYTE).addOnCompleteListener(new OnCompleteListener<byte[]>() {
                @Override
                public void onComplete(@NonNull Task<byte[]> task) {
                    if(task.isSuccessful()) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        Bitmap bmp = BitmapFactory.decodeByteArray(task.getResult(), 0, task.getResult().length, options);
                        Log.v(TAG, reading.getDrawable_portada() + " RUTA DE LA PORTADA");
                        imageView.setImageBitmap(bmp);
                        imagebm = bmp;
                        //progress.setVisibility(View.GONE);
                    }else{
                        imageView.setImageResource(R.drawable.ic_no_internet);
                        imagebm = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.ic_no_internet);
                    }
                }
            });
        //imageView.setImageBitmap(bmp);
        getSupportActionBar().setTitle(reading.getTitulo());
        getSupportActionBar().setSubtitle(readingsManager.getNombreAutor(reading));
        titulo.setText(reading.getTitulo());
        autor.setText(author.getNombre());
        f_inicio.setText("Fecha comienzo: \n"+reading.getFecha_comienzo());
        f_fin.setText("Fecha fin: \n"+reading.getFecha_fin());
        resumen.setText(reading.getResumen());
        ratingBar.setRating(reading.getValoracion());

        FloatingActionButton fab = findViewById(R.id.print);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPrint();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_reading);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.editar:
                toEditar();
                return true;
            case R.id.borrar:
                showAlert();
                //Intent i = new Intent(DisplayReading.this, Mostrar.class);
                //startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void delete(){
        String snackText = reading.getTitulo() + " " + getString(R.string.deleted);
        DBManager.delete(authorsManager,fbc,author);
        DBManager.delete(readingsManager,fbc,reading);
        Intent i = new Intent(DisplayReading.this, MainMenu.class);
        startActivity(i);
        Toast.makeText(DisplayReading.this, snackText,Toast.LENGTH_LONG).show();
    }

    public void toEditar(){
        Intent i = new Intent(DisplayReading.this, AniadirEditar.class);
        i.putExtra("aniadireditar", "edit");
        i.putExtra("firebasekey", reading.getFireBaseKey());
        i.putExtra("id_autor", author.getId());
        startActivity(i);
    }


    public void doPrint(){
        //Get a PrintManager instance
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        //Set job name, wich will be displayed in the print queue
        String jobName = this.getString(R.string.app_name) + " Document";


        //Start a print
        printManager.print(jobName, new MyPrintDocumentAdapter(this),null);
    }

    public void showAlert(){
        AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(DisplayReading.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(DisplayReading.this);
                }
                builder.setTitle("Borrar Libro")
                .setMessage("¿Estás seguro de que quieres borrar el libro?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }
                 })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                 })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    class MyPrintDocumentAdapter extends PrintDocumentAdapter {

        Context context;
        private int pageHeight;
        private int pageWidth;
        public PdfDocument myPdfDocument;
        public int totalpages = 1;

        public MyPrintDocumentAdapter(Context context) {
            this.context = context;
        }

        @Override
        public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, PrintDocumentAdapter.LayoutResultCallback callback, Bundle metadata) {

            //Create a new PdfDocument with the requested page attributes
            myPdfDocument = new PrintedPdfDocument(context, newAttributes);

            pageHeight = newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
            pageWidth = newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

            //Hay que preveer que el usuario puede cancelar la impresión
            if (cancellationSignal.isCanceled()) {
                callback.onLayoutCancelled();
                return;
            }

            //Return print information to print framework // crear archivo de metadatos
            if (totalpages > 0) {
                PrintDocumentInfo.Builder builder = new PrintDocumentInfo.Builder("print_output.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(totalpages);

                PrintDocumentInfo info = builder.build();
                callback.onLayoutFinished(info, true);
            } else {
                callback.onLayoutFailed("Page count is zero.");
            }
        }

        //lo que hace es ir pintando cada una de las páginas
        @Override
        public void onWrite(final PageRange[] pageRanges, final ParcelFileDescriptor destination, final CancellationSignal cancellationSignal, final WriteResultCallback callback) {

            //escribimos según el numero de páginas
            for (int i = 0; i < totalpages; i++) {
                if (pageInRange(pageRanges, i)) {
                    PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                            pageHeight, i).create();

                    PdfDocument.Page page =
                            myPdfDocument.startPage(newPage);

                    if (cancellationSignal.isCanceled()) {
                        callback.onWriteCancelled();
                        myPdfDocument.close();
                        myPdfDocument = null;
                        return;
                    }
                    drawPage(page, i);
                    myPdfDocument.finishPage(page);
                }
            }

            //lo mandamos como un flujo de salida
            try {
                myPdfDocument.writeTo(new FileOutputStream(
                        destination.getFileDescriptor()));
            } catch (IOException e) {
                callback.onWriteFailed(e.toString());
                return;
            } finally {
                myPdfDocument.close();
                myPdfDocument = null;
            }

            callback.onWriteFinished(pageRanges);
        }

        private boolean pageInRange(PageRange[] pageRanges, int page) {
            for (int i = 0; i < pageRanges.length; i++) {
                if ((page >= pageRanges[i].getStart()) &&
                        (page <= pageRanges[i].getEnd()))
                    return true;
            }
            return false;
        }

        private void drawPage(PdfDocument.Page page, int pagenumber) {

            int bmwidth = imagebm.getWidth();
            int bmheight = imagebm.getHeight();
            Log.v(TAG, "width: " + bmwidth + "  height: " + bmheight);
            while (bmwidth > 300 || bmheight > 300) {
                bmwidth = bmwidth - bmwidth / 10;
                bmheight = bmheight - bmheight / 10;
            }
            imagebm = Bitmap.createScaledBitmap(imagebm, bmwidth, bmheight, false);
            bmheight = imagebm.getHeight();


            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);

            int leftMargin = 54;

            int line = 72;
            int linea = (pageWidth - 2 * leftMargin) / 10;
            float x = (pageWidth - imagebm.getWidth()) / 2;

            paint.setTextSize(40);
            float textMeasure = paint.measureText(titulo.getText().toString());
            canvas.drawText(titulo.getText().toString(), (pageWidth - textMeasure) / 2, line, paint);
            line = line + 40;
            canvas.drawBitmap(imagebm, x, line, paint);


            paint.setTextSize(24);
            line = line + bmheight + 50;
            textMeasure = paint.measureText("Título : " + titulo.getText().toString());
            canvas.drawText("Título : " + titulo.getText().toString(), (pageWidth - textMeasure) / 2, line, paint);
            textMeasure = paint.measureText("Autor : " + autor.getText().toString());
            canvas.drawText("Autor : " + autor.getText().toString(), (pageWidth - textMeasure) / 2, line + 30, paint);

            String[] fechainicio = f_inicio.getText().toString().split("\n");
            textMeasure = paint.measureText(fechainicio[0]);
            canvas.drawText(fechainicio[0], (pageWidth - textMeasure) / 4, line + 60, paint);
            textMeasure = paint.measureText(fechainicio[1]);
            canvas.drawText(fechainicio[1], (pageWidth - textMeasure) / 4, line + 85, paint);

            String[] fechafin = f_fin.getText().toString().split("\n");
            textMeasure = paint.measureText(fechafin[0]);
            canvas.drawText(fechafin[0], (pageWidth - textMeasure) * 3 / 4, line + 60, paint);
            textMeasure = paint.measureText(fechafin[1]);
            canvas.drawText(fechafin[1], (pageWidth - textMeasure) * 3 / 4, line + 85, paint);

            textMeasure = paint.measureText("Valoración : " + ratingBar.getNumStars());
            canvas.drawText(String.valueOf("Valoración : " + ratingBar.getNumStars()), (pageWidth - textMeasure) / 2, line + 115, paint);

            paint.setTextSize(20);
            String[] palabras = resumen.getText().toString().split(" ");
            Log.v(TAG, "Tamaño resumen: " + resumen.getText().toString().length());
            Log.v(TAG, "Tamaño array de PALABRAS: " + palabras.length);
            Log.v(TAG, "Ancho de página: " + pageWidth);
            Log.v(TAG, "Margen de página: " + leftMargin);
            Log.v(TAG, "Tamaño de linea: " + linea);
            int posicion = 0;
            line = line + 150;
            while (posicion < palabras.length) {

                String texto = "";
                int letras;
                for (int i = 0; i < linea && posicion < palabras.length; i = i + letras) {
                    texto = texto + palabras[posicion] + " ";
                    Log.v(TAG, palabras[posicion]);
                    letras = palabras[posicion].length() + 1;
                    posicion++;
                    Log.v(TAG, "Contador de posición: " + posicion + " = i: " + i);
                }


                canvas.drawText(texto, leftMargin, line, paint);
                //Log.v(TAG, "texto: "+texto);

                line = line + 20;

            }

        }
    }
}
