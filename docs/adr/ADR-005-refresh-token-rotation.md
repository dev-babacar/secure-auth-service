# ADR-005 — Refresh token rotation avec détection de réutilisation

## Statut
Accepté

## Contexte
Les refresh tokens longue durée sont une cible d'attaque. Si un
attaquant en vole un, il peut générer des access tokens indéfiniment.

## Décision
Rotation systématique avec détection de vol :
- Chaque utilisation génère un nouveau refresh token
- L'ancien est révoqué immédiatement
- Tous les tokens appartiennent à une `family` UUID
- Si un token révoqué est réutilisé → toute la famille est révoquée

## Raisons
C'est la recommandation OAuth 2.1 (RFC en draft). La détection de
réutilisation détecte une compromission de token en production.
Très peu de devs l'implémentent — signal fort de maturité sécurité.

## Conséquences
- ✅ Sécurité production-grade
- ✅ Détection automatique de vol de token
- ❌ Logique de token family à implémenter en base
- ❌ Requêtes légèrement plus complexes