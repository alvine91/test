package org.gestioncomptes.ui;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.model.CategoryBudget;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Boîte de dialogue modale pour créer un nouveau budget (catégorie +
 * limite totale) sur le compte courant, ouverte depuis BudgetPage.
 */
public class AddBudgetDialog extends JDialog {

    private static final int WIDTH = 340;

    private final JComboBox<CategoryBudget> categoryCombo = new JComboBox<>(CategoryBudget.values());
    private final JTextField limitField = new JTextField();
    private final JLabel messageLabel = new JLabel(" ");

    public AddBudgetDialog(JFrame owner, AppContext context, Account account) {
        super(owner, "Créer un budget", true);
        JPanel content = new JPanel();
        content.setBackground(Theme.SURFACE);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 24, 20, 24));

        JLabel title = Theme.title("Nouveau budget");
        title.setFont(Theme.FONT_BOLD.deriveFont(18f));
        content.add(title);
        content.add(Box.createVerticalStrut(16));

        content.add(Theme.fieldGroup("Catégorie", categoryCombo));
        content.add(Box.createVerticalStrut(10));
        content.add(Theme.fieldGroup("Limite totale", limitField));
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
        JButton okButton = Theme.primaryButton("Créer");
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
