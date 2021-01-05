package com.example.gonavigator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;
import android.widget.Toast;

import com.example.gonavigator.Controlador.PagerController;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class ResultadoActivity extends AppCompatActivity {

    private TabLayout tlRuta;
    private ViewPager viewPager;
    private TabItem tabMapa;
    private TabItem tabRuta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        PagerController pagerAdapter;

        tlRuta = findViewById(R.id.tl_Ruta);
        viewPager = findViewById(R.id.viewpager);
        tabMapa = findViewById(R.id.tab_Mapa);
        tabRuta = findViewById(R.id.tab_Ruta);

        pagerAdapter = new PagerController(getSupportFragmentManager(), tlRuta.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tlRuta.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==0){
                    pagerAdapter.notifyDataSetChanged();
                }
                if (tab.getPosition()==1){
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlRuta));

        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(this,"rutas_BD",null, 1);
        SQLiteDatabase bd = admin.getReadableDatabase();
        bd.setForeignKeyConstraintsEnabled(true);

        Cursor cursor = bd.rawQuery("SELECT * FROM Direcciones", null);

        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Log.i("*************", String.valueOf(cursor.getInt(0)));
                Log.i("************#", cursor.getString(1));
                Log.i("***********##", cursor.getString(2));
                Log.i("**********###", String.valueOf(cursor.getDouble(3)));
                Log.i("*********####", String.valueOf(cursor.getDouble(4)));
                Log.i("********#####", String.valueOf(cursor.getInt(5)));
                Log.i("*******######", String.valueOf(cursor.getInt(6)));
                cursor.moveToNext();
            }
        }
        cursor = bd.rawQuery("Select * from Rutas", null);
        if(cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Log.i("#############", String.valueOf(cursor.getInt(0)));
                Log.i("############*", cursor.getString(1));
                cursor.moveToNext();
            }
        }
        //Borra todas ruta en cascada(Borra las direcciones y pasos que referencian a una ruta tambien)
        //TODO: Basicamente borra todo, solo usar en casos de prueba
        //bd.delete("Rutas", null, null);

        cursor.close();
    }
}