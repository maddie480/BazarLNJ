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
        if (key_pressed(41) == 1) { md_console(); } // ² key on AZERTY
        wait(1); // 1 frame
    }
}

// == Since accessing fields of a struct requires putting them in real pointer variables first, here they are.
//    They should be set to null after their usage.

ENTITY* md_entity_holder;
PANEL* md_panel_holder;


// == Debug functions

function md_show_text(txt, y) {
    draw_text(txt, 10, y, vector(0, 0, 255));
}

function md_show_position(ent, prefix, posy) {
    var txt;
    txt = str_create(prefix);

    md_entity_holder = ent;
    str_cat_num(txt, "(%.3f, ", md_entity_holder.x);
    str_cat_num(txt, "%.3f, ", md_entity_holder.y);
    str_cat_num(txt, "%.3f)", md_entity_holder.z);
    md_entity_holder = NULL;

    md_show_text(txt, posy);
}

function md_show_number(num, prefix, y) {
    var txt;
    txt = str_create(prefix);
    str_cat_num(txt, "%.3f", num);

    md_show_text(txt, y);
}


// == The Day Before: a tribute to the zombie extraction shooter that lived for 3 days

var md_zombiego = off;

function md_spawn_zombie() {
    var zombie;
    zombie = ent_create("zombielow.mdl", plBiped01_entity.x, ZombieSPFunc);

    md_entity_holder = zombie;

    // Place the zombie on the player by undoing a "x += 5000" it did on its own
    md_entity_holder.x -= 5000;
    md_entity_holder.z = -25;

    // Then move the zombie away from the player at a random angle
    randomize();
    md_entity_holder.pan = random(360);
    c_move(md_entity_holder, vector(5000, 0, 0), nullvector, GLIDE + IGNORE_PASSABLE);

    md_entity_holder = NULL;

    diag("\n[Maddie] Spawned zombie!");
}

function md_the_day_before(min, max) {
    if (md_zombiego) { return; }
    md_zombiego = on;

    while (md_zombiego) {
        randomize();
        wait(-random(max - min) - min);
        md_spawn_zombie();
    }
}

function md_the_day_after() {
    md_zombiego = off;
}


// == Shortcuts to spawn random stuff

function md_scale_entity(entity, scale) {
    md_entity_holder = entity;
    md_entity_holder.scale_x = scale;
    md_entity_holder.scale_y = scale;
    md_entity_holder.scale_z = scale;
    c_setminmax(md_entity_holder);
    md_entity_holder = NULL;
}
function md_paint_and_control() {
    paint_AIcar();
    car_path();
}

function md_spawn_clio() {
    var clio;
    clio = ent_create("Clio.mdl", plBiped01_entity.x, md_paint_and_control);
    md_scale_entity(clio, 75);
}
function md_spawn_r8() {
    var r8;
    r8 = ent_create("AudiR8.mdl", plBiped01_entity.x, md_paint_and_control);
    md_scale_entity(r8, 3);
}
function md_spawn_bicycle() {
    var bike;
    bike = ent_create("bicycle.mdl", plBiped01_entity.x, bicycle);
    md_scale_entity(bike, 50);
}


// == Commands to become multiplayer characters

function md_get_out_of_the_ground() {
    plBiped01_entity.z += 200;
}

function md_turn_into_character(character) {
    ent_morph(plBiped01_entity, character);
    md_scale_entity(plBiped01_entity, 3);
    md_get_out_of_the_ground();
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


// == Shortcuts to go to dummied out places

function md_enter_place(actionName, placeNamePanel) {
    var teleporter;
    teleporter = ent_create(NULL, plBiped01_entity.x, actionName);

    md_panel_holder = placeNamePanel;
    while (md_panel_holder.visible) { wait(1); }
    md_panel_holder = NULL;

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


// == Wanted level modifications

function md_call_police(count) {
    police_called = count;
    var i = 0;
    while (i < count) {
        ent_create("police_car.mdl", PcarPOS.x, police_event);
        wait(-1);
        i += 1;
    }
}

function md_wanted_clear() {
    trouble = 0;
    more_trouble = 0;
    peds_killed = 0;
    police_killed = 0;
    police_called = 0;
}
function md_wanted_1star() {
    peds_killed = 31;
    trouble = 1;
}
function md_wanted_3stars() {
    peds_killed = 50;
    trouble = 2;
    md_call_police(2);
}
function md_wanted_5stars() {
    peds_killed = 101;
    trouble = 3;
    md_call_police(3);
}


// == Gravity changes

function md_setgravity(factor) {
    ph_setgravity(vector(0, 0, -386 * factor));
    bipedPhy01_gravity = 10 * factor;
}

function md_flipped_gravity() {
    md_setgravity(-1);
}
function md_zero_gravity() {
    md_setgravity(0);
}
function md_low_gravity() {
    md_setgravity(0.1);
}
function md_normal_gravity() {
    md_setgravity(1);
}
function md_mega_gravity() {
    md_setgravity(10);
}


// == Visual effects

function md_poop_effect() {
    Poo_show();
}

function md_enable_snow() {
    if (~snow_on) { toggle_snow(); }
}
function md_disable_snow() {
    if (snow_on) { toggle_snow(); }
}
function md_enable_rain() {
    if (~rain_on) { toggle_rain(); }
}
function md_disable_rain() {
    if (rain_on) { toggle_rain(); }
}
