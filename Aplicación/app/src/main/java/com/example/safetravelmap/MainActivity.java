package com.example.safetravelmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.safetravelmap.entities.Cuenta_administrador;
import com.example.safetravelmap.entities.Cuenta_usuario;
import com.example.safetravelmap.entities.Estaticos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView menu;
    private ArrayList<String> opciones;

    FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirestore = FirebaseFirestore.getInstance();
        this.cargarUsuarios();
        this.cargarMenu();

    }

    public void irALogin(int rol){
        String strRol = "";
        if(rol == 0){
            strRol = "Usuario";
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("rol", strRol);
            startActivity(intent);
        } else if(rol == 1){
            strRol = "Administrador";
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.putExtra("rol", strRol);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
            startActivity(intent);
        }
    }


    public void cargarUsuarios() {
        mFirestore.collection("usuarios").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No existen usuarios registrados.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "Sí existen usuarios registrados.", Toast.LENGTH_SHORT).show();
                    boolean firstMarker = true;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Estaticos.cuenta_usuarios.add(new Cuenta_usuario(
                                document.getString("cod_usuario"),
                                document.getString("nombre"),
                                document.getString("apellido"),
                                document.getString("rut"),
                                document.getLong("edad").intValue(),
                                document.getString("pass"),
                                document.getString("nombre_usuario"),
                                document.getString("correo"),
                                1,
                                100
                        ));
                    }
                }

                cargarAdministradores();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });


    }
    public void cargarAdministradores() {

        mFirestore.collection("administradores").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existen administradores registrados.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Toast.makeText(getApplicationContext(), "Sí existen administradores registrados.", Toast.LENGTH_SHORT).show();
                    boolean firstMarker = true;
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Estaticos.cuenta_administradores.add(new Cuenta_administrador(
                                document.getString("cod_usuario"),
                                document.getString("nombre"),
                                document.getString("apellido"),
                                document.getString("rut"),
                                document.getLong("edad").intValue(),
                                document.getString("pass"),
                                document.getString("nombre_usuario"),
                                document.getString("correo"),
                                1,
                                100
                        ));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });






    }

    public void cargarMenu(){
        menu = (ListView) findViewById(R.id.menu);
        opciones = new ArrayList<String>();
        opciones.add("Usuario");
        opciones.add("Administrador");
        opciones.add("Registro");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, opciones);
        menu.setAdapter(adapter);
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                irALogin(i);
            }
        });
    }
}