package presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.Emprunt;
import dao.Reparateur;
import dao.StatistiquesFinancieres;
import metier.BoutiqueMetierImpl;
import metier.CaisseMetierImpl;
import metier.EmpruntMetierImpl;
import metier.IBoutiqueMetier;
import metier.ICaisseMetier;
import metier.IEmpruntMetier;

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
        JPanel centerPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JButton btnReparateurs = createBigButton(" Gérer les Réparateurs", new Color(51, 153, 255));
        JButton btnStats = createBigButton("Statistiques Financières", new Color(46, 204, 113));
        JButton btnEmprunts = createBigButton(" Gestion des Emprunts", new Color(241, 196, 15));
        JButton btnCaisses = createBigButton(" Voir Toutes les Caisses", new Color(142, 68, 173));
        JButton btnHistorique = createBigButton(" Historique Réparations", new Color(52, 152, 219));
        JButton btnParametres = createBigButton(" Paramètres Boutique", Color.GRAY);
        
        centerPanel.add(btnReparateurs);
        centerPanel.add(btnStats);
        centerPanel.add(btnCaisses);
        centerPanel.add(btnHistorique);
        centerPanel.add(btnEmprunts);
        centerPanel.add(btnParametres);
        
        add(centerPanel, BorderLayout.CENTER);

        // --- ACTIONS ---

        // 1. Gérer Réparateurs
        btnReparateurs.addActionListener(e -> afficherDialogReparateurs());

        // 2. Statistiques
        btnStats.addActionListener(e -> {
            afficherStatistiquesFinancieresDetailles();
        });
        
        // 3. Caisses
        btnCaisses.addActionListener(e -> afficherCaissesReparateurs());
        
        // 4. Historique
        btnHistorique.addActionListener(e -> afficherHistoriqueReparations());

        // 5. Emprunts
        btnEmprunts.addActionListener(e -> afficherDialogEmprunts());

        // 4. Paramètres (Ouvre le dialog boutique existant)
        btnParametres.addActionListener(e -> {
            BoutiqueDialog dialog = new BoutiqueDialog(mainFrame, boutiqueMetier, mainFrame.getBoutiqueActuelle());
            dialog.setVisible(true);
        });
    }
    
    private void afficherCaissesReparateurs() {
        try {
            java.util.List<dao.Caisse> caisses = boutiqueMetier.listerCaissesReparateurs(mainFrame.getProprietaire());
            
            if (caisses == null || caisses.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucune caisse disponible", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== CAISSES DE TOUS LES RÉPARATEURS ===\n\n");
            
            for (dao.Caisse caisse : caisses) {
                sb.append("• Caisse #").append(caisse.getIdCaisse()).append(" : ").append(String.format("%.2f DH", caisse.getSoldeActuel())).append("\n");
            }
            
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Caisses Réparateurs", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void afficherHistoriqueReparations() {
        try {
            // Demander le nombre de réparations à afficher
            String input = JOptionPane.showInputDialog(this, "Nombre de réparations à afficher:", "Limite", JOptionPane.QUESTION_MESSAGE);
            
            if (input == null || input.trim().isEmpty()) return;
            
            int limite = Integer.parseInt(input.trim());
            
            java.util.List<dao.Reparation> reparations = boutiqueMetier.listerDernieresReparations(mainFrame.getProprietaire(), limite);
            
            if (reparations == null || reparations.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Aucune réparation trouvée", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Historique des Réparations", true);
            dialog.setSize(800, 500);
            dialog.setLocationRelativeTo(this);
            
            String[] cols = {"Code", "Client", "Réparateur", "État", "Prix (DH)", "Date Dépôt"};
            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            JTable table = new JTable(model);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            
            for (dao.Reparation rep : reparations) {
                String clientNom = rep.getClient() != null ? rep.getClient().getNom() + " " + rep.getClient().getPrenom() : "N/A";
                String reparateurNom = rep.getReparateur() != null ? rep.getReparateur().getPrenom() + " " + rep.getReparateur().getNom() : "N/A";
                String dateDepot = rep.getDateDepot() != null ? sdf.format(rep.getDateDepot()) : "";
                
                model.addRow(new Object[]{
                    rep.getCodeSuivi(),
                    clientNom,
                    reparateurNom,
                    rep.getEtat(),
                    rep.getPrixTotal(),
                    dateDepot
                });
            }
            
            dialog.add(new JScrollPane(table));
            dialog.setVisible(true);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Limite invalide", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
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
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnAdd = new JButton("Ajouter un réparateur");
        btnAdd.addActionListener(e -> {
            ajouterReparateur();
            dialog.dispose();
        });
        
        JButton btnModifier = new JButton("Modifier %");
        btnModifier.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(dialog, "Sélectionnez un réparateur");
                return;
            }
            
            try {
                int idRep = (int) model.getValueAt(selectedRow, 0);
                List<Reparateur> list = boutiqueMetier.listerReparateurs(mainFrame.getBoutiqueActuelle());
                Reparateur repToModify = null;
                
                for (Reparateur r : list) {
                    if (r.getId() == idRep) {
                        repToModify = r;
                        break;
                    }
                }
                
                if (repToModify == null) return;
                
                String input = JOptionPane.showInputDialog(dialog, 
                    "Nouveau % de gain pour " + repToModify.getPrenom() + " " + repToModify.getNom() + ":", 
                    repToModify.getPourcentageGain());
                
                if (input != null && !input.trim().isEmpty()) {
                    float newPourcentage = Float.parseFloat(input.trim());
                    boutiqueMetier.modifierPourcentageGain(repToModify, newPourcentage);
                    JOptionPane.showMessageDialog(dialog, "Pourcentage modifié!");
                    
                    // Recharger
                    model.setRowCount(0);
                    list = boutiqueMetier.listerReparateurs(mainFrame.getBoutiqueActuelle());
                    for(Reparateur r : list) {
                        model.addRow(new Object[]{r.getId(), r.getNom(), r.getPrenom(), r.getEmail(), r.getPourcentageGain()});
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Erreur: " + ex.getMessage());
            }
        });
        
        bottomPanel.add(btnAdd);
        bottomPanel.add(btnModifier);
        dialog.add(bottomPanel, BorderLayout.SOUTH);
        
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
    
    private void afficherStatistiquesFinancieresDetailles() {
        try {
            StatistiquesFinancieres stats = boutiqueMetier.obtenirStatistiquesFinancieres(mainFrame.getProprietaire());
            
            if (stats == null) {
                JOptionPane.showMessageDialog(this, "Aucune statistique disponible", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            StringBuilder sb = new StringBuilder();
            sb.append("=== STATISTIQUES FINANCIÈRES GLOBALES ===\n\n");
            sb.append("💰 Revenu Total Boutique : ").append(String.format("%.2f DH", stats.getRevenuTotalBoutique())).append("\n");
            sb.append("🏦 Total Caisses Boutique : ").append(String.format("%.2f DH", stats.getTotalCaissesBoutique())).append("\n\n");
            
            sb.append("--- Réparateurs ---\n");
            
            if (stats.getRevenusParReparateur() != null && !stats.getRevenusParReparateur().isEmpty()) {
                for (java.util.Map.Entry<Long, Float> entry : stats.getRevenusParReparateur().entrySet()) {
                    Long idRep = entry.getKey();
                    Float revenu = entry.getValue();
                    Float pourcentage = stats.getPourcentagesParReparateur().get(idRep);
                    Integer nbRep = stats.getNombreReparationsParReparateur().get(idRep);
                    
                    // Trouver le nom du réparateur
                    String nomRep = "Réparateur #" + idRep;
                    try {
                        List<Reparateur> reparateurs = boutiqueMetier.listerReparateurs(mainFrame.getBoutiqueActuelle());
                        for (Reparateur r : reparateurs) {
                            if (r.getId() == idRep) {
                                nomRep = r.getPrenom() + " " + r.getNom();
                                break;
                            }
                        }
                    } catch (Exception e) { }
                    
                    sb.append("\n👤 ").append(nomRep).append("\n");
                    sb.append("   💵 Revenu : ").append(String.format("%.2f DH", revenu)).append("\n");
                    sb.append("   📊 % Gain : ").append(String.format("%.2f%%", pourcentage)).append("\n");
                    sb.append("   🔧 Réparations : ").append(nbRep != null ? nbRep : 0).append("\n");
                }
            } else {
                sb.append("Aucun réparateur actif\n");
            }
            
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
            
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(500, 400));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Statistiques Financières Détaillées", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}