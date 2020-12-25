package com.example.gonavigator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.TabHost;

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

    }
}