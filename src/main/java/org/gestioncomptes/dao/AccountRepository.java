package org.gestioncomptes.dao;

import org.gestioncomptes.model.Account;
import org.gestioncomptes.util.CsvUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepository {

    private static final String HEADER = "id,nom,prenom,email,motDePasseHash,type,solde";

    private final Path csvPath;
    private final List<Account> accounts = new ArrayList<>();

    public AccountRepository(Path csvPath) {
        this.csvPath = csvPath;
        load();
    }

    private void load() {
        accounts.clear();
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
                accounts.add(new Account(f.get(0), f.get(1), f.get(2), f.get(3), f.get(4), f.get(5),
                        Double.parseDouble(f.get(6))));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire " + csvPath, e);
        }
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts);
    }

    public Optional<Account> findById(String id) {
        return accounts.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public Optional<Account> findByNomPrenom(String nom, String prenom) {
        return accounts.stream()
                .filter(a -> a.getNom().equalsIgnoreCase(nom) && a.getPrenom().equalsIgnoreCase(prenom))
                .findFirst();
    }

    public String nextId() {
        int max = 0;
        for (Account a : accounts) {
            try {
                max = Math.max(max, Integer.parseInt(a.getId()));
            } catch (NumberFormatException ignored) {
                // identifiant non numérique, ignoré pour le calcul du prochain id
            }
        }
        return String.valueOf(max + 1);
    }

    public void save(Account account) {
        accounts.removeIf(a -> a.getId().equals(account.getId()));
        accounts.add(account);
        persist();
    }

    private void persist() {
        try {
            if (csvPath.getParent() != null) {
                Files.createDirectories(csvPath.getParent());
            }
            List<String> lines = new ArrayList<>();
            lines.add(HEADER);
            for (Account a : accounts) {
                lines.add(CsvUtils.formatLine(a.getId(), a.getNom(), a.getPrenom(), a.getEmail(),
                        a.getMotDePasseHash(), a.getType(), String.valueOf(a.getBalance())));
            }
            Files.write(csvPath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'écrire " + csvPath, e);
        }
    }
}
