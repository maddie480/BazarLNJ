# Psycho Killer

Peut-être l'un des pires FMV, où tu poursuis (ou te fait poursuivre) (ou poursuis _puis_ te fait poursuivre par) un tueur (ou pas, c'est pas clair s'il a tué ou non)
avec 5 secondes de chargement entre les écrans, et des cinématiques de haute qualité avec 5 images suivies de 5 secondes de pause.

## Le convertisseur de média

Il se trouve que [ffmpeg](https://ffmpeg.org/) sait très bien lire les vieilleries présentes dans ce jeu.
Donc j'ai fait un petit programme qui utilise `ffprobe` pour trouver les images / vidéos / sons
et les convertir respectivement en PNG, MP4 et MP3 avec `ffmpeg`.

A l'exception près que les vidéos sont ralenties pour qu'elles ne fassent pas 0,2 secondes chacune,
le code pourrait être réutilisable si un autre jeu a des assets dans un format chelou que `ffmpeg` sait lire. :sweat_smile: