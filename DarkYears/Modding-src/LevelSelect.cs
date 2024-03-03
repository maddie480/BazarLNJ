using MonoMod;
using System.Collections.Generic;
using UnityEngine;

public class menu {
    private List<Dictionary<string, object>> selectableLevels;
    private int selectedLevel = 0;

    // vanilla fields we want to use
    public bool option;
    public bool advanceoption;
    public bool showcredits;


#pragma warning disable CS0626 // This is expected due to MonoMod, these methods will be implemented by vanilla
    public extern void orig_ctor();
    public virtual extern void orig_OnGUI();
#pragma warning restore CS0626

    [MonoModConstructor]
    public void ctor() {
        orig_ctor();

        // Those were retrieved from the "savereg" trigger thingie scattered across the levels.
        // They are not in a particular order, though...
        selectableLevels = new List<Dictionary<string, object>>() {
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Zendan" },
                { "Tehran", 2 }
            },
            new Dictionary<string, object>() {
                { "CurMovie", 6 },
                { "GoTo", "cutscenes" },
                { "CurLevel", "Villa" },
                { "Villa", 1 },
                { "Tehran", 8 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Tehran" },
                { "Tehran", 1 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Tehran" },
                { "Afsharhome", 3 },
                { "Tehran", 8 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Tehran" },
                { "Afsharhome", 3 },
                { "Tehran", 9 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "Otushoie", 1 },
                { "CurLevel", "Tehran" },
                { "Tehran", 3 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "Otushoie", 1 },
                { "CurLevel", "Tehran" },
                { "Tehran", 5 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "Otushoie", 2 },
                { "CurLevel", "Tehran" },
                { "Tehran", 7 }
            },
            new Dictionary<string, object>() {
                { "CurMovie", 5 },
                { "GoTo", "cutscenes" },
                { "CurLevel", "EdarePolice" },
                { "Police", 5 },
                { "Tehran", 8 }
            },
            new Dictionary<string, object>() {
                { "CurMovie", 8 },
                { "GoTo", "cutscenes" },
                { "CurLevel", "Villa" },
                { "Villa", 3 },
                { "Tehran", 8 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Indoor@Villa" },
                { "VillaIndoor", 1 },
                { "Tehran", 8 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Indoor@Villa" },
                { "VillaIndoor", 2 },
                { "Tehran", 8 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Tehran" },
                { "Afsharhome", 2 },
                { "Tehran", 2 }
            },
            new Dictionary<string, object>() {
                { "AfsharHome", 1 },
                { "GoTo", "Loading" },
                { "CurLevel", "Bandar_CS" },
                { "Tehran", 1 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "AfsharHome" },
                { "Afsharhome", 1 },
                { "Tehran", 1 }
            },
            new Dictionary<string, object>() {
                { "London", 3 },
                { "GoTo", "Loading" },
                { "CurLevel", "London" }
            },
            new Dictionary<string, object>() {
                { "London", 0 },
                { "CurMovie", 4 },
                { "GoTo", "cutscenes" },
                { "CurLevel", "Hospital" },
                { "Tehran", 3 }
            },
            new Dictionary<string, object>() {
                { "London", 2 },
                { "CurMovie", 3 },
                { "JamesHome", 1 },
                { "GoTo", "cutscenes" },
                { "CurLevel", "London" }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Tehran" },
                { "Police", 3 },
                { "Tehran", 6 }
            },
            new Dictionary<string, object>() {
                { "CurMovie", 9 },
                { "GoTo", "cutscenes" },
                { "CurLevel", "Tehran" },
                { "Tehran", 10 }
            },
            new Dictionary<string, object>() {
                { "London", 10 },
                { "CurMovie", 7 },
                { "GoTo", "cutscenes" },
                { "CurLevel", "Villa" },
                { "Villa", 2 }
            },
            new Dictionary<string, object>() {
                { "London", 5 },
                { "JamesHome", 2 },
                { "GoTo", "Loading" },
                { "CurLevel", "London" }
            }, new Dictionary<string, object>() {
                { "London", 1 },
                { "GoTo", "Loading" },
                { "CurLevel", "London" }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Tehran" },
                { "Police", 2 },
                { "Tehran", 4 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "Tehran" },
                { "Police", 4 },
                { "Tehran", 8 }
            },
            new Dictionary<string, object>() {
                { "CurMovie", 2 },
                { "GoTo", "cutscenes" },
                { "CurLevel", "EdarePolice" },
                { "Police", 1 },
                { "Tehran", 3 }
            },
            new Dictionary<string, object>() {
                { "CurMovie", 10 },
                { "GoTo", "cutscenes" },
                { "CurLevel", "Tehran" },
                { "Tehran", 10 }
            },
            new Dictionary<string, object>() {
                { "GoTo", "Loading" },
                { "CurLevel", "AfsaranBazneshaste" },
                { "AfsaranBazneshaste", 0 },
                { "Tehran", 10 }
            }
        };
    }

    public virtual void OnGUI() {
        orig_OnGUI();

        if (!option && !advanceoption && !showcredits) {
            GUI.BeginGroup(new Rect(Screen.width / 2 - 150, Screen.height / 2 - 150, 300f, 600f));

            if (GUI.Button(new Rect(50f, 300f, 150f, 50f), $"Go to #{selectedLevel + 1}")) {
                // load all required keys into PlayerPrefs
                foreach (KeyValuePair<string, object> value in selectableLevels[selectedLevel]) {
                    if (value.Key == "GoTo") continue;

                    // We're dealing with .NET 2.0 here, so no == operator
                    if (value.Value.GetType().Equals(typeof(string))) {
                        PlayerPrefs.SetString(value.Key, (string) value.Value);
                    } else {
                        PlayerPrefs.SetInt(value.Key, (int) value.Value);
                    }
                }

                // then load either the "Loading" or the "cutscenes" scene, as required
                Application.LoadLevel((string) selectableLevels[selectedLevel]["GoTo"]);
            }

            if (GUI.Button(new Rect(0f, 300f, 50f, 50f), "<") && selectedLevel > 0) {
                selectedLevel--;
            }
            if (GUI.Button(new Rect(200f, 300f, 50f, 50f), ">") && selectedLevel < selectableLevels.Count - 1) {
                selectedLevel++;
            }

            GUI.EndGroup();
        }
    }
}
