# ADR-002 — JWT signé RS256 plutôt que HS256

## Statut
Accepté

## Contexte
Les JWT peuvent être signés symétriquement (HS256, une clé partagée)
ou asymétriquement (RS256, paire clé privée/publique).

## Décision
RS256 avec paire RSA 2048 bits générée au démarrage.

## Raisons
En production, les resource servers doivent valider les tokens sans
connaître le secret. RS256 permet de publier la clé publique via
`/.well-known/jwks.json` — c'est le standard OAuth2 réel. HS256 impose
de partager le secret entre tous les services, ce qui est un anti-pattern
de sécurité en microservices.

## Conséquences
- ✅ Clé publique partageable sans risque
- ✅ Standard industrie pour les microservices
- ❌ Setup du keystore plus complexe