package com.example.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.safetravelmap.entities.Estaticos;

import java.util.ArrayList;

public class NuevoTutorialActivity extends AppCompatActivity {

    private ListView listOpciones;
    private ArrayList<String> opciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_tutorial);
        cargarMenu();
    }

    public void cargarMenu(){
        listOpciones = (ListView) findViewById(R.id.listOpciones);
        opciones = new ArrayList<String>();
        if(!Estaticos.tipo_usuario){
            opciones.add("Nuevo tutorial");
        }
        opciones.add("Tutoriales");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opciones);
        listOpciones.setAdapter(adapter);
        listOpciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);

                if ("Nuevo tutorial".equals(selectedItem)) {
                    Intent intent = new Intent(getApplicationContext(), IngresoTutorialActivity.class);
                    startActivity(intent);
                }

                if ("Tutoriales".equals(selectedItem)) {
                    Intent intent = new Intent(getApplicationContext(), TutorialesActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}