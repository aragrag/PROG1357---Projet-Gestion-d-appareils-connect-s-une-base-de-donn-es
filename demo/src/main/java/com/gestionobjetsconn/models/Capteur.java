package com.gestionobjetsconn.models;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.gestionobjetsconn.database.AppareilDAO;
import com.gestionobjetsconn.database.DatabaseConnection;

// Classe représentant un capteur
public class Capteur extends ObjetConnecte {
    private int id;
    private String typeMesure;
    private String uniteMesure;

    public Capteur(int id, String nom, String deviceID, String adresseIP, boolean etat, String typeMesure, String uniteMesure) {
        super(id, nom, deviceID, adresseIP, etat);
        this.typeMesure = typeMesure;
        this.uniteMesure = uniteMesure;
    }
    public Capteur(String nom, String deviceID, String adresseIP, boolean etat, String typeMesure, String uniteMesure) {
        super(nom, deviceID, adresseIP, etat);
        this.typeMesure = typeMesure;
        this.uniteMesure = uniteMesure;
    }    

    public void mesurer() {
        System.out.println("Capteur " + getNom() + " mesure " + typeMesure);
    }

    public int getId() {
        return id;
    }

    public String getTypeMesure() {
        return typeMesure;
    }

    public String getUniteMesure() {
        return uniteMesure;
    }
    @Override
    public String toString() {
        return super.toString() + ", Type de Mesure: " + typeMesure + ", Unité de Mesure: " + uniteMesure;
    }
    
    @Override
    public void insererDonnees() {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            Random random = new Random();
            List<Data> pileDonnees = new LinkedList<>();
            AppareilDAO appareilDAO = new AppareilDAO(dbConnection.getConnection());

            for (int i = 0; i < 10; i++) {
                String valeurMesure = String.format("%.2f", 10 + (100 - 10) * random.nextDouble());

                Data data = new Data(getTypeMesure(), valeurMesure);

                pileDonnees.addFirst(data);

                System.out.println("Data générée: " + data.getTypeData() + " = " + data.getValeur());

                try {
                    Thread.sleep(2000); // Pause de 2 secondes
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("La simulation a été interrompue.");
                }

            }

            appareilDAO.insererData(this.getID(), pileDonnees);
            dbConnection.close(); // Assurez-vous de fermer la connexion
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer ou logger l'exception
        }
    }
}