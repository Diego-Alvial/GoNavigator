package com.example.gonavigator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnTest;
    private ListView lvRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvRutas = findViewById(R.id.lv_rutas);
        this.btnTest = findViewById(R.id.button_Test);

        this.btnTest.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), RutaActivity.class);
            startActivity(intent);
        });

        // llamamos a la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"rutas_BD",null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        Log.e("###############", "Se abre la base de datos");

        // creamos el cursor que empieza con valor vacio(nulo)
        List<String> rutaListado = new ArrayList<>();
        Cursor cursor = bd.rawQuery("SELECT * FROM Rutas", null);
        Log.e("###############", "Se pasa al cursor las rutas");

        // Aqui se guardan los nombres de las rutas creadas
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                Log.e("###############", "Se almacena el nombre de la ruta");
                rutaListado.add(cursor.getString(1));

                cursor.moveToNext();
            }
        }
        cursor.close();

        if(rutaListado.isEmpty()){
            Log.e("###############", "lista vacia");
            return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.lista_rutas_inicio, R.id.tv_lista_rutas, rutaListado);
        this.lvRutas.setAdapter(adapter);
        Log.e("###############", "Se crea la lista de rutas");

        this.lvRutas.setOnItemClickListener((parent, view, position, id) -> {
            Log.e("###############", "Redirecciona a la ruta seleccionada");
            Intent intent = new Intent(view.getContext(), RutaActivity.class);
            startActivity(intent);
        });
    }
}