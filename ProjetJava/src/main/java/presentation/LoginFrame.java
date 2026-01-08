package presentation;

import java.awt.*;
import javax.swing.*;

import dao.Proprietaire;
import dao.Reparateur;
import dao.User;
import exception.AuthException;
import metier.AuthMetierImpl;
import metier.BoutiqueMetierImpl;
import metier.IAuthMetier;
import metier.IBoutiqueMetier;

public class LoginFrame extends JFrame {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private final IAuthMetier authMetier;
    private final IBoutiqueMetier boutiqueMetier;

    public LoginFrame() {
        authMetier = new AuthMetierImpl();
        boutiqueMetier = new BoutiqueMetierImpl();
        initComponents();
    }

    private void initComponents() {
        setTitle("Connexion");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        JLabel lblTitle = new JLabel("Connexion", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblTitle.setBackground(new Color(0, 0, 0));
        lblTitle.setBounds(111, 35, 157, 35);
        mainPanel.add(lblTitle);
        JLabel label = new JLabel("Email:");
        label.setBounds(81, 97, 31, 14);
        mainPanel.add(label);
        txtEmail = new JTextField(20);
        txtEmail.setBounds(122, 95, 221, 20);
        mainPanel.add(txtEmail);
        JLabel label_1 = new JLabel("Mot de passe:");
        label_1.setBounds(42, 127, 70, 14);
        mainPanel.add(label_1);
        txtPassword = new JPasswordField(20);
        txtPassword.setBounds(122, 124, 221, 20);
        mainPanel.add(txtPassword);
        
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setBounds(139, 159, 107, 33);
        btnLogin = new JButton("Se connecter");
        
        btnPanel.add(btnLogin);
        mainPanel.add(btnPanel);

        getContentPane().add(mainPanel);

        // Actions
        btnLogin.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());
    }

    // --- Logique Métier ---
    private void login() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) return;

        try {
            User user = authMetier.login(email, password);
            
            // Redirection selon le type d'utilisateur
            if (user instanceof Proprietaire) {
                // Connexion en tant que Propriétaire (Admin)
                Proprietaire p = (Proprietaire) user;
                
                if (p.getBoutiques() == null || p.getBoutiques().isEmpty()) {
                    BoutiqueDialog d = new BoutiqueDialog(this, p, true);
                    d.setVisible(true);
                }
                
                new MainFrame(p).setVisible(true);
                this.dispose();
                
            } else if (user instanceof Reparateur) {
                // Connexion en tant que Réparateur (Employé)
                Reparateur r = (Reparateur) user;
                new ReparateurFrame(r).setVisible(true);
                this.dispose();
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Type d'utilisateur non reconnu", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (AuthException e) {
            JOptionPane.showMessageDialog(this, 
                "Erreur : " + e.getMessage(), 
                "Erreur d'authentification", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showInscriptionDialog() {
        // Ta logique d'inscription (simplifiée ici pour l'exemple)
        JOptionPane.showMessageDialog(this, "Fonctionnalité d'inscription à implémenter ici (copier ton code précédent)");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}