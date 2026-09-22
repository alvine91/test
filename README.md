# Gestion de Comptes

Application de gestion de comptes bancaires en **Java 17 / Swing / Maven**, basée sur le mockup
(`MockUP.jpeg`), le diagramme de classe (`DiagrammeDeCLasse.png`) et les données d'exemple
(`compte.csv`) présents à la racine du dépôt.

## Lancer l'application

```bash
mvn package
java -jar target/gestion-comptes.jar
```

L'application crée un dossier `data/` (ignoré par git) au premier lancement, dans lequel
`comptes.csv`, `transactions.csv` et `budgets.csv` sont lus/écrits à chaque opération. Le
`compte.csv` fourni à la racine du dépôt sert uniquement de **jeu de données initial** :
il est importé une seule fois vers `data/comptes.csv` si ce dernier n'existe pas encore
(voir `SeedImporter`), puis n'est plus jamais modifié.

Lancer les tests unitaires :

```bash
mvn test
```

## Écrans (fidèles au mockup)

- **Login Page** : Nom, Prénom, Email, Mot de passe + bouton VALIDER, dans une carte centrée.
- **Compte** : carte de solde mise en avant, propriétaire, type, numéro de compte, boutons vers
  Budget / Historique, et ajout d'une transaction.
- **Budget** : liste des budgets (ID, catégorie, limite totale, barre de progression colorée
  des dépenses), bouton "Éditer" par ligne, et création de nouveaux budgets.
- **Historique** : liste des transactions du compte, montants colorés (vert = crédit,
  rouge = débit).

## Design

L'interface utilise [FlatLaf](https://www.formdev.com/flatlaf/) (thème plat/moderne pour
Swing) plutôt que le rendu Swing par défaut, avec une couleur d'accent, des coins arrondis
et une police plus lisible configurés dans `Main.java`. Les couleurs, polices et fabriques de
boutons/cartes communes à tous les écrans sont centralisées dans `ui/Theme.java`, pour une
apparence cohérente sans dupliquer le style dans chaque page. `mvn package` produit un jar
exécutable unique (FlatLaf embarqué via `maven-shade-plugin`).

## Décisions de conception (points ambigus du mockup/diagramme)

Ces choix ont été validés avec l'utilisateur avant l'implémentation :

1. **Persistance** : fichiers CSV (`data/comptes.csv`, `data/transactions.csv`,
   `data/budgets.csv`), pas de base de données.
2. **Login avec mot de passe** : le mockup ne montre pas de champ mot de passe, mais le
   diagramme de classe a `motDePasse` sur `LoginPage`. Le mot de passe est donc demandé et
   stocké **haché (SHA-256)**, jamais en clair. Le mockup indique aussi qu'« on n'a pas
   vraiment de login une fois le compte créé, mais au début on l'a » : `checkAccount()` a donc
   trois comportements :
   - Nom/Prénom inconnus → un nouveau compte est créé automatiquement (solde 0).
   - Nom/Prénom connus mais sans email/mot de passe (cas d'un compte importé depuis
     `compte.csv`, comme `GOUGANG Alvine`) → le compte est "réclamé" avec les identifiants
     saisis lors de cette première connexion.
   - Nom/Prénom connus avec des identifiants déjà définis → email et mot de passe doivent
     correspondre.
3. **Catégories de budget** (`CategoryBudget`) : `SANTE`, `VACANCES`, `EPARGNE`, conformément
   aux exemples du diagramme de classe.
4. **Fonctionnalités au-delà de l'affichage** : ajout de transactions (crédit/débit avec
   description, montant, date, catégorie, récurrence), création de nouveaux budgets, et
   édition de la limite d'un budget existant (bouton "Éditer" du mockup `BudgetPage`).

Le bloc "Interface calculation" du diagramme de classe est un template UML générique
(attribut1/opération1...) et n'a pas de contrepartie métier dans l'application.

## Architecture

```
src/main/java/org/gestioncomptes/
├── Main.java                 point d'entrée, seeding, lancement Swing
├── model/                    Account, Transaction, Budget, CategoryBudget, History
├── dao/                      AccountRepository, TransactionRepository, BudgetRepository (CSV)
├── service/                  AuthService (checkAccount), AccountService (transactions/budgets)
└── ui/                       MainFrame (CardLayout), LoginPage, AccountPage, BudgetPage,
                               HistoryPage, dialogues d'ajout/édition
```

- Le modèle suit le diagramme de classe fourni (`Account.getBalance()`, `doTransaction()`,
  `History.afficherTransactions()`, `Budget.getCategoryBudget()`, etc.), avec `nom`/`prenom`
  séparés pour rester compatible avec les colonnes de `compte.csv`.
- La couche `dao` lit/écrit chaque CSV et reconstruit une liste en mémoire à chaque appel de
  `save()` (volumes de données visés : usage local, pas de accès concurrent).
- La couche `service` porte la logique métier (authentification, mise à jour du solde,
  calcul des dépenses par catégorie de budget) pour garder les classes Swing simples.

## Formats CSV

- `comptes.csv` (racine du dépôt, donnée d'origine) : `ID, Nom, Prénom`
- `data/comptes.csv` : `id,nom,prenom,email,motDePasseHash,type,solde`
- `data/transactions.csv` : `id,accountId,description,montant,date,categorie,recurrente`
- `data/budgets.csv` : `id,accountId,categorie,limite`
