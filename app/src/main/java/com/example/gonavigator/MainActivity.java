package com.example.gonavigator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gonavigator.models.Direcciones;
import com.example.gonavigator.models.Rutas;

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

        // llamamos a la base de datos
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"rutas_BD",null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        // creamos el cursor que empieza con valor vacio(nulo)
        List<String> rutaListado = new ArrayList<>();
        Cursor cursor = bd.rawQuery("SELECT * FROM Rutas", null);

        // Aqui se guardan los nombres de las rutas creadas
        if(cursor.moveToFirst())
        {
            while(!cursor.isAfterLast())
            {
                rutaListado.add(cursor.getString(1));
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.activity_main, rutaListado);
        this.lvRutas.setAdapter(adapter);

        this.lvRutas.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(view.getContext(), RutaActivity.class);
                startActivity(intent);
            }
        });

        this.btnTest = findViewById(R.id.button_Test);
        this.btnTest.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RutaActivity.class);
                startActivity(intent);
            }
        });
    }
}