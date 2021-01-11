package com.example.gonavigator;
import android.content.Context;
// se hizo import a las clases: SQLiteDatabase y SQLiteOpenHelper de la base de datos SQLite
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

// esta clase tiene como objetivo administrar la base de datos
public class AdminSQLiteOpenHelper extends SQLiteOpenHelper {

    private final String SQLDirecciones = "CREATE TABLE Direcciones(id_direcciones INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                    "nombre_dir TEXT, " +
                                                                    "ciudad_dir TEXT, " +
                                                                    "latitud DOUBLE, " +
                                                                    "longitud DOUBLE, " +
                                                                    "id_ruta INTEGER, " +
                                                                    "FOREIGN KEY(id_ruta) REFERENCES Rutas(id_ruta) ON DELETE CASCADE ON UPDATE CASCADE);";

    private final String SQLRutas = "CREATE TABLE Rutas(id_ruta INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                        "nombre_ruta TEXT)";

    // aqui se agrego el constructor
    public AdminSQLiteOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Habilita el uso de Foreign Key
            db.execSQL("PRAGMA foreign_keys=ON");
        }
    }

    // aqui se agrega los metodos onCreate y onUpgrade
    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {

        // se crea una tabla en Mi Base de Datos que tiene: nombre, direccion de destino y direccion inicial
        BaseDeDatos.execSQL(SQLRutas);
        BaseDeDatos.execSQL(SQLDirecciones);
    }

    @Override
    public void onUpgrade(SQLiteDatabase BaseDeDatos, int oldVersion, int newVersion) {
        BaseDeDatos.execSQL("DROP TABLE IF EXISTS Direcciones");
        BaseDeDatos.execSQL("DROP TABLE IF EXISTS Rutas");

        onCreate(BaseDeDatos);

    }
}
