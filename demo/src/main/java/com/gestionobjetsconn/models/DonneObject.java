package com.gestionobjetsconn.models;

public class DonneObject {
    private String typeData;
    private String valeur;

    public DonneObject(String typeData, String valeur) {
        this.typeData = typeData;
        this.valeur = valeur;
    }

    public String getTypeData() {
        return typeData;
    }

    public String getValeur() {
        return valeur;
    }
}
