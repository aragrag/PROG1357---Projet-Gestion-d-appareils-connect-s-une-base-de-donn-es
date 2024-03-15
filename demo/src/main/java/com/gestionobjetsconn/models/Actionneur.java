package com.gestionobjetsconn.models;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.gestionobjetsconn.database.DatabaseConnection;

public class Actionneur extends Dispositif {
    private String typeAction;
    private String emplacement;
    
    public Actionneur(int id, String nom, boolean etat, String typeAction, String emplacement, int objetConnecteId) {
        super(id, nom, etat, objetConnecteId);
        this.typeAction = typeAction;
        this.emplacement = emplacement;
    }
    public Actionneur(String nom, boolean etat, String typeAction, String emplacement, int objetConnecteId) {
        super(nom, etat, objetConnecteId);
        this.typeAction = typeAction;
        this.emplacement = emplacement;
    }    
    public void setID(int id) {
        this.id = id;
    } 
    public void actionner() {
        System.out.println("Actionneur " + getNom() + " effectue une action de type " + typeAction);
    }
    public  int getID() {
        return id;    
    }    
    public void setTypeAction(String typeAction) {
        this.typeAction = typeAction;    
    }
    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;    
    }
    public String getTypeAction() {
        return typeAction;    
    }

    public String getEmplacement() {
        return emplacement;    
    }
    
    public  int getobjetConnecteId() {
        return objetConnecteId;    
    }    
    @Override
    public String toString() {
        return super.toString() + ", Type d'Action: " + typeAction + ", Emplacement: " + emplacement;
    }
    
    @Override
    public void insererDonnees() {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            Random random = new Random();
            // List<Donne> fileDonnees = new ArrayList<>();
            Queue<DonneObject> fileDonnees = new LinkedList<>();

            for (int i = 0; i < 10; i++) {
                String valeurMesure = String.format("%.2f", 10 + (100 - 10) * random.nextDouble());

                DonneObject data = new DonneObject(getTypeAction(), valeurMesure);
                fileDonnees.add(data);
                System.out.println("Data: " + data.getTypeData() + " = " + data.getValeur());

                try {
                    Thread.sleep(2000); // Pause 2s pour la simulation de données en temps réel provenant de capteurs
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("La simulation a été interrompue.");
                }

            }

            // appareilDAO.insererData(this.getID(), fileDonnees);
            dbConnection.close(); // fermer la connexion
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }
    
}
