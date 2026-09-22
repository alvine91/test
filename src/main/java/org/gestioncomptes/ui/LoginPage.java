package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.service.AuthException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;

/**
 * Écran "Login Page" du mockup : Nom, Prénom, Email, Mot de passe + bouton
 * VALIDER, présentés dans une carte centrée façon application moderne.
 * Ne fait pas de distinction visuelle entre "créer un compte" et "se
 * connecter" : c'est {@link org.gestioncomptes.service.AuthService} qui
 * décide, selon que le nom/prénom existe déjà ou non, s'il faut créer un
 * compte, le "réclamer" (première connexion) ou vérifier le mot de passe
 * (voir le Javadoc de AuthService).
 */
public class LoginPage extends JPanel {

    private static final int CARD_WIDTH = 380;

    private final Navigator navigator;
    private final AppContext context;

    private final JTextField nomField = new JTextField();
    private final JTextField prenomField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel messageLabel = new JLabel(" ");

    public LoginPage(Navigator navigator, AppContext context) {
        this.navigator = navigator;
        this.context = context;
        setLayout(new GridBagLayout());
        setBackground(Theme.BACKGROUND);
        add(buildCard());
    }

    /**
     * La carte de connexion est centrée dans la page grâce à un
     * GridBagLayout sans aucune contrainte particulière : sans gridx/gridy
     * explicites, un unique composant ajouté à un GridBagLayout se place
     * automatiquement au centre du conteneur.
     */
    private JPanel buildCard() {
        RoundedPanel card = new RoundedPanel(20);
        card.setBackground(Theme.SURFACE);
        card.setBorder(BorderFactory.createEmptyBorder(36, 36, 28, 36));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(CARD_WIDTH, 470));

        JLabel title = Theme.title("Gestion de Comptes");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = Theme.subtitle("Connectez-vous ou créez un compte");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(24));

        card.add(Theme.fieldGroup("Nom", nomField));
        card.add(Box.createVerticalStrut(12));
        card.add(Theme.fieldGroup("Prénom", prenomField));
        card.add(Box.createVerticalStrut(12));
        card.add(Theme.fieldGroup("Email", emailField));
        card.add(Box.createVerticalStrut(12));
        card.add(Theme.fieldGroup("Mot de passe", passwordField));
        card.add(Box.createVerticalStrut(20));

        JButton validerButton = Theme.primaryButton("VALIDER");
        validerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        validerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        validerButton.addActionListener(e -> valider());
        card.add(validerButton);

        card.add(Box.createVerticalStrut(10));
        messageLabel.setForeground(Theme.DANGER);
        messageLabel.setFont(Theme.FONT_BODY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(messageLabel);

        card.add(Box.createVerticalStrut(8));
        card.add(buildHint());

        return card;
    }

    /**
     * Texte d'aide sous le bouton VALIDER. On utilise un JTextArea plutôt
     * qu'un JLabel HTML : un JLabel HTML calcule sa largeur "idéale" sur
     * une seule ligne avant d'être positionné par le layout, ce qui le
     * fait déborder du cadre de la carte de connexion. JTextArea avec
     * {@code setColumns} fixe un nombre de caractères par ligne, ce qui
     * force un retour à la ligne fiable quel que soit le layout parent.
     */
    private Component buildHint() {
        javax.swing.JTextArea hint = new javax.swing.JTextArea(
                "Un nouveau compte est créé automatiquement si aucun compte "
                        + "ne correspond à ce nom/prénom.");
        hint.setEditable(false);
        hint.setFocusable(false);
        hint.setOpaque(false);
        hint.setLineWrap(true);
        hint.setWrapStyleWord(true);
        hint.setColumns(34);
        hint.setFont(Theme.FONT_SUBTITLE);
        hint.setForeground(Theme.TEXT_SECONDARY);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        return hint;
    }

    /** Appelé par le bouton VALIDER : délègue à AuthService puis navigue vers la page Compte. */
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
