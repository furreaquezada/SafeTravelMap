package com.example.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.safetravelmap.entities.Estaticos;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class IngresoTutorialActivity extends AppCompatActivity {

    Button btnIngreso;
    EditText titulo;
    EditText desc2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingreso_tutorial);

        btnIngreso = (Button) findViewById(R.id.btnIngreso);
        titulo = (EditText) findViewById(R.id.titulo);
        desc2 = (EditText) findViewById(R.id.desc2);


        btnIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearTutorial();
            }
        });
    }

    public void crearTutorial(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        // Agregar un nuevo documento a la colección "tutorial"
        // Crear un nuevo objeto con los atributos que quieres almacenar
        Map<String, Object> tutorial = new HashMap<>();

        String tituloStr = titulo.getText().toString();
        String descStr = desc2.getText().toString();
        if (!esValido(tituloStr) || !esValido(descStr)) {
            // Mostrar un mensaje al usuario indicando que hay campos vacíos o inválidos
            Toast.makeText(this, "Por favor, rellene todos los campos correctamente.", Toast.LENGTH_SHORT).show();
            return;
        }
        btnIngreso.setEnabled(false);



        tutorial.put("titulo", tituloStr);
        tutorial.put("desc", descStr);
        tutorial.put("tipo_usuario", Estaticos.tipo_usuario);
        if(Estaticos.tipo_usuario == true) {
            tutorial.put("cod_usuario", Estaticos.cuenta_usuario.getCod_usuario()); // Aquí pon el número que corresponda
        } else {
            tutorial.put("cod_usuario", Estaticos.cuenta_administrador.getCod_usuario()); // Aquí pon el número que corresponda
        }




        mFirestore.collection("tutoriales")
                .add(tutorial)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Documento añadido con éxito
                        Log.d(TAG, "Tutorial añadido con ID: " + documentReference.getId());
                        btnIngreso.setEnabled(true);
                        titulo.setText("");
                        desc2.setText("");
                        Toast.makeText(getApplicationContext(), "Tutorial añadido con ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Error al añadir documento
                        Log.w(TAG, "Error añadiendo documento", e);
                    }
                });
    }

    private boolean esValido(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }


}