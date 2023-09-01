# Team 6 

Documentation commune à la plupart des jeux de [Team 6](https://team6-games.com) qui sont sortis à peu près entre 2004 et 2007 et qui sont basés sur Manhattan Chase, notamment Alpha Zylon, [Pizza Dude](https://archive.org/details/pizza-dude), [Glacier](https://archive.org/details/glacier_202306) et [Ultimate Motorcross (sic)](https://archive.org/details/umcross).

## Arguments ligne de commande

Pour passer des arguments à ces jeux, vous pouvez créer un raccourci vers le jeu et le modifier pour ajouter un ou plusieurs de ces paramètres :

- `-F` : lancer le jeu en mode fenêtré
- `-M` : passer la cinématique d'intro
- `-TEST` : lancer le jeu en mode test, ce qui
  - active certaines commandes de la console (qui donneraient "Command not recognized" sans ce paramètre), ainsi que certaines touches de débug
  - charge la carte `Maps\Test.dcw` (vu qu'elle n'existe pas par défaut, donc **il faut copier `Maps\World.dcw`** pour éviter un crash du jeu
  - lance le script `Scripts\Autoload.dcScript`. S'il existe pas, il y aura juste pas de script charge et vous serez coincé sur une map avec un ciel bleu vif 😛 Vous pourrez toujours vous échapper avec le menu pause.
- `-ME` : lance le _mission editor_, un outil qui permet ~~de se plonger dans la vie d'un développeur Team 6~~ d'éditer les fichiers `.dcScript` présents dans le dossier `Scripts` des jeux. 🍝

## Commandes de la console

Une console pop en appuyant sur ². Voici quelques commandes (lancer le jeu en mode test pour avoir accès à toutes les commandes) :
- `loadscript <name>` : charge `Scripts\<name>.dcScript`, ce qui permet de skip des niveaux dans certains jeux
- `setgravity <val>` : modifie la gravité du jeu (ce qui n'affecte pas les piétons en général). La valeur par défaut a l'air d'être 100 à peu près ?
- `teleport` : place le personnage devant la caméra, utile en combinaison avec la free cam
- `missioneditor` : ouvre le _mission editor_. Vu qu'il n'y a pas de curseur, c'est pas très très pratique à moins de lancer le jeu en fenêtré 😛 La commande affiche aussi les triggers dans le jeu et affiche des modèles supplémentaires sur la map.
- `camedit` : permet de déplacer la caméra par rapport au joueur (+/- sur le pavé numérique modifie le FOV). Relancer la commande va fixer la caméra de nouveau, ce qui permet de jouer avec la caméra modifiée.
- `shownodes` : affiche le parcours du trafic dans Pizza Dude (et peut-être d'autres trucs dans d'autres jeux, j'ai pas testé 😛)
- `fps` : un compteur de FPS
- `wireframe` : passe en affichage "fil de fer"
- `sleeptime <secondes>` : ajoute une pause entre deux frames. Oui, il y a une commande pour faire lag le jeu. 😛

## Touches de débug

Quand le mode test est activé, quelques touches gagnent une nouvelle fonction :
- X : passe en mode free cam
  - ZQSD : se déplacer
  - +/- : modifier le FOV
  - Il me semble qu'il y a des touches pour modifier la distance d'affichage mais je m'en souviens plus 😅
  - F1 : sortir du mode free cam
- T : affiche une grille et fait lag le jeu (je sais pas trop ce que ça représente ce truc là)





