package com.example.safetravelmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    public TextView mensaje;
    public EditText correo;
    public EditText pass;
    public Button login;

    FirebaseFirestore mFirestore;
    FirebaseAuth mAuth;


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
                                    consultar_usuario();
                                }else if(rol.equals("Administrador")){
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
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("usuarios").whereEqualTo("correo", correo.getText().toString().trim()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existe este usuario en base de datos.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Intent intent = new Intent(getApplicationContext(), SistemaActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void consultar_administrador(){
        mFirestore = FirebaseFirestore.getInstance();
        mFirestore.collection("administrador").whereEqualTo("correo", correo.getText().toString().trim()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    Toast.makeText(getApplicationContext(), "No existe este administrador en base de datos.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    Intent intent = new Intent(getApplicationContext(), SistemaActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}