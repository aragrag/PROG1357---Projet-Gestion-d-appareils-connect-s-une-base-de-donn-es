package com.gestionobjetsconn;

import java.sql.SQLException;
import java.util.Scanner;

import com.gestionobjetsconn.database.AppareilDAO;
import com.gestionobjetsconn.database.DatabaseConnection;
import com.gestionobjetsconn.models.Actionneur;
import com.gestionobjetsconn.models.Capteur;
import com.gestionobjetsconn.models.DonneObject;
import com.gestionobjetsconn.models.ObjetConnecte;
 
public class Main {



    private static Actionneur creerActionneur(Scanner scanner) {
        scanner.nextLine();        
        System.out.print("Entrer le nom : ");
        String nom = scanner.nextLine();
        System.out.print("Entrer le deviceID : ");
        String deviceID = scanner.nextLine();        
        System.out.print("Entrer l'adresse IP : ");
        String adresseIP = scanner.nextLine();
        System.out.print("Entrer l'etat ( 0 = inactif | 1 = actif )' : ");
        String etat = scanner.nextLine();
        boolean etatTF = etat.equals("1");      
        System.out.print(etatTF);      
        System.out.print("Entrer le type d'action : ");        
        String typeAction = scanner.nextLine();
        System.out.print("Entrer l'emplacement : ");
        String emplacement = scanner.nextLine();

        return new Actionneur(nom, deviceID, adresseIP, etatTF, typeAction, emplacement);
    } 
    private static Capteur creerCapteur(Scanner scanner) {
        scanner.nextLine(); // Pour consommer la ligne en attente après nextInt()
        System.out.print("Entrer le nom : ");
        String nom = scanner.nextLine();
        System.out.print("Entrer le deviceID : ");
        String deviceID = scanner.nextLine();         
        System.out.print("Entrer l'adresse IP : ");
        String adresseIP = scanner.nextLine();
        System.out.print("Entrer l'etat ( 0 = inactif | 1 = actif )' : ");
        String etat = scanner.nextLine();
        boolean etatTF = etat.equals("1");      
        System.out.print("Entrer le type de mesure : ");
        String typeMesure = scanner.nextLine();
        System.out.print("Entrer l'unité de mesure : ");
        String uniteMesure = scanner.nextLine();

        return new Capteur(nom, deviceID, adresseIP, etatTF, typeMesure, uniteMesure);
    }   
    
    
    public static void main(String[] args) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            AppareilDAO appareilDAO = new AppareilDAO(dbConnection.getConnection());
            Scanner scanner = new Scanner(System.in);
            int choix = 0;

            while (choix != 7) {
                afficherMenu();
                choix = scanner.nextInt();

                switch (choix) {
                    case 1:
                        Actionneur actionneur = creerActionneur(scanner);
                        appareilDAO.ajouterActionneur(actionneur);
                        break;
                    case 2:                    
                        Capteur capteur = creerCapteur(scanner);
                        appareilDAO.ajouterCapteur(capteur);
                        break;
                    case 3:
                        // Mettre à jour un appareil
                        System.out.println("Entrez l'ID de l'appareil que vous souhaitez mettre à jour :");
                        int idAppareil = scanner.nextInt();
                        
                        // Vérifier le type de l'appareil
                        String typeAppareil = appareilDAO.getTypeAppareil(idAppareil);
                        if (typeAppareil.equals("Actionneur")) {
                            // L'appareil est un actionneur   
                            Actionneur _actionneur = creerActionneur(scanner);
                            appareilDAO.mettreAJourAppareil(_actionneur, idAppareil);
                        } else if (typeAppareil.equals("Capteur")) {
                            // L'appareil est un capteur                            
                            Capteur _capteur = creerCapteur(scanner);
                            appareilDAO.mettreAJourAppareil(_capteur, idAppareil);
                        } else {
                            System.out.println("Aucun appareil trouvé avec l'ID spécifié.");
                        }
                        break;                      
                    case 4:
                        System.out.print("Entrer l'ID device de l'appareil à supprimer : ");
                        appareilDAO.supprimerAppareil(); // Appel à la méthode de suppression avec l'ID saisi
                        break;                    
                    case 5:
                        System.out.println("Affichage des appareils :");
                        appareilDAO.afficherAppareils(); // Affiche les appareils
                        break;
                    case 6:
                        System.out.print("Entrer l'ID de l'appareil à Simuler/Génèree : ");
                        int idAppareilASimulerGeneree = scanner.nextInt();  
                        ObjetConnecte  objetConnecte = appareilDAO.getObjetConnecteById(idAppareilASimulerGeneree);
                        System.out.println(objetConnecte);
                        objetConnecte.insererDonnees();

                        break;
                    case 7:
                        System.out.println("Fin du programme.");
                        break;                        
                    default:
                        System.out.println("Choix invalide, veuillez réessayer.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }
    }

   
    private static void afficherMenu() {
        System.out.println("Menu:");
        System.out.println("1. Ajouter un appareil ( Actionneur )");
        System.out.println("2. Ajouter un appareil ( Capteur ) ");
        System.out.println("3. Mettre à jour l'etat d'un appareil");
        System.out.println("4. Supprimer un appareil");
        System.out.println("5. Affichage des appareils ( Actionneurs/Capteurs )");
        System.out.println("6. Simuler/Génèree aléatoirement  des données d'un Objet");
        
        System.out.println("7. Quitter");
        System.out.print("\nChoisir une option : \n");
    }
      
}