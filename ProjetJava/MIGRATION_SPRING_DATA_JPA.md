# Migration vers Spring Data JPA - Documentation

## Architecture Mise en Place

### 1. **Repositories (package `repository`)**
Remplacement des DAO manuels par des interfaces Spring Data JPA :
- `ClientRepository` - Gestion des clients
- `ReparationRepository` - Gestion des réparations  
- `ReparateurRepository` - Gestion des réparateurs
- `CaisseRepository` - Gestion de la caisse
- `EmpruntRepository` - Gestion des emprunts
- `ProprietaireRepository` - Gestion des propriétaires
- `UserRepository` - Gestion des utilisateurs

**Avantages :**
- Plus besoin d'écrire les requêtes CRUD basiques
- Méthodes générées automatiquement (`save`, `findById`, `findAll`, `delete`, etc.)
- Requêtes personnalisées par convention de nommage
- Support des requêtes @Query pour les cas complexes

### 2. **Configuration (package `config`)**
`JpaConfig.java` - Configuration Spring minimale qui :
- Utilise le `persistence.xml` existant (persistence unit: `repairPU`)
- Active Spring Data JPA repositories
- Gère les transactions avec `@Transactional`
- Scanne les packages `metier` et `repository`

### 3. **Services Métier (package `metier`)**
Mise à jour des classes métier :
- `@Service` - Marque la classe comme bean Spring
- `@Transactional` - Gestion automatique des transactions
- `@Autowired` - Injection des repositories
- Plus besoin de gérer manuellement `EntityManager` ou transactions

**Modifications :**
- `ReparationMetierImpl` : utilise `ReparationRepository`
- `CaisseMetierImpl` : utilise `CaisseRepository`  
- `EmpruntMetierImpl` : utilise `EmpruntRepository` et `CaisseRepository`

### 4. **Point d'entrée (Main.java)**
Exemple d'initialisation du contexte Spring :
```java
ApplicationContext context = new AnnotationConfigApplicationContext(JpaConfig.class);
IReparationMetier metier = context.getBean(ReparationMetierImpl.class);
```

## Utilisation

### Démarrer l'application
```java
public class Main {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(JpaConfig.class);
        
        // Récupérer les services
        IReparationMetier reparationMetier = ctx.getBean(ReparationMetierImpl.class);
        
        // Utiliser les services
        // ...
    }
}
```

### Exemple de méthodes Repository disponibles

**Méthodes automatiques (sans code) :**
- `save(entity)` - Créer ou mettre à jour
- `findById(id)` - Recherche par ID
- `findAll()` - Liste complète
- `delete(entity)` - Supprimer
- `count()` - Compter

**Méthodes personnalisées (par convention) :**
- `findByNom(String nom)` - Recherche par nom
- `findByClientAndEtat(Client c, String etat)` - Multi-critères
- `findByNomContainingIgnoreCase(String nom)` - Recherche partielle

**Méthodes avec @Query :**
```java
@Query("SELECT r FROM Reparation r WHERE r.dateDepot BETWEEN :debut AND :fin")
List<Reparation> findByDateDepotBetween(@Param("debut") Date debut, @Param("fin") Date fin);
```

## Avantages de cette architecture

1. **Moins de code** : Plus besoin de DAO avec requêtes manuelles
2. **Transactions automatiques** : `@Transactional` gère tout
3. **Maintenance facilitée** : Code standardisé et conventionnel
4. **Testabilité** : Injection de dépendances facilite les tests unitaires
5. **Performance** : Optimisations automatiques de Spring/Hibernate

## Ancien vs Nouveau

### Avant (DAO manuel)
```java
public class ReparationDao extends GenericDao<Reparation> {
    public List<Reparation> findByClient(Client client) {
        TypedQuery<Reparation> q = em.createQuery(
            "SELECT r FROM Reparation r WHERE r.client = :client",
            Reparation.class
        );
        q.setParameter("client", client);
        return q.getResultList();
    }
}
```

### Maintenant (Spring Data JPA)
```java
@Repository
public interface ReparationRepository extends JpaRepository<Reparation, Long> {
    List<Reparation> findByClient(Client client); // Généré automatiquement !
}
```

## Configuration Base de Données

Toujours dans `persistence.xml` :
- URL : `jdbc:mysql://localhost:3306/repair_shop`
- User : `root`
- Password : (vide)
- Dialect : `MySQL8Dialect`
- `hibernate.hbm2ddl.auto` : `update`
