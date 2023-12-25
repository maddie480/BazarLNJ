// == Console: adapted from another 3D GameStudio game called W.A.R. Soldiers (https://www.myabandonware.com/game/w-a-r-soldiers-ge7)

font md_console_font = "Courier New", 1, 24;

string md_exec_buffer = "#60"; // yes, this means 60 spaces

text md_console_txt {
    pos_x = 10;
    pos_y = 10;
    layer = 10;
    font = md_console_font;
    strings 1;
    string md_exec_buffer;
    flags = SHADOW | OUTLINE;
}

function md_console() {
    if (md_console_txt.visible == on) { return; } // already running
    md_console_txt.visible = on;

    inkey(md_exec_buffer);

    if (result == 13) { // Enter
        wait(1); // wait for 1 frame to be sure key_enter is correct
        while (key_enter) { wait(1); } // wait for Enter to be released before executing command
        execute(md_exec_buffer);
    }

    md_console_txt.visible = off;
}

function md_console_listener() {
    while (1) {
        if(key_pressed(41) == 1) { md_console(); } // ² key on AZERTY
        wait(1); // 1 frame
    }
}


// == The Day Before: a tribute to the zombie extraction shooter that lived for 3 days

var md_zombiego = off;

function md_the_day_before(min, max) {
    if (md_zombiego) { return; }
    md_zombiego = on;

    while (md_zombiego) {
        randomize();
        wait(-random(max - min) - min);

        ent_create("zombielow.mdl", plBiped01_entity.x, ZombieSPFunc);
    }
}

function md_the_day_after() {
    md_zombiego = off;
}


// == Shortcuts to spawn random stuff

function md_spawn_invisible_clio() {
    ent_create("Clio.mdl", plBiped01_entity.x, car_path);
}
function md_spawn_mini_r8() {
    ent_create("AudiR8.mdl", plBiped01_entity.x, car_path);
}
function md_spawn_bicycle_r8() {
    ent_create("AudiR8.mdl", plBiped01_entity.x, bicycle);
}


// == Commands to become multiplayer characters

function md_turn_into_character(character) {
    ent_morph(plBiped01_entity, character);
    plBiped01_entity.scale_x = 3;
    plBiped01_entity.scale_y = 3;
    plBiped01_entity.scale_z = 3;
    plBiped01_entity.z += 200;
}

function md_turn_into_mainplayer() {
    md_turn_into_character("MainPlayerMP.mdl");
}
function md_turn_into_man1() {
    md_turn_into_character("Pedestrian02.mdl");
}
function md_turn_into_woman1() {
    md_turn_into_character("walkwomen1.mdl");
}
function md_turn_into_woman2() {
    md_turn_into_character("walkwomen2MP.mdl");
}
function md_turn_into_woman3() {
    md_turn_into_character("Pedestrian03.mdl");
}
function md_turn_into_fbiagent() {
    md_turn_into_character("FbiAgentMP.mdl");
}
function md_turn_into_jessica() {
    md_turn_into_character("jessica.mdl");
}
function md_turn_into_john() {
    md_turn_into_character("john.mdl");
}
function md_turn_into_simon() {
    md_turn_into_character("simon.mdl");
}
function md_turn_into_toni() {
    md_turn_into_character("ToniMP.mdl");
}
function md_turn_into_bikeman() {
    md_turn_into_character("mpbikeman.mdl");
}

function md_get_out_of_the_ground() {
    plBiped01_entity.z += 200;
}


// == Shortcuts to go to dummied out places

panel* md_panel_pointer;

function md_enter_place(actionName, placeNamePanel) {
    var teleporter;
    teleporter = ent_create(NULL, plBiped01_entity.x, actionName);

    // You need to assign your pointer to a REAL pointer before you can use it. Go figure.
    md_panel_pointer = placeNamePanel;
    while (md_panel_pointer.visible) { wait(1); }
    md_panel_pointer = NULL;

    ent_remove(teleporter);
    diag("\n[Maddie] Removed teleporter!");
}

function md_exit_place(actionName) {
    var teleporter;
    teleporter = ent_create(NULL, plBiped01_entity.x, actionName);
    while (fader_ent.alpha != 0) { wait(1); }
    ent_remove(teleporter);
    diag("\n[Maddie] Removed teleporter!");
}

function md_enter_bowling() {
    md_enter_place(Bowling_entry, bowlinglbl_pan);
}
function md_exit_bowling() {
    md_exit_place(Bowling_exit);
}

function md_enter_caesars() {
    md_enter_place(caesars_entry, caesarlbl_pan);
}
function md_exit_caesars() {
    md_exit_place(caesars_exit);
}
