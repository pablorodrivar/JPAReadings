package org.izv.aad.proyectotrimestre.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import org.izv.aad.proyectotrimestre.R;

public class ThemeActivity extends AppCompatActivity {

    private Button btaplicar;
    private RadioButton rbt1, rbt2, rbt3, rbt4, rbt5;
    public static SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        leerSharedTheme(this, true);
        setContentView(R.layout.activity_theme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Apariencia");

        btaplicar = findViewById(R.id.btaplicar);
        rbt1 = findViewById(R.id.rbt1);
        rbt2 = findViewById(R.id.rbt2);
        rbt3 = findViewById(R.id.rbt3);
        rbt4 = findViewById(R.id.rbt4);
        rbt5 = findViewById(R.id.rbt5);

        btaplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbt1.isChecked()==true){
                    sharedPreferences.edit().putInt("value",1).apply();
                }
                if(rbt2.isChecked()==true){
                    sharedPreferences.edit().putInt("value",2).apply();
                }
                if(rbt3.isChecked()==true){
                    sharedPreferences.edit().putInt("value",3).apply();
                }
                if(rbt4.isChecked()==true){
                    sharedPreferences.edit().putInt("value",4).apply();
                }
                if(rbt5.isChecked()==true){
                    sharedPreferences.edit().putInt("value",5).apply();
                }
                if (rbt1.isChecked()==true || rbt2.isChecked()==true || rbt3.isChecked()==true
                        || rbt4.isChecked()==true || rbt5.isChecked()==true){
                    Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(intent);
                }else {
                    Toast toast1 =
                            Toast.makeText(getApplicationContext(),
                                    "Debes seleccionar un tema", Toast.LENGTH_SHORT);

                    toast1.show();
                }

            }
        });
    }

    public static void leerSharedTheme(Context context){
        sharedPreferences = context.getSharedPreferences("Themes", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("value", 1);
        switch (theme){
            case 1: context.setTheme(R.style.AppTheme_NoActionBar);
                break;
            case 2: context.setTheme(R.style.AppTheme2_NoActionBar);
                break;
            case 3: context.setTheme(R.style.AppTheme3_NoActionBar);
                break;
            case 4: context.setTheme(R.style.AppTheme4_NoActionBar);
                break;
            case 5: context.setTheme(R.style.AppTheme5_NoActionBar);
                break;
        }
    }

    public static void leerSharedTheme(Context context, boolean actionBar){
        sharedPreferences = context.getSharedPreferences("Themes", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("value", 1);
        switch (theme){
            case 1: context.setTheme(R.style.AppTheme);
                break;
            case 2: context.setTheme(R.style.AppTheme2);
                break;
            case 3: context.setTheme(R.style.AppTheme3);
                break;
            case 4: context.setTheme(R.style.AppTheme4);
                break;
            case 5: context.setTheme(R.style.AppTheme5);
                break;
        }
    }

    public static int leerSharedThemeDrawer(Context context, int color){
        sharedPreferences = context.getSharedPreferences("Themes", MODE_PRIVATE);
        int theme = sharedPreferences.getInt("value", 1);
        switch (theme){
            case 1:
                color = context.getResources().getColor(R.color.colorPrimary);
                return color;
            case 2:
                color = context.getResources().getColor(R.color.colorPrimary2);
                return color;
            case 3:
                color = context.getResources().getColor(R.color.colorPrimary3);
                return color;
            case 4:
                color = context.getResources().getColor(R.color.colorPrimary4);
                return color;
            case 5:
                color = context.getResources().getColor(R.color.colorPrimary5);
                return color;
        }
        return color;
    }
}
