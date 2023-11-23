package com.example.safetravelmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.safetravelmap.entities.Desperfecto;
import com.example.safetravelmap.entities.Estaticos;
import com.example.safetravelmap.entities.Voto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {
    public TextView mensaje;

    public EditText correo;
    public EditText pass;
    public Button login;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;

    ArrayList<Voto> votos = new ArrayList<Voto>();
    ArrayList<Desperfecto> desperfectos = new ArrayList<Desperfecto>();

    boolean bloqueado = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String rol = intent.getStringExtra("rol");

        mensaje = (TextView) findViewById(R.id.mensaje);
        correo = (EditText) findViewById(R.id.correo);
        pass = (EditText) findViewById(R.id.pass);
        login = (Button) findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();

        if(rol.equals("Usuario")){
            mensaje.setText("Login de usuarios");
        }else if(rol.equals("Administrador")){
            mensaje.setText("Login de administradores");
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!correo.getText().toString().equals("") && !pass.getText().toString().equals("")){
                    mAuth.signInWithEmailAndPassword(correo.getText().toString().trim(), pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                finish();
                                if(rol.equals("Usuario")){
                                    Estaticos.tipo_usuario = true;
                                    consultar_usuario();
                                }else if(rol.equals("Administrador")){
                                    Estaticos.tipo_usuario = false;
                                    consultar_administrador();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "Error en los datos de autenticación!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(), "Debe ingresar ambos campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void consultar_usuario(){
        Toast.makeText(getApplicationContext(), "Consultando usuario en base de datos...", Toast.LENGTH_SHORT).show();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("usuarios").whereEqualTo("correo", correo.getText().toString().trim()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existe este usuario en base de datos.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Toast.makeText(getApplicationContext(), "Bienvenido: " + document.getString("nombre") + " " + document.getString("apellido"), Toast.LENGTH_SHORT).show();
                        Estaticos.cuenta_usuario.setApellido(document.getString("apellido"));
                        Estaticos.cuenta_usuario.setCod_usuario(document.getString("cod_usuario"));
                        Estaticos.cuenta_usuario.setCorreo(document.getString("correo"));
                        Estaticos.cuenta_usuario.setEdad(document.getLong("edad").intValue());
                        Estaticos.cuenta_usuario.setNombre(document.getString("nombre"));
                        Estaticos.cuenta_usuario.setNombre_usuario(document.getString("nombre_usuario"));
                        Estaticos.cuenta_usuario.setPass(document.getString("pass"));
                        Estaticos.cuenta_usuario.setRut(document.getString("rut"));
                        Estaticos.cuenta_usuario.setPuntaje(document.getLong("puntaje").intValue());
                    }
                    cargarDesperfectos();
                    // Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void consultar_administrador(){
        Toast.makeText(getApplicationContext(), "Consultando administrador en base de datos...", Toast.LENGTH_SHORT).show();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("administradores").whereEqualTo("correo", correo.getText().toString().trim()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existe este administrador en base de datos.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Toast.makeText(getApplicationContext(), "Bienvenido: " + document.getString("nombre") + " " + document.getString("apellido"), Toast.LENGTH_SHORT).show();
                        Estaticos.cuenta_administrador.setApellido(document.getString("apellido"));
                        Estaticos.cuenta_administrador.setCod_usuario(document.getString("cod_usuario"));
                        Estaticos.cuenta_administrador.setCorreo(document.getString("correo"));
                        Estaticos.cuenta_administrador.setEdad(document.getLong("edad").intValue());
                        Estaticos.cuenta_administrador.setNombre(document.getString("nombre"));
                        Estaticos.cuenta_administrador.setNombre_usuario(document.getString("nombre_usuario"));
                        Estaticos.cuenta_administrador.setPass(document.getString("pass"));
                        Estaticos.cuenta_administrador.setRut(document.getString("rut"));
                        Estaticos.cuenta_administrador.setPuntaje(document.getLong("puntaje").intValue());
                    }
                    cargarDesperfectos();
                    //Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cargarDesperfectos(){
        String cod_usuario = "";
        if(Estaticos.tipo_usuario){
            cod_usuario = Estaticos.cuenta_usuario.getCod_usuario();
        }else{
            cod_usuario = Estaticos.cuenta_administrador.getCod_usuario();
        }
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("desperfectos")
                .whereEqualTo("cod_usuario", cod_usuario)
                .whereEqualTo("tipo_usuario", Estaticos.tipo_usuario)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            Toast.makeText(getApplicationContext(), "No existen desperfectos para el usuario.", Toast.LENGTH_SHORT).show();
                        }else {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Desperfecto desperfecto = new Desperfecto(
                                        document.getString("cod_usuario"),
                                        document.getString("desc"),
                                        document.getString("desperfecto"),
                                        document.getString("imagen"),
                                        document.getDouble("latitud"),
                                        document.getDouble("longitud"),
                                        document.getLong("riesgo").intValue(),
                                        document.getBoolean("tipo_usuario"),
                                        document.getLong("puntos").intValue(),
                                        false,
                                        false
                                );
                                desperfectos.add(desperfecto);
                            }
                        }

                        // Toast.makeText(getApplicationContext(), "Existen " + desperfectos.size() + " desperfectos para el usuario.", Toast.LENGTH_SHORT).show();

                        cargarVotos();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void cargarVotos(){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("votos")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty()){
                            Toast.makeText(getApplicationContext(), "No existen votos para el usuario." + bloqueado, Toast.LENGTH_SHORT).show();
                        }else {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                votos.add(new Voto(
                                        document.getLong("cod_usuario").intValue(),
                                        document.getDouble("latitud"),
                                        document.getBoolean("tipo_usuario")
                                ));
                            }

                            // Comprobamos que algún desperfecto no pase las votaciones negativas
                            for(int i = 0;i < desperfectos.size(); i++) {
                                Desperfecto desperfectoTemp = desperfectos.get(i);
                                comprobarBloqueoCuenta(desperfectoTemp);
                            }
                        }

                        if(bloqueado == false){
                            Toast.makeText(getApplicationContext(), "Ingresando...", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), SistemaActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "Su cuenta ha sido bloqueada por desperfectos falsos.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@androidx.annotation.NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "ERROR: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void comprobarBloqueoCuenta(Desperfecto desperfecto){
        int contador = 0;
        for(int i = 0;i < votos.size(); i++){
            Voto votoTemp = votos.get(i);
            if(desperfecto.getLatitud() == votoTemp.getLatitud()){
                contador++;
            }
        }
        // 10 votos significa una cuenta redicida a 0
        int puntaje = 0;
        if(Estaticos.tipo_usuario == true) {
            puntaje = Estaticos.cuenta_usuario.getPuntaje();
        } else {
            puntaje = Estaticos.cuenta_administrador.getPuntaje();
        }

        Toast.makeText(getApplicationContext(), "El contador es " + contador + " y el puntaje " + puntaje, Toast.LENGTH_SHORT).show();
        if(puntaje <= (contador * 50)){ // Cambiar 50 por 10
            bloqueado = true;
        }
    }
}