package fr.caraito.lguhc.enums;

public enum GState {
    LOBBY,      // Attente des joueurs
    STARTING,   // Téléportation en cours
    GAME,       // Jeu (Minage/Combat)
    FINISH;     // Fin de partie
}