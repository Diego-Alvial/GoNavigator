package com.example.gonavigator.Controlador;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;

import com.example.gonavigator.AdminSQLiteOpenHelper;
import com.example.gonavigator.models.ListViewModel;
import com.example.gonavigator.R;
import com.example.gonavigator.models.Direcciones;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MapaFragment extends SupportMapFragment implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 15f;
    private final PolylineOptions polilinea = new PolylineOptions();
    private List<Direcciones> direcciones = new ArrayList<>();
    private GeoApiContext geoApiContext = null;
    private DirectionsResult resultadoDireccion;
    private ListViewModel viewModel;
    private boolean inicial = true;

    public MapaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ListViewModel.class);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i("Inicializacion mapa", "El mapa fue creado sin problemas");
        //Habilita el zoom
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        //Se traen y guardan los datos desde bd
        direcciones = extraerDatosBD();
        //Añade los marker
        for (Direcciones item : direcciones) {
            addMarker(item, googleMap, DEFAULT_ZOOM);
        }
        //Si no esta iniciada la api, la consigue
        if (geoApiContext == null) {
            geoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();
        }
        //Calcula la ruta optima
        calcularDirecciones();
        //Dibuja la ruta optima
        dibujarPolilineas(googleMap);
    }

    private void calcularDirecciones(){
        DirectionsApiRequest direccionApi = new DirectionsApiRequest(geoApiContext);
        //Desabilita rutas alternativas
        direccionApi.alternatives(false);
        //Habilita la optimizacion de la ruta
        direccionApi.optimizeWaypoints(true);

        //Direccion de origen
        direccionApi.origin(
                new com.google.maps.model.LatLng(
                        direcciones.get(0).getLatitud(),
                        direcciones.get(0).getLongitud()
                )
        );

        //Direcciones intermedias que se optimizaran, la API acepta un maximo de 10 incluyendo la de inicio y final
        switch (direcciones.size()){
            case 3: direccionApi.waypoints(direcciones.get(1).getCiudad_dir());
                break;
            case 4: direccionApi.waypoints(direcciones.get(1).getCiudad_dir(), direcciones.get(2).getCiudad_dir());
                break;
            case 5: direccionApi.waypoints(direcciones.get(1).getCiudad_dir(), direcciones.get(2).getCiudad_dir(),
                                            direcciones.get(3).getCiudad_dir());
                break;
            case 6: direccionApi.waypoints(direcciones.get(1).getCiudad_dir(), direcciones.get(2).getCiudad_dir(),
                                            direcciones.get(3).getCiudad_dir(), direcciones.get(4).getCiudad_dir());
                break;
            case 7: direccionApi.waypoints(direcciones.get(1).getCiudad_dir(), direcciones.get(2).getCiudad_dir(),
                                            direcciones.get(3).getCiudad_dir(), direcciones.get(4).getCiudad_dir(),
                                            direcciones.get(5).getCiudad_dir());
                break;
            case 8: direccionApi.waypoints(direcciones.get(1).getCiudad_dir(), direcciones.get(2).getCiudad_dir(),
                                            direcciones.get(3).getCiudad_dir(), direcciones.get(4).getCiudad_dir(),
                                            direcciones.get(5).getCiudad_dir(), direcciones.get(6).getCiudad_dir());
                break;
            case 9: direccionApi.waypoints(direcciones.get(1).getCiudad_dir(), direcciones.get(2).getCiudad_dir(),
                                            direcciones.get(3).getCiudad_dir(), direcciones.get(4).getCiudad_dir(),
                                            direcciones.get(5).getCiudad_dir(), direcciones.get(6).getCiudad_dir(),
                                            direcciones.get(7).getCiudad_dir());
                break;
            case 10: direccionApi.waypoints(direcciones.get(1).getCiudad_dir(), direcciones.get(2).getCiudad_dir(),
                                            direcciones.get(3).getCiudad_dir(), direcciones.get(4).getCiudad_dir(),
                                            direcciones.get(5).getCiudad_dir(), direcciones.get(6).getCiudad_dir(),
                                            direcciones.get(7).getCiudad_dir(), direcciones.get(8).getCiudad_dir());
                break;
        }
        //Direccion final o de destino
        resultadoDireccion = direccionApi.destination(
                new com.google.maps.model.LatLng(
                        direcciones.get(direcciones.size()-1).getLatitud(),
                        direcciones.get(direcciones.size()-1).getLongitud()
                )).awaitIgnoreError();
    }

    private void dibujarPolilineas(GoogleMap googleMap){
        Log.i("polilinea", "resultado al generar la ruta " + resultadoDireccion.routes.length);

        for(DirectionsRoute route: resultadoDireccion.routes){
            Log.i("polilinea", "Cantidad de legs: " + route.legs[0].toString());
            List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

            // Se generaron "legs" que son basicamente nuevos puntos, de los cuales se calcula la polilinea para cada uno de ellos
            for(com.google.maps.model.LatLng latLng: decodedPath){
                Direcciones pasos = new Direcciones();
                pasos.setLatitud(latLng.lat);
                pasos.setLongitud(latLng.lng);
                addPolyline(pasos);
            }
            //Se obtienen las direcciones ordenadas
            List<Integer>ordenadas = new ArrayList<>();
            int[] ordenDirecciones = route.waypointOrder;
            for(int i:ordenDirecciones){
                ordenadas.add(i);
            }

            //Traspasar la informacio al otro fragment
            viewModel.setOriginales(direcciones);
            viewModel.setDirecciones(ordenadas);

            //Añadir las polilineas y cambiarles el color
            Polyline polyline = googleMap.addPolyline(polilinea);
            polyline.setColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.maps_qu_google_blue_500));
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getMapAsync(this);
    }
    //Dibujador de polilineas
    public void addPolyline(Direcciones direccion){

        LatLng pos = new LatLng(direccion.getLatitud(), direccion.getLongitud());
        this.polilinea.add(pos);
    }
    //Agregador de markers
    public void addMarker(Direcciones direccion, GoogleMap googleMap, float zoom){

    LatLng pos = new LatLng(direccion.getLatitud(), direccion.getLongitud());
    MarkerOptions mk = new MarkerOptions().position(pos).title(direccion.getNombre_dir());
    googleMap.addMarker(mk);
    Log.i("Marker agregado", direccion.getNombre_dir());

    if(inicial){
            Log.i("Marker inicial", direccion.getNombre_dir());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, zoom));
            inicial=false;
        }
    }

    public List<Direcciones> extraerDatosBD(){

        List<Direcciones> direcciones = new ArrayList<>();
        // Cargamos la bd y las shared preferences
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(),"rutas_BD",null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("user_preferences", Context.MODE_PRIVATE);

        //Habilitan las foreign
        bd.setForeignKeyConstraintsEnabled(true);

        //Se obtiene la id de las shared preferences
        int id = prefs.getInt("id_RUTA", 0);

        //Se trae de la bd las direcciones que coinciden con la id de la ruta
        String[] args = new String[]{String.valueOf(id)};
        Cursor cursor = bd.rawQuery("SELECT * FROM Direcciones WHERE id_ruta = ?", args);

        //Se recorre y se guardan las direcciones en una lista
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

                direcciones.add(direccion);
                cursor.moveToNext();
            }
        }
        cursor.close();

        return direcciones;
    }
}