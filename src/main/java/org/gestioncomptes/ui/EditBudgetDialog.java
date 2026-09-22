package org.gestioncomptes.ui;

import org.gestioncomptes.model.Budget;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Boîte de dialogue modale pour modifier la limite totale d'un budget
 * existant. Ouverte depuis BudgetPage en cliquant sur le bouton "Éditer"
 * d'une ligne du tableau (voir {@link ButtonCellEditor}). Le champ de
 * saisie est pré-rempli avec la valeur actuelle du budget.
 */
public class EditBudgetDialog extends JDialog {

    private final JTextField limitField;
    private final JLabel messageLabel = new JLabel(" ");

    public EditBudgetDialog(JFrame owner, AppContext context, Budget budget) {
        super(owner, "Éditer le budget " + budget.getCategoryBudget().getLibelle(), true);
        limitField = new JTextField(String.valueOf(budget.getTotalLimit()), 15);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Nouvelle limite totale"), gbc);
        gbc.gridx = 1;
        add(limitField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton okButton = new JButton("Enregistrer");
        okButton.addActionListener(e -> submit(context, budget));
        add(okButton, gbc);

        gbc.gridy++;
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);

        pack();
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
