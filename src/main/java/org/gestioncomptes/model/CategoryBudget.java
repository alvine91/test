package org.gestioncomptes.model;

public enum CategoryBudget {
    SANTE("Santé"),
    VACANCES("Vacances"),
    EPARGNE("Épargne");

    private final String libelle;

    CategoryBudget(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
