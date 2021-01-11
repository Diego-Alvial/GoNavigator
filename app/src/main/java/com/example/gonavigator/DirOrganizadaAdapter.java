package com.example.gonavigator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gonavigator.models.Direcciones;

import java.util.List;

public class DirOrganizadaAdapter extends BaseAdapter {

    private final Context context;
    private List<Direcciones> direccionesList;
    private final int layout;

    public DirOrganizadaAdapter(Context context, List<Direcciones> direccionesList, int layout){
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

        TextView tvNombreDirOrd = convertView.findViewById(R.id.tv_nombre_dir_ord);
        tvNombreDirOrd.setText(this.direccionesList.get(position).getNombre_dir());

        TextView tvCiudadDirOrd = convertView.findViewById(R.id.tv_ciudad_dir_ord);
        tvCiudadDirOrd.setText(this.direccionesList.get(position).getCiudad_dir().replace(this.direccionesList.get(position).getNombre_dir(), "").replace(',', ' ').trim());

        return convertView;
    }
}
