package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import dao.Boutique;
import dao.Emprunt;
import dao.Reparateur;
import metier.*;

public class AdminPanel extends JPanel {

    private final MainFrame mainFrame;
    private final IBoutiqueMetier boutiqueMetier;
    private final ICaisseMetier caisseMetier;
    private final IEmpruntMetier empruntMetier;

    public AdminPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.boutiqueMetier = new BoutiqueMetierImpl();
        this.caisseMetier = new CaisseMetierImpl();
        this.empruntMetier = new EmpruntMetierImpl();
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
    }

    private void initComponents() {
        // En-tête
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Administration");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        topPanel.add(lblTitle);
        
        add(topPanel, BorderLayout.NORTH);

        // Panneau central avec boutons
        JPanel centerPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JButton btnReparateurs = createBigButton(" Gérer les Réparateurs", new Color(51, 153, 255));
        JButton btnStats = createBigButton("Statistiques Financières", new Color(46, 204, 113));
        JButton btnEmprunts = createBigButton(" Gestion des Emprunts", new Color(241, 196, 15));
        JButton btnParametres = createBigButton(" Paramètres Boutique", Color.GRAY);
        
        centerPanel.add(btnReparateurs);
        centerPanel.add(btnStats);
        centerPanel.add(btnEmprunts);
        centerPanel.add(btnParametres);
        
        add(centerPanel, BorderLayout.CENTER);

        // --- ACTIONS ---

        // 1. Gérer Réparateurs
        btnReparateurs.addActionListener(e -> afficherDialogReparateurs());

        // 2. Statistiques
        btnStats.addActionListener(e -> {
            try {
                float total = caisseMetier.consulterTotalCaissesBoutique(mainFrame.getProprietaire());
                JOptionPane.showMessageDialog(this, 
                    "Total Trésorerie (Propriétaire + Réparateurs) : \n" + total + " DH", 
                    "Statistiques", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // 3. Emprunts
        btnEmprunts.addActionListener(e -> afficherDialogEmprunts());

        // 4. Paramètres (Ouvre le dialog boutique existant)
        btnParametres.addActionListener(e -> {
            BoutiqueDialog dialog = new BoutiqueDialog(mainFrame, boutiqueMetier, mainFrame.getBoutiqueActuelle());
            dialog.setVisible(true);
        });
    }

    // Helper pour créer de jolis boutons
    private JButton createBigButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    // --- DIALOGUES INTERNES ---

    private void afficherDialogReparateurs() {
        JDialog dialog = new JDialog(mainFrame, "Liste des Réparateurs", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        String[] cols = {"ID", "Nom", "Prénom", "Email", "% Gain"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        
        // Charger données
        try {
            List<Reparateur> list = boutiqueMetier.listerReparateurs(mainFrame.getBoutiqueActuelle());
            for(Reparateur r : list) {
                model.addRow(new Object[]{r.getId(), r.getNom(), r.getPrenom(), r.getEmail(), r.getPourcentageGain()});
            }
        } catch(Exception e) {}

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        
        JButton btnAdd = new JButton("Ajouter un réparateur");
        btnAdd.addActionListener(e -> {
            ajouterReparateur();
            dialog.dispose(); // Fermer pour rafraichir (ou recharger le model)
        });
        dialog.add(btnAdd, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void ajouterReparateur() {
        JTextField txtNom = new JTextField();
        JTextField txtPrenom = new JTextField();
        JTextField txtEmail = new JTextField();
        JPasswordField txtMdp = new JPasswordField();
        JTextField txtGain = new JTextField("50");

        Object[] message = {
            "Nom:", txtNom, "Prénom:", txtPrenom, 
            "Email:", txtEmail, "Mot de passe:", txtMdp, 
            "Pourcentage Gain (%):", txtGain
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Nouveau Réparateur", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                boutiqueMetier.creerReparateur(mainFrame.getBoutiqueActuelle(), 
                    txtNom.getText(), txtPrenom.getText(), txtEmail.getText(), 
                    new String(txtMdp.getPassword()), Float.parseFloat(txtGain.getText()));
                JOptionPane.showMessageDialog(this, "Réparateur ajouté !");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage());
            }
        }
    }

    private void afficherDialogEmprunts() {
        JDialog dialog = new JDialog(mainFrame, "Liste des Emprunts", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        String[] cols = {"Montant", "Type", "Date", "Remboursé?"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        
        try {
            List<Emprunt> list = empruntMetier.listerEmpruntsParReparateur(mainFrame.getReparateurActuel());
            for(Emprunt emp : list) {
                model.addRow(new Object[]{
                    emp.getMontant(), emp.getType(), emp.getDate(), emp.isRembourse() ? "Oui" : "Non"
                });
            }
        } catch(Exception e) {}

        dialog.add(new JScrollPane(table));
        dialog.setVisible(true);
    }
}