package com.example.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView menu;
    private ArrayList<String> opciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (ListView) findViewById(R.id.menu);
        opciones = new ArrayList<String>();
        opciones.add("Usuario");
        opciones.add("Administrador");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opciones);
        menu.setAdapter(adapter);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                irALogin(i);
            }
        });


    }

    public void irALogin(int rol){
        String strRol;
        if(rol == 0){
            strRol = "Usuario";
        }else{
            strRol = "Administrador";
        }
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.putExtra("rol", strRol);
        startActivity(intent);
    }
}