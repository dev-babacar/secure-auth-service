# ADR-003 — Blacklist JWT dans Redis plutôt qu'en base

## Statut
Accepté

## Contexte
Les JWT sont stateless par nature. Pour supporter la déconnexion
et la révocation, il faut invalider des tokens avant leur expiration.

## Décision
Redis avec TTL égal au temps restant du token.

## Raisons
- Une requête SQL à chaque validation de token tue les performances
- Redis répond en < 1ms
- TTL natif : le token s'efface automatiquement à expiration
- Zéro maintenance — pas de job de nettoyage à écrire
- Standard industrie pour ce cas d'usage

## Conséquences
- ✅ Performance maximale sur le filtre de sécurité
- ✅ Nettoyage automatique via TTL
- ❌ Dépendance supplémentaire (Redis dans Docker Compose)