package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.model.CategoryBudget;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddTransactionDialog extends JDialog {

    private final JTextField descriptionField = new JTextField(15);
    private final JTextField amountField = new JTextField(15);
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 15);
    private final JComboBox<CategoryBudget> categoryCombo = new JComboBox<>(CategoryBudget.values());
    private final JCheckBox recurringCheck = new JCheckBox("Transaction récurrente");
    private final JLabel messageLabel = new JLabel(" ");

    public AddTransactionDialog(JFrame owner, AppContext context, Account account) {
        super(owner, "Ajouter une transaction", true);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Description"), gbc);
        gbc.gridx = 1;
        add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Montant (+ crédit / - débit)"), gbc);
        gbc.gridx = 1;
        add(amountField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Date (AAAA-MM-JJ)"), gbc);
        gbc.gridx = 1;
        add(dateField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Catégorie"), gbc);
        gbc.gridx = 1;
        add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(recurringCheck, gbc);

        gbc.gridy++;
        JButton okButton = new JButton("Ajouter");
        okButton.addActionListener(e -> submit(context, account));
        add(okButton, gbc);

        gbc.gridy++;
        messageLabel.setForeground(Color.RED);
        add(messageLabel, gbc);

        pack();
        setLocationRelativeTo(owner);
    }

    private void submit(AppContext context, Account account) {
        try {
            String description = descriptionField.getText().trim();
            if (description.isBlank()) {
                throw new IllegalArgumentException("La description est obligatoire.");
            }
            double amount = Double.parseDouble(amountField.getText().trim().replace(',', '.'));
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            CategoryBudget category = (CategoryBudget) categoryCombo.getSelectedItem();
            context.getAccountService().addTransaction(account, description, amount, date, category,
                    recurringCheck.isSelected());
            dispose();
        } catch (NumberFormatException ex) {
            messageLabel.setText("Montant invalide.");
        } catch (DateTimeParseException ex) {
            messageLabel.setText("Date invalide (format attendu AAAA-MM-JJ).");
        } catch (IllegalArgumentException ex) {
            messageLabel.setText(ex.getMessage());
        }
    }
}
