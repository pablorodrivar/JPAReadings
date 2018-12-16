package org.izv.aad.proyectotrimestre.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.izv.aad.proyectotrimestre.R;

import java.util.Random;

public class HelpActivity extends AppCompatActivity {

    private TextView tvhelp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeActivity.leerSharedTheme(this, true);
        setContentView(R.layout.activity_help);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Ayuda");

        tvhelp = findViewById(R.id.tvhelp);
        String lorem = "Lorem ipsum dolor sit amet consectetur adipiscing elit aliquam dis, nascetur tristique risus dapibus lobortis pharetra sodales cubilia montes convallis, vestibulum donec vivamus dignissim orci curae elementum tempus. Tempus fermentum pretium quam velit pellentesque et mauris hendrerit risus condimentum scelerisque, nostra praesent ante est dignissim hac neque bibendum id. Nec torquent quisque tellus neque senectus facilisi bibendum, etiam habitasse iaculis gravida conubia enim dui accumsan, mattis fames ultrices tristique taciti fermentum. Cras placerat donec nullam malesuada dapibus etiam, rhoncus phasellus eget primis integer turpis, nisi sociis dignissim vitae nibh.\n" +
                "\n" +
                "Parturient rutrum venenatis eros ante in luctus gravida, nostra netus erat maecenas dictumst proin. Netus integer diam sociosqu platea himenaeos vitae magnis, ac elementum nunc blandit tempus augue in luctus, etiam venenatis viverra inceptos imperdiet vestibulum. Dapibus donec nisl ante nam ad, molestie magnis velit nascetur neque, congue lectus mauris ullamcorper.\n" +
                "\n" +
                "Tellus egestas morbi odio metus taciti convallis ac, nullam sapien primis ornare nostra mattis, class vel at nibh eleifend commodo. Diam et nascetur tincidunt imperdiet potenti libero dapibus a malesuada ad lobortis nam, turpis tellus porttitor condimentum tristique est elementum interdum ligula ultricies senectus. Tortor conubia ad in parturient justo elementum morbi nullam quam, blandit turpis mi auctor vestibulum libero curae sagittis, quisque risus curabitur ultrices posuere erat molestie dis.\n" +
                "\n" +
                "Scelerisque vehicula fermentum habitasse rutrum gravida velit turpis eros sem, viverra eleifend magnis tellus imperdiet dictum non vestibulum volutpat lobortis, etiam metus ornare risus quisque ridiculus placerat sed. Dignissim suspendisse euismod accumsan donec commodo lacinia tristique integer mus nisl, vitae eget pellentesque sagittis mauris sapien litora netus potenti, inceptos nibh eros class ornare condimentum aliquam congue cras. Dictum fames interdum egestas laoreet parturient nisi iaculis inceptos condimentum suspendisse, massa nunc mi habitant montes felis hendrerit ligula scelerisque, hac dapibus erat suscipit tortor litora himenaeos placerat duis. Tortor at commodo magna class habitasse montes donec fermentum platea consequat eu proin, posuere condimentum torquent imperdiet massa etiam euismod morbi vitae integer ultrices nisl, leo ut aliquet parturient duis maecenas nec nibh sed semper pharetra.\n" +
                "\n" +
                "Vitae aptent purus erat sociosqu ad sociis viverra mauris, eu hendrerit curabitur tincidunt lectus duis habitant per, luctus praesent ridiculus ac habitasse interdum nisl. Felis tincidunt nec interdum nam venenatis integer class, sociis habitasse purus euismod luctus tempor diam suspendisse, erat auctor potenti non congue aliquam. Justo odio eros sociosqu pharetra hac phasellus consequat quisque, vulputate habitant malesuada netus commodo facilisi placerat.";
        tvhelp.setText(lorem);
    }

    private String generateString(int lenght){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for(int i=0; i<lenght; i++){
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }
}
