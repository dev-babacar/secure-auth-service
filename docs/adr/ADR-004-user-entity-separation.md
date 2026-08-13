# ADR-004 — Séparer UserEntity (JPA) de User (domain)

## Statut
Accepté

## Contexte
L'architecture hexagonale exige que le domaine ne connaisse pas JPA.
Mais Spring Data JPA requiert des annotations @Entity sur les classes
persistées.

## Décision
Deux classes distinctes :
- `User.java` dans `domain/model/` — pur Java, zéro annotation
- `UserEntity.java` dans `adapter/out/persistence/` — annoté JPA
- Mapper `toEntity()` / `toDomain()` dans l'adapter

## Raisons
Si demain on change PostgreSQL pour MongoDB, on modifie uniquement
l'adapter et le mapper. Le domaine ne change pas. Le domaine est
isolé de toute infrastructure — c'est l'argument central de
l'architecture hexagonale.

## Conséquences
- ✅ Domaine testable sans Spring context
- ✅ Changement de base de données sans impact sur le domaine
- ❌ Plus de classes à maintenir
- ❌ Mapper à écrire et maintenir