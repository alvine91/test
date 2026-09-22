package org.gestioncomptes.model;

/**
 * Catégories de budget disponibles (enum "CategoryBudget" du diagramme de
 * classe, exemples donnés : santé, vacances, épargne). Chaque valeur porte
 * un libellé accentué séparé de son nom Java (les noms d'enum ne peuvent
 * pas contenir d'accents), utilisé pour l'affichage dans les combo box et
 * tableaux Swing, et pour le stockage textuel dans le CSV (via {@code name()}).
 */
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

    /** Utilisé par JComboBox/JTable pour afficher directement le libellé français. */
    @Override
    public String toString() {
        return libelle;
    }
}
