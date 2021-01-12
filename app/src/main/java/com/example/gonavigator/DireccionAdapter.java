package com.example.gonavigator;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gonavigator.models.Direcciones;

import java.util.List;

public class DireccionAdapter extends BaseAdapter {

    private final Context context;
    private List<Direcciones> direccionesList;
    private final int layout;

    public DireccionAdapter(Context context, List<Direcciones> direccionesList, int layout){
        this.context = context;
        this. direccionesList = direccionesList;
        this. layout = layout;
    }

    @Override
    public int getCount() {
        return this.direccionesList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.direccionesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Se infla el layout que ocupara el adapter
        LayoutInflater layoutInflater = LayoutInflater.from(this.context);
        convertView = layoutInflater.inflate(this.layout, null);

        ImageButton ibtnBorrarDir = convertView.findViewById(R.id.ibtn_borrar_dir);

        //Se elimina la direccion seleccionada
        ibtnBorrarDir.setOnClickListener(v -> {
            direccionesList.remove(position);
            notifyDataSetChanged();
        });

        //Se asignan los textos de las direcciones
        TextView tvNombreDir = convertView.findViewById(R.id.tv_nombre_dir);
        TextView tvCiudadDir = convertView.findViewById(R.id.tv_ciudad_dir);
        tvNombreDir.setText(this.direccionesList.get(position).getNombre_dir());
        tvCiudadDir.setText(this.direccionesList.get(position).getCiudad_dir().replace(this.direccionesList.get(position).getNombre_dir(), "").replace(',', ' ').trim());

        return convertView;
    }
}
