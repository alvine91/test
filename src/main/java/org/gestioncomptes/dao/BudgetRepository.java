package org.gestioncomptes.dao;

import org.gestioncomptes.model.Budget;
import org.gestioncomptes.model.CategoryBudget;
import org.gestioncomptes.util.CsvUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dépôt CSV pour les budgets : mêmes principes que
 * {@link AccountRepository} (fichier {@code data/budgets.csv}). Contrairement
 * à {@link TransactionRepository}, {@link #save(Budget)} fait un upsert par
 * id, car un budget existant peut être modifié (bouton "Éditer" de
 * BudgetPage change sa limite via {@code AccountService.editBudget}).
 */
public class BudgetRepository {

    private static final String HEADER = "id,accountId,categorie,limite";

    private final Path csvPath;
    private final List<Budget> budgets = new ArrayList<>();

    public BudgetRepository(Path csvPath) {
        this.csvPath = csvPath;
        load();
    }

    private void load() {
        budgets.clear();
        if (!Files.exists(csvPath)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(csvPath, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                List<String> f = CsvUtils.parseLine(line);
                if (f.size() < 4) {
                    continue;
                }
                budgets.add(new Budget(f.get(0), f.get(1), CategoryBudget.valueOf(f.get(2)),
                        Double.parseDouble(f.get(3))));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire " + csvPath, e);
        }
    }

    /** Renvoie tous les budgets d'un compte, affichés dans BudgetPage. */
    public List<Budget> findByAccountId(String accountId) {
        return budgets.stream()
                .filter(b -> b.getAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    public String nextId() {
        int max = 0;
        for (Budget b : budgets) {
            try {
                max = Math.max(max, Integer.parseInt(b.getId()));
            } catch (NumberFormatException ignored) {
                // identifiant non numérique, ignoré pour le calcul du prochain id
            }
        }
        return String.valueOf(max + 1);
    }

    /** Insère ou met à jour un budget (upsert par id), puis réécrit le CSV. */
    public void save(Budget budget) {
        budgets.removeIf(b -> b.getId().equals(budget.getId()));
        budgets.add(budget);
        persist();
    }

    private void persist() {
        try {
            if (csvPath.getParent() != null) {
                Files.createDirectories(csvPath.getParent());
            }
            List<String> lines = new ArrayList<>();
            lines.add(HEADER);
            for (Budget b : budgets) {
                lines.add(CsvUtils.formatLine(b.getId(), b.getAccountId(), b.getCategoryBudget().name(),
                        String.valueOf(b.getTotalLimit())));
            }
            Files.write(csvPath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'écrire " + csvPath, e);
        }
    }
}
