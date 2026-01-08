package presentation;

import dao.Reparateur;
import metier.*;
import javax.swing.*;
import java.awt.*;

public class ReparateurFrame extends JFrame {

    private Reparateur reparateur;
    private IReparationMetier reparationMetier;
    private ICaisseMetier caisseMetier;
    private IClientMetier clientMetier;
    private IComposantMetier composantMetier;
    
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
