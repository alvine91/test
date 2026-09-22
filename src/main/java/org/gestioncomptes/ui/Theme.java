package org.gestioncomptes.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

/**
 * Palette de couleurs, polices et fabriques de composants partagées par
 * tous les écrans, pour une apparence cohérente sans dupliquer le style
 * dans chaque page. S'appuie sur FlatLaf (voir {@link org.gestioncomptes.Main})
 * pour le rendu de base (champs, cases à cocher, tables...) et ajoute ici
 * les touches spécifiques à l'application : couleur d'accent, boutons
 * "primaire"/"secondaire", cartes, et mise en forme des montants.
 */
final class Theme {

    static final Color PRIMARY = new Color(0x4F46E5);
    static final Color PRIMARY_DARK = new Color(0x4338CA);
    static final Color SUCCESS = new Color(0x16A34A);
    static final Color DANGER = new Color(0xDC2626);
    static final Color WARNING = new Color(0xD97706);
    static final Color BACKGROUND = new Color(0xF3F4F6);
    static final Color SURFACE = Color.WHITE;
    static final Color TEXT_PRIMARY = new Color(0x111827);
    static final Color TEXT_SECONDARY = new Color(0x6B7280);
    static final Color BORDER = new Color(0xE5E7EB);

    static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    static final Font FONT_BALANCE = new Font("Segoe UI", Font.BOLD, 38);

    private Theme() {
    }

    /** Bouton d'action principale : fond de couleur, texte blanc, coins arrondis (FlatLaf). */
    static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFont(FONT_BOLD);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /** Bouton d'action secondaire (retour, annuler) : contour discret, pas de fond coloré. */
    static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setFont(FONT_BODY);
        button.setForeground(TEXT_SECONDARY);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /** Petit bouton discret utilisé dans les cellules de tableau (ex: "Éditer"). */
    static JButton tableButton(String text) {
        JButton button = new JButton(text);
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.setFont(FONT_BODY);
        button.setForeground(PRIMARY);
        button.setFocusPainted(false);
        button.setMargin(new java.awt.Insets(2, 10, 2, 10));
        return button;
    }

    /** Panneau blanc avec bordure discrète et marge intérieure, pour regrouper visuellement un bloc de contenu. */
    static JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)));
        return panel;
    }

    static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    /** Formatage monétaire commun à tous les écrans (montant à 2 décimales + devise). */
    static String formatMoney(double amount) {
        return String.format("%,.2f CHF", amount);
    }

    /** Vert pour un montant positif (crédit), rouge pour un montant négatif (débit). */
    static Color amountColor(double amount) {
        return amount < 0 ? DANGER : SUCCESS;
    }

    /**
     * Empile un libellé discret au-dessus d'un champ pleine largeur (motif
     * de formulaire moderne), utilisé par LoginPage et les boîtes de
     * dialogue d'ajout/édition pour garder des formulaires cohérents.
     */
    static JPanel fieldGroup(String label, JComponent field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel jLabel = new JLabel(label);
        jLabel.setFont(FONT_BOLD.deriveFont(12f));
        jLabel.setForeground(TEXT_SECONDARY);
        jLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        field.setBackground(Color.WHITE);

        group.add(jLabel);
        group.add(Box.createVerticalStrut(4));
        group.add(field);
        return group;
    }
}
