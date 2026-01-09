package presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import dao.MouvementCaisse;
import dao.StatistiquesCaisse;

public class CaissePanelReparateur extends JPanel {

    private ReparateurFrame reparateurFrame;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblSolde;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public CaissePanelReparateur(ReparateurFrame reparateurFrame) {
        this.reparateurFrame = reparateurFrame;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        initComponents();
        loadCaisseData();
    }

    private void initComponents() {
        // Top
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topPanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Ma Caisse");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        lblSolde = new JLabel("Solde: ...");
        lblSolde.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSolde.setForeground(new Color(0, 128, 0));
        
        JButton btnRefresh = new JButton("Actualiser");
        btnRefresh.addActionListener(e -> loadCaisseData());
        
        JButton btnStats = new JButton("Statistiques détaillées");
        btnStats.setBackground(new Color(46, 204, 113));
        btnStats.setForeground(Color.WHITE);
        btnStats.addActionListener(e -> afficherStatistiquesDetaillees());
        
        topPanel.add(lblTitle);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(lblSolde);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(btnRefresh);
        topPanel.add(btnStats);
        
        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Type", "Montant", "Description", "Date"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void loadCaisseData() {
        model.setRowCount(0);
        if (reparateurFrame == null || reparateurFrame.getReparateur() == null) return;
        
        try {
            float solde = reparateurFrame.getCaisseMetier().consulterSolde(reparateurFrame.getReparateur());
            lblSolde.setText("Solde: " + solde + " DH");
            
            List<MouvementCaisse> list = reparateurFrame.getCaisseMetier().listerMouvements(reparateurFrame.getReparateur());
            for (MouvementCaisse mv : list) {
                model.addRow(new Object[]{
                    mv.getIdMouvement(),
                    mv.getTypeMouvement(),
                    mv.getMontant() + " DH",
                    mv.getDescription(),
                    mv.getDateMouvement() != null ? sdf.format(mv.getDateMouvement()) : ""
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void afficherStatistiquesDetaillees() {
        if (reparateurFrame == null || reparateurFrame.getReparateur() == null) return;
        
        try {
            StatistiquesCaisse stats = reparateurFrame.getCaisseMetier().obtenirStatistiques(reparateurFrame.getReparateur());
            
            if (stats != null) {
                // Créer un tableau pour les statistiques - UNIQUEMENT LE SOLDE RÉEL
                String[] colonnes = {"Indicateur", "Valeur"};
                Object[][] donnees = {
                    {" Solde réel de ma caisse", String.format("%.2f DH", stats.getSoldeActuel())},
                    {"", ""}, // Ligne vide pour séparer
                    {" Revenu total", String.format("%.2f DH", stats.getRevenuTotal())},
                    {" Revenus période (30j)", String.format("%.2f DH", stats.getRevenusPeriode())},
                    {" Réparations terminées", String.valueOf(stats.getNombreReparationsTerminees())},
                    {" Total réparations", String.valueOf(stats.getNombreReparations())}
                };
                
                DefaultTableModel modelStats = new DefaultTableModel(donnees, colonnes) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                
                JTable tableStats = new JTable(modelStats);
                tableStats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                tableStats.setRowHeight(30);
                tableStats.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
                
                // Ajuster la largeur des colonnes
                tableStats.getColumnModel().getColumn(0).setPreferredWidth(250);
                tableStats.getColumnModel().getColumn(1).setPreferredWidth(150);
                
                JScrollPane scrollPane = new JScrollPane(tableStats);
                scrollPane.setPreferredSize(new Dimension(450, 350));
                
                JOptionPane.showMessageDialog(this, scrollPane, "Statistiques Détaillées - Ma Caisse", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Aucune statistique disponible", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erreur: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
