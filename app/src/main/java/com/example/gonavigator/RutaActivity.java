package com.example.gonavigator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gonavigator.models.Direcciones;
import com.example.gonavigator.models.Rutas;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RutaActivity extends AppCompatActivity{
    //Borrar lo relacionado con cambiar nombre, es perder tiempo seguir intentandolo

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
    private ImageView imgUbicacionActual;
    private Toolbar toolbar;
    private ProgressBar progressBar;

    private SharedPreferences prefs;
    private Direcciones inicial = new Direcciones();
    private final List<Direcciones> listaDireccion = new ArrayList<>();
    private final Rutas ruta = new Rutas();
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onBackPressed() {
    }

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
        imgUbicacionActual = findViewById(R.id.img_ubicacion_actual);
        toolbar = findViewById(R.id.toolbar_personalizada);
        progressBar = findViewById(R.id.progress_bar);

        setSupportActionBar(this.toolbar);
        //Verificar que la toolbar no sea null para colocarle el titulo
        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle(R.string.creacion);
        }

        //Obtener el boton para volver
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Activar el boton para volver
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white);
        toolbar.setNavigationOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),MainActivity.class)));
        //Inicializar las preferencias
        this.prefs = this.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);

        //Inicializar lugares para el autorelleno con la key de la API
        Places.initialize(this, getString(R.string.google_maps_key));

        //Inicializar detector de hubicacion actual
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //EditText del autollenado de la direccion inicial
        etDirInicial.setFocusable(false);
        etDirInicial.setOnClickListener(v -> {
            //Inicializar la lista de lugares
            List<Place.Field> lugarLista = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            //Crear intent
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, lugarLista).build(RutaActivity.this);
            //Empezar el resultado de la actividad
            startActivityForResult(intent, 100);
        });

        //EditText del autollenado de direcciones
        etDirNueva.setFocusable(false);
        etDirNueva.setOnClickListener(v -> {
            //Inicializar la lista de lugares
            List<Place.Field> lugarLista = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
            //Crear intent
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, lugarLista).build(RutaActivity.this);
            //Empezar el resultado de la actividad
            startActivityForResult(intent, 99);
        });

        //If para verificar si la ruta es nueva o una que se quiere modificar
        String nombreRuta = prefs.getString("nombre_ruta","");
        if(nombreRuta.isEmpty()){

            btnEliminarRuta.setVisibility(View.INVISIBLE);

            //Boton para calcular ruta
            btnCalcularRuta.setOnClickListener(v -> {

                if(ValidarDireccion()) {
                    if (listaDireccion.size()>9){
                        Toast.makeText(getApplicationContext(), "Debe ingresar menos de diez direcciones", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ruta.setNombeRuta(etNombreRuta.getText().toString());
                    if(Registrar()){
                        Intent intent = new Intent(v.getContext(), ResultadoActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
        else{
            //Si la ruta ya existe
            etNombreRuta.setFocusable(false);
            this.ruta.setNombeRuta(nombreRuta);

            prefs.edit().clear().apply();
            // creamos un objeto de la clase que se creo anteriormente con nombre admin = a un objeto de la misma clase(parametros: nombre de la DB, version 1, )
            AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"rutas_BD",null, 1);

            // se crea un objeto de la clase SQLite + nombre: admin luego se abre la base de datos en modo lectura y escritura con getWritableDatabase()
            SQLiteDatabase bd = admin.getWritableDatabase();

            int rutaId = leerBD(bd);

            //Colocar la direccion inicial en el editText
            tvNombreDirIni.setText(inicial.getNombre_dir());
            tvCiudadDirIni.setText(inicial.getCiudad_dir().replace(inicial.getNombre_dir(), "").replace(',', ' ').trim());

            //Lista las direcciones
            DireccionAdapter direccionAdapter = new DireccionAdapter(this, listaDireccion, R.layout.list_direcciones);
            this.lvDirecciones.setAdapter(direccionAdapter);
            //Boton eliminar ruta
            btnEliminarRuta.setOnClickListener(v -> {
                String[] args = new String[]{String.valueOf(rutaId)};
                bd.delete("Rutas", "id_ruta = ? ", args);
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            });

            //Boton para calcular ruta
            btnCalcularRuta.setOnClickListener(v -> {

                if(ValidarDireccion()) {
                    if (listaDireccion.size()>9){
                        Toast.makeText(getApplicationContext(), "Debe ingresar menos de diez direcciones", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String[] args = new String[]{String.valueOf(rutaId)};
                    bd.delete("Direcciones", "id_ruta = ?", args);
                    escribirBD(bd);

                    Intent intent = new Intent(v.getContext(), ResultadoActivity.class);
                    startActivity(intent);
                }
            });
        }
        //Obtiene la posicion actual
        imgUbicacionActual.setOnClickListener(v -> obtenerPosicionActual());
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // resultado para la direccion Inicial
        if(requestCode == 100 && resultCode == RESULT_OK){
            //Inicializar lugar en caso de exito
            assert data != null;
            Place direccion = Autocomplete.getPlaceFromIntent(data);

            //Colocar la direccion inicial en el editText
            tvNombreDirIni.setText(direccion.getName());
            tvCiudadDirIni.setText(Objects.requireNonNull(direccion.getAddress()).replace(Objects.requireNonNull(direccion.getName()), "").replace(',', ' ').trim());

            //Guardar la direccion inicial
            inicial.setNombre_dir(direccion.getName());
            inicial.setCiudad_dir(tvCiudadDirIni.getText().toString());
            inicial.setLatitud(Objects.requireNonNull(direccion.getLatLng()).latitude);
            inicial.setLongitud(direccion.getLatLng().longitude);

            //Resultado para las direcciones normales
        }else if(requestCode == 99 && resultCode == RESULT_OK){
            //Inicializar lugar en caso de exito
            assert data != null;
            Place direccion = Autocomplete.getPlaceFromIntent(data);

            //Guardar en la lista
            Direcciones dir = new Direcciones();
            dir.setNombre_dir(direccion.getName());
            dir.setCiudad_dir(Objects.requireNonNull(direccion.getAddress()));
            dir.setLatitud(Objects.requireNonNull(direccion.getLatLng()).latitude);
            dir.setLongitud(direccion.getLatLng().longitude);

            listaDireccion.add(dir);

            //Lista las direcciones
            DireccionAdapter direccionAdapter = new DireccionAdapter(this, listaDireccion, R.layout.list_direcciones);
            this.lvDirecciones.setAdapter(direccionAdapter);
        }

        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            //Inicializar status en caso de error
            assert data != null;
            Status status = Autocomplete.getStatusFromIntent(data);
            //Mostrar por toast
            Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    public boolean ValidarDireccion(){

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
    // guardar la ruta ingresada
    public boolean Registrar(){

        // creamos un objeto de la clase que se creo anteriormente con nombre admin = a un objeto de la misma clase(parametros: nombre de la DB, version 1, )
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"rutas_BD",null, 1);

        // se crea un objeto de la clase SQLite + nombre: admin luego se abre la base de datos en modo lectura y escritura con getWritableDatabase()
        SQLiteDatabase bd = admin.getWritableDatabase();
        //Habilitar foreign key
        bd.setForeignKeyConstraintsEnabled(true);

        ContentValues contentValuesRuta = new ContentValues();

        Cursor cursor = bd.rawQuery("SELECT * FROM Rutas WHERE nombre_ruta = '" +ruta.getNombeRuta() +"'", null);

        //Cuenta los resultados de la query y si encuentra una una ruta con el nombre retorna falso
        if(cursor.getCount()>0){
            return false;
        }

        //Si no encuentra se agrega  obtiene la id de la ruta
        contentValuesRuta.put("nombre_ruta", ruta.getNombeRuta());

        bd.insert("Rutas", null, contentValuesRuta);
        cursor.close();

        escribirBD(bd);

        return true;
    }

    private void escribirBD(SQLiteDatabase bd) {
        Cursor cursor = bd.rawQuery("SELECT * FROM Rutas WHERE nombre_ruta = '" +ruta.getNombeRuta() +"'", null);

        if (cursor.moveToFirst()) {
            int rutaId = cursor.getInt(0);

            //Guardar la ID en las shared Preferences
            saveOnSharedPreferences(rutaId);

            // vamos a guardar en nuestra base de datos lo que el usuario a escrito
            ContentValues cvDirecciones = new ContentValues();
            cvDirecciones.put("id_ruta", rutaId);
            cvDirecciones.put("nombre_dir", inicial.getNombre_dir());
            cvDirecciones.put("ciudad_dir", inicial.getCiudad_dir());
            cvDirecciones.put("latitud", inicial.getLatitud());
            cvDirecciones.put("longitud", inicial.getLongitud());

            // aqui lo insertamos dentro de la tabla ruta
            bd.insert("Direcciones", null, cvDirecciones);

            for (int i = 0; i < listaDireccion.size(); i++) {

                cvDirecciones.put("id_ruta", rutaId);
                cvDirecciones.put("nombre_dir", listaDireccion.get(i).getNombre_dir());
                cvDirecciones.put("ciudad_dir", listaDireccion.get(i).getCiudad_dir());
                cvDirecciones.put("latitud", listaDireccion.get(i).getLatitud());
                cvDirecciones.put("longitud", listaDireccion.get(i).getLongitud());

                bd.insert("Direcciones", null, cvDirecciones);
            }

            cursor.close();
        }
    }
    //Se lee la db en caso de que ya existe la ruta
    private int leerBD(SQLiteDatabase bd) {
        Cursor cursor = bd.rawQuery("SELECT * FROM Rutas WHERE nombre_ruta = '" +ruta.getNombeRuta() +"'", null);

        if (cursor.moveToFirst()) {
            int rutaId = cursor.getInt(0);
            etNombreRuta.setText(cursor.getString(1));
            Log.i("Ruta buscada", "El ID de la ruta solicitada es: " + rutaId + " y el nombre: "+cursor.getString(1));

            //Guardar la ID en las shared Preferences
            saveOnSharedPreferences(rutaId);

            String[] args = new String[]{String.valueOf(rutaId)};

            cursor = bd.rawQuery("SELECT * FROM Direcciones WHERE id_ruta = ?", args);

            //Se guardan las direcciones en una lista
            if(cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    Direcciones direccion= new Direcciones();

                    direccion.setNombre_dir(cursor.getString(1));
                    Log.i("BD de direcciones", direccion.getNombre_dir());

                    direccion.setCiudad_dir(cursor.getString(2));
                    Log.i("BD de direcciones", direccion.getCiudad_dir());

                    direccion.setLatitud(cursor.getDouble(3));
                    Log.i("BD de direcciones", String.valueOf(direccion.getLatitud()));

                    direccion.setLongitud(cursor.getDouble(4));
                    Log.i("BD de direcciones", String.valueOf(direccion.getLongitud()));

                    listaDireccion.add(direccion);
                    cursor.moveToNext();
                }
            }
            //Elimina la inicial de la lista
            inicial = listaDireccion.get(0);
            listaDireccion.remove(0);
            cursor.close();
            return rutaId;
        }return 0;
    }

    private void obtenerPosicionActual(){
        Log.e("Acceso", "Se ha iniciado obtenerPosicionActual");
        //DIreccion actual
        progressBar.setVisibility(View.VISIBLE);
        if (ActivityCompat.checkSelfPermission(RutaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                // Se obtiene la ultima posición, puede ser nula
                Log.e("Posicion actual", String.valueOf(location.getLatitude()));
                Log.e("Posicion actual", String.valueOf(location.getLongitude()));

                //Guardar la direccion inicial
                inicial.setNombre_dir("Usando la ubicación actual");
                inicial.setCiudad_dir("LATITUD: "+inicial.getLatitud()+"  LONGITUD: "+inicial.getLongitud());
                inicial.setLatitud(location.getLatitude());
                inicial.setLongitud(location.getLongitude());

                //Colocar la direccion inicial en el editText
                tvNombreDirIni.setText(inicial.getNombre_dir());
                tvCiudadDirIni.setText(inicial.getCiudad_dir());
            });
        }else{
            ActivityCompat.requestPermissions(RutaActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
        progressBar.setVisibility(View.GONE);
    }

    //Método para guardar las shared preferences de la id de la ruta
    private void saveOnSharedPreferences(int idRuta){
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.clear();

        editor.putInt("id_RUTA", idRuta);

        editor.apply();
    }
}