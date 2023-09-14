# Team 6

Documentation commune à la plupart des jeux de [Team 6](https://team6-games.com) qui sont sortis à peu près entre 2004 et 2007 et qui sont basés sur [Manhattan Chase](https://archive.org/details/manhattan-chase), notamment [Alpha Zylon](https://store.steampowered.com/app/313210/Alpha_Zylon/), [Pizza Dude](https://archive.org/details/pizza-dude), [Glacier](https://archive.org/details/glacier_202306) et [Ultimate Motorcross (sic)](https://archive.org/details/umcross).

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
- `showdf` : met en surbrillance toutes les pubs de ce jeu. Il y en a **beaucoup**, elles sont stockées dans `misc\DF`, et le jeu essaie même de contacter une régie publicitaire du nom de "DoubleFusion" pour les actualiser, mais ça fonctionne pas. 😛

## Touches de débug

Quand le mode test est activé, quelques touches gagnent une nouvelle fonction :
- X : passe en mode free cam
  - ZQSD : se déplacer
  - +/- : modifier le FOV
  - Il me semble qu'il y a des touches pour modifier la distance d'affichage mais je m'en souviens plus 😅
  - F1 : sortir du mode free cam
- T : affiche une grille et fait lag le jeu (je sais pas trop ce que ça représente ce truc là)

## Jeu en réseau

La plupart des jeux de cette époque fonctionnent de la même façon pour ce qui est du jeu en réseau : en mode "jeu local", le serveur envoie un paquet à tout le réseau local (broadcast UDP sur le port 24958), les clients l'attrapent et se connectent en TCP (toujours sur le port 24958) à l'expéditeur du paquet UDP.

Le programme Java présent dans le dossier `NetworkPlay-src` permet de jouer en réseau en utilisant le mode "jeu local" en transmettant juste les paquets TCP et UDP entre les participants. Celui qui host le serveur doit ouvrir le port 4480 et lancer `Run Server.bat`, les clients doivent lancer `Run Client.bat` et taper l'adresse IP du serveur.

Les jeux Team 6 ont un mode "jeu en ligne", mais il essaie de récupérer une page qui n'existe plus sur le site de Team 6 (plus précisément `http://team6-games.com:80/Game_GetHosts.php`), et donc ça va pas très loin. 😛

## Les scripts custom Pizza Dude (dossier `PizzaDudeScripts`)

Les seuls et uniques mods de Pizza Dude qui existent sur la planète, réalisés avec le superbe mission editor de Pizza Dude.

Pour les lancer, soit remplacer `Scripts\Pizzadude.dcScript` par l'un des scripts, ou les extraire tous dans `Scripts` et en mode test, lancer la commande `loadscript <nom du script>`.

- `RushHour.dcScript` : augmente **considérablement** la quantité de trafic. Passez près des voitures avec le quad pour qu'elles s'empilent et que ça donne un flot de voitures qui a aucun sens !
- `cauchemar_en_cuisine.dcScript` : augmente la durée des animations de "cuisine" des ingrédients. On peut donc activer toutes les animations en même temps, et ça donne une cuisine hantée... voilà.
- `argh.dcScript` (dérivé de `cauchemar_en_cuisine.dcScript`) : remplace le son de découpe de la tomate par plusieurs sons de chute du quad de Pizza Dude.

## Le script custom Glacier (dossier `GlacierScripts`)

Même chose que Pizza Dude pour le lancer.

- `OneToRuleThemAll.dcscript` : la première course du jeu, mais tu contrôles _les 3 voitures en même temps_.

## Le Turbo Quad :tm:

Dans `vehicles`, remplacer `Quad.dcc` par `TurboQuad.dcc` pour obtenir un quad qui peut facilement monter à 250 km/h. Fonctionne avec tous les véhicules (que j'ai testés) dans les jeux de Team 6 de l'époque, notamment Glacier.

## Pizza Code Finder

Un petit projet qui recherche des codes valides pour... [Call-a-Pizza Dude](https://web.archive.org/web/20070513220907/http://www.call-a-pizza.de/dude/), que tu pouvais visiblement obtenir en achetant une pizza chez Call-a-Pizza en Allemagne. :shrug:

D'après ce machin-là, il y a plus d'1 million de codes valides. J'en ai testé uniquement 3 ou 4 hein :stuck_out_tongue:

~~sinon la commande `loadscript bonus` ça marche aussi~~

## Débloquer toutes les courses

- Dans Glacier, lancer le jeu en mode test et lancer `loadscript gameover`. La cinématique de fin te donne suffisamment d'argent et de respect pour déverrouiller toutes les courses. :stuck_out_tongue:
- Dans Ultimate Motorcross, déposer le script `UltimateMotorcrossScripts/Win.dcScript` dans le dossier `Scripts` du jeu, puis le lancer en mode test. Puis, lancer une course et taper `loadscript win` pour la gagner et débloquer la suivante. ^^
- La procédure pour Ultimate Motorcross fonctionne aussi pour Scooter War3z... parce que bon, c'est pratiquement le même jeu. :stuck_out_tongue:


