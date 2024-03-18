if exist "..\Nightmare Simulator_Data\Managed\Assembly-CSharp-vanilla.dll" (
  copy /Y "..\Nightmare Simulator_Data\Managed\Assembly-CSharp-vanilla.dll" "..\Nightmare Simulator_Data\Managed\Assembly-CSharp.dll"
) else (
  copy "..\Nightmare Simulator_Data\Managed\Assembly-CSharp.dll" "..\Nightmare Simulator_Data\Managed\Assembly-CSharp-vanilla.dll"
)

copy /Y Assembly-CSharp.Mod.mm.dll "..\Nightmare Simulator_Data\Managed"
MonoMod.exe "..\Nightmare Simulator_Data\Managed\Assembly-CSharp.dll"
move /Y "..\Nightmare Simulator_Data\Managed\MONOMODDED_Assembly-CSharp.dll" "..\Nightmare Simulator_Data\Managed\Assembly-CSharp.dll"
del "..\Nightmare Simulator_Data\Managed\MONOMODDED_Assembly-CSharp.pdb" "..\Nightmare Simulator_Data\Managed\Assembly-CSharp.Mod.mm.dll"

pause
