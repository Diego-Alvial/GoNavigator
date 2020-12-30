package com.example.gonavigator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class RutaActivity extends AppCompatActivity {

    //Inicialización variables
    EditText etNombreRuta;
    EditText etDirInicial;
    EditText etDirNueva;
    TextView tvNombreDirIni;
    TextView tvCiudadDirIni;
    Button btnCalcularRuta;
    Button btnEliminarRuta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruta);

        //Asignación variables
        etNombreRuta = findViewById(R.id.et_nombre_ruta);
        etDirInicial = findViewById(R.id.et_dir_inicial);
        etDirNueva = findViewById(R.id.et_dir_nueva);
        tvNombreDirIni = findViewById(R.id.tv_nombre_dir_ini);
        tvCiudadDirIni = findViewById(R.id.tv_ciudad_dir_ini);
        btnCalcularRuta = findViewById(R.id.btn_calcular_ruta);
        btnEliminarRuta = findViewById(R.id.btn_eliminar_ruta);

        //Inicializar lugares para el autorelleno con la key de la API
        Places.initialize(this, "AIzaSyBUVlX9y8_henwvB8AOPo0hfr0Edc4FrTw");

        etDirInicial.setFocusable(false);
        etDirInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Inicializar la lista de lugares
                List<Place.Field> lugarLista = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                //Crear intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, lugarLista).build(RutaActivity.this);
                //Empezar el resultado de la actividad
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100 && resultCode == RESULT_OK){
            //Inicializar lugar en caso de exito
            Place direccion = Autocomplete.getPlaceFromIntent(data);
            //Colocar la direccion en el editText
            tvNombreDirIni.setText(direccion.getName());
            tvCiudadDirIni.setText(direccion.getAddress().replace(direccion.getName(), "").replace(',', ' ').trim());

        }else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            //Inicializar status en caso de error
            Status status = Autocomplete.getStatusFromIntent(data);
            //Mostrar por toast
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}