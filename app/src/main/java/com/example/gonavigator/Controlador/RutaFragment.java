package com.example.gonavigator.Controlador;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.gonavigator.AdminSQLiteOpenHelper;
import com.example.gonavigator.DirOrganizadaAdapter;
import com.example.gonavigator.models.ListViewModel;
import com.example.gonavigator.MainActivity;
import com.example.gonavigator.R;
import com.example.gonavigator.RutaActivity;
import com.example.gonavigator.models.Direcciones;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class RutaFragment extends Fragment {

    private Button btnVolverLista;
    private Button btnEditarRuta;
    private ListView lvDirOrdenado;

    private ListViewModel viewModel;
    private final List<Direcciones> originales = new ArrayList<>();
    private final List<Direcciones> aux = new ArrayList<>();
    private final List<Integer> ordenadas = new ArrayList<>();
    private int id;
    private SharedPreferences prefs;

    public RutaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ListViewModel.class);
        //Se obtienen las direcciones desde el mapaFragment
        viewModel.getOriginales().observe(getViewLifecycleOwner(), list -> {
            for( Direcciones item: list){
                Log.i("originales", item.getNombre_dir());
                originales.add(item);
                Direcciones auxiliar = new Direcciones();
                auxiliar.setNombre_dir(item.getNombre_dir());
                auxiliar.setCiudad_dir(item.getCiudad_dir());
                auxiliar.setLatitud(item.getLatitud());
                auxiliar.setLongitud(item.getLongitud());
                aux.add(auxiliar);
            }
        });
        //Se obtiene el orden en el que deben ir las direcciones, menos la de inicio y la de destino
        viewModel.getOrdenadas().observe(getViewLifecycleOwner(), list -> {
            for( int i: list) {
                Log.i("ordenadas", String.valueOf(i));
                ordenadas.add(i);
            }
            //Codigo de ordenamiento para las direcciones
            for( int i=1; i<originales.size()-1;i++){
                Log.e("original antes", originales.get(i).getNombre_dir()+" con i= "+i);
                Log.e("auxiliar antes", aux.get(ordenadas.get(i-1)+1).getNombre_dir()+" con i= "+i+" ordenadas.get= " +ordenadas.get(i-1)+1);
                originales.get(i).setNombre_dir(aux.get(ordenadas.get(i-1)+1).getNombre_dir());
                Log.e("original despues", originales.get(i).getNombre_dir()+" con i= "+i);
                Log.e("auxiliar despues", aux.get(ordenadas.get(i-1)+1).getNombre_dir()+" con i= "+i+" ordenadas.get= " +ordenadas.get(i-1)+1);
                originales.get(i).setCiudad_dir(aux.get(ordenadas.get(i-1)+1).getCiudad_dir());
                originales.get(i).setLatitud(aux.get(ordenadas.get(i-1)+1).getLatitud());
                originales.get(i).setLongitud(aux.get(ordenadas.get(i-1)+1).getLongitud());
            }
            for(int i=0; i<originales.size(); i++) {
                Log.i("ordenadas resultado", originales.get(i).getNombre_dir());
                Log.i("ordenadas resultado", aux.get(i).getNombre_dir());
            }
            crearLista();

        });
        //Se obtienen las shared preferences
        this.prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        this.id = prefs.getInt("id_RUTA", 0);

        this.btnEditarRuta = getActivity().findViewById(R.id.btn_editar_ruta);
        this.btnVolverLista = getActivity().findViewById(R.id.btn_volver_lista);
        //Boton volver ruta
        this.btnVolverLista.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            startActivity(intent);
        });
        //Boton editar ruta
        this.btnEditarRuta.setOnClickListener(v -> {
            saveOnSharedPreferences(id);
            Intent intent = new Intent(view.getContext(), RutaActivity.class);
            startActivity(intent);
        });
    }


    public void crearLista(){
        //Llenar la lista
        this.lvDirOrdenado = Objects.requireNonNull(getActivity()).findViewById(R.id.lv_dir_ordenado);
        DirOrganizadaAdapter dirOrganizadaAdapter = new DirOrganizadaAdapter(getActivity(), originales, R.layout.list_dir_ordenadas);
        this.lvDirOrdenado.setAdapter(dirOrganizadaAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ruta, container, false);
    }
    //Guardar las preferencias antes de volver al editar ruta
    private void saveOnSharedPreferences(int id){
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.clear();
        String nombreRuta="";

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(),"rutas_BD",null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();

        String[] args = new String[]{String.valueOf(id)};

        Cursor cursor = bd.rawQuery("SELECT * FROM Rutas WHERE id_ruta = ?", args);

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                nombreRuta = cursor.getString(1);
                cursor.moveToNext();
            }
            editor.putString("nombre_ruta", nombreRuta);
            Log.i("shared preferences", "Se guarda el nombre de la ruta: "+nombreRuta);
            cursor.close();
            editor.apply();
        }
    }
}