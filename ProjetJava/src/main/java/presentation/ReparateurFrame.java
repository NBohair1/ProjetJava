package presentation;

import dao.Reparateur;
import metier.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ReparateurFrame extends JFrame {

    private Reparateur reparateur;
    private IReparationMetier reparationMetier;
    private ICaisseMetier caisseMetier;
    private IClientMetier clientMetier;
    private IComposantMetier composantMetier;
    private IEmpruntMetier empruntMetier;
    
    private JTabbedPane tabbedPane;
    private ReparationPanelReparateur reparationPanel;
    private ClientPanelReparateur clientPanel;
    private CaissePanelReparateur caissePanel;
    private ComposantPanelReparateur composantPanel;

    /**
     * @wbp.parser.constructor
     */
    public ReparateurFrame() {
        this.reparateur = null;
        initComponents();
    }

    public ReparateurFrame(Reparateur reparateur) {
        this.reparateur = reparateur;
        if (reparateur != null) {
            this.reparationMetier = new ReparationMetierImpl();
            this.caisseMetier = new CaisseMetierImpl();
            this.clientMetier = new ClientMetierImpl();
            this.composantMetier = new ComposantMetierImpl();
            this.empruntMetier = new EmpruntMetierImpl();
        }
        initComponents();
    }

    private void initComponents() {
        setTitle("Interface Réparateur" + (reparateur != null ? " - " + reparateur.getPrenom() + " " + reparateur.getNom() : ""));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        if (reparateur != null) {
            // Onglet Mes Réparations (filtré pour ce réparateur uniquement)
            reparationPanel = new ReparationPanelReparateur(this);
            tabbedPane.addTab("Mes Réparations", reparationPanel);

            // Onglet Gestion Clients (nécessaire pour créer des réparations)
            clientPanel = new ClientPanelReparateur(this);
            tabbedPane.addTab("Clients", clientPanel);

            // Onglet Gestion Composants (pièces détachées)
            composantPanel = new ComposantPanelReparateur(this);
            tabbedPane.addTab("Composants", composantPanel);

            // Onglet Ma Caisse (accès à sa propre caisse uniquement)
            caissePanel = new CaissePanelReparateur(this);
            tabbedPane.addTab("Ma Caisse", caissePanel);
            
            // Onglet Gestion Emprunts
            JPanel empruntPanel = createEmpruntPanel();
            tabbedPane.addTab("Emprunts", empruntPanel);
        }

        // Menu
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuFichier = new JMenu("Fichier");
        
        JMenuItem itemRefresh = new JMenuItem("Actualiser");
        itemRefresh.addActionListener(e -> refreshAll());
        
        JMenuItem itemDeconnexion = new JMenuItem("Déconnexion");
        itemDeconnexion.addActionListener(e -> deconnexion());
        
        JMenuItem itemQuitter = new JMenuItem("Quitter");
        itemQuitter.addActionListener(e -> System.exit(0));
        
        menuFichier.add(itemRefresh);
        menuFichier.addSeparator();
        menuFichier.add(itemDeconnexion);
        menuFichier.addSeparator();
        menuFichier.add(itemQuitter);
        
        menuBar.add(menuFichier);
        setJMenuBar(menuBar);

        getContentPane().add(tabbedPane);
    }

    public void refreshAll() {
        if (reparationPanel != null) reparationPanel.loadReparations();
        if (clientPanel != null) clientPanel.loadClients();
        if (composantPanel != null) composantPanel.loadComposants();
        if (caissePanel != null) caissePanel.loadCaisseData();
    }

    private void deconnexion() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Voulez-vous vraiment vous déconnecter?", 
            "Déconnexion", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }

    public Reparateur getReparateur() { 
        return reparateur; 
    }

    public IReparationMetier getReparationMetier() {
        return reparationMetier;
    }

    public IClientMetier getClientMetier() {
        return clientMetier;
    }

    public ICaisseMetier getCaisseMetier() {
        return caisseMetier;
    }

    public IComposantMetier getComposantMetier() {
        return composantMetier;
    }
    
    public IEmpruntMetier getEmpruntMetier() {
        return empruntMetier;
    }
    
    private JPanel createEmpruntPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Titre et boutons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("Mes Emprunts");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(lblTitle);
        
        JButton btnRefresh = new JButton("Actualiser");
        JButton btnNouveau = new JButton("Nouveau emprunt");
        JButton btnRembourser = new JButton("Rembourser");
        
        topPanel.add(btnRefresh);
        topPanel.add(btnNouveau);
        topPanel.add(btnRembourser);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Table
        String[] cols = {"ID", "Montant (DH)", "Type", "Date", "Remboursé", "État"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Charger données
        Runnable loadData = () -> {
            model.setRowCount(0);
            try {
                java.util.List<dao.Emprunt> emprunts = empruntMetier.listerEmpruntsParReparateur(reparateur);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                
                for (dao.Emprunt emp : emprunts) {
                    String etat = emp.isRembourse() ? "✓ Remboursé" : "⏳ En cours";
                    model.addRow(new Object[]{
                        emp.getIdEmprunt(),
                        emp.getMontant(),
                        emp.getType(),
                        emp.getDate() != null ? sdf.format(emp.getDate()) : "",
                        emp.isRembourse() ? "Oui" : "Non",
                        etat
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        
        // Actions
        btnRefresh.addActionListener(e -> loadData.run());
        
        btnNouveau.addActionListener(e -> {
            try {
                JTextField txtMontant = new JTextField();
                JComboBox<String> cbType = new JComboBox<>(new String[]{"AVANCE", "PRET", "AUTRE"});
                
                Object[] message = {
                    "Montant (DH):", txtMontant,
                    "Type:", cbType
                };
                
                int option = JOptionPane.showConfirmDialog(panel, message, "Nouvel Emprunt", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    float montant = Float.parseFloat(txtMontant.getText());
                    empruntMetier.creerEmprunt(reparateur, montant, (String) cbType.getSelectedItem(), "");
                    JOptionPane.showMessageDialog(panel, "Emprunt créé!");
                    loadData.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnRembourser.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(panel, "Sélectionnez un emprunt");
                return;
            }
            
            try {
                int idEmprunt = (int) model.getValueAt(selectedRow, 0);
                java.util.List<dao.Emprunt> emprunts = empruntMetier.listerEmpruntsParReparateur(reparateur);
                dao.Emprunt emp = emprunts.stream().filter(emprunt -> emprunt.getIdEmprunt() == idEmprunt).findFirst().orElse(null);
                
                if (emp == null) {
                    JOptionPane.showMessageDialog(panel, "Emprunt introuvable");
                    return;
                }
                
                if (emp.isRembourse()) {
                    JOptionPane.showMessageDialog(panel, "Cet emprunt est déjà remboursé");
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(panel, 
                    "Rembourser " + emp.getMontant() + " DH?", 
                    "Confirmation", 
                    JOptionPane.YES_NO_OPTION);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    empruntMetier.rembourserEmprunt(emp);
                    JOptionPane.showMessageDialog(panel, "Emprunt remboursé!");
                    loadData.run();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        loadData.run();
        return panel;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ReparateurFrame frame = new ReparateurFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
