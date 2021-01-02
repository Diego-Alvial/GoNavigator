package com.example.gonavigator;
import android.content.Context;
// se hizo import a las clases: SQLiteDatabase y SQLiteOpenHelper de la base de datos SQLite
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// esta clase tiene como objetivo administrar la base de datos
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    // aqui se agrego el constructor
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // aqui se agrega los metodos onCreate y onUpgrade
    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {

        // se crea una tabla en Mi Base de Datos que tiene: nombre, direccion de destino y direccion inicial
        // string, text y real son los tipos de datos que se ocuparan
        BaseDeDatos.execSQL("create table direccion(id_ruta Primary key, nombre_dir string, cordenada string)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
