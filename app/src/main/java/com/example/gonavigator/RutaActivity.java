package com.example.gonavigator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gonavigator.models.Direcciones;
import com.example.gonavigator.models.Rutas;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.encoders.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RutaActivity extends AppCompatActivity {

    //Inicialización variables
    private EditText etNombreRuta;
    private EditText etDirInicial;
    private EditText etDirNueva;
    private TextView tvNombreDirIni;
    private TextView tvCiudadDirIni;
    private Button btnCalcularRuta;
    private Button btnEliminarRuta;
    private ImageButton ibtnBorrarDir;
    private ListView lvDirecciones;

    private SharedPreferences prefs;
    private final Direcciones inicial = new Direcciones();
    private final List<Direcciones> listaDireccion = new ArrayList<>();
    private Rutas ruta = new Rutas();

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
        ibtnBorrarDir = findViewById(R.id.ibtn_borrar_dir);
        lvDirecciones = findViewById(R.id.lv_direcciones);

        this.prefs = this.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);


        //Inicializar lugares para el autorelleno con la key de la API
        Places.initialize(this, getString(R.string.google_maps_key));

        //If para verificar si la ruta es nueva o una que se quiere modificar
        if(this.ruta.getDistancia() == null){

            btnEliminarRuta.setVisibility(View.INVISIBLE);

            etDirInicial.setFocusable(false);
            etDirInicial.setOnClickListener(v -> {
                //Inicializar la lista de lugares
                List<Place.Field> lugarLista = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                //Crear intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, lugarLista).build(RutaActivity.this);
                //Empezar el resultado de la actividad
                startActivityForResult(intent, 100);
            });

            etDirNueva.setFocusable(false);
            etDirNueva.setOnClickListener(v -> {
                //Inicializar la lista de lugares
                List<Place.Field> lugarLista = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                //Crear intent
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, lugarLista).build(RutaActivity.this);
                //Empezar el resultado de la actividad
                startActivityForResult(intent, 99);
            });

            //Boton para calcular ruta
            btnCalcularRuta.setOnClickListener(v -> {

                if(ValidarNuevaDireccion()) {

                    ruta.setNombe_Ruta(etNombreRuta.getText().toString());
                    if(Registrar()){

                        Intent intent = new Intent(v.getContext(), ResultadoActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        else{
            /////////Codigo de cuando se edita una ruta///////////
            // creamos un objeto de la clase que se creo anteriormente con nombre admin = a un objeto de la misma clase(parametros: nombre de la DB, version 1, )
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"rutas_BD",null, 1);

            // se crea un objeto de la clase SQLite + nombre: admin luego se abre la base de datos en modo lectura y escritura con getWritableDatabase()
            SQLiteDatabase bd = admin.getWritableDatabase();
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // resultado para la direccion Inicial
        if(requestCode == 100 && resultCode == RESULT_OK){
            //Inicializar lugar en caso de exito
            Place direccion = Autocomplete.getPlaceFromIntent(data);

            //Colocar la direccion inicial en el editText
            tvNombreDirIni.setText(direccion.getName());
            tvCiudadDirIni.setText(direccion.getAddress().replace(direccion.getName(), "").replace(',', ' ').trim());

            //Guardar la direccion inicial
            inicial.setNombre_dir(direccion.getName());
            inicial.setCiudad_dir(tvCiudadDirIni.getText().toString());
            inicial.setLatitud(direccion.getLatLng().latitude);
            inicial.setLongitud(direccion.getLatLng().longitude);
            inicial.setInicial(true);

            //Resultado para las direcciones normales
        }else if(requestCode == 99 && resultCode == RESULT_OK){
            //Inicializar lugar en caso de exito
            Place direccion = Autocomplete.getPlaceFromIntent(data);

            //Guardar en la lista
            Direcciones dir = new Direcciones();
            dir.setNombre_dir(direccion.getName());
            dir.setCiudad_dir(direccion.getAddress().replace(direccion.getName(), "").replace(',', ' ').trim());
            dir.setLatitud(direccion.getLatLng().latitude);
            dir.setLongitud(direccion.getLatLng().longitude);
            dir.setInicial(false);

            listaDireccion.add(dir);

            //Lista las direcciones
            MyAdapterDireccion myAdapterDireccion = new MyAdapterDireccion(this, listaDireccion, R.layout.list_direcciones);
            this.lvDirecciones.setAdapter(myAdapterDireccion);
        }

        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            //Inicializar status en caso de error
            Status status = Autocomplete.getStatusFromIntent(data);
            //Mostrar por toast
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    public boolean ValidarNuevaDireccion(){

        if (etNombreRuta.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Debe ingresar un nombre para la ruta", Toast.LENGTH_SHORT).show();
            return false;
        }if(inicial.getNombre_dir() == null){
            Toast.makeText(getApplicationContext(), "Debe ingresar la direccion inicial", Toast.LENGTH_SHORT).show();
            return false;
        }if(listaDireccion.size() < 1) {
            Toast.makeText(getApplicationContext(), "Debe agregar las direcciones", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    // metodos para dar de alta las rutas
    // guardar las o la ruta ingresada
    public boolean Registrar()
    {
        // creamos un objeto de la clase que se creo anteriormente con nombre admin = a un objeto de la misma clase(parametros: nombre de la DB, version 1, )
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"rutas_BD",null, 1);

        // se crea un objeto de la clase SQLite + nombre: admin luego se abre la base de datos en modo lectura y escritura con getWritableDatabase()
        SQLiteDatabase bd = admin.getWritableDatabase();
        //Habilitar foreign key
        bd.setForeignKeyConstraintsEnabled(true);

        if(ruta.getDistancia() == null)
        {
            ContentValues contentValuesRuta = new ContentValues();

            Cursor cursor = bd.rawQuery("SELECT * FROM Rutas WHERE nombre_ruta = '" +ruta.getNombe_Ruta() +"'", null);

            //Cuenta los resultados de la query y si encuentra una una ruta con el nombre retorna falso
            if(cursor.getCount()>0){
                return false;
            }

            //Si no encuentra se agrega  obtiene la id de la ruta
            contentValuesRuta.put("nombre_ruta", ruta.getNombe_Ruta());

            bd.insert("Rutas", null, contentValuesRuta);
            cursor.close();

            cursor = bd.rawQuery("SELECT * FROM Rutas WHERE nombre_ruta = '" +ruta.getNombe_Ruta() +"'", null);

            if (cursor.moveToFirst()){
                int rutaId = cursor.getInt(0);
                Log.i("Ruta añadida a la tabla", "El ID de la ruta creada es: "+String.valueOf(rutaId));

                cursor.close();

                //Guardar la ID en las shared Preferences
                saveOnSharedPreferences(rutaId);

                // vamos a guardar en nuestra base de datos lo que el usuario a escrito
                ContentValues cvDirecciones = new ContentValues();
                cvDirecciones.put("id_ruta",rutaId);
                cvDirecciones.put("nombre_dir",inicial.getNombre_dir());
                cvDirecciones.put("ciudad_dir", inicial.getCiudad_dir());
                cvDirecciones.put("latitud",inicial.getLatitud());
                cvDirecciones.put("longitud",inicial.getLongitud());
                cvDirecciones.put("inicial",inicial.isInicial());

                // aqui lo insertamos dentro de la tabla ruta
                bd.insert("Direcciones", null, cvDirecciones);

                for(int i=0; i<listaDireccion.size(); i++){

                    cvDirecciones.put("id_ruta",rutaId);
                    cvDirecciones.put("nombre_dir",listaDireccion.get(i).getNombre_dir());
                    cvDirecciones.put("ciudad_dir", listaDireccion.get(i).getCiudad_dir());
                    cvDirecciones.put("latitud",listaDireccion.get(i).getLatitud());
                    cvDirecciones.put("longitud",listaDireccion.get(i).getLongitud());
                    cvDirecciones.put("inicial",listaDireccion.get(i).isInicial());

                    bd.insert("Direcciones", null, cvDirecciones);
                }

                // aqui cerramos la entrada de la base de datos
                bd.close();
                return true;
            }else{
                return false;
            }
        }else{
            ///////Codigo para cuando se edita la ruta////////////////
            return false;
        }
    }

    //Método para guardar las shared preferences de usuario y contraseña
    private void saveOnSharedPreferences(int idRuta){
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.clear();

        editor.putInt("id_RUTA", idRuta);

        editor.apply();
    }
}