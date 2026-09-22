package org.gestioncomptes;

import org.gestioncomptes.dao.AccountRepository;
import org.gestioncomptes.dao.BudgetRepository;
import org.gestioncomptes.dao.TransactionRepository;
import org.gestioncomptes.ui.AppContext;
import org.gestioncomptes.ui.MainFrame;
import org.gestioncomptes.util.SeedImporter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.nio.file.Path;

/**
 * Point d'entrée de l'application. Le déroulement est toujours le même :
 * <ol>
 *     <li>importer une fois compte.csv vers data/comptes.csv si besoin
 *     (voir {@link SeedImporter}) ;</li>
 *     <li>construire les trois dépôts CSV, chacun se chargeant de lire son
 *     propre fichier ;</li>
 *     <li>les regrouper dans un {@link AppContext} partagé par toute
 *     l'interface Swing ;</li>
 *     <li>afficher la fenêtre principale sur l'Event Dispatch Thread, le
 *     thread dédié à l'interface graphique dans Swing (toute création/
 *     modification de composant Swing doit s'y faire, d'où l'utilisation de
 *     {@link SwingUtilities#invokeLater}).</li>
 * </ol>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Path dataDir = Path.of("data");
        Path accountsCsv = dataDir.resolve("comptes.csv");
        Path transactionsCsv = dataDir.resolve("transactions.csv");
        Path budgetsCsv = dataDir.resolve("budgets.csv");

        SeedImporter.seedAccountsIfMissing(Path.of("compte.csv"), accountsCsv);

        AccountRepository accountRepository = new AccountRepository(accountsCsv);
        TransactionRepository transactionRepository = new TransactionRepository(transactionsCsv);
        BudgetRepository budgetRepository = new BudgetRepository(budgetsCsv);

        AppContext context = new AppContext(accountRepository, transactionRepository, budgetRepository);

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // on garde le look and feel par défaut si celui du système n'est pas disponible
            }
            new MainFrame(context).setVisible(true);
        });
    }
}
