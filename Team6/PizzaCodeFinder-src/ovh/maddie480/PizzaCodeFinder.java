package ovh.maddie480;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PizzaCodeFinder {
    /**
     * Part à la recherche d'un code valide pour le niveau bonus de Call-a-Pizza Dude (c'est quand même sacrément spécifique).
     * <p>
     * Le code assembleur de la boucle à partir de Pizza CAP.exe+9255 (valeur initiale de eax = 0xBABE) :
     * movzx edi,byte ptr [ecx+esi] // alterne entre le code ASCII du chiffre et 0
     * mov ebx,eax
     * shr ebx,18
     * xor edi,ebx
     * shl eax,08
     * xor eax,[edi*4+Pizza CAP.exe+2BBB78] // j'ai retrouvé le contenu de Pizza CAP.exe+2BBB78 à 0x2BBF80 dans l'exe du jeu
     * add ecx,01
     * cmp ecx,edx
     * jnge Pizza CAP.exe+9255 // boucle 20 fois, edx valant 0x14
     * [...]
     * xor eax,edx
     * <p>
     * Il faut qu'à la fin, eax soit inférieur à 0x7A120. (:shrug:)
     */

    // vu que edi vient d'un int >> 24, sa valeur maximale est 0xFF, donc 0x100 valeurs possibles
    private static final long[] chunksFromExeFile = new long[0x100];

    /**
     * Recherche et imprime sur la sortie standard tous les pizza codes (tm) valides.
     */
    public static void main(String[] args) throws IOException {
        // on lit les 256 fragments du fichier exe qu'on xor à l'avance pour aller plus vite
        for (int i = 0; i < chunksFromExeFile.length; i++) {
            chunksFromExeFile[i] = readFromExeFile(i * 4);
        }

        char[] candidate = "0000000000".toCharArray();
        do {
            if (computeEax(candidate) < 0x7A120) {
                System.out.println(new String(candidate));
            }
        } while (increment(candidate, 0));
    }

    /**
     * Lit 4 octets venant du fichier EXE du jeu, qui fait partie de ceux qui peuvent être utilisés pour la validation
     * du pizza code (tm).
     */
    private static long readFromExeFile(int offset) throws IOException {
        try (InputStream is = Files.newInputStream(Paths.get("Pizza CAP.exe"))) {
            is.skip(0x2BBF80);
            is.skip(offset);

            long b1 = is.read();
            long b2 = is.read();
            long b3 = is.read();
            long b4 = is.read();

            // "big endian" : les octets sont inversés
            return (b4 << 24) + (b3 << 16) + (b2 << 8) + b1;
        }
    }

    /**
     * Reproduit l'algorithme de validation du pizza code (tm). Le code doit faire 10 chiffres.
     */
    private static long computeEax(char[] input) {
        long eax = 0xBABE;
        for (int i = 0; i < 20; i++) {
            int edi = (i % 2 == 0 ? input[i / 2] : 0);
            int ebx = (int) (eax >> 24);
            edi ^= ebx;
            eax = (eax << 8) % 0x1_0000_0000L;
            eax ^= chunksFromExeFile[edi];
        }
        eax ^= 20;
        return eax;
    }

    /**
     * Ajoute 1 au chiffre indiqué du pizza code (tm) donc 0000000000 => 1000000000 avec index = 0.
     * L'index permet d'appeler la méthode de manière récursive pour incrémenter le deuxième chiffre quand le premier a atteint 9.
     */
    private static boolean increment(char[] number, int index) {
        number[index]++;
        if (number[index] > '9') {
            if (index == number.length - 1) return false;

            number[index] = '0';
            return increment(number, index + 1);
        }

        return true;
    }
}
