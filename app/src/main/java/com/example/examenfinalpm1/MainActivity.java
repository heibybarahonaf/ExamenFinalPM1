package com.example.examenfinalpm1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.examenfinalpm1.Model.Entrevista;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    EditText nomE, desE,periE,fecha;
    ListView listV_entrevistas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private List<Entrevista> listE = new ArrayList<Entrevista>();
    ArrayAdapter<Entrevista> arrayAdapterE;
    Entrevista entrevistaSelected;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imView;

    Button btnPhoto;
    private StorageReference storageReference;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Entrevistas");
        }

        nomE = findViewById(R.id.txt_nombre);
        desE = findViewById(R.id.txt_descripcion);
        periE = findViewById(R.id.txt_periodista);
        fecha = findViewById(R.id.txt_fecha);
        btnPhoto = findViewById(R.id.btnFoto);
        imView = findViewById(R.id.imageView);

        listV_entrevistas = findViewById(R.id.lv_datosEntrevistas);
        inicializarFirebase();
        listarDatos();
        storageReference = FirebaseStorage.getInstance().getReference("imagenes_entrevista");

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirGaleria();

            }
        });

        listV_entrevistas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                entrevistaSelected = (Entrevista) parent.getItemAtPosition(position);
                nomE.setText(entrevistaSelected.getNombre());
                desE.setText(entrevistaSelected.getDescripcion());
                periE.setText(entrevistaSelected.getPeriodista());
                fecha.setText(entrevistaSelected.getFecha());
            }
        });

    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    private void listarDatos() {
        databaseReference.child("Entrevista").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listE.clear();
                for (DataSnapshot objSnaptshot : dataSnapshot.getChildren()) {
                    Entrevista p = objSnaptshot.getValue(Entrevista.class);
                    listE.add(p);

                    arrayAdapterE = new ArrayAdapter<Entrevista>(MainActivity.this, android.R.layout.simple_list_item_1, listE);
                    listV_entrevistas.setAdapter(arrayAdapterE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        String nombre = nomE.getText().toString();
        String desc = desE.getText().toString();
        String period = periE.getText().toString();
        String date = fecha.getText().toString();

        if (itemId == R.id.icon_add) {
            if (nombre.equals("")||desc.equals("")||period.equals("")||date.equals("")){
                validacion();
            }
            else {
                try {
                    subirImagen();

                    Entrevista p = new Entrevista();
                    p.setId(UUID.randomUUID().toString());
                    p.setNombre(nombre);
                    p.setDescripcion(desc);
                    p.setPeriodista(period);
                    p.setFecha(date);
                    p.setImagenUrl(url);

                    databaseReference.child("Entrevista").child(p.getId()).setValue(p);
                    Toast.makeText(this, "Agregado", Toast.LENGTH_LONG).show();
                    limpiarCajas();
                }catch (Exception e) {
                    e.printStackTrace();
                    Log.e("FirebaseError", "Error al guardar en Firebase: " + e.getMessage());
                    Toast.makeText(this, "Error al guardar en Firebase", Toast.LENGTH_LONG).show();
                }
            }
        } else if (itemId == R.id.icon_update) {
            Entrevista e = new Entrevista();
            e.setId(entrevistaSelected.getId());
            e.setNombre(nomE.getText().toString().trim());
            e.setDescripcion(desE.getText().toString());
            e.setPeriodista(periE.getText().toString().trim());
            e.setFecha(fecha.getText().toString().trim());
            if (nombre.equals("")||desc.equals("")||period.equals("")||date.equals("")){
                validacion();
            }else{
                databaseReference.child("Entrevista").child(e.getId()).setValue(e);
                Toast.makeText(this,"Actualizado", Toast.LENGTH_LONG).show();
                limpiarCajas();
            }

        } else if (itemId == R.id.icon_delete) {
            Entrevista e = new Entrevista();
            e.setId(entrevistaSelected.getId());
            databaseReference.child("Entrevista").child(e.getId()).removeValue();
            Toast.makeText(this,"Eliminado", Toast.LENGTH_LONG).show();
            limpiarCajas();
        }

        return true;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Procesa la imagen seleccionada
            Uri imageUri = data.getData();
            cargarImagen(imageUri);
        }
    }

    private void cargarImagen(Uri imageUri) {
        // Cargar la imagen en el ImageView
        imView.setImageURI(imageUri);
    }

    private void subirImagen() {
        if (imView.getDrawable() != null) {
            // Genera un nombre único para la imagen en Firebase Storage
            String nombreImagen = "imagen_" + UUID.randomUUID().toString();

            // Obtiene la referencia al storage de Firebase
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(nombreImagen);

            // Obteniene la URI de la imagen desde el ImageView
            imView.setDrawingCacheEnabled(true);
            imView.buildDrawingCache();
            Bitmap bitmap = ((BitmapDrawable) imView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            // Subir la imagen a Firebase Storage
            storageReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String urlDescarga = uri.toString();
                                    url = urlDescarga;
                                    Log.d("SubirImagen", "URL de descarga: " + urlDescarga);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("SubirImagen", "Error al subir la imagen", e);
                            Toast.makeText(MainActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Selecciona una imagen primero", Toast.LENGTH_SHORT).show();
        }
    }


    private void limpiarCajas() {
        nomE.setText("");
        desE.setText("");
        periE.setText("");
        fecha.setText("");
        int idDrawable = getResources().getIdentifier("ic_foto_foreground", "drawable", getPackageName());
        imView.setImageResource(idDrawable);
    }

    private void validacion() {
        String n = nomE.getText().toString();
        String d = desE.getText().toString();
        String p = periE.getText().toString();
        String f = fecha.getText().toString();
        if (n.equals("")){
            nomE.setError("Requerido");
        }
        else if (d.equals("")){
            desE.setError("Requerido");
        }
        else if (p.equals("")){
            periE.setError("Requerido");
        }
        else if (f.equals("")){
            fecha.setError("Requerido");
        }
    }

}