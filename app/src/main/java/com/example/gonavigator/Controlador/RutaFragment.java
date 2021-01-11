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
import com.example.gonavigator.ListViewModel;
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
    private final List<Integer> ordenadas = new ArrayList<>();
    private int id;
    private SharedPreferences prefs;

    public RutaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(requireActivity()).get(ListViewModel.class);

        viewModel.getOriginales().observe(getViewLifecycleOwner(), list -> {
            for( Direcciones item: list){
                Log.i("originales", item.getNombre_dir());
                originales.add(item);
            }
        });

        viewModel.getOrdenadas().observe(getViewLifecycleOwner(), list -> {
            for( int i: list) {
                Log.i("ordenadas", String.valueOf(i));
                ordenadas.add(i);
            }

            Direcciones temp = new Direcciones();
            for( int i=1; i<originales.size()-2;i++){
                for (int j = 0; j <originales.size() - i - 2; j++) {
                    Log.e("antes burbuja",ordenadas.get(j+1) + " es mayor que " + ordenadas.get(j));
                    if (ordenadas.get(j+1) < ordenadas.get(j)) {

                        temp.setNombre_dir(originales.get(ordenadas.get(j+1)+1).getNombre_dir());
                        temp.setCiudad_dir(originales.get(ordenadas.get(j+1)+1).getCiudad_dir());
                        temp.setLatitud(originales.get(ordenadas.get(j+1)+1).getLatitud());
                        temp.setLongitud(originales.get(ordenadas.get(j+1)+1).getLongitud());
                        Log.e("burbuja",temp.getNombre_dir() + " esta siendo cambiado por " + originales.get(ordenadas.get(j)+1).getNombre_dir());

                        originales.get(ordenadas.get(j+1)+1).setNombre_dir(originales.get(ordenadas.get(j)+1).getNombre_dir());
                        originales.get(ordenadas.get(j+1)+1).setCiudad_dir(originales.get(ordenadas.get(j)+1).getCiudad_dir());
                        originales.get(ordenadas.get(j+1)+1).setLatitud(originales.get(ordenadas.get(j)+1).getLatitud());
                        originales.get(ordenadas.get(j+1)+1).setLongitud(originales.get(ordenadas.get(j)+1).getLongitud());

                        originales.get(ordenadas.get(j)+1).setNombre_dir(temp.getNombre_dir());
                        originales.get(ordenadas.get(j)+1).setCiudad_dir(temp.getCiudad_dir());
                        originales.get(ordenadas.get(j)+1).setLatitud(temp.getLatitud());
                        originales.get(ordenadas.get(j)+1).setLongitud(temp.getLongitud());
                    }
                }
            }
            for( Direcciones item: originales) {
                Log.i("ordenadas resultado", item.getNombre_dir());
            }
            crearLista();

        });

        this.prefs = Objects.requireNonNull(getActivity()).getSharedPreferences("user_preferences", Context.MODE_PRIVATE);
        this.id = prefs.getInt("id_RUTA", 0);

        this.btnEditarRuta = getActivity().findViewById(R.id.btn_editar_ruta);
        this.btnVolverLista = getActivity().findViewById(R.id.btn_volver_lista);

        this.btnVolverLista.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(view.getContext(), MainActivity.class);
            startActivity(intent);
        });

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