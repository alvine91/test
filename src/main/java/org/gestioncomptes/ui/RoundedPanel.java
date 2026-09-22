package org.gestioncomptes.ui;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * JPanel dont le fond est dessiné à la main comme un rectangle aux coins
 * arrondis, plutôt qu'un rectangle droit. Swing ne propose pas ça
 * nativement pour un simple JPanel : on redéfinit donc
 * {@link #paintComponent} pour remplir un rond-rectangle de la couleur de
 * fond avant de laisser les enfants se dessiner par-dessus. Utilisé pour
 * les blocs "carte" mis en avant (carte de connexion, carte de solde).
 */
class RoundedPanel extends JPanel {

    private final int arc;

    RoundedPanel(int arc) {
        this.arc = arc;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        g2.dispose();
        super.paintComponent(g);
    }
}
