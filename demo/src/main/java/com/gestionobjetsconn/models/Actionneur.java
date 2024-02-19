package com.gestionobjetsconn.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import com.gestionobjetsconn.database.AppareilDAO;
import com.gestionobjetsconn.database.DatabaseConnection;

public class Actionneur extends ObjetConnecte {
    private String typeAction;
    private String emplacement;
    
    public Actionneur(int id, String nom, String deviceID,String adresseIP, boolean etat, String typeAction, String emplacement) {
        super(id, nom, deviceID, adresseIP, etat);
        this.typeAction = typeAction;
        this.emplacement = emplacement;
    }
    public Actionneur(String nom, String deviceID,String adresseIP, boolean etat, String typeAction, String emplacement) {
        super(nom, deviceID, adresseIP, etat);
        this.typeAction = typeAction;
        this.emplacement = emplacement;
    }    

    public void actionner() {
        System.out.println("Actionneur " + getNom() + " effectue une action de type " + typeAction);
    }

    public String getTypeAction() {
        return typeAction;    
    }

    public String getEmplacement() {
        return emplacement;    
    }
    @Override
    public String toString() {
        return super.toString() + ", Type d'Action: " + typeAction + ", Emplacement: " + emplacement;
    }
    
    @Override
    public void insererDonnees() {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            Random random = new Random();
            List<Data> fileDonnees = new ArrayList<>();
            AppareilDAO appareilDAO = new AppareilDAO(dbConnection.getConnection());

            for (int i = 0; i < 10; i++) {
                String valeurMesure = String.format("%.2f", 10 + (100 - 10) * random.nextDouble());

                Data data = new Data(getTypeAction(), valeurMesure);
                fileDonnees.add(data);
                System.out.println("Data générée: " + data.getTypeData() + " = " + data.getValeur());

                try {
                    Thread.sleep(2000); // Pause 2s pour la simulation de données en temps réel provenant de capteurs
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("La simulation a été interrompue.");
                }

            }

            appareilDAO.insererData(this.getID(), fileDonnees);
            dbConnection.close(); // fermer la connexion
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }
    
}
