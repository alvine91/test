package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.model.CategoryBudget;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Boîte de dialogue modale (bloque le reste de l'application tant qu'elle
 * est ouverte, voir le {@code true} passé au constructeur de JDialog) pour
 * ajouter une transaction depuis AccountPage. Le montant saisi est signé
 * par l'utilisateur (+ pour un crédit, - pour un débit) et transmis tel
 * quel à {@link org.gestioncomptes.service.AccountService#addTransaction}.
 */
public class AddTransactionDialog extends JDialog {

    private static final int WIDTH = 360;

    private final JTextField descriptionField = new JTextField();
    private final JTextField amountField = new JTextField();
    private final JTextField dateField = new JTextField(LocalDate.now().toString());
    private final JComboBox<CategoryBudget> categoryCombo = new JComboBox<>(CategoryBudget.values());
    private final JCheckBox recurringCheck = new JCheckBox("Transaction récurrente");
    private final JLabel messageLabel = new JLabel(" ");

    public AddTransactionDialog(JFrame owner, AppContext context, Account account) {
        super(owner, "Ajouter une transaction", true);
        JPanel content = new JPanel();
        content.setBackground(Theme.SURFACE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));

        JLabel title = Theme.title("Nouvelle transaction");
        title.setFont(Theme.FONT_BOLD.deriveFont(18f));
        content.add(title);
        content.add(Box.createVerticalStrut(16));

        content.add(Theme.fieldGroup("Description", descriptionField));
        content.add(Box.createVerticalStrut(10));
        content.add(Theme.fieldGroup("Montant (+ crédit / - débit)", amountField));
        content.add(Box.createVerticalStrut(10));
        content.add(Theme.fieldGroup("Date (AAAA-MM-JJ)", dateField));
        content.add(Box.createVerticalStrut(10));
        content.add(Theme.fieldGroup("Catégorie", categoryCombo));
        content.add(Box.createVerticalStrut(6));

        recurringCheck.setOpaque(false);
        recurringCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(recurringCheck);
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
        JButton okButton = Theme.primaryButton("Ajouter");
        okButton.addActionListener(e -> submit(context, account));
        buttons.add(cancelButton);
        buttons.add(okButton);
        content.add(buttons);

        setContentPane(content);
        setResizable(false);
        pack();
        setSize(WIDTH, getHeight());
        setLocationRelativeTo(owner);
    }

    /**
     * Valide puis enregistre la transaction. Chaque type d'erreur possible
     * (montant non numérique, date mal formée, description vide) est
     * capturé séparément pour afficher un message précis, plutôt qu'un
     * message générique.
     */
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
