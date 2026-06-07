# 🐺 LGUHC - Minecraft Werewolf UHC Plugin

**LGUHC** is a Werewolf UHC game plugin currently in **Beta**. It allows you to organize games combining survival, strategy, and hidden roles.

## 🚀 Main Features

The plugin includes several "Fast-UHC" mechanics to speed up the early game:
* **Timber / Fast Leaf Decay**: Trees break automatically for fast wood gathering.
* **Auto-Smelt**: Ores are automatically smelted when mined.
* **Auto-Enchant**: Tools are automatically enchanted to speed up preparation.
* **Meetup Mode**: Configurable option to enable "Meetup" mode (with pre-distributed gear).
* **Auto-Revive**: Automatic respawn system for any player dying before the official role announcement.
* **Dynamic Role Distribution**: Intelligent system ensuring balanced camps even with few players (2 players = 1 wolf + 1 village).
* **Vengeance System**: Players have a 15-second window to be revived by special roles before being permanently eliminated.

---

## 🛠️ Commands

### Administration & Management
* **/lgstart**: Starts the Werewolf UHC game.
* **/lgstop**: Stops the current game.
* **/lgconfig**: Opens the configuration management interface.
* **/lgworld**: Manage worlds dedicated to the LG game.
    * `create [--stop]`: Creates a new world.
    * `load`: Loads chunks around pre-generated spawn/respawn positions.
    * `prepare <spawns_per_map> <respawns_per_map> <nb_maps>`: Prepares safe coordinates.
    * `delete`: Deletes all worlds.
* **/lgspawn [all]**: Returns to the main spawn (world).

### Players
* **/lgrole**: Review your role description and allies.
* **/lginfo**: Alternative command for role info.
* **/lgvoyante <player>**: (Seer) See a player's role.
* **/lgrenard**: (Fox) Detect nearby wolves (3 charges).
* **/lggrimer [role]**: (Disguise Wolf) Change your appearance for the Seer.
* **/lgsacrifice [player]**: (White Wolf) Sacrifice a wolf to gain power.

---

## 🎭 Roles List

### 🌲 The Village
* **Simple Villager** 🧑: Basic member of the village.
* **Seer** 🔮: Can see a player's role every 10/20 minutes.
* **Fox** 🦊: Can detect if a werewolf is within 10 blocks (3 uses).
* **Little Girl** 👧: Can become invisible at night by removing armor (once per night, 10 min).
* **Hunter** 🏹: On final death, can curse an attacker to reduce their max health to 4 hearts.
* **Salvateur** 🛡️: Can protect a player each episode, giving them Resistance I.
* **Witch** 🧙: Possesses one revival potion to save a dying player.

### 🐺 The Werewolves
* **Werewolf** 🐾: Base wolf, gains Strength I at night.
* **Alpha Wolf** 🐺: Leader of the pack, gains extra raw damage at night.
* **Disguise Wolf** 🎭: Can disguise themselves as any village role to deceive the Seer.
* **Infectious Wolf (IPDL)** ☣️: Can infect a player dying to a wolf, turning them into a werewolf.
* **Treacherous Wolf** 👤: Can become invisible like the Little Girl but never gets Strength at night.

### 💜 Neutral / Solitaires
* **White Wolf** ❄️: Must be the last survivor. Can sacrifice other wolves to gain permanent health and strength.
* **Assassin** 🔪: Must kill everyone. Gains permanent Speed I and Strength I during the day.

---

## 🧪 Project Status (Beta)

This plugin is currently in **Beta**. It is expected to evolve regularly with new features and balancing.

---

## ⚙️ Installation

1. Drop the `LGUHC.jar` file into your `plugins` folder.
2. Restart your server (API Version 1.8+).
3. Configure your options via `/lgconfig`.
4. Setup your world with `/lgworld create` and then `/lgworld prepare`.

---
---

# 🐺 LGUHC - Plugin Minecraft UHC Loup-Garou

**LGUHC** est un plugin de jeu Loup-Garou UHC en pleine phase de développement (**Beta**).

## 🚀 Fonctionnalités principales

* **Timber / Fast Leaf Decay** : Récolte de bois instantanée.
* **Auto-Smelt & Auto-Enchant** : Minage et préparation ultra-rapides.
* **Distribution Dynamique** : Système intelligent garantissant des camps équilibrés (2 joueurs = 1 loup + 1 villageois).
* **Système de Vengeance** : Délai de 15s pour être réanimé avant la mort définitive.

---

## 🛠️ Commandes

### Joueurs
* **/lgrole** : Revoir la description de son rôle et ses alliés.
* **/lgvoyante <joueur>** : (Voyante) Voir le rôle d'un joueur.
* **/lgrenard** : (Renard) Flairer les loups à proximité (3 utilisations).
* **/lggrimer [rôle]** : (Loup Grimeur) Changer son apparence pour la Voyante.
* **/lgsacrifice [joueur]** : (Loup Blanc) Sacrifier un loup pour gagner en puissance.

---

## 🎭 Liste des Rôles

### 🌲 Le Village
* **Simple Villageois** 🧑 : Membre de base du village.
* **Voyante** 🔮 : Peut voir le rôle d'un joueur toutes les 10/20 minutes.
* **Renard** 🦊 : Peut détecter un loup dans un rayon de 10 blocs (3 utilisations).
* **Petite Fille** 👧 : Peut devenir invisible la nuit en retirant son armure (1x/nuit, 10 min).
* **Chasseur** 🏹 : À sa mort, peut réduire la vie d'un de ses attaquants à 4 cœurs.
* **Salvateur** 🛡️ : Peut protéger un joueur chaque épisode (Résistance I).
* **Sorcière** 🧙 : Possède une potion de résurrection.

### 🐺 Les Loups-Garous
* **Loup-Garou** 🐾 : Loup de base, Force I la nuit.
* **Loup Alpha** 🐺 : Chef de meute, dégâts bruts augmentés la nuit.
* **Loup Grimeur** 🎭 : Peut se grimer en rôle du village pour tromper la Voyante.
* **Infect Père des Loups (IPDL)** ☣️ : Peut infecter une victime des loups pour la convertir.
* **Loup Perfide** 👤 : Peut devenir invisible comme la Petite Fille mais n'a jamais de force.

### 💜 Neutres / Solitaires
* **Loup Blanc** ❄️ : Doit gagner seul. Peut sacrifier ses pairs pour devenir plus fort.
* **Assassin** 🔪 : Doit tuer tout le monde. Vitesse I permanente et Force I le jour.
