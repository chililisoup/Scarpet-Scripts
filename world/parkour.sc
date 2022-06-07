// Create, play, and compete with this complete parkour package

//Config thingies for u :) vvv
global_gamemode_toggle = {
    'survival' -> true,
    'creative' -> false,
    'adventure'-> true,
    'spectator'-> false
};
global_feature_toggle = {
    'checkpoint_teleport' -> true,
    'checkpoint_highlight' -> true
};

//Config thingies not for u :( vvv
__config() -> {
    'commands' -> {
        '' -> 'list_parks',
        'list' -> 'list_parks',
        'help' -> 'help',
        'create <name>' -> 'create_park',
        'delete <park>' -> ['delete_park', false],
        'del <park>' -> ['delete_park', false],
        'delete <park> force' -> ['delete_park', true],
        'del <park> force' -> ['delete_park', true],
        'reset_times <park>' -> ['reset_times', false],
        'reset_times <park> force' -> ['reset_times', true],
        'modify' -> ['modify_park', false],
        'modify <park>' -> 'modify_park',
        'modify <park> <park_setting> <bool>' -> 'modify_park_setting',
        'mod' -> ['modify_park', false],
        'mod <park>' -> 'modify_park',
        'mod <park> <park_setting> <bool>' -> 'modify_park_setting',
        'edit' -> ['modify_park', false],
        'edit <park>' -> 'modify_park',
        'points' -> 'point_menu',
        'highlight_point start' -> ['highlight_point', 'start'],
        'highlight_point finish' -> ['highlight_point', 'finish'],
        'highlight_point checkpoint <index>' -> 'highlight_point',
        'delete_point start' -> ['modify_park_setting', false, 'start', []],
        'delete_point finish' -> ['modify_park_setting', false, 'finish', []],
        'delete_point checkpoint <point>' -> 'delete_checkpoint',
        'select <park>' -> ['select_park', true],
        'sel <park>' -> ['select_park', true],
        'deselect' -> ['select_park', false, false],
        'desel' -> ['select_park', false, false],
        'rename <park> <name>' -> 'rename_park',
        'preferences' -> 'preferences',
        'preferences reset' -> ['reset_preferences', true],
        'preferences set bool <bool_setting> <bool>' -> ['set_preference', false],
        'preferences set bool <bool_setting> <bool> <alt_return>' -> 'set_preference',
        'preferences set block <block_setting> <block>' -> ['set_preference', false],
        'prefs' -> 'preferences',
        'prefs reset' -> ['reset_preferences', true],
        'prefs set bool <bool_setting> <bool>' -> ['set_preference', false],
        'prefs set bool <bool_setting> <bool> <alt_return>' -> 'set_preference',
        'prefs set block <block_setting> <block>' -> ['set_preference', false],
        'settings' -> 'preferences',
        'settings reset' -> ['reset_preferences', true],
        'settings set bool <bool_setting> <bool>' -> ['set_preference', false],
        'settings set bool <bool_setting> <bool> <alt_return>' -> 'set_preference',
        'settings set block <block_setting> <block>' -> ['set_preference', false],
        'cancel' -> 'cancel_parkour',
        'leaderboard' -> ['leaderboard', false],
        'leaderboard <park>' -> 'leaderboard',
        'leaderboard <park> <uuid>' -> 'leaderboard_display',
        'checkpoint' -> ['retry', 'checkpoint'],
        'cp' -> ['retry', 'checkpoint'],
        'retry' -> ['retry', 'checkpoint'],
        'retry checkpoint' -> ['retry', 'checkpoint'],
        'retry cp' -> ['retry', 'checkpoint'],
        'retry start' -> ['retry', 'start'],
        'restart' -> ['retry', 'start']
    },
    'arguments' -> {
        'park' -> {
            'type' -> 'string',
            'suggest' -> []
        },
        'name' -> {
            'type' -> 'string',
            'suggest' -> []
        },
        'bool_setting' -> {
            'type' -> 'term',
            'suggest' -> ['easy_place'],
            'options' -> ['easy_place']
        },
        'block_setting' -> {
            'type' -> 'term',
            'suggest' -> [
                'start',
                'checkpoint',
                'finish'
            ],
            'options' -> [
                'start',
                'checkpoint',
                'finish'
            ]
        },
        'park_setting' -> {
            'type' -> 'term',
            'suggest' -> [
                'swimming',
                'elytra',
                'potions',
                'pearls',
                'retry'
            ],
            'options' -> [
                'swimming',
                'elytra',
                'potions',
                'pearls',
                'retry'
            ]
        },
        'index' -> {
            'type' -> 'int',
            'min' -> 0,
            'suggest' -> []
        },
        'block' -> {'type' -> 'block'},
        'bool' -> {'type' -> 'bool'},
        'alt_return' -> {'type' -> 'bool'},
        'point' -> {'type' -> 'pos'}
    }
};

help() -> (
    print(format('w [', 'd Parkour', 'w ] ', 'y Help Menu'));
    print(format('y Create a park with ', 'w /parkour create <name>'));
    print(format('y View your parks with ', 'w /parkour or /parkour list'));
    print(format('y You must be out of build mode to start a park'));
    print(format('w /parkour leaderboard ', 'y will show parks with leaderboard stats'));
    print(format('w /parkour leaderboard <name> ', 'y is the same but filtered'));
    print(format('y You can find mostly everything you need through the chat GUIs'));
);

__on_player_connects(player) -> (
    active_park = read_file('park_active_' + player ~ 'uuid', 'json');
    if (active_park, print(format('w [', 'd Parkour', 'w ] ', 'y Parkour Failed! You left the game!')));
    delete_file('park_active_' + player ~ 'uuid', 'json');
    entity_event(player, 'on_move', _(plr, vel, pos1, pos2) -> player_move_handler(plr, pos1));
    select_park(false, false);
);

find_park(name) -> (
    json = read_file('park_list_' + player() ~ 'uuid', 'json');
    park = null;
    if (json, for(json, if (lower(_:'name') == lower(name), park = _)));
    return(park);
);

player_move_handler(plr, pos) -> (
    if (!global_gamemode_toggle:(plr ~ 'gamemode'),
        if(read_file('park_active_' + plr ~ 'uuid', 'json'),
            print(plr, format('w [', 'd Parkour', 'w ] ', 'y Parkour Failed! Banned gamemode!'));
            delete_file('park_active_' + plr ~ 'uuid', 'json');
        );
        exit();
    );

    if (plr ~ 'flying',
        if(read_file('park_active_' + plr ~ 'uuid', 'json'),
            print(plr, format('w [', 'd Parkour', 'w ] ', 'y Parkour Failed! No flying!'));
            delete_file('park_active_' + plr ~ 'uuid', 'json');
        );
        exit();
    );

    settings = read_file('preferences_' + plr ~ 'uuid', 'json');
    if (settings:'easy_place', exit());

    parks = read_file('global_park_list', 'json');

    for(parks, if (
        loaded_status(_:'start') == 3 &&
        plr ~ 'dimension' == _:'dimension' &&
        pos(block(pos)) == _:'start',

        start_parkour(_);
        exit();
    ));

    active_park = read_file('park_active_' + plr ~ 'uuid', 'json');
    if (!active_park, exit());
    if (plr ~ 'dimension' != active_park:'dimension', exit());

    if (plr ~ 'pose' == 'fall_flying' && !active_park:'elytra',
        print(plr, format('w [', 'd Parkour', 'w ] ', 'y Parkour Failed! No elytras!'));
        delete_file('park_active_' + plr ~ 'uuid', 'json');
    );
    if(plr ~ 'effect' && !active_park:'potions',
        print(plr, format('w [', 'd Parkour', 'w ] ', 'y Parkour Failed! You have an effect!'));
        delete_file('park_active_' + plr ~ 'uuid', 'json');
    );
    if(((plr ~ 'holds'):0 == 'ender_pearl' || query(plr, 'holds', 'offhand'):0 == 'ender_pearl') && !active_park:'pearls',
        print(plr, format('w [', 'd Parkour', 'w ] ', 'y Parkour Failed! No ender pearls!'));
        delete_file('park_active_' + plr ~ 'uuid', 'json');
    );
    if (block(pos:0, pos:1+0.5, pos:2) == 'water' && !active_park:'swimming',
        if (global_feature_toggle:'checkpoint_teleport' && active_park:'retry',
            retry('checkpoint'),
            print(plr, format('w [', 'd Parkour', 'w ] ', 'y Parkour Failed! No swimming!'));
            delete_file('park_active_' + plr ~ 'uuid', 'json');
        );
    );

    for (active_park:'checkpoints',
        if (pos(block(pos)) == _,

            active_park:'warning' = false;
            active_park:'last_checkpoint' = _;
            delete(active_park:'checkpoints', _i);

            print(plr, format(
                'w [', 'd Parkour', 'w ] ', 'y Reached checkpoint ',
                'wb ' + (active_park:'checkpoint_count' - length(active_park:'checkpoints')) + '/' + active_park:'checkpoint_count',
                'y  in ', 'wb ' + round(time() - active_park:'start_time')/1000, 'y  seconds')
            );

            write_file('park_active_' + plr ~ 'uuid', 'json', active_park);
            break();
        );
    );

    if (pos(block(pos)) == active_park:'finish',

        if(!length(active_park:'checkpoints'),
            finish_parkour(),

            if (!active_park:'warning',
                print(plr, format('w [', 'd Parkour', 'w ] ', 'y You must hit each checkpoint before heading to the end!'));
                active_park:'warning' = true;
                write_file('park_active_' + plr ~ 'uuid', 'json', active_park);
            );
        );
    );
);

start_parkour(park) -> (
    active_park = read_file('park_active_' + player() ~ 'uuid', 'json');

    if (active_park:'owner' == park:'owner' && active_park:'name' == park:'name', exit());

    if (active_park, print(player(), format('w [', 'd Parkour', 'w ] ', 'y Parkour Failed! You started a new one!')));
    print(player(), format('w [', 'd Parkour', 'w ] ', 'y Now starting ', 'wb ' + park:'name'));

    for (read_file('park_list_' + park:'owner', 'json'),
        if (_:'name' == park:'name', active_park = _; break());
    );

    if (!length(active_park:'start') || !length(active_park:'finish'),
        print(player(), format('w [', 'd Parkour', 'w ] ', 'y Parkour Cancelled! Missing start/finish!'));
        delete_file('park_active_' + player() ~ 'uuid', 'json');
        exit();
    );
    active_park:'owner' = park:'owner';
    active_park:'start_time' = time();
    write_file('park_active_' + player() ~ 'uuid', 'json', active_park);

    exit();
);

finish_parkour() -> (
    active_park = read_file('park_active_' + player() ~ 'uuid', 'json');
    leaderboard = read_file('global_park_leaderboard', 'json');
    if (!leaderboard, leaderboard = []);

    time = time() - active_park:'start_time';
    old_time = false;
    found = false;

    for (leaderboard, if (active_park:'name' == _:'name' && active_park:'owner' == _:'owner',
        found = true;
        old_time = _:'times':(player() ~ 'uuid'):0;
        leaderboard:_i:'times':(player() ~ 'uuid') = [];
        if (!old_time || time < old_time,
            leaderboard:_i:'times':(player() ~ 'uuid'):0 = time,
            leaderboard:_i:'times':(player() ~ 'uuid'):0 = old_time;
        );
        leaderboard:_i:'times':(player() ~ 'uuid'):1 = player();
        break();
    ));

    if (!found,
        leaderboard += {
            'owner' -> active_park:'owner',
            'name' -> active_park:'name',
            'times' -> {player() ~ 'uuid' -> [time, player()]}
        };
    );

    if (!old_time || time < old_time,
        print(player(), format(
            'w [', 'd Parkour', 'w ] ', 'd [✯] ', '^w Show Leaderboard', '!/parkour leaderboard "' + active_park:'name' + '" ' + active_park:'owner', //♚♔★☆✮✯
            'wb ' + active_park:'name', 'y  finished in ', 'wb ' + round(time)/1000,
            'y  seconds! New best!'
        )),
        print(player(), format(
            'w [', 'd Parkour', 'w ] ', 'd [✯] ', '^w Show Leaderboard', '!/parkour leaderboard "' + active_park:'name' + '" ' + active_park:'owner', //♚♔★☆✮✯
            'wb ' + active_park:'name', 'y  finished in ', 'wb ' + round(time)/1000,
            'y  seconds! Best: ', 'wb ' + round(old_time)/1000, 'y  seconds'
        ));
    );

    write_file('global_park_leaderboard', 'json', leaderboard);
    delete_file('park_active_' + player() ~ 'uuid', 'json');
);

retry(point) -> (
    if (!global_feature_toggle:'checkpoint_teleport', exit(print(format('w [', 'd Parkour', 'w ] ', 'y Feature disabled.'))));
    active_park = read_file('park_active_' + player() ~ 'uuid', 'json');
    if (!active_park, exit(print(format('w [', 'd Parkour', 'w ] ', 'y You are not currently doing a parkour!'))));
    
    if (point == 'checkpoint',
        if (!active_park:'retry', exit(print(format('w [', 'd Parkour', 'w ] ', 'y Not allowed on this park!'))));
        point = active_park:'last_checkpoint';
        if (point,
            modify(player(), 'pos', point:0 + 0.5, point:1, point:2 + 0.5);
            exit();
        );
        point = 'start';
    );
    
    if (point == 'start',
        modify(player(), 'pos', active_park:'start':0 + 0.5, active_park:'start':1, active_park:'start':2 + 0.5);
        cancel_parkour();
        exit();
    );
    
    exit(print(format('w [', 'd Parkour', 'w ] ', 'y Unknown point ', 'wb ' + point)));
);

cancel_parkour() -> (
    active_park = read_file('park_active_' + player() ~ 'uuid', 'json');
    if (!active_park, exit(print(format('w [', 'd Parkour', 'w ] ', 'y You are not currently doing a parkour!'))));
    print(format('w [', 'd Parkour', 'w ] ', 'y Parkour Cancelled!'));
    delete_file('park_active_' + player() ~ 'uuid', 'json');
);

leaderboard(park) -> (
    json = read_file('global_park_list', 'json');
    found_parks = [];
    if (park,
        for (json, if (lower(_:'name') == lower(park), found_parks += _)),
        found_parks = json;
    );
    if (!length(found_parks), exit(print(format('w [', 'd Parkour', 'w ] ', 'y No such park ', 'wb ' + park))));

    print(format('db Found parks', 'w :'));
    for (found_parks,
        print(format(
            'd  [✯] ', '^w Show Leaderboard', '!/parkour leaderboard "' + _:'name' + '" ' + _:'owner', //♚♔★☆✮✯
            'y ' + _:'name', 'wb  - ', 'y ' + _:'owner_name'
        ));
    );
);

leaderboard_display(park, uuid) -> (
    json = read_file('global_park_leaderboard', 'json');
    leaderboard = null;
    for (json, if (lower(_:'name') == lower(park) && _:'owner' == uuid,
        leaderboard = _;
        break();
    ));

    if (!leaderboard, exit(print(format('w [', 'd Parkour', 'w ] ', 'y No stats found!'))));

    sorted_board = [];
    for (values(leaderboard:'times'), //Slow af sort but idgaf
        if (!length(sorted_board), sorted_board += _; continue());
        i = 0;
        time = _:2;
        for (sorted_board,
            if (_:2 < time && _i >= i, i = _i + 1);
        );
        put(sorted_board, i, _, 'insert');
    );

    print(format('w [', 'd ' + park, 'w ] ', 'y Park Leaderboard'));
    for (sorted_board,
        print(format('w  [' + (_i + 1) + '] ', 'y ' + round(_:0)/1000 + 's', 'wb  - ', 'y ' + _:1));
    );
);

reset_preferences(display) -> (
    settings = {
        'easy_place' -> false,
        'start' -> 'light_weighted_pressure_plate',
        'checkpoint' -> 'heavy_weighted_pressure_plate',
        'finish' -> 'light_weighted_pressure_plate'
    };
    write_file('preferences_' + player() ~ 'uuid', 'json', settings);
    if (display, preferences());
    return(settings);
);

set_preference(setting, value, alt_return) -> (
    settings = read_file('preferences_' + player() ~ 'uuid', 'json');
    if (!settings, settings = reset_preferences(false));

    settings:setting = value;
    write_file('preferences_' + player() ~ 'uuid', 'json', settings);

    if (setting == 'easy_place' && value,
        active_park = read_file('park_active_' + player() ~ 'uuid', 'json');
        if (active_park, print(format('w [', 'd Parkour', 'w ] ', 'y Parkour Failed! You entered build mode!')));
        delete_file('park_active_' + player() ~ 'uuid', 'json');
    );

    if (alt_return, modify_park(false); exit());
    preferences();
);

preferences() -> (
    settings = read_file('preferences_' + player() ~ 'uuid', 'json');
    if (!settings, settings = reset_preferences(false));

    print(format('w [', 'd Parkour', 'w ] ', 'y Preferences Menu ', 'l [←]', '^w Park Menu', '!/parkour modify'));
    if (settings:'easy_place',
        print(format('w  [', 'y Build Mode', '^w Toggle Build Mode', '!/parkour preferences set bool easy_place false', 'w ] ', 'lb On')),
        print(format('w  [', 'y Build Mode', '^w Toggle Build Mode', '!/parkour preferences set bool easy_place true', 'w ] ', 'rb Off'));
    );
    print(format('w  [', 'l Start Block', '^w Change Start Block', '?/parkour preferences set block start ', 'w ] ', 'g ' + settings:'start'));
    print(format('w  [', 'c Checkpoint Block', '^w Change Checkpoint Block', '?/parkour preferences set block checkpoint ', 'w ] ', 'g ' + settings:'checkpoint'));
    print(format('w  [', 'm Finish Block', '^w Change Finish Block', '?/parkour preferences set block finish ', 'w ] ', 'g ' + settings:'finish'));
    print(format('w  [', 'r Defaults', '^w Reset to Default Settings', '!/parkour preferences reset', 'w ] '));
    
    exit();
);

list_parks() -> (
    json = read_file('park_list_' + player() ~ 'uuid', 'json');
    if (!json, print(format('w [', 'd Parkour', 'w ] ', 'y No parks found!')),
        print(format('db Your parks', 'w :'));
        for(json,
        prefix = 'y ';
        if (_:'dimension' != player() ~ 'dimension', prefix = 'g ');
        print(format(
            'r  [✖] ', '^w Delete park', '!/parkour delete "' + _:'name' + '"',
            'w [⚒] ', '^w Edit park', '!/parkour modify "' + _:'name' + '"',
            prefix + _:'name'
        )));
    );
);

select_park(name, enter) -> (
    if (!enter,
        if (!name, name = read_file('park_selected_' + player() ~ 'uuid', 'raw'):0);
        delete_file('park_selected_' + player() ~ 'uuid', 'raw');
        if (name, print(format('w [', 'd Parkour', 'w ] ', 'yi You are no longer editing ', 'wbi ' + name)));
        return();
    );

    print(format('w [', 'd Parkour', 'w ] ', 'yi You are now in edit mode for ', 'wbi ' + name));
    delete_file('park_selected_' + player() ~ 'uuid', 'raw');
    write_file('park_selected_' + player() ~ 'uuid', 'raw', name);
);

__on_player_places_block(player, item_tuple, hand, block) -> (
    park = read_file('park_selected_' + player() ~ 'uuid', 'raw'):0;
    if (!park, exit());

    park_json = find_park(park);
    if (!park_json, select_park(false, false); exit());

    if (park_json:'dimension' != player() ~ 'dimension', exit());

    settings = read_file('preferences_' + player() ~ 'uuid', 'json');
    if (!settings, settings = reset_preferences(false));
    if (!settings:'easy_place', exit());

    if (block == settings:'start',
        if (!length(park_json:'start'),
            park_json:'start' = pos(block);
            print(format(
                'w [', 'd Parkour ', 'l Build Mode', 'w ] ', 'y Set start point at ',
                'wb ' + pos(block):0 + ', ' + pos(block):1 + ', ' + pos(block):2)
            );
            send_park_json(park_json),
            if (block == settings:'finish' && !length(park_json:'finish'),
                park_json:'finish' = pos(block);
                print(format(
                    'w [', 'd Parkour ', 'l Build Mode', 'w ] ', 'y Set end point at ',
                    'wb ' + pos(block):0 + ', ' + pos(block):1 + ', ' + pos(block):2)
                );
                send_park_json(park_json);
                exit();
            );
        );
    );

    if (block == settings:'finish' && !length(park_json:'finish'),
        park_json:'finish' = pos(block);
        print(format(
            'w [', 'd Parkour ', 'l Build Mode', 'w ] ', 'y Set end point at ',
            'wb ' + pos(block):0 + ', ' + pos(block):1 + ', ' + pos(block):2)
        );
        send_park_json(park_json);
        exit();
    );

    if (block == settings:'checkpoint',
        for(park_json:'checkpoints', if (_ == pos(block), exit()));
        park_json:'checkpoints' += pos(block);
        print(format(
            'w [', 'd Parkour ', 'l Build Mode', 'w ] ', 'y Added checkpoint at ',
            'wb ' + pos(block):0 + ', ' + pos(block):1 + ', ' + pos(block):2)
        );
        send_park_json(park_json);
    );

    exit();
);

__on_player_breaks_block(player, block) -> (
    park = read_file('park_selected_' + player() ~ 'uuid', 'raw'):0;
    if (!park, exit());

    park_json = find_park(park);
    if (!park_json, select_park(false, false); exit());

    if (park_json:'dimension' != player() ~ 'dimension', exit());

    settings = read_file('preferences_' + player() ~ 'uuid', 'json');
    if (!settings, settings = reset_preferences(false));
    if (!settings:'easy_place', exit());

    if (pos(block) == park_json:'start',
        park_json:'start' = [];
        print(format('w [', 'd Parkour ', 'l Build Mode', 'w ] ', 'y Removed start point'));
        send_park_json(park_json);
        exit();
    );

    if (pos(block) == park_json:'finish',
        park_json:'finish' = [];
        print(format('w [', 'd Parkour ', 'l Build Mode', 'w ] ', 'y Removed end point'));
        send_park_json(park_json);
        exit();
    );

    for(park_json:'checkpoints', if (_ == pos(block),
        delete(park_json:'checkpoints':_i);
        print(format(
            'w [', 'd Parkour ', 'l Build Mode', 'w ] ', 'y Removed checkpoint at ',
            'wb ' + pos(block):0 + ', ' + pos(block):1 + ', ' + pos(block):2)
        );
        send_park_json(park_json);
    ));

    exit();
);

update_global_parks(park_json, plr) -> (
    uuid = plr ~ 'uuid';
    fixed_json = {
        'start' -> park_json:'start',
        'name' -> park_json:'name',
        'dimension' -> park_json:'dimension',
        'owner' -> uuid,
        'owner_name' -> plr
    };

    json = read_file('global_park_list', 'json');
    if (!json, json = []);

    found = false;
    for(json, if (_:'owner' == uuid && lower(_:'name') == lower(park_json:'name'),
        json:_i = fixed_json;
        found = true;
        if (!length(park_json:'start'), delete(json:_i));
    ));

    if (!found, json += fixed_json);

    write_file('global_park_list', 'json', json);
);

send_park_json(park_json) -> (
    park_json:'checkpoint_count' = length(park_json:'checkpoints');
    json = read_file('park_list_' + player() ~ 'uuid', 'json');
    found = false;
    if (json, for(json, if (lower(_:'name') == lower(park_json:'name'),
        if (_:'start' != park_json:'start', update_global_parks(park_json, player()));
        json:_i = park_json;
        found = true;
    )));
    if (!found, exit(print(format('w [', 'd Parkour', 'w ] ', 'y No such park ', 'wb ' + park_json:'name'))));
    write_file('park_list_' + player() ~ 'uuid', 'json', json);
);

modify_park(name) -> (
    if (!name,
        name = read_file('park_selected_' + player() ~ 'uuid', 'raw'):0;
        if (!find_park(name), select_park(false, false); name = false);
        if (!name, exit(print(format('w [', 'd Parkour', 'w ] ', 'y Please choose a park to modify!'))));
    );
    if (!find_park(name), exit(print(format('w [', 'd Parkour', 'w ] ', 'y No such park ', 'wb ' + name))));
    

    park = find_park(name);

    settings = read_file('preferences_' + player() ~ 'uuid', 'json');
    if (!settings, settings = reset_preferences(false));

    if (name != read_file('park_selected_' + player() ~ 'uuid', 'raw'):0, select_park(name, true));

    print(format('w [', 'd ' + name, 'w ] ', 'y Park Editor Menu ', 'l [←]', '^w Park List', '!/parkour list'));
    if (settings:'easy_place',
        print(format(
            'w  [', 'y Build Mode', '^w Toggle Build Mode', '!/parkour preferences set bool easy_place false true', 'w ] ',
            'w [⚒] ', '^w Preferences', '!/parkour preferences', 'lb On')
        ),
        print(format(
            'w  [', 'y Build Mode', '^w Toggle Build Mode', '!/parkour preferences set bool easy_place true true', 'w ] ',
            'w [⚒] ', '^w Preferences', '!/parkour preferences', 'rb Off')
        );
    );
    print(format('w  [', 'l Edit Points', '^w Edit Points', '!/parkour points', 'w ]'));
    print(format('w  [', 'c Rename Park', '^w Rename Park', '?/parkour rename "' + name + '" ', 'w ]'));
    print(format(
        'w  [', 'm Reset Times', '^w Reset Times', '!/parkour reset_times "' + name + '" ', 'w ]',
        'w  [', 'm Delete Park', '^w Delete Park', '!/parkour delete "' + name + '" ', 'w ]'
    ));

    swim_suff = ['r ', true];
    elyt_suff = ['r ', true];
    pots_suff = ['r ', true];
    perl_suff = ['r ', true];
    chek_suff = ['r ', true];
    if (park:'swimming', swim_suff = ['l ', false]);
    if (park:'elytra', elyt_suff = ['l ', false]);
    if (park:'potions', pots_suff = ['l ', false]);
    if (park:'pearls', perl_suff = ['l ', false]);
    if (park:'retry', chek_suff = ['l ', false]);
    print(format(
        'w  [', swim_suff:0 + 'Swimming', '^w Toggle Swimming', '!/parkour modify "' + name + '" swimming ' + swim_suff:1, 'w ]',
        'w  [', elyt_suff:0 + 'Elytra', '^w Toggle Elytra', '!/parkour modify "' + name + '" elytra ' + elyt_suff:1, 'w ]',
        'w  [', pots_suff:0 + 'Potions', '^w Toggle Potions', '!/parkour modify "' + name + '" potions ' + pots_suff:1,  'w ]'
    ));
    print(format(
        'w  [', perl_suff:0 + 'Pearls', '^w Toggle Pearls', '!/parkour modify "' + name + '" pearls ' + perl_suff:1,  'w ]',
        'w  [', chek_suff:0 + 'Checkpoint Retry', '^w Toggle Checkpoint Retry', '!/parkour modify "' + name + '" retry ' + chek_suff:1,  'w ]'
    ));

    print(format('w  [', 'g Exit Editor', '^w Exit Editor', '!/parkour deselect', 'w ]'));

    exit();
);

modify_park_setting(name, setting, value) -> (
    json = read_file('park_list_' + player() ~ 'uuid', 'json');
    if (!name, name = read_file('park_selected_' + player() ~ 'uuid', 'raw'):0);
    park = find_park(name);
    if (!park, exit(print(format('w [', 'd Parkour', 'w ] ', 'y No such park ', 'wb ' + name))));

    park:setting = value;

    for(json, if (lower(_:'name') == lower(name), json:_i = park));
    write_file('park_list_' + player() ~ 'uuid', 'json', json);

    if (value != [], modify_park(name), point_menu());
);

point_menu() -> (
    park = read_file('park_selected_' + player() ~ 'uuid', 'raw'):0;
    park = find_park(park);
    if (!park, exit(print(format('w [', 'd Parkour', 'w ] ', 'y Please choose a park to modify!'))));

    print(format('w [', 'd ' + park:'name', 'w ] ', 'y Park Point Menu ', 'l [←]', '^w Park Menu', '!/parkour modify'));
    if (park:'start',
        print(format(
            'r  [✖]', '^w Delete Start', '!/parkour delete_point start',
            'l [◎] ', '^w Highlight Start', '!/parkour highlight_point start',
            'y Start: ', 'wb ' + park:'start':0 + ', ' + park:'start':1 + ', ' + park:'start':2
        )),
        print(format('g  [✖][◎] ', 'y Start: ', 'wb none'));
    );
    if (park:'finish',
        print(format(
            'r  [✖]', '^w Delete Finish', '!/parkour delete_point finish',
            'l [◎] ', '^w Highlight Finish', '!/parkour highlight_point finish',
            'y Finish: ', 'wb ' + park:'finish':0 + ', ' + park:'finish':1 + ', ' + park:'finish':2
        )),
        print(format('g  [✖][◎] ', 'y Finish: ', 'wb none'));
    );

    print(format('db Checkpoints', 'w :'));
    for (park:'checkpoints',
        print(format(
            'r  [✖]', '^w Delete Checkpoint', '!/parkour delete_point checkpoint ' + _: 0 + ' ' + _:1 + ' ' + _:2,
            'l [◎] ', '^w Highlight Checkpoint', '!/parkour highlight_point checkpoint ' + _i, 'wb ' + _:0 + ', ' + _:1 + ', ' + _:2
        ));
    );
);

highlight_point(point) -> (
    if (!global_feature_toggle:'checkpoint_highlight', exit(print(format('w [', 'd Parkour', 'w ] ', 'y Feature disabled.'))));
    park = read_file('park_selected_' + player() ~ 'uuid', 'raw'):0;
    park = find_park(park);
    if (!park, exit(print(format('w [', 'd Parkour', 'w ] ', 'y Please choose a park to modify!'))));

    block = 'white_stained_glass';
    if (point == 'start', point = park:'start'; block = 'lime_stained_glass');
    if (point == 'finish', point = park:'finish'; block = 'yellow_stained_glass');
    if (type(point) == 'number', point = park:'checkpoints':point);
    if (!point, exit(print(format('w [', 'd Parkour', 'w ] ', 'y That point does not exist'))));

    spawn('falling_block', point:0 + 0.5, point:1, point:2 + 0.5, '{BlockState:{Name:"' + block + '"},Time:400,NoGravity:1b,Glowing:1b,DropItem:0b}');
);

delete_checkpoint(point) -> (
    park = read_file('park_selected_' + player() ~ 'uuid', 'raw'):0;
    park = find_park(park);
    if (!park, exit(print(format('w [', 'd Parkour', 'w ] ', 'y Please choose a park to modify!'))));

    for (park:'checkpoints', if (_ == point,
        delete(park:'checkpoints':_i);
        break();
    ));
    send_park_json(park);
    
    point_menu();
);

rename_park(park, name) -> (
    found = find_park(name);
    if (found && lower(found:'name') != lower(park), exit(print(format('w [', 'd Parkour', 'w ] ', 'y A park with that name already exists!'))));
    
    json = read_file('park_list_' + player() ~ 'uuid', 'json');
    found = false;
    if (json, for(json, if (lower(_:'name') == lower(park),
        _:'name' = name;
        found = true;
        write_file('park_list_' + player() ~ 'uuid', 'json', json);
        break();
    )));
    if (!found, exit(print(format('w [', 'd Parkour', 'w ] ', 'y No such park ', 'wb ' + park))));
    

    json = read_file('global_park_list', 'json');
    if (json, for(json, if (lower(_:'name') == lower(park) && _:'owner' == player() ~ 'uuid',
        _:'name' = name;
        write_file('global_park_list', 'json', json);
        break();
    )));

    json = read_file('global_park_leaderboard', 'json');
    if (json, for(json, if (lower(_:'name') == lower(park) && _:'owner' == player() ~ 'uuid',
        _:'name' = name;
        write_file('global_park_leaderboard', 'json', json);
        break();
    )));

    
    print(format('w [', 'd Parkour', 'w ] ', 'y Successfully renamed your park to ', 'wb ' + name));

    if (park == read_file('park_selected_' + player() ~ 'uuid', 'raw'):0,
        modify_park(name);
    );

    exit();
);

reset_times(name, warn) -> (
    if (name && !find_park(name), exit(print(format('w [', 'd Parkour', 'w ] ', 'y No such park ', 'wb ' + name))));
    if (!warn,
        print(format('w [', 'd Parkour', 'w ] ', 'y Are you sure you want to reset the leaderboard times for ', 'wb ' + name, 'y ?'));
        print(format('w  ', '#FF0F0Fu This cannot be undone!', 'w  [', 'rb Confirm', '^w Click here to confirm.', '!/parkour reset_times "' + name + '" force', 'w ]'));
        exit();
    );

    json = read_file('global_park_leaderboard', 'json');
    if (!json, exit(print(format('w [', 'd Parkour', 'w ] ', 'y No parks exist to reset times for!'))));

    found = false;
    for(json, if (lower(_:'name') == lower(name) && _:'owner' == player() ~ 'uuid',
        delete(json, _i);
        print(format('w [', 'd Parkour', 'w ] ', 'y Successfully reset times for ', 'wb ' + _:'name'));
        found = true;
    ));
    
    if (!found, exit(print(format('w [', 'd Parkour', 'w ] ', 'y No leaderboard found for ', 'wb ' + name))));
    
    write_file('global_park_leaderboard', 'json', json);

    exit();
);

create_park(name) -> (
    if (find_park(name), exit(print(format('w [', 'd Parkour', 'w ] ', 'y A park with that name already exists!'))));

    json = read_file('park_list_' + player() ~ 'uuid', 'json');
    if (!json, json = []);
    json += {
        'name' -> name,
        'start' -> [],
        'checkpoints' -> [],
        'finish' -> [],
        'elytra' -> false,
        'swimming' -> true,
        'potions' -> false,
        'pearls' -> false,
        'retry' -> true,
        'dimension' -> player() ~ 'dimension'
    };
    write_file('park_list_' + player() ~ 'uuid', 'json', json);

    print(format('w [', 'd Parkour', 'w ] ', 'y Created new park ', 'wb ' + name));
    modify_park(name);

    exit();
); 

delete_park(name, warn) -> (
    if (name && !find_park(name), exit(print(format('w [', 'd Parkour', 'w ] ', 'y No such park ', 'wb ' + name))));
    if (!warn,
        print(format('w [', 'd Parkour', 'w ] ', 'y Are you sure you want to delete ', 'wb ' + name, 'y ?'));
        print(format('w  ', '#FF0F0Fu This cannot be undone!', 'w  [', 'rb Confirm', '^w Click here to confirm.', '!/parkour delete "' + name + '" force', 'w ]'));
        exit();
    );

    json = read_file('park_list_' + player() ~ 'uuid', 'json');
    if (!json, exit(print(format('w [', 'd Parkour', 'w ] ', 'y No parks exist to delete!'))));

    found = false;
    for(json, if (lower(_:'name') == lower(name),
        delete(json, _i);
        print(format('w [', 'd Parkour', 'w ] ', 'y Successfully deleted park ', 'wb ' + _:'name'));
        found = true;
    ));
    
    if (!found, exit(print(format('w [', 'd Parkour', 'w ] ', 'y No such park ', 'wb ' + name))));
    
    write_file('park_list_' + player() ~ 'uuid', 'json', json);

    json = read_file('global_park_list', 'json');
    for(json, if (_:'owner' == player() ~ 'uuid' && lower(_:'name') == lower(name),
        delete(json:_i);
        write_file('global_park_list', 'json', json);
        exit();
    ));

    json = read_file('global_park_leaderboard', 'json');
    for(json, if (_:'owner' == player() ~ 'uuid' && lower(_:'name') == lower(name),
        delete(json:_i);
        write_file('global_park_leaderboard', 'json', json);
        exit();
    ));

    select_park(false, false);

    exit();
);
