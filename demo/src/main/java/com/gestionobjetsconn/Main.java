package com.gestionobjetsconn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.SQLException;
import java.util.Scanner;

import com.gestionobjetsconn.database.AppareilDAO;
import com.gestionobjetsconn.database.DatabaseConnection;
import com.gestionobjetsconn.models.Actionneur;
import com.gestionobjetsconn.models.Capteur;
import com.gestionobjetsconn.models.ObjetConnecte;


import com.sun.net.httpserver.HttpServer;


public class Main {

    private static ObjetConnecte creerObjetConnecte(Scanner scanner) {
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

        return new ObjetConnecte(nom, deviceID, adresseIP, etatTF);
    } 
    private static Actionneur creerActionneur(Scanner scanner) {
        scanner.nextLine();        

        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            AppareilDAO appareilDAO = new AppareilDAO(dbConnection.getConnection());        
            appareilDAO.afficherObjetConnectes();
        
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }  
        System.out.print("Entrer Objet Connecte (UC) : ");
        int objetConnecteId = scanner.nextInt();    

        scanner.nextLine(); 
        System.out.print("Entrer le nom : ");
        String nom = scanner.nextLine();
          
        System.out.print("Entrer l'etat ( 0 = inactif | 1 = actif )' : ");
        String etat = scanner.nextLine();
        boolean etatTF = etat.equals("1");  

        System.out.print("Entrer le type d'action : ");        
        String typeAction = scanner.nextLine();

        System.out.print("Entrer l'emplacement : ");
        String emplacement = scanner.nextLine();

        return new Actionneur(nom, etatTF, typeAction, emplacement, objetConnecteId);
    } 
    private static Capteur creerCapteur(Scanner scanner) {
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            AppareilDAO appareilDAO = new AppareilDAO(dbConnection.getConnection());        
            appareilDAO.afficherObjetConnectes();
        
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données : " + e.getMessage());
        }    
        
        System.out.print("Entrer Objet Connecte (UC) : ");
        int objetConnecteId = scanner.nextInt();    

        scanner.nextLine(); 
        System.out.print("Entrer le nom : ");
        String nom = scanner.nextLine();
        
        System.out.print("Entrer l'etat ( 0 = inactif | 1 = actif )' : ");
        String etat = scanner.nextLine();
        boolean etatTF = etat.equals("1");  

        System.out.print("Entrer le type de mesure : ");
        String typeMesure = scanner.nextLine();

        System.out.print("Entrer l'unité de mesure : ");
        String uniteMesure = scanner.nextLine();

        return new Capteur(nom, etatTF, typeMesure, uniteMesure, objetConnecteId);
        
    }   
    
    private static void afficherMenu() {
        System.out.println("Menu:");
        System.out.println("1. Ajouter un appareil ( UC )");
        System.out.println("2. Ajouter un Actionneur");
        System.out.println("3. Ajouter un Capteur");
        System.out.println("4. Mettre à jour l'etat d'un appareil (O/D)");
        System.out.println("5. Supprimer un appareil (O/D)");
        System.out.println("6. Affichage des appareils ( UCs )");
        System.out.println("7. Affichage des appareils ( Actionneurs/Capteurs )");
        System.out.println("8. Simuler/Génèree aléatoirement  des données d'un Objet");
        
        System.out.println("9. Quitter");
        System.out.print("\nChoisir une option : \n");
    }
          
    public static void main(String[] args) throws IOException {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Associer le gestionnaire de données au chemin "/receive-data"
        server.createContext("/receive-data", new AppHttpServer.GeneralHandler());

        // Associer le gestionnaire de données au chemin "/objetsconnecte"
        server.createContext("/objetsconnecte", new AppHttpServer.GeneralHandler());  
        
        // Associer le gestionnaire de données au chemin "/actionneur"
        server.createContext("/actionneur", new AppHttpServer.GeneralHandler());          
   
        // Associer le gestionnaire de données au chemin "/capteur"
        server.createContext("/capteur", new AppHttpServer.GeneralHandler());  
        
        // Associer le gestionnaire de données au chemin "/objetsconnectes"
        server.createContext("/objetsconnectes", new AppHttpServer.GeneralHandler());

        
        // Démarrer le serveur
        server.start();
        
        System.out.println("Server started on port " + port);
        
        
        try (DatabaseConnection dbConnection = new DatabaseConnection()) {
            AppareilDAO appareilDAO = new AppareilDAO(dbConnection.getConnection());
            Scanner scanner = new Scanner(System.in);
            int choix = 0;

            while (choix != 9) {
                afficherMenu();
                choix = scanner.nextInt();

                switch (choix) {
                    case 1:
                        ObjetConnecte objetConnecte = creerObjetConnecte(scanner);
                        appareilDAO.ajouterObjetConnecte(objetConnecte);
                        break;
                    case 2:
                        Actionneur actionneur = creerActionneur(scanner);
                        appareilDAO.ajouterActionneur(actionneur);
                        break;                        
                    case 3:                    
                        Capteur capteur = creerCapteur(scanner);
                        appareilDAO.ajouterCapteur(capteur);
                        break;
                    case 4:
                        Scanner scan = new Scanner(System.in);

                        System.out.println("Voulez-vous mettre à jour un ObjetConnecte ou un Dispositif? (O/D)");
                        String ObjetConnecte_or_Dispositif = scan.nextLine().trim().toUpperCase();
                
                        if ("O".equals(ObjetConnecte_or_Dispositif)) {
                            // Logique de mise à jour pour ObjetConnecte
                            System.out.println("Entrez le nom de l'ObjetConnecte que vous souhaitez mettre à jour :");
                            String nomObjetConnecte = scan.nextLine();

                            // Appel de getIdParNomAppareil et vérification si le résultat est null
                            Integer idObjetConnecte = appareilDAO.getIdParNomAppareil(nomObjetConnecte);

                            if (idObjetConnecte != null) {
                                objetConnecte = creerObjetConnecte(scanner);
                                appareilDAO.mettreAJourAppareil(objetConnecte, idObjetConnecte);                                
                            } else {
                                System.out.println("Objet Connecte n'existe pas.");
                            }

                        } else if ("D".equals(ObjetConnecte_or_Dispositif)) {
                            // Mise à jour pour Dispositif
                            System.out.println("Entrez le nom du Dispositif que vous souhaitez mettre à jour :");
                            String nomDispositif = scan.nextLine();
                            
                            // Appel de getIdParNomAppareil et vérification si le résultat est null
                            Integer idDispositif = appareilDAO.getIdParNomAppareil(nomDispositif);
                            
                            if (idDispositif != null) {
                                // Vérifier le type de l'appareil
                                String typeAppareil = appareilDAO.getTypeAppareil(idDispositif);
                                if (typeAppareil.equals("Actionneur")) {
                                    // L'appareil est un actionneur
                                    actionneur = creerActionneur(scanner);
                                    appareilDAO.mettreAJourAppareil(actionneur, idDispositif);
                                } else if (typeAppareil.equals("Capteur")) {
                                    // L'appareil est un capteur
                                    capteur = creerCapteur(scanner);
                                    appareilDAO.mettreAJourAppareil(capteur, idDispositif);
                                } else {
                                    System.out.println("Aucun dispositif trouvé avec l'ID spécifié.");
                                }
                            } else {
                                System.out.println("Dispositif n'existe pas.");
                            }

                        } else {
                            System.out.println("Choix invalide.");
                        }
                        break;                      
                    case 5:
                        Scanner scan1 = new Scanner(System.in);
                        System.out.print("Entrer le nom de l'appareil (O/D) à supprimer : ");
                        String nomAppareil = scan1.nextLine();

                        appareilDAO.supprimerAppareilParNom(nomAppareil); // Appel à la méthode de suppression avec l'ID saisi
                        break;                    
                    case 6:
                        System.out.println("Affichage des appareils ( UCs ) :");
                        appareilDAO.afficherObjetConnectes(); // Affiche les appareils
                        break;
                    case 7:
                        System.out.println("Affichage des appareils ( Actionneurs/Capteurs ) :");
                        appareilDAO.afficherDispositif(); // Affiche les appareils Ucs
                        break;                        
                    case 8:
                        // System.out.print("Entrer l'ID de l'appareil à Simuler/Génèree : ");
                        // int idAppareilASimulerGeneree = scanner.nextInt();  
                        // ObjetConnecte  objetConnecte = appareilDAO.getObjetConnecteById(idAppareilASimulerGeneree);
                        // System.out.println(objetConnecte);
                        // objetConnecte.insererDonnees();


                        break;
                    case 9:
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

   
   
}