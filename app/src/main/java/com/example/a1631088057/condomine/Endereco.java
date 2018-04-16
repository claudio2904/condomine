package com.example.a1631088057.condomine;

/**
 * Created by ander on 12/04/2018.
 */

public class Endereco {
    private String localidade;
    private String logradouro;
    private String uf;
    private String bairro;

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getLogradouro() {return logradouro; }

    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }

    public String getBairro() {  return bairro;   }

    public void setBairro(String bairro) {  this.bairro = bairro;  }
}
