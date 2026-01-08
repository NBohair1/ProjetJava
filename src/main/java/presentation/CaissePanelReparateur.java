package presentation;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import dao.MouvementCaisse;

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
        
        topPanel.add(lblTitle);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(lblSolde);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(btnRefresh);
        
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
}
