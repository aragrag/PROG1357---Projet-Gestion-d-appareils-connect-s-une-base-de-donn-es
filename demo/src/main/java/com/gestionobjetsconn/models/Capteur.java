package com.gestionobjetsconn.models;

import java.sql.SQLException;
import java.util.Random;
import java.util.Stack;

import com.gestionobjetsconn.database.DatabaseConnection;


// Classe représentant un capteur
public class Capteur extends Dispositif {
    private String typeMesure;
    private String uniteMesure;

    public Capteur(int id, String nom, boolean etat, String typeMesure, String uniteMesure, int objetConnecteId) {
        super(id, nom, etat, objetConnecteId);
        this.typeMesure = typeMesure;
        this.uniteMesure = uniteMesure;
    }
    public Capteur(String nom, boolean etat, String typeMesure, String uniteMesure, int objetConnecteId) {
        super(nom, etat, objetConnecteId);
        this.typeMesure = typeMesure;
        this.uniteMesure = uniteMesure;
    }    

    public void mesurer() {
        System.out.println("Capteur " + getNom() + " mesure " + typeMesure);
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
            // List<Donne> pileDonnees = new LinkedList<>();
            Stack<DonneObject> pileDonnees = new Stack<>();

            for (int i = 0; i < 10; i++) {
                String valeurMesure = String.format("%.2f", 10 + (100 - 10) * random.nextDouble());

                DonneObject data = new DonneObject(getTypeMesure(), valeurMesure);

                pileDonnees.add(data);

                System.out.println("Data: " + data.getTypeData() + " = " + data.getValeur());

                try {
                    Thread.sleep(2000); // Pause de 2 secondes
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("La simulation a été interrompue.");
                }

            }

            // appareilDAO.insererData(this.getID(), pileDonnees);
            dbConnection.close(); // Assurez-vous de fermer la connexion
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer ou logger l'exception
        }
    }
}