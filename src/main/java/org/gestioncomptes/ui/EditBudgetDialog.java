package org.gestioncomptes.ui;

import org.gestioncomptes.model.Budget;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Boîte de dialogue modale pour modifier la limite totale d'un budget
 * existant. Ouverte depuis BudgetPage en cliquant sur le bouton "Éditer"
 * d'une ligne du tableau (voir {@link ButtonCellEditor}). Le champ de
 * saisie est pré-rempli avec la valeur actuelle du budget.
 */
public class EditBudgetDialog extends JDialog {

    private static final int WIDTH = 340;

    private final JTextField limitField;
    private final JLabel messageLabel = new JLabel(" ");

    public EditBudgetDialog(JFrame owner, AppContext context, Budget budget) {
        super(owner, "Éditer un budget", true);
        limitField = new JTextField(String.valueOf(budget.getTotalLimit()));

        JPanel content = new JPanel();
        content.setBackground(Theme.SURFACE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));

        JLabel title = Theme.title(budget.getCategoryBudget().getLibelle());
        title.setFont(Theme.FONT_BOLD.deriveFont(18f));
        content.add(title);
        content.add(Box.createVerticalStrut(16));

        content.add(Theme.fieldGroup("Nouvelle limite totale", limitField));
        content.add(Box.createVerticalStrut(14));

        messageLabel.setForeground(Theme.DANGER);
        messageLabel.setFont(Theme.FONT_BODY);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(messageLabel);
        content.add(Box.createVerticalStrut(6));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JButton cancelButton = Theme.secondaryButton("Annuler");
        cancelButton.addActionListener(e -> dispose());
        JButton okButton = Theme.primaryButton("Enregistrer");
        okButton.addActionListener(e -> submit(context, budget));
        buttons.add(cancelButton);
        buttons.add(okButton);
        content.add(buttons);

        setContentPane(content);
        setResizable(false);
        pack();
        setSize(WIDTH, getHeight());
        setLocationRelativeTo(owner);
    }

    /** Valide la nouvelle limite puis délègue la mise à jour à AccountService. */
    private void submit(AppContext context, Budget budget) {
        try {
            double newLimit = Double.parseDouble(limitField.getText().trim().replace(',', '.'));
            if (newLimit < 0) {
                throw new IllegalArgumentException("La limite doit être positive.");
            }
            context.getAccountService().editBudget(budget, newLimit);
            dispose();
        } catch (NumberFormatException ex) {
            messageLabel.setText("Limite invalide.");
        } catch (IllegalArgumentException ex) {
            messageLabel.setText(ex.getMessage());
        }
    }
}
