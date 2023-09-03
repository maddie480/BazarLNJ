using Mono.Cecil;
using Mono.Cecil.Cil;
using MonoMod;
using MonoMod.Cil;
using MonoMod.Utils;
using System;
using UnityEngine;

public class patch_menu {
    private int levelSelect;

#pragma warning disable CS0626
    public extern void orig_ctor();
#pragma warning restore CS0626

    [MonoModConstructor]
    public void ctor() {
        orig_ctor();
        levelSelect = 1;
    }

    [MonoModIgnore]
    [PatchMenu]
    public virtual extern void OnGUI();

    private static bool nop(Rect position, string text) { return false; }

    private void addButtons() {
        if (GUI.Button(new Rect(Screen.width / 2 - 90, Screen.height / 2 + 160, 180f, 40f), "Go to level " + levelSelect)) {
            Application.LoadLevel(levelSelect);
        }
        if (GUI.Button(new Rect(Screen.width / 2 + 110, Screen.height / 2 + 160, 40f, 40f), "-") && levelSelect > 1) {
            levelSelect--;
        }
        if (GUI.Button(new Rect(Screen.width / 2 + 150, Screen.height / 2 + 160, 40f, 40f), "+") && levelSelect < 234) {
            levelSelect++;
        }
        if (GUI.Button(new Rect(Screen.width / 2 + 200, Screen.height / 2 + 160, 60f, 40f), "-10")) {
            levelSelect = Math.Max(1, levelSelect - 10);
        }
        if (GUI.Button(new Rect(Screen.width / 2 + 260, Screen.height / 2 + 160, 60f, 40f), "+10")) {
            levelSelect = Math.Min(234, levelSelect + 10);
        }
    }
}

namespace MonoMod {
    [MonoModCustomMethodAttribute(nameof(MonoModRules.PatchMenu))]
    class PatchMenuAttribute : Attribute { }


    static partial class MonoModRules {
        public static void PatchMenu(ILContext context, CustomAttribute attrib) {
            ILCursor cursor = new ILCursor(context);

            // remove "Coming soon" button
            cursor.GotoNext(MoveType.After, instr => instr.MatchLdstr("Coming soon"));
            cursor.Next.Operand = context.Method.DeclaringType.FindMethod("nop");

            // add level select buttons instead
            cursor.GotoNext(MoveType.AfterLabel, instr => instr.OpCode == OpCodes.Call && (instr.Operand as MethodReference).Name == "get_width");
            cursor.Emit(OpCodes.Ldarg_0);
            cursor.Emit(OpCodes.Call, context.Method.DeclaringType.FindMethod("addButtons"));
        }
    }
}