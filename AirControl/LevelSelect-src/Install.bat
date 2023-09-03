if exist "..\Air Control Final_Data\Managed\Assembly-UnityScript-vanilla.dll" (
  copy /Y "..\Air Control Final_Data\Managed\Assembly-UnityScript-vanilla.dll" "..\Air Control Final_Data\Managed\Assembly-UnityScript.dll"
) else (
  copy "..\Air Control Final_Data\Managed\Assembly-UnityScript.dll" "..\Air Control Final_Data\Managed\Assembly-UnityScript-vanilla.dll"
)

copy /Y Assembly-UnityScript.Mod.mm.dll "..\Air Control Final_Data\Managed"
MonoMod.exe "..\Air Control Final_Data\Managed\Assembly-UnityScript.dll"
move /Y "..\Air Control Final_Data\Managed\MONOMODDED_Assembly-UnityScript.dll" "..\Air Control Final_Data\Managed\Assembly-UnityScript.dll"
del "..\Air Control Final_Data\Managed\MONOMODDED_Assembly-UnityScript.pdb" "..\Air Control Final_Data\Managed\Assembly-UnityScript.Mod.mm.dll"

pause
