# ADR-001 — Spring Authorization Server plutôt que Keycloak

## Statut
Accepté

## Contexte
Le projet nécessite un serveur OAuth2. Deux options réalistes :
- Keycloak : solution clé en main, configuration via interface graphique
- Spring Authorization Server : implémentation codée en Java

## Décision
Implémenter Spring Authorization Server à la main.

## Raisons
Keycloak est une boîte noire —  le configurer
ne prouve rien sur la compréhension d'OAuth2. Implémenter le serveur
prouve la maîtrise des flux (Authorization Code, PKCE, Client Credentials),
la gestion des tokens, et la spec RFC 6749.

## Conséquences
- ✅ Portfolio incomparablement plus fort
- ✅ Compréhension profonde d'OAuth2
- ❌ Plus long à implémenter (+1 semaine)