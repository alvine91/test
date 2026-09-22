package org.gestioncomptes.dao;

import org.gestioncomptes.model.CategoryBudget;
import org.gestioncomptes.model.Transaction;
import org.gestioncomptes.util.CsvUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionRepository {

    private static final String HEADER = "id,accountId,description,montant,date,categorie,recurrente";

    private final Path csvPath;
    private final List<Transaction> transactions = new ArrayList<>();

    public TransactionRepository(Path csvPath) {
        this.csvPath = csvPath;
        load();
    }

    private void load() {
        transactions.clear();
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
                if (f.size() < 7) {
                    continue;
                }
                transactions.add(new Transaction(
                        f.get(0), f.get(1), f.get(2),
                        Double.parseDouble(f.get(3)),
                        LocalDate.parse(f.get(4)),
                        CategoryBudget.valueOf(f.get(5)),
                        Boolean.parseBoolean(f.get(6))));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire " + csvPath, e);
        }
    }

    public List<Transaction> findByAccountId(String accountId) {
        return transactions.stream()
                .filter(t -> t.getAccountId().equals(accountId))
                .collect(Collectors.toList());
    }

    public String nextId() {
        int max = 0;
        for (Transaction t : transactions) {
            try {
                max = Math.max(max, Integer.parseInt(t.getId()));
            } catch (NumberFormatException ignored) {
                // identifiant non numérique, ignoré pour le calcul du prochain id
            }
        }
        return String.valueOf(max + 1);
    }

    public void save(Transaction transaction) {
        transactions.add(transaction);
        persist();
    }

    private void persist() {
        try {
            if (csvPath.getParent() != null) {
                Files.createDirectories(csvPath.getParent());
            }
            List<String> lines = new ArrayList<>();
            lines.add(HEADER);
            for (Transaction t : transactions) {
                lines.add(CsvUtils.formatLine(t.getId(), t.getAccountId(), t.getDescription(),
                        String.valueOf(t.getAmount()), t.getDate().toString(),
                        t.getCategoryBudget().name(), String.valueOf(t.isRecurring())));
            }
            Files.write(csvPath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'écrire " + csvPath, e);
        }
    }
}
