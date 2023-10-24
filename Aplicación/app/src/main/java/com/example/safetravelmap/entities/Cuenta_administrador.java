package com.example.safetravelmap.entities;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Cuenta_administrador extends Cuenta{
    public int id_cuenta_administrador;
    public FirebaseFirestore mFirestore;
    public FirebaseAuth mAuth;


    public Cuenta_administrador(int cod_usuario, String nombre, String apellido, String rut, int edad, String pass, String nombre_usuario, String correo, int id_cuenta_administrador, int puntaje) {
        super(cod_usuario, nombre, apellido, rut, edad, pass, nombre_usuario, correo, puntaje);
        this.id_cuenta_administrador = id_cuenta_administrador;
    }

    public Cuenta_administrador() {
        super();
    }

    public int getId_cuenta_administrador() {
        return id_cuenta_administrador;
    }

    public void setId_cuenta_administrador(int id_cuenta_administrador) {
        this.id_cuenta_administrador = id_cuenta_administrador;
    }

    public boolean consultar_administrador(String correo, boolean test){
        if(!test) {
            boolean[] resultado = {false};
            mFirestore = FirebaseFirestore.getInstance();
            mFirestore.collection("administrador").whereEqualTo("correo", correo.toString().trim()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (queryDocumentSnapshots.isEmpty()) {
                        resultado[0] = false;
                    } else {
                        resultado[0] = true;

                    }
                }
            });

            return resultado[0];
        }else{
            return false;
        }
    }
}
