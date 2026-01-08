package presentation;

import dao.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;
import java.util.Calendar;

/**
 * Classe pour insérer des données de simulation dans la base de données
 */
public class DataSimulation {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("repairPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            System.out.println("=== Début de l'insertion des données de simulation ===");

            // 1. Créer un Proprietaire (Admin)
            Proprietaire proprietaire = new Proprietaire();
            proprietaire.setNom("Alami");
            proprietaire.setPrenom("Hassan");
            proprietaire.setEmail("admin@repairshop.ma");
            proprietaire.setMdp("admin123");
            proprietaire.setPourcentageGain(100f);
            em.persist(proprietaire);
            System.out.println("✓ Propriétaire créé: " + proprietaire.getEmail());

            // 2. Créer une Caisse pour le propriétaire
            Caisse caisseProprietaire = new Caisse();
            caisseProprietaire.setSoldeActuel(10000f);
            caisseProprietaire.setDernierMouvement(new Date());
            em.persist(caisseProprietaire);
            System.out.println("✓ Caisse créée avec solde: 10000 DH");

            // 3. Créer une Boutique
            Boutique boutique = new Boutique();
            boutique.setNom("RepairShop Casablanca");
            boutique.setAdresse("123 Boulevard Mohammed V, Casablanca");
            boutique.setNumTelephone("0522-123456");
            boutique.setNumP("123456");
            boutique.setProprietaire(proprietaire);
            em.persist(boutique);
            System.out.println("✓ Boutique créée: " + boutique.getNom());

            // 4. Créer des Réparateurs
            Reparateur reparateur1 = new Reparateur();
            reparateur1.setNom("Bennani");
            reparateur1.setPrenom("Ahmed");
            reparateur1.setEmail("ahmed@repairshop.ma");
            reparateur1.setMdp("rep123");
            reparateur1.setPourcentageGain(15f);
            reparateur1.setBoutique(boutique);
            
            Caisse caisseRep1 = new Caisse();
            caisseRep1.setSoldeActuel(2500f);
            caisseRep1.setDernierMouvement(new Date());
            em.persist(caisseRep1);
            reparateur1.setCaisse(caisseRep1);
            em.persist(reparateur1);
            System.out.println("✓ Réparateur créé: " + reparateur1.getEmail());

            Reparateur reparateur2 = new Reparateur();
            reparateur2.setNom("El Idrissi");
            reparateur2.setPrenom("Fatima");
            reparateur2.setEmail("fatima@repairshop.ma");
            reparateur2.setMdp("rep123");
            reparateur2.setPourcentageGain(20f);
            reparateur2.setBoutique(boutique);
            
            Caisse caisseRep2 = new Caisse();
            caisseRep2.setSoldeActuel(3200f);
            caisseRep2.setDernierMouvement(new Date());
            em.persist(caisseRep2);
            reparateur2.setCaisse(caisseRep2);
            em.persist(reparateur2);
            System.out.println("✓ Réparateur créé: " + reparateur2.getEmail());

            // 5. Créer des Clients
            Client client1 = new Client();
            client1.setNom("Tazi");
            client1.setPrenom("Mohamed");
            client1.setTelephone("0661-234567");
            client1.setAdresse("45 Rue Allal Ben Abdellah, Rabat");
            client1.setFidele(true);
            em.persist(client1);

            Client client2 = new Client();
            client2.setNom("Hassani");
            client2.setPrenom("Amina");
            client2.setTelephone("0662-345678");
            client2.setAdresse("78 Avenue Hassan II, Fès");
            client2.setFidele(true);
            em.persist(client2);

            Client client3 = new Client();
            client3.setNom("Chraibi");
            client3.setPrenom("Youssef");
            client3.setTelephone("0663-456789");
            client3.setAdresse("12 Boulevard Zerktouni, Marrakech");
            client3.setFidele(false);
            em.persist(client3);

            Client client4 = new Client();
            client4.setNom("Benjelloun");
            client4.setPrenom("Laila");
            client4.setTelephone("0664-567890");
            client4.setAdresse("90 Rue de la Liberté, Tanger");
            client4.setFidele(false);
            em.persist(client4);
            System.out.println("✓ 4 Clients créés");

            // 6. Créer des Composants
            Composant ecran = new Composant();
            ecran.setNom("Écran LCD iPhone 12");
            ecran.setPrix(450f);
            ecran.setQuantite(15);
            em.persist(ecran);

            Composant batterie = new Composant();
            batterie.setNom("Batterie Samsung Galaxy S21");
            batterie.setPrix(180f);
            batterie.setQuantite(25);
            em.persist(batterie);

            Composant cameraArriere = new Composant();
            cameraArriere.setNom("Caméra arrière Xiaomi Redmi Note 10");
            cameraArriere.setPrix(220f);
            cameraArriere.setQuantite(10);
            em.persist(cameraArriere);

            Composant connecteurCharge = new Composant();
            connecteurCharge.setNom("Connecteur de charge USB-C");
            connecteurCharge.setPrix(45f);
            connecteurCharge.setQuantite(50);
            em.persist(connecteurCharge);

            Composant vitre = new Composant();
            vitre.setNom("Vitre protection iPhone 13");
            vitre.setPrix(35f);
            vitre.setQuantite(40);
            em.persist(vitre);
            System.out.println("✓ 5 Composants créés");

            // 7. Créer des Réparations avec Appareils
            Calendar cal = Calendar.getInstance();
            
            // Réparation 1 - EN_COURS
            Reparation rep1 = new Reparation();
            rep1.setCodeSuivi("REP-2026-001");
            rep1.setEtat("EN_COURS");
            rep1.setCommentaire("Remplacement écran LCD fissuré");
            cal.add(Calendar.DAY_OF_MONTH, -5);
            rep1.setDateDepot(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 7);
            rep1.setDateLivraison(cal.getTime());
            rep1.setPrixTotal(500f);
            rep1.setClient(client1);
            rep1.setReparateur(reparateur1);
            em.persist(rep1);

            Appareil app1 = new Appareil();
            app1.setImei("356789012345671");
            app1.setMarque("Apple");
            app1.setModele("iPhone 12 Pro");
            app1.setTypeAppareil("Smartphone");
            app1.setReparation(rep1);
            em.persist(app1);

            // Réparation 2 - TERMINEE
            cal = Calendar.getInstance();
            Reparation rep2 = new Reparation();
            rep2.setCodeSuivi("REP-2026-002");
            rep2.setEtat("TERMINEE");
            rep2.setCommentaire("Changement batterie + nettoyage");
            cal.add(Calendar.DAY_OF_MONTH, -10);
            rep2.setDateDepot(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 3);
            rep2.setDateLivraison(cal.getTime());
            rep2.setPrixTotal(220f);
            rep2.setClient(client2);
            rep2.setReparateur(reparateur1);
            em.persist(rep2);

            Appareil app2 = new Appareil();
            app2.setImei("356789012345672");
            app2.setMarque("Samsung");
            app2.setModele("Galaxy S21");
            app2.setTypeAppareil("Smartphone");
            app2.setReparation(rep2);
            em.persist(app2);

            // Réparation 3 - EN_ATTENTE
            cal = Calendar.getInstance();
            Reparation rep3 = new Reparation();
            rep3.setCodeSuivi("REP-2026-003");
            rep3.setEtat("EN_ATTENTE");
            rep3.setCommentaire("En attente de pièce - Caméra arrière");
            cal.add(Calendar.DAY_OF_MONTH, -2);
            rep3.setDateDepot(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 10);
            rep3.setDateLivraison(cal.getTime());
            rep3.setPrixTotal(280f);
            rep3.setClient(client3);
            rep3.setReparateur(reparateur2);
            em.persist(rep3);

            Appareil app3 = new Appareil();
            app3.setImei("356789012345673");
            app3.setMarque("Xiaomi");
            app3.setModele("Redmi Note 10 Pro");
            app3.setTypeAppareil("Smartphone");
            app3.setReparation(rep3);
            em.persist(app3);

            // Réparation 4 - EN_COURS
            cal = Calendar.getInstance();
            Reparation rep4 = new Reparation();
            rep4.setCodeSuivi("REP-2026-004");
            rep4.setEtat("EN_COURS");
            rep4.setCommentaire("Problème connecteur de charge");
            cal.add(Calendar.DAY_OF_MONTH, -1);
            rep4.setDateDepot(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 5);
            rep4.setDateLivraison(cal.getTime());
            rep4.setPrixTotal(95f);
            rep4.setClient(client4);
            rep4.setReparateur(reparateur2);
            em.persist(rep4);

            Appareil app4 = new Appareil();
            app4.setImei("356789012345674");
            app4.setMarque("Huawei");
            app4.setModele("P30 Lite");
            app4.setTypeAppareil("Smartphone");
            app4.setReparation(rep4);
            em.persist(app4);

            // Réparation 5 - TERMINEE
            cal = Calendar.getInstance();
            Reparation rep5 = new Reparation();
            rep5.setCodeSuivi("REP-2026-005");
            rep5.setEtat("TERMINEE");
            rep5.setCommentaire("Installation vitre protection");
            cal.add(Calendar.DAY_OF_MONTH, -7);
            rep5.setDateDepot(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            rep5.setDateLivraison(cal.getTime());
            rep5.setPrixTotal(50f);
            rep5.setClient(client1);
            rep5.setReparateur(reparateur2);
            em.persist(rep5);

            Appareil app5 = new Appareil();
            app5.setImei("356789012345675");
            app5.setMarque("Apple");
            app5.setModele("iPhone 13");
            app5.setTypeAppareil("Smartphone");
            app5.setReparation(rep5);
            em.persist(app5);
            System.out.println("✓ 5 Réparations créées avec appareils");

            // 8. Créer des Mouvements de Caisse
            MouvementCaisse mv1 = new MouvementCaisse();
            mv1.setTypeMouvement("ENTREE");
            mv1.setMontant(220f);
            mv1.setDescription("Paiement réparation REP-2026-002");
            mv1.setDateMouvement(new Date());
            mv1.setCaisse(caisseRep1);
            mv1.setReparation(rep2);
            em.persist(mv1);

            MouvementCaisse mv2 = new MouvementCaisse();
            mv2.setTypeMouvement("ENTREE");
            mv2.setMontant(50f);
            mv2.setDescription("Paiement réparation REP-2026-005");
            mv2.setDateMouvement(new Date());
            mv2.setCaisse(caisseRep2);
            mv2.setReparation(rep5);
            em.persist(mv2);

            MouvementCaisse mv3 = new MouvementCaisse();
            mv3.setTypeMouvement("SORTIE");
            mv3.setMontant(450f);
            mv3.setDescription("Achat écrans LCD iPhone");
            mv3.setDateMouvement(new Date());
            mv3.setCaisse(caisseProprietaire);
            em.persist(mv3);
            System.out.println("✓ 3 Mouvements de caisse créés");

            // 9. Créer des Recus
            Recu recu1 = new Recu();
            recu1.setNumeroRecu("REC-2026-001");
            recu1.setDate(new Date());
            recu1.setMontant(220f);
            recu1.setModePaiement("ESPECES");
            recu1.setClient(client2);
            recu1.setReparateur(reparateur1);
            recu1.setReparation(rep2);
            em.persist(recu1);

            Recu recu2 = new Recu();
            recu2.setNumeroRecu("REC-2026-002");
            recu2.setDate(new Date());
            recu2.setMontant(50f);
            recu2.setModePaiement("CARTE");
            recu2.setClient(client1);
            recu2.setReparateur(reparateur2);
            recu2.setReparation(rep5);
            em.persist(recu2);
            System.out.println("✓ 2 Reçus créés");

            em.getTransaction().commit();
            System.out.println("\n=== ✓✓✓ Données de simulation insérées avec succès! ===");
            
            System.out.println("\n=== Comptes de test ===");
            System.out.println("PROPRIETAIRE (Admin):");
            System.out.println("  Email: admin@repairshop.ma");
            System.out.println("  Mot de passe: admin123");
            System.out.println("\nREPARATEUR 1:");
            System.out.println("  Email: ahmed@repairshop.ma");
            System.out.println("  Mot de passe: rep123");
            System.out.println("\nREPARATEUR 2:");
            System.out.println("  Email: fatima@repairshop.ma");
            System.out.println("  Mot de passe: rep123");
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erreur lors de l'insertion: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
