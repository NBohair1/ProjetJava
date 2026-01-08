package presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import dao.Boutique;
import dao.Proprietaire;
import dao.Reparateur;
import metier.BoutiqueMetierImpl;
import metier.IBoutiqueMetier;

public class MainFrame extends JFrame {

    private Proprietaire proprietaire;
    private Boutique boutiqueActuelle;
    private Reparateur reparateurActuel;
    private IBoutiqueMetier boutiqueMetier;

    private JTabbedPane tabbedPane;
    
    // Panels
    private ReparationPanel reparationPanel;
    private ClientPanel clientPanel;
    private CaissePanel caissePanel;
    private AdminPanel adminPanel;

    /**
     * @wbp.parser.constructor
     */
    public MainFrame() {
        this.proprietaire = null;
        this.reparateurActuel = null;
        this.boutiqueMetier = null;
        this.boutiqueActuelle = null;
        
        initComponents();
    }

    public MainFrame(Proprietaire proprietaire) {
        this.proprietaire = proprietaire;
        this.reparateurActuel = proprietaire; // Par défaut
        this.boutiqueMetier = new BoutiqueMetierImpl();
        
        if (proprietaire != null && proprietaire.getBoutiques() != null && !proprietaire.getBoutiques().isEmpty()) {
            this.boutiqueActuelle = proprietaire.getBoutiques().get(0);
        }
        
        initComponents();
    }

    private void initComponents() {
        setTitle("RepairShop - " + (boutiqueActuelle != null ? boutiqueActuelle.getNom() : ""));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Plein écran
        setSize(1200, 800);

        // --- Menu Simple ---
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        JMenu menuFile = new JMenu("Fichier");
        JMenu menuBoutique = new JMenu("Boutique");
        
        JMenuItem itemQuit = new JMenuItem("Quitter");
        itemQuit.addActionListener(e -> System.exit(0));
        
        JMenuItem itemLogout = new JMenuItem("Déconnexion");
        itemLogout.addActionListener(e -> { new LoginFrame().setVisible(true); dispose(); });
        
        JMenuItem itemChange = new JMenuItem("Changer de Boutique");
        itemChange.addActionListener(e -> changerBoutique());

        menuFile.add(itemLogout);
        menuFile.addSeparator();
        menuFile.add(itemQuit);
        menuBoutique.add(itemChange);
        
        menuBar.add(menuFile);
        menuBar.add(menuBoutique);
        setJMenuBar(menuBar);

        // --- Onglets ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tabbedPane.setBackground(Color.WHITE);

        reparationPanel = new ReparationPanel(this);
        clientPanel = new ClientPanel(this);
        caissePanel = new CaissePanel(this);
        adminPanel = new AdminPanel(this);

        // Ajout des onglets avec icônes texte
        tabbedPane.addTab(" Réparations ", reparationPanel);
        tabbedPane.addTab(" Clients ", clientPanel);
        tabbedPane.addTab(" Caisse ", caissePanel);
        tabbedPane.addTab(" Administration ", adminPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private void changerBoutique() {
        // Logique changement boutique (idem ton code)
        try {
            List<Boutique> list = boutiqueMetier.listerBoutiques(proprietaire);
            if(list.isEmpty()) return;
            String[] noms = list.stream().map(Boutique::getNom).toArray(String[]::new);
            String choix = (String) JOptionPane.showInputDialog(this, "Choisir boutique", "Choix", JOptionPane.QUESTION_MESSAGE, null, noms, noms[0]);
            if(choix != null) {
                for(Boutique b : list) if(b.getNom().equals(choix)) boutiqueActuelle = b;
                setTitle("RepairShop - " + boutiqueActuelle.getNom());
                refreshAll();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void refreshAll() {
        reparationPanel.loadReparations();
        clientPanel.loadClients();
        caissePanel.loadCaisseData();
    }

    public Proprietaire getProprietaire() { return proprietaire; }
    public Reparateur getReparateurActuel() { return reparateurActuel; }
    public Boutique getBoutiqueActuelle() { return boutiqueActuelle; }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MainFrame frame = new MainFrame(null);
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    
    
    
}