package com.example.safetravelmap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safetravelmap.entities.Estaticos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class RegistroActivity extends AppCompatActivity {
    public Button btnR;

    public EditText nombreR;
    public EditText apellidoR;
    public EditText nombreUsuarioR;
    public EditText correoR;
    public EditText passR;
    public EditText rutR;
    public EditText edadR;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        btnR = (Button) findViewById(R.id.btnR);

        nombreR = (EditText) findViewById(R.id.nombreR);
        apellidoR = (EditText) findViewById(R.id.apellidoR);
        nombreUsuarioR = (EditText) findViewById(R.id.nombreUsuarioR);
        correoR = (EditText) findViewById(R.id.correoR);
        passR = (EditText) findViewById(R.id.passR);
        rutR = (EditText) findViewById(R.id.rutR);
        edadR = (EditText) findViewById(R.id.edadR);

        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
            }
        });

    }


    public void registrar() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();

        String correo = correoR.getText().toString().trim();
        String pass = passR.getText().toString().trim();
        String nombre = nombreR.getText().toString().trim();
        String apellido = apellidoR.getText().toString().trim();
        String nombreUsuario = nombreUsuarioR.getText().toString().trim();
        String rut = rutR.getText().toString().trim();
        String edad = edadR.getText().toString().trim();

        // Validaciones
        if (nombre.isEmpty() || apellido.isEmpty() || nombreUsuario.isEmpty() ||
                correo.isEmpty() || pass.isEmpty() || rut.isEmpty() || edad.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Todos los campos son obligatorios.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(getApplicationContext(), "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceder con el registro
        mAuth.createUserWithEmailAndPassword(correo, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, ahora agrega información adicional en Firestore
                            Map<String, Object> usuario = new HashMap<>();
                            usuario.put("nombre", nombre);
                            usuario.put("apellido", apellido);
                            usuario.put("nombre_usuario", nombreUsuario);
                            usuario.put("correo", correo);
                            usuario.put("rut", rut);
                            usuario.put("puntaje", 100);
                            usuario.put("cod_usuario", ""); // Este campo se actualizará más adelante
                            usuario.put("edad", Integer.parseInt(edad));

                            // Agregar usuario a Firestore
                            mFirestore.collection("usuarios")
                                    .add(usuario)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            // Actualizar el campo cod_usuario con el ID del documento
                                            String userId = documentReference.getId();
                                            mFirestore.collection("usuarios").document(userId)
                                                    .update("cod_usuario", userId)
                                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Usuario actualizado con ID: " + userId))
                                                    .addOnFailureListener(e -> Log.w(TAG, "Error al actualizar el usuario", e));

                                            Toast.makeText(getApplicationContext(), "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error añadiendo documento", e);
                                        }
                                    });
                        } else {
                            // Si el registro falla, muestra un mensaje al usuario
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Autenticación fallida: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




}