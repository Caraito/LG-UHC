# 🐺 LGUHC - Minecraft Werewolf UHC Plugin

**LGUHC** is a Werewolf UHC game plugin currently in **Beta**. It allows you to organize games combining survival, strategy, and hidden roles.

## 🚀 Main Features

The plugin includes several "Fast-UHC" mechanics to speed up the early game:
* **Timber / Fast Leaf Decay**: Trees break automatically for fast wood gathering.
* **Auto-Smelt**: Ores are automatically smelted when mined.
* **Auto-Enchant**: Tools are automatically enchanted to speed up preparation.
* **Meetup Mode**: Configurable option to enable "Meetup" mode (with pre-distributed gear).
* **Auto-Revive**: Automatic respawn system for any player dying before the official role announcement.

---

## 🛠️ Commands

### Administration & Management
* **/lgstart**: Starts the Werewolf UHC game.
* **/lgstop**: Stops the current game.
* **/lgconfig**: Opens the configuration management interface.
* **/lgworld**: Manage worlds dedicated to the LG game.
    * `create [--stop]`: Creates a new world (the `--stop` argument stops the server once finished).
    * `load`: Loads chunks around pre-generated spawn/respawn positions.
    * `prepare <spawns_per_map> <respawns_per_map> <nb_maps>`: Prepares safe coordinates before each game.
    * `delete`: Deletes all worlds.
* **/lgspawn [all]**: Returns to the main spawn (world). Use the `all` argument to teleport everyone to the spawn.

### Players
* **/lginfo**: Displays information about your role and the list of your allies (for Werewolves).

---

## 🧪 Project Status (Beta)

This plugin is currently in **Beta**. It is expected to evolve regularly with new features and balancing.

💡 **Role Idea?** If you have suggestions for original roles to integrate, feel free to open an **Issue** on the GitHub repository.

---

## ⚙️ Installation

1. Drop the `LGUHC.jar` file into your `plugins` folder.
2. Restart your server (API Version 1.8+).
3. Configure your options via `/lgconfig`.
4. Setup your world with `/lgworld create` and then `/lgworld prepare` (optional).

---
---

# 🐺 LGUHC - Plugin Minecraft UHC Loup-Garou

**LGUHC** est un plugin de jeu Loup-Garou UHC en pleine phase de développement (**Beta**). Il permet d'organiser des parties mêlant survie, stratégie et rôles cachés.

## 🚀 Fonctionnalités principales

Le plugin intègre plusieurs mécaniques "Fast-UHC" pour dynamiser le début de partie :
* **Timber / Fast Leaf Decay** : Les arbres se cassent automatiquement pour une récolte de bois rapide.
* **Auto-Smelt** : Les minerais sont automatiquement cuits lors du minage.
* **Auto-Enchant** : Les outils s'enchantent tout seuls pour accélérer la préparation.
* **Mode Meetup** : Option configurable pour activer ou non le mode "Meetup", c'est-à-dire avec le stuff pré-distribué.
* **Revive Automatique** : Système de réanimation automatique pour tout joueur mourant avant l'annonce officielle des rôles.

---

## 🛠️ Commandes

### Administration & Gestion
* **/lgstart** : Lance la partie de Loup-Garou UHC.
* **/lgstop** : Arrête la partie en cours.
* **/lgconfig** : Ouvre l'interface de gestion de la configuration du plugin.
* **/lgworld** : Gestion des mondes dédiés au LG.
    * `create [--stop]` : Crée un nouveau monde (l'argument `--stop` permet de stopper le serveur une fois fini).
    * `load` : Charge les chunks aux alentours des positions de spawn / respawn pré-générés.
    * `prepare <spawns_par_map> <respawns_par_map> <nb_maps>` : Prépare des coordonnées sécurisées avant chaque partie.
    * `delete` : Supprime tous les mondes.
* **/lgspawn [all]** : Retourne au spawn principal (monde world). Utilisez l'argument `all` pour téléporter tous les joueurs au spawn.

### Joueurs
* **/lginfo** : Affiche les informations relatives à votre rôle et affiche la liste de vos alliés (pour les Loups-Garous).

---

## 🧪 État du projet (Beta)

Le plugin est actuellement en version **Beta**. Il est voué à évoluer régulièrement avec l'ajout de nouvelles fonctionnalités et l'équilibrage des mécaniques.

💡 **Une idée de rôle ?** Si vous avez des suggestions de rôles originaux à intégrer, n'hésitez pas à ouvrir une **Issue** sur le dépôt GitHub.

---

## ⚙️ Installation

1. Déposez le fichier `LGUHC.jar` dans votre dossier `plugins`.
2. Redémarrez votre serveur (Version API 1.8+).
3. Configurez vos options via `/lgconfig`.
4. Préparez votre monde avec `/lgworld create` puis `/lgworld prepare` (ce dernier étant optionnel).