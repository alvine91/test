package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.service.AuthException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class LoginPage extends JPanel {

    private final Navigator navigator;
    private final AppContext context;

    private final JTextField nomField = new JTextField(15);
    private final JTextField prenomField = new JTextField(15);
    private final JTextField emailField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JLabel messageLabel = new JLabel(" ");

    public LoginPage(Navigator navigator, AppContext context) {
        this.navigator = navigator;
        this.context = context;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        buildUi();
    }

    private void buildUi() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel title = new JLabel("Login Page", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Nom"), gbc);
        gbc.gridx = 1;
        add(nomField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Prénom"), gbc);
        gbc.gridx = 1;
        add(prenomField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Mot de passe"), gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        JButton validerButton = new JButton("VALIDER");
        validerButton.addActionListener(e -> valider());
        add(validerButton, gbc);

        gbc.gridy++;
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);

        gbc.gridy++;
        JLabel hint = new JLabel("<html><i>Un nouveau compte est créé automatiquement si aucun<br>"
                + "compte ne correspond à ce nom/prénom.</i></html>");
        add(hint, gbc);
    }

    private void valider() {
        try {
            Account account = context.getAuthService().checkAccount(
                    nomField.getText(),
                    prenomField.getText(),
                    emailField.getText(),
                    new String(passwordField.getPassword()));
            messageLabel.setText(" ");
            clearFields();
            navigator.showAccount(account);
        } catch (AuthException ex) {
            messageLabel.setText(ex.getMessage());
        }
    }

    private void clearFields() {
        nomField.setText("");
        prenomField.setText("");
        emailField.setText("");
        passwordField.setText("");
    }
}
