function md_handle_command_line_params() {
    var forcedMode = "";
    if (str_stri(command_str, "--christmas")){
        Halloween = 0;
        Christmas = 1;
        forcedMode = " --christmas";
    }
    if (str_stri(command_str, "--halloween")){
        Halloween = 1;
        Christmas = 0;
        forcedMode = " --halloween";
    }
    if (str_stri(command_str, "--normal")){
        Halloween = 0;
        Christmas = 0;
        forcedMode = " --normal";
    }

    diag_var("\n[Maddie] Halloween = %.0f", Halloween);
    diag_var(", Christmas = %.0f", Christmas);
    diag(", forcedMode = \"");
    diag(forcedMode);
    diag("\"");

    return (forcedMode);
}

function md_get_command_to_run_main_game(forcedMode) {
    var command;
    command = str_create("Main\\SHSprogram.exe /b -nx60 -diag -nc");
    str_cat(command, forcedMode);
    if(Multiplayer == 1) {
        str_cat(command, " -cl");
    }
    diag("\n[Maddie] Running command: ");
    diag(command);
    return (command);
}