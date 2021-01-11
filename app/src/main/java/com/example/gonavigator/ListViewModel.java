package com.example.gonavigator;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.gonavigator.models.Direcciones;

import java.util.List;
import java.util.Objects;

public class ListViewModel extends ViewModel {

    private MutableLiveData<List<Integer>> direccionesOrdenada = new MutableLiveData<>();
    private MutableLiveData<List<Direcciones>> direccionesOriginal = new MutableLiveData<>();

    public LiveData<List<Integer>> getOrdenadas() {
        return direccionesOrdenada;
    }

    public void setDirecciones(List<Integer> direccion){
        direccionesOrdenada.setValue(direccion);
    }

    public LiveData<List<Direcciones>> getOriginales() {
        return direccionesOriginal;
    }

    public void setOriginales(List<Direcciones> direccion){
        direccionesOriginal.setValue(direccion);
    }


}