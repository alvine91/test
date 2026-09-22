package org.gestioncomptes.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Importe une seule fois le compte.csv historique (colonnes ID, Nom, Prénom)
 * vers le fichier de données de l'application, la première fois que celui-ci
 * n'existe pas encore. Les comptes ainsi importés n'ont pas d'email ni de mot
 * de passe : ils sont "réclamés" (email + mot de passe attribués) lors de la
 * première connexion réussie via LoginPage.
 */
public final class SeedImporter {

    private static final String HEADER = "id,nom,prenom,email,motDePasseHash,type,solde";

    private SeedImporter() {
    }

    public static void seedAccountsIfMissing(Path legacyCsv, Path targetCsv) {
        if (Files.exists(targetCsv) || !Files.exists(legacyCsv)) {
            return;
        }
        try {
            List<String> legacyLines = Files.readAllLines(legacyCsv, StandardCharsets.UTF_8);
            List<String> outputLines = new ArrayList<>();
            outputLines.add(HEADER);
            for (int i = 1; i < legacyLines.size(); i++) {
                String line = legacyLines.get(i);
                if (line.isBlank()) {
                    continue;
                }
                List<String> fields = CsvUtils.parseLine(line);
                if (fields.size() < 3) {
                    continue;
                }
                String id = fields.get(0).trim();
                String nom = fields.get(1).trim();
                String prenom = fields.get(2).trim();
                if (id.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                    continue;
                }
                outputLines.add(CsvUtils.formatLine(id, nom, prenom, "", "", "Courant", "0.0"));
            }
            if (targetCsv.getParent() != null) {
                Files.createDirectories(targetCsv.getParent());
            }
            Files.write(targetCsv, outputLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'importer " + legacyCsv, e);
        }
    }
}
