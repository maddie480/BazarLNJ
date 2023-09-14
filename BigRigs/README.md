# Big Rigs: Over the Road Racing

Un jeu de "course" complètement pété sans collision et avec une marche arrière infinie... qui a quand même réussi à sortir en magasin, apparemment.

[The Cutting Room Floor](https://tcrf.net/Big_Rigs:_Over_the_Road_Racing) a pas mal d'information au sujet de certains trucs qui traînent dans ce jeu.

Voici ce que j'ai trouvé de mon côté...

## La police des compteurs

Les compteurs débordent, certes... à moins d'installer la police [SF Digital Readout](https://www.1001fonts.com/digital-readout-font.html), qui les transforme en compteur digital style micro-ondes. (oui, l'auteur de la police a été inspiré par son micro-ondes, visiblement)

Il semblerait que les devs aient oublié d'inclure la police dans leur jeu, ou qu'ils se soient rendus compte qu'elle était "gratuite pour une utilisation personnelle uniquement" :stuck_out_tongue:

_Fun fact :_ la police est fournie au format bmp avec le jeu... mais vu qu'elle est regénérée par le jeu, ça sert à rien. :stuck_out_tongue:

## La musique

Le jeu part du principe que tu as un CD qui possède les musiques du jeu au format CD audio qui est inséré dans le lecteur D: de ton ordinateur. Si c'est le cas, tu auras de la musique dans ton jeu.

Il y a un ISO disponible au téléchargement sur My Abandonware, mais au lieu de vraies pistes audio, il contient les musiques au format WAV pour une raison obscure. Ce dépôt contient un script pour convertir la musique de cet ISO au format CD audio (les fichiers de sortie sont un BIN et un CUE). Si tu insères ce CD dans ton lecteur D:, tu pourras profiter de la _super_ musique de ce jeu ! (Sinon, écoute-la sur Radio LNJ, ça marche aussi.)

## Les véhicules cachés

Il est possible de jouer avec des voitures dans ce jeu de camions (ce qui ne change absolument rien, vu que les véhicules n'ont aucune différence entre eux :stuck_out_tongue:), en modifiant le fichier `Data\Cars\wh28.nfo` :

```
4
Mega1 Mega1
Mega2 Mega2
Mega3 Mega3
Sun Sun

Ford Ford
Z350 Z350
Celica Celica
Hummer Hummer
```

Permutez juste les lignes pour que les 4 premières soient les véhicules que vous voulez, et pouf :sparkles:

Le jeu utilise beaucoup de fichiers texte, donc il doit être facilement moddable... mais je suis absolument pas inspirée là-dessus. :sweat_smile:

## La version non patchée

_(Il s'agit de la version "setup" sur My Abandonware, la version ISO est à priori une autre version)_

- Il y a une free cam ! En appuyant sur C, on peut se balader dans le niveau avec ZQSD, A et E bougent verticalement, W se téléporte vers ton véhicule, et Shift fait aller la caméra plus vite.
- Tab te permet de contrôler ton adversaire (qui ne bouge pas)... ou de faire planter le jeu.
- Les checkpoints ne sont pas remis à zéro entre deux courses, ce qui fait qu'ils s'accumulent dans l'Ultranav :tm: et que si tu quittes une course sans la finir, tu dois redémarrer le jeu pour pouvoir finir une autre course, sinon tu auras des checkpoints que tu peux pas passer _parce qu'ils sont sur une autre map_. Fun.

## La version patchée

- Même sur la version patchée, essayer de lancer la course 3 puis la course 5 fait planter le jeu... voilà voilà.
- Cette version contient des meilleurs scores qui sont uniques pour tous les circuits et qui ne sont jamais mis à jour... mais qui ne sont pas codés en dur ! En fait, ils sont enregistrés dans le _Registre Windows_, sous `HKEY_CURRENT_USER\Software\StellarStone\BigRigs`, dans le champ nommé `Config1`. Je ne sais pas sous quel format les temps sont stockés, mais bon, est-ce que ça a une importance ? :stuck_out_tongue: