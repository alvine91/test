package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.model.CategoryBudget;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Boîte de dialogue modale pour créer un nouveau budget (catégorie +
 * limite totale) sur le compte courant, ouverte depuis BudgetPage.
 */
public class AddBudgetDialog extends JDialog {

    private final JComboBox<CategoryBudget> categoryCombo = new JComboBox<>(CategoryBudget.values());
    private final JTextField limitField = new JTextField(15);
    private final JLabel messageLabel = new JLabel(" ");

    public AddBudgetDialog(JFrame owner, AppContext context, Account account) {
        super(owner, "Créer un budget", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Catégorie"), gbc);
        gbc.gridx = 1;
        add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Limite totale"), gbc);
        gbc.gridx = 1;
        add(limitField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton okButton = new JButton("Créer");
        okButton.addActionListener(e -> submit(context, account));
        add(okButton, gbc);

        gbc.gridy++;
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);

        pack();
        setLocationRelativeTo(owner);
    }

    /** Valide la limite saisie puis délègue la création à AccountService. */
    private void submit(AppContext context, Account account) {
        try {
            double limit = Double.parseDouble(limitField.getText().trim().replace(',', '.'));
            if (limit < 0) {
                throw new IllegalArgumentException("La limite doit être positive.");
            }
            CategoryBudget category = (CategoryBudget) categoryCombo.getSelectedItem();
            context.getAccountService().addBudget(account, category, limit);
            dispose();
        } catch (NumberFormatException ex) {
            messageLabel.setText("Limite invalide.");
        } catch (IllegalArgumentException ex) {
            messageLabel.setText(ex.getMessage());
        }
    }
}
