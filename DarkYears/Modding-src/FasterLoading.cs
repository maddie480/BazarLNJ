using Mono.Cecil;
using Mono.Cecil.Cil;
using MonoMod;
using MonoMod.Cil;
using System;

public class Loading2 {
    [MonoModIgnore]
    [PatchLoadingWait]
    public virtual extern void Update();
}

namespace MonoMod {
    [MonoModCustomMethodAttribute(nameof(MonoModRules.PatchLoadingWait))]
    class PatchLoadingWaitAttribute : Attribute { }

    static partial class MonoModRules {
        public static void PatchLoadingWait(ILContext context, CustomAttribute attrib) {
            ILCursor cursor = new ILCursor(context);

            // remove that stupid fake loading delay
            cursor.GotoNext(instr => instr.MatchLdcI4(10));
            cursor.Next.OpCode = OpCodes.Ldc_I4_0;
        }
    }
}