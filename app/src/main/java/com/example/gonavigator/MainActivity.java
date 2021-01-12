package com.example.gonavigator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private Button btnTest;
    private ListView lvRutas;
    private SharedPreferences prefs;
    private TextView tvRutasCreadas;

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.prefs = this.getSharedPreferences("user_preferences", Context.MODE_PRIVATE);

        lvRutas = findViewById(R.id.lv_rutas);
        tvRutasCreadas = findViewById(R.id.tv_rutas_creadas);
        this.btnTest = findViewById(R.id.button_Test);

        this.btnTest.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RutaActivity.class);
            startActivity(intent);
        });

        // llamamos a la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"rutas_BD",null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Log.i("Acceso bd", "Se abre la base de datos");

        // creamos el cursor que empieza con valor vacio(nulo)
        List<String> rutaListado = new ArrayList<>();
        Cursor cursor = bd.rawQuery("SELECT * FROM Rutas", null);
        Log.i("Query realizada", "Se pasa al cursor las rutas");

        // Aqui se guardan los nombres de las rutas creadas
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                Log.i("Rutas de la bd", "Se almacena el nombre de la ruta "+cursor.getString(1));
                rutaListado.add(cursor.getString(1));

                cursor.moveToNext();
            }
        }
        cursor.close();

        if(rutaListado.isEmpty()){
            Log.e("###############", "lista vacia");
            this.lvRutas.setVisibility(View.INVISIBLE);
            this.tvRutasCreadas.setVisibility(View.INVISIBLE);
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.lista_rutas_inicio, R.id.tv_lista_rutas, rutaListado);
        this.lvRutas.setAdapter(adapter);
        Log.i("Adaptador de la lista", "Se crea la lista de rutas");

        this.lvRutas.setOnItemClickListener((parent, view, position, id) -> {
            Log.i("Seleccionada una ruta", "Redirecciona a la ruta seleccionada");
            saveOnSharedPreferences(parent.getItemAtPosition(position).toString());
            Intent intent = new Intent(view.getContext(), RutaActivity.class);
            startActivity(intent);
        });
    }

    //Método para guardar las shared preferences del nombre de la ruta
    private void saveOnSharedPreferences(String nombreRuta){
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.clear();

        editor.putString("nombre_ruta", nombreRuta);
        Log.i("shared preferences", "Se guarda el nombre de la ruta: "+nombreRuta);

        editor.apply();
    }
}