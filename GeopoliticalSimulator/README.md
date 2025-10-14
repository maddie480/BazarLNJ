# Mission Président : Geopolitical Simulator

Un jeu de simulation politique ultra-réaliste sorti en 2007 par Eversim, studio français qui sort toujours des suites / mises à jour de ce jeu à ce jour. (Et ils coûtent une blinde.)

[Le site web du jeu est resté en 2007](https://www.geo-political-simulator.com/index.php?langue=fr&page=news), et propose un lien d'achat qui ne marche plus... mais pas de panique, [15 ans plus tard, pas grand-chose a changé](https://www.geo-political-simulator-5.com/news.php), et il est toujours possible d'acheter la version 2025 de Geo-Political Simulator 5 sur leur site Web, avec un code d'activation, comme si on était encore dans les années 2000 !

Mission Président est aussi sorti en version CD physique, avec une protection contre la copie made in Mindscape... c'est toujours mieux protégé qu'une activation en ligne qui contacte sans chiffrement un serveur d'Eversim qui, d'une façon ou d'une autre, tourne encore 15 ans plus tard...

## Les sons du jeu

Les sons du jeu sont accessibles très facilement, mais sont masqués : les fichiers `.gsw` qui sont dans le dossier `gsw` sont en fait... des fichiers WAV. Il suffit de les ouvrir avec VLC pour les écouter ~~et se spoiler les coups de téléphone à l'acting absolument impeccable~~.

## Les textures

Les textures sont cachées dans les fichiers `.gp`, qui sont... des genres d'archives ? Chaque fichier `.gp` contient plusieurs textures avec leurs noms. Elles sont stockées dans des formats divers et variés, dont certains que je n'ai pas réussi à déchiffrer (mais bon, good enough hein) :
- bitmap 16 bits RGB 555, compressé avec zlib, avec une somme de contrôle invalide
- bitmap 32 bits RGBA 8888, compressé avec zlib, avec une somme de contrôle invalide
- bitmap 16 bits ARGB 4444, compressé avec zlib, avec une somme de contrôle invalide
- JPEG (juste un JPEG collé directement dans le fichier `.gp`)
- bitmap 16 bits RGB 555, **non** compressé
- bitmap 32 bits RGBA 8888, **non** compressé

Donc j'ai fait un petit outil dans `TextureExtractor-src`, en Java (comme d'habitude), pour extraire les fichiers au format `.png` et pouvoir les admirer... si c'est possible. L'encodage de certaines textures reste un mystère pour moi.

Fun fact : dans `face.gp` et `face2.gp`, il y a les visages des différents personnages... en kit (sauf pour le pape, visiblement) : l'arrière-plan, la tête, les yeux, la bouche, les cheveux, le costume, le chapeau, etc, sont des textures différentes, qui sont assemblées par le jeu pour faire des visages variés... ce qui pourrait expliquer pourquoi certains personnages ont des tronches un peu bizarres.

La pipeline GitHub fait tourner le script sur tous les fichiers `.gp` du jeu, et met le résultat dans un artifact.
