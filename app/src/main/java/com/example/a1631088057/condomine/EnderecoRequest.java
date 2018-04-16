package com.example.a1631088057.condomine;

import android.location.Address;
import android.os.AsyncTask;

import com.example.a1631088057.condomine.activities.Atualiza_Activity;
import com.example.a1631088057.condomine.activities.Cadastro_Activity;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;

/**
 * Created by ander on 12/04/2018.
 */

public class EnderecoRequest extends AsyncTask<Void, Void, Endereco> {
    private WeakReference<Cadastro_Activity> activityC;
    private WeakReference<Atualiza_Activity> activityA;
    private String local;

    public EnderecoRequest(Cadastro_Activity activity, String local) {
        this.activityC = new WeakReference<>(activity);
        this.local = local;
    }

    public EnderecoRequest(Atualiza_Activity activity, String local) {
        this.activityA = new WeakReference<>(activity);
        this.local = local;
    }

    @Override
    protected Endereco doInBackground(Void... voids) {
        try{
            String jsonString;
            if (local.equals("cadastro")) {
                jsonString = JsonRequest.request(activityC.get().getUriZipCode());
            } else {
                jsonString = JsonRequest.request(activityA.get().getUriZipCode());
            }
            Gson gson = new Gson();

            /* GERACAO DA INSTÂNCIA */
            return gson.fromJson(jsonString, Endereco.class);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Endereco endereco) {
        super.onPostExecute(endereco);
        if (local.equals("cadastro")){
            if( activityC.get() != null ){
                if( endereco != null ){
                    /* ENVIANDO INSTÂNCIA */
                    activityC.get().setEndereco( endereco );
                }
            }
        }
        else if (local.equals("atualiza")) {
            if (activityA.get() != null) {
                if (endereco != null) {
                /* ENVIANDO INSTÂNCIA */
                    activityA.get().setEndereco(endereco);
                }
            }
        }
    }
}
