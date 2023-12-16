package com.example.examenfinalpm1.Model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    private static final String ORDENES_NODE = "Entrevista";
    private DatabaseReference databaseReference;

    public FirebaseHelper() {
        // Obtener la referencia a la base de datos
        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void agregar(Entrevista orden) {
        // Generar una nueva clave única para la orden
        String key = databaseReference.child(ORDENES_NODE).push().getKey();
        orden.setId(key);

        // Guardar la orden en la base de datos
        databaseReference.child(ORDENES_NODE).child(key).setValue(orden);
    }

    // Implementar métodos para actualizar, eliminar y recuperar órdenes según sea necesario
}
