# Big Rigs: Over the Road Racing

Un jeu de "course" complètement pété sans collision et avec une marche arrière infinie... qui a quand même réussi à sortir en magasin, apparemment.

[The Cutting Room Floor](https://tcrf.net/Big_Rigs:_Over_the_Road_Racing) a pas mal d'information au sujet de certains trucs qui traînent dans ce jeu.

Voici ce que j'ai trouvé de mon côté...

## La police des compteurs

Les compteurs débordent, certes... à moins d'installer la police [SF Digital Readout](https://www.1001fonts.com/digital-readout-font.html), qui les transforme en compteur digital style micro-ondes. (oui, l'auteur de la police a été inspiré par son micro-ondes, visiblement)

Il semblerait que les devs aient oublié d'inclure la police dans leur jeu, ou qu'ils se soient rendus compte qu'elle était "gratuite pour une utilisation personnelle uniquement" :stuck_out_tongue:

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
