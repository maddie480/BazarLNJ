if exist "..\DarkYears_data\Managed\Assembly-UnityScript-vanilla.dll" (
  copy /Y "..\DarkYears_data\Managed\Assembly-UnityScript-vanilla.dll" "..\DarkYears_data\Managed\Assembly-UnityScript.dll"
) else (
  copy "..\DarkYears_data\Managed\Assembly-UnityScript.dll" "..\DarkYears_data\Managed\Assembly-UnityScript-vanilla.dll"
)

copy /Y Assembly-UnityScript.Mod.mm.dll "..\DarkYears_data\Managed"
MonoMod.exe "..\DarkYears_data\Managed\Assembly-UnityScript.dll"
move /Y "..\DarkYears_data\Managed\MONOMODDED_Assembly-UnityScript.dll" "..\DarkYears_data\Managed\Assembly-UnityScript.dll"
del "..\DarkYears_data\Managed\MONOMODDED_Assembly-UnityScript.pdb" "..\DarkYears_data\Managed\Assembly-UnityScript.Mod.mm.dll"

pause
