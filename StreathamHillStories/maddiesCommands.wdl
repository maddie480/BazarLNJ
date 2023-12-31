// Note for whoever is reading this: this is C-Script. Like C, but without pointers, or even variable types.
// Reference: http://manual.conitec.net/

// == Console: adapted from another 3D GameStudio game called W.A.R. Soldiers (https://www.myabandonware.com/game/w-a-r-soldiers-ge7)

font md_console_font = "Courier New", 1, 24;

string md_exec_buffer = "#60"; // yes, this means 60 spaces

TEXT md_console_txt {
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

function md_turn_into_random_character() {
    randomize();
    var drawn;
    drawn = random(11);

    // This language doesn't have switch cases!
    if(drawn < 1) { md_turn_into_mainplayer(); return; }
    if(drawn < 2) { md_turn_into_man1(); return; }
    if(drawn < 3) { md_turn_into_woman1(); return; }
    if(drawn < 4) { md_turn_into_woman2(); return; }
    if(drawn < 5) { md_turn_into_woman3(); return; }
    if(drawn < 6) { md_turn_into_fbiagent(); return; }
    if(drawn < 7) { md_turn_into_jessica(); return; }
    if(drawn < 8) { md_turn_into_john(); return; }
    if(drawn < 9) { md_turn_into_simon(); return; }
    if(drawn < 10) { md_turn_into_toni(); return; }
    if(drawn < 11) { md_turn_into_bikeman(); return; }
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
function md_wanted_random() {
    randomize();
    var drawn;
    drawn = random(3);
    if (drawn < 1) { md_wanted_1star(); return; }
    if (drawn < 2) { md_wanted_3stars(); return; }
    if (drawn < 3) { md_wanted_5stars(); return; }
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
function md_random_gravity() {
    randomize();
    var drawn;
    drawn = random(4);
    if (drawn < 1) { md_flipped_gravity(); return; }
    if (drawn < 2) { md_zero_gravity(); return; }
    if (drawn < 3) { md_low_gravity(); return; }
    if (drawn < 4) { md_mega_gravity(); return; }
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


// == Camera effects

var md_upside_down = off;
var md_tiny_screen = off;
var md_flip = off;
var md_clipping_land = off;
var md_mirror_mode = off;
var md_sniper_view = off;
var md_tunnel_vision = off;

VIEW md_modified_view {
    layer = 1;
}
PANEL md_black_backdrop {
    pos_x = 0;
    pos_y = 0;
    size_x = 1920;
    size_y = 1080;
    layer = -1;
    red = 0;
    green = 0;
    blue = 0;
    flags = LIGHT;
}

function md_upside_down_on() {
    md_upside_down = on;
    md_run_modified_view();
}
function md_upside_down_off() {
    md_upside_down = off;
}
function md_tiny_screen_on() {
    md_tiny_screen = on;
    md_run_modified_view();
}
function md_tiny_screen_off() {
    md_tiny_screen = off;
}
function md_flip_on() {
    md_flip = on;
    md_run_modified_view();
}
function md_flip_off() {
    md_flip = off;
}
function md_clipping_land_on() {
    md_clipping_land = on;
    md_run_modified_view();
}
function md_clipping_land_off() {
    md_clipping_land = off;
}
function md_mirror_mode_on() {
    md_mirror_mode = on;
    md_run_modified_view();
}
function md_mirror_mode_off() {
    md_mirror_mode = off;
}
function md_sniper_view_on() {
    md_sniper_view = on;
    md_run_modified_view();
}
function md_sniper_view_off() {
    md_sniper_view = off;
}
function md_tunnel_vision_on() {
    md_tunnel_vision = on;
    md_run_modified_view();
}
function md_tunnel_vision_off() {
    md_tunnel_vision = off;
}

function md_run_modified_view() {
    if (md_modified_view.visible == on) { return; }

    md_modified_view.visible = on;
    md_black_backdrop.visible = on;
    camera.visible = off;

    while (md_upside_down || md_tiny_screen || md_flip || md_clipping_land || md_mirror_mode || md_sniper_view || md_tunnel_vision) {
        proc_late(); // be sure that the camera was updated before us

        // copy the regular camera by default
        md_modified_view.genius = camera.genius;
        md_modified_view.aspect = camera.aspect;
        md_modified_view.arc = camera.arc;
        md_modified_view.fog_start = camera.fog_start;
        md_modified_view.fog_end = camera.fog_end;
        md_modified_view.clip_far = camera.clip_far;
        md_modified_view.clip_near = camera.clip_near;
        md_modified_view.x = camera.x;
        md_modified_view.y = camera.y;
        md_modified_view.z = camera.z;
        md_modified_view.pan = camera.pan;
        md_modified_view.tilt = camera.tilt;
        md_modified_view.roll = camera.roll;

        if (md_upside_down) {
            md_modified_view.roll = (camera.roll - 180) % 360;
        }

        if (md_tiny_screen) {
            md_modified_view.pos_x = screen_size.x * 0.4;
            md_modified_view.pos_y = screen_size.y * 0.4;
            md_modified_view.size_x = screen_size.x * 0.2;
            md_modified_view.size_y = screen_size.y * 0.2;
        } else {
            md_modified_view.pos_x = 0;
            md_modified_view.pos_y = 0;
            md_modified_view.size_x = screen_size.x;
            md_modified_view.size_y = screen_size.y;
        }

        // inspired by the official docs at http://manual.conitec.net/aview-aspect.htm
        if (md_flip) {
            md_modified_view.aspect = cos(total_ticks);
        }

        if (md_clipping_land) {
            md_modified_view.clip_far = 3000;
        }

        // FOV effects: md_tunnel_vision (FOV++) and md_sniper_view (FOV--) cancel each other out
        if (md_tunnel_vision && md_sniper_view == 0) {
            md_modified_view.arc = 170;
        }
        if (md_sniper_view && md_tunnel_vision == 0) {
            md_modified_view.arc = 20;
        }
        if (md_mirror_mode) {
            md_modified_view.arc = -md_modified_view.arc;
        }

        wait(1);
    }

    // all effects are disabled, so get rid of the modified view
    md_modified_view.visible = off;
    md_black_backdrop.visible = off;
    camera.visible = on;
}


// == Radio LNJ

var md_radio_lnj_handle = NULL;

function md_radio_lnj() {
    if (radio_playing || md_radio_lnj_handle != NULL) { return; }

    // mute all other radios... this is how it works, yes!
    media_tune(track_handle, 1,0,0);
    media_tune(track2_handle, 1,0,0);
    media_tune(track3_handle, 1,0,0);
    media_tune(track4_handle, 1,0,0);
    media_tune(track5_handle, 1,0,0);
    media_tune(track6_handle, 1,0,0);
    media_tune(track7_handle, 1,0,0);
    media_tune(track8_handle, 1,0,0);
    media_tune(xmas_handle, 1,0,0);
    media_tune(xmas2_handle, 1,0,0);

    // we are tuning the radio (not playing it, mind you)
    radio_playing = on;
    media_play("tune.mp3", NULL, 300);

    // it's unlikely the whole process will take less than 2 seconds, so just start it now

    while (1) {
        diag("\n[Maddie] Radio requested");

        // request a radio MP3 and wait for it
        file_close(file_open_write("RadioPlz.txt")); // aka file_create
        while (!file_exists("RadioReady.txt")) { wait(1); }

        var file;
        file = file_open_read("RadioReady.txt");
        var time_left;
        time_left = file_var_read(file);
        file_close(file);
        file_delete(file);

        diag("\n[Maddie] Radio received");

        radio_playing = off;

        // then just play it
        md_radio_lnj_handle = media_play("RadioLNJ.mp3", NULL, 150);

        // and wait for it to be finished
        // except you cannot wait for more than 60 seconds, so it's stupid workaround time
        while (time_left > 60000) {
            diag_var("\n[Maddie] Time left: %.0f ms, waiting for 1 minute", time_left);
            wait(-60);
            time_left = time_left - 60000;
        }
        diag_var("\n[Maddie] Time left: %.0f ms, waiting", time_left);
        wait(-time_left / 1000);

        diag("\n[Maddie] Radio stopped due to end of song");
        media_stop(md_radio_lnj_handle);
        md_radio_lnj_handle = NULL;
    }
}

// called from the different parts of the code that mute all radios
function md_on_radio_stop() {
    if (md_radio_lnj_handle != NULL) {
        diag("\n[Maddie] Radio stopped due to game");
        media_stop(md_radio_lnj_handle);
        md_radio_lnj_handle = NULL;
        proc_kill(md_radio_lnj);
    }
}


// == Game speed manipulation

function md_ultra_slow() {
    time_factor = 0.2;
}
function md_ultra_fast() {
    time_factor = 4;
}
function md_normal_speed() {
    time_factor = 1;
}


// == Vanilla game cheats

function md_superman_on() {
    // this is a vanilla copy-paste, from the middle of a 800-line method called PhonePosition().
    // yes, this is the kind of code we're dealing with.
    plBiped01_entity._max_force_x__003 = 197.5;
    plBiped01_entity._max_ang_force_pan__003 = 11.0;
    plBiped01_entity._jump_height__003 = 675.0;
}

function md_superman_off() {
    plBiped01_entity._max_force_x__003 = 12.5;
    plBiped01_entity._max_ang_force_pan__003 = 10.0;
    plBiped01_entity._jump_height__003 = 75.0;
}

function md_spawn_fighter_jet() {
    // yes, this *really* spawns a fighter jet that is absolutely not a helicopter.
    enable_copter();
}


// == Murquède

var md_murk_cooldown = off;

function md_murk() {
    plBiped01_entity._health__003 = 0;
}


// == Change weapons (verbatim copypastes from vanilla)

var md_bat_entity = NULL;

function md_switch_weapon_common() {
    have_bat = 0;
    have_flamethrower = 0;
    have_grenade = 0;

    GunHUD_pan.visible = off;
    FlameHUD_pan.visible = off;
    BatHUD_pan.visible = off;
    BombHUD_pan.visible = off;

    plBipedWeap00_current_weapon = 0;

    if (md_bat_entity != NULL) { ent_remove(md_bat_entity); }
}

function md_switch_to_gun() {
    md_switch_weapon_common();
    plBipedWeap00_current_weapon = 3;
    Gun_show();
    media_play("gunselect.mp3", null, 380);
}

function md_switch_to_grenade() { // aka bomb
    md_switch_weapon_common();
    have_grenade = 1;
    had_grenade = 1;
    if (bomb_ammo <= 80) { bomb_ammo += 20; }
    Bomb_Show();
    media_play("grenade_pickup.mp3", null, 100);
}

function md_switch_to_flamethrower() {
    md_switch_weapon_common();
    have_flamethrower = 1;
    had_flamethrower = 1;
    flame_ammo += 200;
    flame_show();
    media_play("grenade_pickup.mp3", null, 100);
}

function md_switch_to_bat() {
    md_switch_weapon_common();
    have_bat = 1;
    had_bat = 1;
    md_bat_entity = ent_create("bat.mdl", nullvector, swingbat);
    bat_show();
    media_play("grenade_pickup.mp3", null, 100);
}


// == Explosive pedestrians

var md_exploding_pedestrians = off;

TEXT md_exploding_pedestrians_notice {
    string = "Exploding Pedestrians Active";
    blue = 0;
    green = 0;
    red = 255;
    pos_x = 10;
    pos_y = 50;
    flags = SHADOW | OUTLINE;
    font = md_console_font;
}

function md_exploding_pedestrians_on() {
    md_exploding_pedestrians = on;
    md_exploding_pedestrians_notice.visible = on;
}
function md_exploding_pedestrians_off() {
    md_exploding_pedestrians = off;
    md_exploding_pedestrians_notice.visible = off;
}

function md_on_pedestrian_hit() {
    // note: this is called from pedestrian methods, so "me" is the pedestrian in question
    if (md_exploding_pedestrians == off) { return; }

    // this is the code for grenades, kind of.
    ent_create(NULL, my.x, firstex_emitter);
    ent_create("ball.mdl", my.x, physix_bomb);
    wait(-0.1); //0.02
    proc_kill(firstex_emitter);

    if (plSelect_curr_ent == NULL) { return; }

    // the game doesn't come with, like, an actual physical explosion blow effect.
    // so, we need to do it ourselves!
    // initialize it with the vector going from the bomb to whatever entity the player is controlling.
    var explosion_force;
    explosion_force = vector(0, 0, 0);
    vec_set(explosion_force, my.x);
    vec_sub(explosion_force, plSelect_curr_ent.x);

    // the strength of the push depends on the distance.
    var distance_factor;
    distance_factor = vec_length(explosion_force);
    distance_factor = 1 - (distance_factor / 2000);

    diag_var("\n[Maddie] Distance vector: (%.3f, ", explosion_force.x);
    diag_var("%.3f, ", explosion_force.y);
    diag_var("%.3f), ", explosion_force.z);
    diag_var("distance factor: %.3f", distance_factor);

    // give the right force to the vector, and apply it!
    if (distance_factor <= 0) { return; }

    if (plSelect_curr_ent == plBiped01_entity) {
        vec_normalize(explosion_force, distance_factor * 1000);
        plBiped01_entity._gid01_vel_x += explosion_force.x;
        plBiped01_entity._gid01_vel_y += explosion_force.y;
        plBiped01_entity._gid01_vel_z += explosion_force.z;
    } else {
        vec_normalize(explosion_force, distance_factor * 50000);
        phent_addvelcentral(plSelect_curr_ent, explosion_force);
    }
}


// == CHIRAC EN 3D

BMAP md_chirac = <chirac_en_3d.png>;

PANEL md_chirac_container {
    bmap = md_chirac;
}

var md_chirac_ongoing = off;

function md_chirac_en_3d() {
    md_chirac_ongoing = on;

    md_chirac_container.pos_x = (screen_size.x - 500) / 2;
    md_chirac_container.pos_y = screen_size.y;
    md_chirac_container.visible = on;

    while (md_chirac_container.pos_y > screen_size.y - 620) {
        wait(1);
        md_chirac_container.pos_y -= 800 * time_step / 16;
    }

    wait(-5);

    while (md_chirac_container.pos_y < screen_size.y) {
        wait(1);
        md_chirac_container.pos_y += 800 * time_step / 16;
    }

    md_chirac_container.visible = off;

    md_chirac_ongoing = off;
}


// == Trigger the different effects based on IDs (Chat Control + Chaos Mode)

var md_is_in_place = off;
var md_has_modified_gravity = off;

function md_trigger_effect(id) {
    if (id == 1) { md_spawn_zombie(); return; }
    if (id == 2) { md_spawn_clio(); return; }
    if (id == 3) { md_spawn_r8(); return; }
    if (id == 4) { md_turn_into_random_character(); return; }
    if (id == 5) {
        if (md_is_in_place) { return; }
        md_is_in_place = on;
        md_enter_bowling();
        wait(-30);
        md_exit_bowling();
        md_is_in_place = off;
        return;
    }
    if (id == 6) {
        if (md_is_in_place) { return; }
        md_is_in_place = on;
        md_enter_caesars();
        wait(-30);
        md_exit_caesars();
        md_is_in_place = off;
        return;
    }
    if (id == 7) { md_wanted_random(); return; }
    if (id == 8) {
        if (md_has_modified_gravity) { return; }
        md_has_modified_gravity = on;
        md_random_gravity();
        wait(-30);
        md_normal_gravity();
        md_has_modified_gravity = off;
        return;
    }
    if (id == 9) { md_poop_effect(); return; }
    if (id == 10) {
        if (snow_on) { return; }
        md_enable_snow();
        wait(-30);
        md_disable_snow();
        return;
    }
    if (id == 11) {
        if (rain_on) { return; }
        md_enable_rain();
        wait(-30);
        md_disable_rain();
        return;
    }
    if (id == 12) {
        if (md_upside_down) { return; }
        md_upside_down_on();
        wait(-30);
        md_upside_down_off();
        return;
    }
    if (id == 13) {
        if (md_tiny_screen) { return; }
        md_tiny_screen_on();
        wait(-30);
        md_tiny_screen_off();
        return;
    }
    if (id == 14) {
        if (md_flip) { return; }
        md_flip_on();
        wait(-30);
        md_flip_off();
        return;
    }
    if (id == 15) { md_radio_lnj(); return; }
    if (id == 16) {
        if (time_factor != 1) { return; }
        md_ultra_slow();
        wait(-30);
        md_normal_speed();
        return;
    }
    if (id == 17) {
        if (time_factor != 1) { return; }
        md_ultra_fast();
        wait(-30);
        md_normal_speed();
        return;
    }
    if (id == 18) {
        if (plBiped01_entity._max_force_x__003 == 197.5) { return; }
        md_superman_on();
        wait(-30);
        md_superman_off();
        return;
    }
    if (id == 19) { md_spawn_fighter_jet(); return; }
    if (id == 20) {
        if (md_clipping_land) { return; }
        md_clipping_land_on();
        wait(-30);
        md_clipping_land_off();
        return;
    }
    if (id == 21) {
        if (md_mirror_mode) { return; }
        md_mirror_mode_on();
        wait(-30);
        md_mirror_mode_off();
        return;
    }
    if (id == 22) {
        if (md_sniper_view) { return; }
        md_sniper_view_on();
        wait(-30);
        md_sniper_view_off();
        return;
    }
    if (id == 23) {
        if (md_tunnel_vision) { return; }
        md_tunnel_vision_on();
        wait(-30);
        md_tunnel_vision_off();
        return;
    }
    if (id == 24) {
        if (md_murk_cooldown) { return; }
        md_murk_cooldown = on;
        md_murk();
        // 2 minutes, except GameStudio won't let you wait more than 1
        wait(-60);
        wait(-60);
        md_murk_cooldown = off;
        return;
    }
    if (id == 25) { md_switch_to_gun(); return; }
    if (id == 26) { md_switch_to_grenade(); return; }
    if (id == 27) { md_switch_to_flamethrower(); return; }
    if (id == 28) { md_switch_to_bat(); return; }
    if (id == 29) {
        if (md_exploding_pedestrians) { return; }
        md_exploding_pedestrians_on();
        wait(-30);
        md_exploding_pedestrians_off();
        return;
    }
    if (id == 30) { md_chirac_en_3d(); return; }
    diag_var("\n[Maddie] Asked to trigger action %.0f that does not exist!", id);
}

starter md_chat_control() {
    wait(-30);

    while (1) {
        if (file_exists("ChatControl.txt")) {
            var file_handle;
            file_handle = file_open_read("ChatControl.txt");
            var command;
            command = file_var_read(file_handle);
            file_close(file_handle);

            diag_var("\n[Maddie] Received command: %.0f", command);

            // 0 is just a keepalive
            if (command != 0) {
                md_trigger_effect(command);
            }

            file_delete("ChatControl.txt");
        }

        wait(-1);
    }
}
