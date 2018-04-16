package com.example.a1631088057.condomine;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.a1631088057.condomine.activities.Atualiza_Activity;
import com.example.a1631088057.condomine.activities.Cadastro_Activity;

/**
 * Created by ander on 12/04/2018.
 */

public class ZipCodeListener implements TextWatcher {
    private Context context;
    private String local;

//    public ZipCodeListener(){
//
//    }

    public ZipCodeListener( Context context, String local ){
        this.context = context;
        this.local = local;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String zipCode = editable.toString();
        if (local.equals("cadastro")) {
            if (zipCode.length() == 8) {
                new EnderecoRequest((Cadastro_Activity) context, local).execute();
            }
        } else {
            if (zipCode.length() == 8) {
                new EnderecoRequest((Atualiza_Activity) context, local).execute();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
}
