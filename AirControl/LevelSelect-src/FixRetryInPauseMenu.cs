using Mono.Cecil;
using Mono.Cecil.Cil;
using MonoMod;
using MonoMod.Cil;
using MonoMod.Utils;
using System;
using UnityEngine;

public class patch_pause4 {
    [MonoModIgnore]
    [PatchPauseMenu]
    public virtual extern void OnGUI();

    private static string getRestartButtonLabel() {
        return "Restart Level " + Application.loadedLevel + " (" + Application.loadedLevelName + ")";
    }

    private static void resetTimeScale() {
        Time.timeScale = 1f;
    }
}

namespace MonoMod {
    [MonoModCustomMethodAttribute(nameof(MonoModRules.PatchPauseMenu))]
    class PatchPauseMenuAttribute : Attribute { }


    static partial class MonoModRules {
        public static void PatchPauseMenu(ILContext context, CustomAttribute attrib) {
            ILCursor cursor = new ILCursor(context);

            // relabel "restart" button
            cursor.GotoNext(instr => instr.MatchLdstr("restart"));
            cursor.Next.OpCode = OpCodes.Call;
            cursor.Next.Operand = context.Method.DeclaringType.FindMethod("getRestartButtonLabel");

            // restore timeScale to 1 after level is loaded
            cursor.Index = 0;

            for (int i = 0; i < 2; i++) {
                cursor.GotoNext(MoveType.After, instr => instr.OpCode == OpCodes.Call && (instr.Operand as MethodReference).Name == "LoadLevel");
                cursor.Emit(OpCodes.Call, context.Method.DeclaringType.FindMethod("resetTimeScale"));
            }
        }
    }
}