using MonoMod;
using UnityEngine;

public class patch_startgame {
    private string[] levelPrefsNames;
    private string[] levelMapNames;

#pragma warning disable CS0626
    public extern void orig_ctor();
#pragma warning restore CS0626

    [MonoModConstructor]
    public void ctor() {
        orig_ctor();

        // the PlayerPrefs tracking progression and the actual level names are ALMOST the same.
        levelPrefsNames = new string[] {
            "szkola", "drabinascena", "furnace", "market", "psychodela", "eskimosi", "apokalipsa", "scenasciany",
            "lekarzbandit", "puciung", "occult", "zebra", "witch", "czech", "domkruk", "autostrada", "koniec"
        };
        levelMapNames = new string[] {
            "szkola", "drabinascena", "piec", "market", "psychode", "eskimosi", "apokalipsa", "scenasciany",
            "lekarzbandit", "puciung", "occult", "zebra", "witch", "czech", "domkruk", "autostrada", "koniec"
        };
    }

    public void OnGUI() {
        int posX = 20;
        int posY = 20;

        for (int i = 0; i < levelMapNames.Length; i++) {
            string prefName = levelPrefsNames[i];
            string mapName = levelMapNames[i];

            string finishedLabel = (PlayerPrefs.GetInt(prefName) == 0 ? " (non terminé)" : " (terminé)");
            if (mapName == "koniec") finishedLabel = "";

            if (GUI.Button(new Rect(posX, posY, 180f, 40f), mapName + finishedLabel)) {
                Application.LoadLevel(mapName);
            }

            posY += 60;
            if (posY + 60 > Screen.height) {
                posY = 20;
                posX += 200;
            }
        }
    }
}
