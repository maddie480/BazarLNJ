# Guerre & Soldat

Un FPS très très Eco+ qui parle de guerre du Vietnam (à peu près).

## Les bugs

- La transparence est en PLS sur les textures (peut-être un problème de machine virtuelle, vu que c'est une antiquité :thinking:)
- La musique du menu principal se relance si on va dans les options et qu'on revient, et toutes les musiques s'accumulent, ce qui fait qu'on peut se faire exploser les oreilles en faisant des allers-retours
- On peut se déplacer et mourir en marchant sur une mine... dans le menu pause
- L'arme de base du niveau 1 est en fait posée dans le niveau et tu tombes dessus... sauf si tu bouges directement au lancement du niveau. Dans ce cas, tu te balades dans le niveau sans arme... ce qui ne t'empêche absolument pas de tirer.
- Il est possible de spammer le bouton "recharger" ce qui va accumuler les effets sonores et répéter l'animation beaucoup plus rapidement
- Quand on met en pause et qu'on appuie sur Echap, l'écran devient noir un instant... on est donc en train de mettre la pause en pause. Ensuite, quand on revient au jeu, les bruits d'ambiance sont manquants.
- Même la console est buguée : le jeu enlève le premier caractère de la commande avant de l'exécuter, donc il faut en ajouter un pour que les commandes fonctionnent...

```
function console()
{
  if(console_txt.visible == on) { return; } //already running
  console_txt.visible = on;

  inkey exec_buffer;

  if(result == 13)
  {
    str_clip(exec_buffer,1);
    execute exec_buffer;
  }

  console_txt.visible = off;
}
```

(Oui, les fichiers `.wdl` contiennent le code du jeu dans un format lisible. Malheureusement, pas moyen de l'éditer. J'ai essayé, le jeu dira forcément que "les fichiers sont corrompus".)

## Les commandes

Ce jeu a une console qui s'active avec ². Voici 2-3 choses qu'on peut faire avec :
- `god_mode()` : passer en god mode
- `introtext.visible = on` : afficher le scénario du jeu. Oui, il y a un scénario.
- `boden_text.visible = on` : affiche sur quoi tu marches. "Boden" voulant dire "sol" en allemand
- `endbild()` : affiche ~~la cinématique~~ l'image de fin du jeu. "Endbild" voulant _littéralement_ dire "image de fin" en allemand
- `credits.string = "c'est d'la b*te"` : .... allez voir les crédits
- `menu_load.visible = on` : juste pour montrer que tout est chargé à tout instant mais juste caché : fait apparaître la bannière "load" à gauche de l'écran, même en plein gameplay.
- `player.gesundheit = 999` : met ta vie à 999%. "Gesundheit" voulant dire ... "santé" en allemand
- `tot = 1` : officiellement, tu es mort, donc tu ne peux plus bouger. ("tot" veut dire "mort" bla bla bla)
- `_tog_dbg()` : affiche des infos de debug en haut à gauche de l'écran
- `_tog_mov()` : permet de VOLER. Les touches directionnelles permettent de se déplacer, et maintenir Alt permet de changer l'angle de la caméra. `tot = 1` est plutôt pratique pour ça, vu que le mouvement n'est plus contraint par la physique du joueur.

## Les features cachées

Je sais pas si ces fonctions sont documentées dans le manuel, en tout cas rien ne laisse penser qu'elles existent dans le jeu lui-même...

- F2 : quick save
- F3 : quick load
- F5 : change de résolution (sans repositionner le HUD en conséquence :facepalm:)
- F6 : prend une capture d'écran et l'enregistre dans le dossier du jeu, sous le nom `shot_X.PCX`. Reste maintenant à trouver un éditeur d'image qui sait ouvrir les fichiers PCX.
