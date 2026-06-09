# 🐺 Wiki LGUHC - Minecraft Werewolf UHC

Bienvenue sur le wiki officiel du plugin **LGUHC**. Ce plugin combine la survie intense d'un UHC avec les rôles cachés et la stratégie du célèbre jeu du Loup-Garou.

---

## 📜 Principe du Jeu

Le jeu se déroule en plusieurs phases :
1. **Préparation (Épisode 1)** : Les joueurs sont téléportés aléatoirement sur la carte. Ils doivent récolter des ressources, s'équiper et miner. Les mécaniques de "Fast-UHC" (Timber, Auto-Smelt, Auto-Enchant) facilitent cette phase.
2. **Attribution des Rôles** : Après un temps défini (5 ou 20 min par défaut), chaque joueur reçoit un rôle secret appartenant à l'un des trois camps.
3. **Le Combat** : Les joueurs doivent utiliser leurs pouvoirs pour faire gagner leur camp tout en survivant aux autres joueurs et à la réduction de la bordure.

---

## 🎭 Les Camps et Rôles

### 🌲 Le Village
L'objectif du Village est d'éliminer tous les Loups-Garous et les Menaces Solitaires.

*   **Simple Villageois** 🧑 : N'a pas de pouvoir particulier, mais sa voix compte dans les déductions.
*   **Voyante** 🔮 : Peut voir le rôle d'un joueur toutes les 20 minutes (via `/lgvoyante <pseudo>`).
*   **Renard** 🦊 : Possède 3 charges pour flairer si un Loup-Garou se trouve dans un rayon de 10 blocs (via `/lgrenard`).
*   **Petite Fille** 👧 : Peut devenir invisible la nuit si elle retire toute son armure. Elle reçoit l'effet Faiblesse durant son invisibilité.
*   **Chasseur** 🏹 : À sa mort, il peut choisir un joueur à maudir via une interface. Le joueur maudit verra sa vie maximale réduite à 4 cœurs pendant 10 minutes.
*   **Salvateur** 🛡️ : Peut protéger un joueur (ou lui-même) une fois par épisode durant les 5 premières minutes. Le joueur protégé reçoit Résistance I pour tout l'épisode. Il ne peut pas protéger la même personne deux fois de suite.
*   **Sorcière** 🧙 : Possède une potion de résurrection unique pour sauver un joueur venant de mourir.

### 🐺 Les Loups-Garous
L'objectif des Loups est d'éliminer tous les autres camps. Ils se connaissent entre eux (via `/lgrole`). Ils reçoivent l'effet **Force I** durant la nuit.

*   **Loup-Garou** 🐾 : Loup classique, gagne en puissance la nuit.
*   **Loup Alpha** 🐺 : Chef de meute, il inflige +1 cœur de dégâts bruts supplémentaire la nuit.
*   **Loup Grimeur** 🎭 : Peut choisir un rôle du village à usurper via `/lggrimer <rôle>`. La Voyante verra le rôle choisi au lieu de son vrai rôle.
*   **Infect Père des Loups (IPDL)** ☣️ : Si un joueur meurt à cause des loups, il peut l'infecter pour le transformer en Loup-Garou (système automatique).
*   **Loup Perfide** 👤 : Possède la même capacité d'invisibilité que la Petite Fille, mais ne reçoit jamais d'effet Force, même la nuit.

### 💜 Neutres / Solitaires
Ces rôles ont des objectifs propres et doivent souvent gagner seuls.

*   **Loup Blanc** ❄️ : Doit être le dernier survivant. Il peut sacrifier un autre Loup-Garou (via `/lgsacrifice <pseudo>`) pour gagner définitivement des cœurs supplémentaires et un boost de Force.
*   **Assassin** 🔪 : Doit tuer tout le monde. Il possède **Vitesse I** permanente et **Force I** durant le jour.

---

## ⚙️ Mécaniques de Jeu

### 💎 Limites de Minage
Pour équilibrer le jeu, le minage est limité :
*   **Diamants** : Limite configurable (défaut: 15).
*   **Auto-Smelt** : Les minerais de fer et d'or tombent directement en lingots.
*   **Multiplicateur** : Vous recevez plusieurs lingots par minerai (défaut: x2).

### 🍎 Pommes et Nourriture
*   **Timber** : Couper le bas d'un arbre casse tout le tronc.
*   **Pommes** : Les feuilles ont une chance augmentée de donner des pommes (défaut: 5%).

### 🏹 Système de Vengeance
Lorsqu'un joueur meurt, il n'est pas éliminé immédiatement. Il reste "en attente" pendant 15 secondes, laissant le temps à une **Sorcière** ou un **IPDL** d'utiliser son pouvoir.

---

## 🛠️ Commandes Utiles

*   `/lgrole` : Affiche votre rôle, votre camp et vos alliés.
*   `/lginfo` : Rappel du fonctionnement de votre rôle.
*   `/lgconfig` : (OP uniquement) Ouvre l'interface de configuration du jeu.
*   `/lgstart` : (OP uniquement) Lance la partie.
