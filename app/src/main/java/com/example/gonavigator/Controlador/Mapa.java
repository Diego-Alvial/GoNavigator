package com.example.gonavigator.Controlador;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;

import com.example.gonavigator.AdminSQLiteOpenHelper;
import com.example.gonavigator.models.Direcciones;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class Mapa extends SupportMapFragment implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 15f;

    public Mapa() {
        // Required empty public constructor
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i("Inicializacion mapa", "El mapa fue creado sin problemas");

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        //FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(getActivity());

        List<Direcciones> direcciones = extraerDatosBD();

        for(Direcciones item: direcciones) {
            addMarker(item, googleMap, DEFAULT_ZOOM);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMapAsync(this);
    }



    public void addMarker(Direcciones direccion, GoogleMap googleMap, float zoom){

    LatLng pos = new LatLng(direccion.getLatitud(), direccion.getLongitud());
    MarkerOptions mk = new MarkerOptions().position(pos).title(direccion.getNombre_dir());
    googleMap.addMarker(mk);
    Log.i("Marker agregado", direccion.getNombre_dir());

    if(direccion.isInicial()){
            Log.i("Marker inicial", String.valueOf(direccion.isInicial()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
        }
    }

    public List<Direcciones> extraerDatosBD(){

        List<Direcciones> direcciones = new ArrayList<>();
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(),"rutas_BD",null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("user_preferences", Context.MODE_PRIVATE);

        bd.setForeignKeyConstraintsEnabled(true);

        int id = prefs.getInt("id_RUTA", 0);

        String[] args = new String[]{String.valueOf(id)};

        Cursor cursor = bd.rawQuery("SELECT * FROM Direcciones WHERE id_ruta = ?", args);

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

                direccion.setInicial(cursor.getInt(5) == 1);
                Log.i("BD de direcciones", String.valueOf(direccion.isInicial()));

                direcciones.add(direccion);
                cursor.moveToNext();
            }
        }
        //Borra todas ruta en cascada(Borra las direcciones y pasos que referencian a una ruta tambien)
        //TODO: Basicamente borra todo, solo usar en casos de prueba
        //bd.delete("Rutas", null, null);
        //admin.onUpgrade(bd, 1, 1);

        cursor.close();

        return direcciones;
    }
}