package org.gestioncomptes.model;

/**
 * Représente un compte bancaire (classe "Account" du diagramme de classe).
 *
 * <p>Un compte est identifié par son {@code id} et possède un propriétaire
 * (nom/prénom séparés pour rester compatible avec les colonnes de
 * compte.csv), des identifiants de connexion (email + mot de passe haché),
 * un type de compte (ex: "Courant") et un solde qui évolue au fil des
 * transactions. Cette classe ne contient aucune logique de persistance :
 * c'est {@link org.gestioncomptes.dao.AccountRepository} qui la lit/écrit
 * depuis le CSV, et {@link org.gestioncomptes.service.AccountService} qui
 * orchestre les opérations métier autour d'elle.
 */
public class Account {

    private final String id;
    private String nom;
    private String prenom;
    private String email;
    // Mot de passe stocké sous forme de hash SHA-256 (jamais en clair), voir PasswordUtils.
    private String motDePasseHash;
    private String type;
    private double balance;

    public Account(String id, String nom, String prenom, String email, String motDePasseHash, String type,
                    double balance) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasseHash = motDePasseHash;
        this.type = type;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /** Nom complet affiché dans l'UI (correspond au champ "name" du diagramme de classe). */
    public String getName() {
        return nom + " " + prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasseHash() {
        return motDePasseHash;
    }

    public void setMotDePasseHash(String motDePasseHash) {
        this.motDePasseHash = motDePasseHash;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getBalance() {
        return balance;
    }

    /**
     * Applique une transaction au solde du compte. Le montant est déjà signé
     * (positif = crédit, négatif = débit), donc on l'ajoute simplement.
     * Appelée par {@link org.gestioncomptes.service.AccountService#addTransaction}
     * après création de la transaction.
     */
    public void doTransaction(Transaction transaction) {
        this.balance += transaction.getAmount();
    }
}
