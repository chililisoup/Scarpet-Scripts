// Lets you create and use warps!

__config() -> {
    'commands' -> {
        '' -> ['open_warp_menu', 0, false],
        'menu' -> ['open_warp_menu', 0, false],
        'help' -> 'help',
        '<destination>' -> 'warp',
        'list' -> ['list_warps', 1],
        'list <page>' -> 'list_warps',
        'create <name>' -> 'create_warp',
        'delete <destination>' -> 'delete_warp'
    },
    'arguments' -> {
        'destination' -> {
            'type' -> 'text',
            'case_sensitive' -> false,
            'suggester' -> _(args) -> (
				json = read_file('warp_list', 'json');
                destinations = [];
                if (!json, return([]),
                    for(json, destinations += _:'name');
                    return(destinations);
                );
			)
        },
        'name' -> {
            'type' -> 'text',
            'suggest' -> []
        },
        'page' -> {
            'type' -> 'int',
            'min' -> 1,
            'suggester' -> _(args) -> (
                json = read_file('warp_list', 'json');
                if (!json || length(json) < 1, return([]));
                return([1, floor(length(json) / 8) + 1]);
            );
        }
    }
};

find_warp_index(warp) -> (
    json = read_file('warp_list', 'json');
    if (json, for(json, if (lower(_:'name') == lower(warp:'name'), return(_i))));
);

find_warp(name) -> (
    json = read_file('warp_list', 'json');
    if (json, for(json, if (lower(_:'name') == lower(name), return(_))));
);

help() -> (
    print('/warp OR /warp menu - Open warp menu');
    print('/warp <destination> - Warp to destination');
    print('/warp list <page> - List available warps');
    print('/warp create <name> - Create a new warp at your location');
    print('/warp delete <name> - Delete an existing warp');
    print('/warp help - Display this message');
);

execute_warp(warp) -> (
    run('execute in ' + warp:'dim' + ' run tp ' + warp:'pos':0 + ' ' + warp:'pos':1 + ' ' + warp:'pos':2);
    print(format('w [', 'd Warp', 'w ] ', 'y Successfully warped to ', 'wb ' + warp:'name'));
);

warp(destination) -> (
    warp = find_warp(destination);
    if (!warp, exit(print(format('w [', 'd Warp', 'w ] ', 'y Warp ', 'wb ' + destination, 'y  does not exist!'))));
    execute_warp(warp);
);

open_anvil_screen(item, item_data, name, callback) -> (
    screen = create_screen(player(), 'anvil', name, _(screen, player, action, data, outer(callback)) -> (
        if (!screen_property(screen, 'open'), return('cancel'));
        screen_property(screen, 'level_cost', 1);
        screen_property(screen, 'level_cost', 0);
        
        if (action == 'pickup' && data:'slot' == 2, call(callback, screen));

        return('cancel');
    ));

    inventory_set(screen, 0, 1, item, encode_nbt(item_data));
    screen_property(screen, 'level_cost', 0);
);

open_warp_creation_menu() -> (
    open_anvil_screen('oak_sign', {'display' -> {'Name' -> '""'}}, 'Enter Warp Name...', _(screen) -> (
        name = item_display_name(inventory_get(screen, 2));
        create_warp(name);
        close_screen(screen);
        open_warp_edit_menu(find_warp(name));
    ));
);

open_warp_rename_menu(warp) -> (
    open_anvil_screen(warp:'item' || 'oak_sign', warp:'item_data' || {'display' -> {'Name' -> encode_json({'text' -> warp:'name', 'italic' -> false})}}, 'Rename Warp...', _(screen, outer(warp)) -> (
        name = item_display_name(inventory_get(screen, 2));
        if (find_warp(name),
            print(format('w [', 'd Warp', 'w ] ', 'y A warp with that name already exists!'));
            close_screen(screen);
            open_warp_edit_menu(warp);
            return();
        );

        old = warp:'name';
        warp:'name' = name;
        edit_warp({'name' -> old}, warp);
        close_screen(screen);
        print(format('w [', 'd Warp', 'w ] ', 'y Successfully renamed warp ', 'wb ' + old, 'y  to ', 'wb ' + name));
        open_warp_edit_menu(warp);
    ));
);

move_warp(warp, index, target) -> (
    json = read_file('warp_list', 'json');
    delete(json, index);
    put(json, target, warp, 'insert');
    write_file('warp_list', 'json', json);
);

edit_warp(original, edit) -> (
    json = read_file('warp_list', 'json');
    if (json, for(json, if (lower(_:'name') == lower(original:'name'),
        json:_i = edit;
        write_file('warp_list', 'json', json);
        return();
    )));
);

open_warp_edit_menu(warp) -> (
    screen = create_screen(player(), 'generic_9x3', 'Warp Edit Menu', _(screen, player, action, data, outer(warp)) -> (
        if (action != 'pickup', return('cancel'));

        if (data:'slot' == 9,
            index = find_warp_index(warp);
            move_warp(warp, index, max(index - 1, 0));
            return('cancel');
        );
        
        if (data:'slot' == 10,
            json = read_file('warp_list', 'json');
            index = find_warp_index(warp);
            move_warp(warp, index, min(index + 1, length(json) - 1));
            return('cancel');
        );

        if (data:'slot' == 12,
            pos = player ~ 'pos';
            pos:0 = round(pos:0 * 10) / 10;
            pos:1 = round(pos:1 * 10) / 10;
            pos:2 = round(pos:2 * 10) / 10;
            warp:'pos' = pos;
            warp:'dim' = player ~ 'dimension';
            edit_warp(warp, warp);

            print(
                format(
                    'w [', 'd Warp', 'w ] ', 'y Successfully moved warp ', 'wb ' + warp:'name', 'y  to ', 'wb ' +
                    pos:0 + ', ' + 
                    pos:1 + ', ' + 
                    pos:2,
                    'y  in ', 'wb ' + warp:'dim'
                )
            );
            return('cancel');
        );

        if (data:'slot' == 14,
            close_screen(screen);
            open_warp_rename_menu(warp);
            return('cancel');
        );

        if (data:'slot' == 16,
            move_warp(warp, find_warp_index(warp), 0);
            return('cancel');
        );

        if (data:'slot' == 17,
            move_warp(warp, find_warp_index(warp), null);
            return('cancel');
        );

        if (data:'slot' == 19,
            close_screen(screen);
            open_warp_menu(floor(find_warp_index(warp) / 45), true);
            return('cancel');
        );

        if (data:'slot' == 25,
            item = inventory_get(screen, 25):0;

            if (item == 'barrier',
                inventory_set(screen, 25, 1, 'red_terracotta', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Click 2 more times to delete', 'italic' -> false, 'color' -> 'red'})}}));
                return('cancel');
            );

            if (item == 'red_terracotta',
                inventory_set(screen, 25, 1, 'bedrock', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Click 1 more time to delete', 'italic' -> false, 'color' -> 'red'})}}));
                return('cancel');
            );

            delete_warp(warp:'name');
            close_screen(screen);
            return('cancel');
        );

        if (data:'slot' >= 27,
            item = inventory_get(screen, data:'slot');
            if (!item, return('cancel'));
            warp:'item' = item:0;
            edit_warp(warp, warp);
            inventory_set(screen, 4, 1, item:0, inventory_get(screen, 4):2);
        );

        return('cancel');
    ));

    for (range(9), inventory_set(screen, _, 1, 'white_stained_glass_pane', encode_nbt({'display' -> {'Name' -> '""'}})));
    for (range(9), inventory_set(screen, _ + 18, 1, 'white_stained_glass_pane', encode_nbt({'display' -> {'Name' -> '""'}})));

    inventory_set(screen, 4, 1, warp:'item' || 'oak_sign', encode_nbt(warp:'item_data' || {'display' -> {'Name' -> encode_json({'text' -> warp:'name', 'italic' -> false})}}));
    
    inventory_set(screen, 9, 1, 'iron_ingot', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Push up in list', 'italic' -> false})}}));
    inventory_set(screen, 10, 1, 'iron_nugget', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Push down in list', 'italic' -> false})}}));

    inventory_set(screen, 12, 1, 'ender_pearl', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Reposition warp', 'italic' -> false})}}));
    inventory_set(screen, 13, 1, 'glass_pane', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Click an inventory item to set custom item', 'italic' -> false})}}));
    inventory_set(screen, 14, 1, 'name_tag', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Rename warp', 'italic' -> false})}}));

    inventory_set(screen, 16, 1, 'gold_ingot', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Push to front of list', 'italic' -> false})}}));
    inventory_set(screen, 17, 1, 'gold_nugget', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Push to back of list', 'italic' -> false})}}));

    inventory_set(screen, 19, 1, 'arrow', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Back', 'italic' -> false})}}));
    inventory_set(screen, 25, 1, 'barrier', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Delete Warp', 'italic' -> false, 'color' -> 'red'})}}));
);

set_warp_menu_edit_mode(screen, slot, enabled) -> (
    inventory_set(screen, slot, 1, enabled && 'diamond_pickaxe' || 'wooden_pickaxe', encode_nbt(
        enabled && {'Enchantments' -> [{}], 'display' -> {'Name' -> encode_json({'text' -> 'Edit Mode ON', 'italic' -> false})}}
        || {'display' -> {'Name' -> encode_json({'text' -> 'Edit Mode OFF', 'italic' -> false})}}
    ));
);

open_warp_menu(page, edit_mode) -> (
    json = slice(read_file('warp_list', 'json'), 45 * page);
    json_rows = ceil(length(json) / 9);
    screen_size = min(json_rows + 1, 6);
    bottom_row_slot = min(json_rows * 9, 45);

    screen = create_screen(player(), 'generic_9x' + screen_size, 'Warp Menu', _(screen, player, action, data, outer(json), outer(bottom_row_slot), outer(json_rows), outer(page)) -> (
        if (action != 'pickup', return('cancel'));

        edit_mode = inventory_get(screen, bottom_row_slot + 3):0 != 'wooden_pickaxe';
        if (data:'slot' == bottom_row_slot + 3,
            set_warp_menu_edit_mode(screen, bottom_row_slot + 3, !edit_mode);
            return('cancel');
        );

        if (data:'slot' == bottom_row_slot + 5,
            close_screen(screen);
            open_warp_creation_menu();
            return('cancel');
        );

        if (data:'slot' == bottom_row_slot + 1 && page > 0,
            close_screen(screen);
            open_warp_menu(page - 1, edit_mode);
            return('cancel');
        );

        if (data:'slot' == bottom_row_slot + 7 && json_rows > 5,
            close_screen(screen);
            open_warp_menu(page + 1, edit_mode);
            return('cancel');
        );

        if (data:'slot' >= length(json) || data:'slot' >= 45, return('cancel'));

        close_screen(screen);
        if (edit_mode,
            open_warp_edit_menu(json:(data:'slot')),
            execute_warp(json:(data:'slot'));
        );
        return('cancel');
    ));

    for (json,
        if (_i >= 45, break());
        item = _:'item' || 'oak_sign';
        item_data = _:'item_data' || {'display' -> {'Name' -> encode_json({'text' -> _:'name', 'italic' -> false})}};
        inventory_set(screen, _i, 1, item, encode_nbt(item_data));
    );

    for (range(9), inventory_set(screen, _ + bottom_row_slot, 1, 'white_stained_glass_pane', encode_nbt({'display' -> {'Name' -> '""'}})));
    if (page > 0, inventory_set(screen, bottom_row_slot + 1, 1, 'arrow', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Previous Page', 'italic' -> false})}})));
    if (length(json) > 45, inventory_set(screen, bottom_row_slot + 7, 1, 'arrow', encode_nbt({'display' -> {'Name' -> encode_json({'text' -> 'Next Page', 'italic' -> false})}})));
    set_warp_menu_edit_mode(screen, bottom_row_slot + 3, edit_mode);
    inventory_set(screen, bottom_row_slot + 5, 1, 'oak_sign', encode_nbt({'Enchantments' -> [{}], 'display' -> {'Name' -> encode_json({'text' -> 'Create new warp', 'italic' -> false})}}));
);

list_warps(page) -> (
    json = read_file('warp_list', 'json');
    if (!json, return(print(format('w [', 'd Warp', 'w ] ', 'y No warps found!'))));

    max_page = floor(length(json) / 8) + 1;
    if ((page - 1) * 8 > length(json), page = max_page);
    print(format('db Available warps', 'w :'));
    c_for (i = (page - 1) * 8, i < page * 8 && i < length(json), i += 1,
        print(format('y  ' + json:i:'name', '^w Click here to warp', '!/warp ' + json:i:'name'))
    );
    print(format('w [', 'cb <', '!/warp list ' + max(page - 1, 1), 'w ] ', 'l ' + page, 'w /', 'l ' + max_page, 'w  [', 'cb >', '!/warp list ' + (page + 1), 'w ] '))
);

create_warp(name) -> (
    if (find_warp(name), exit(print(format('w [', 'd Warp', 'w ] ', 'y A warp with that name already exists!'))));

    pos = player() ~ 'pos';
    pos:0 = round(pos:0 * 10) / 10;
    pos:1 = round(pos:1 * 10) / 10;
    pos:2 = round(pos:2 * 10) / 10;

    dim = player() ~ 'dimension';

    print(
        format(
            'w [', 'd Warp', 'w ] ', 'y Successfully created warp ', 'wb ' + name, 'y  at ', 'wb ' +
            pos:0 + ', ' + 
            pos:1 + ', ' + 
            pos:2,
            'y  in ', 'wb ' + dim
        )
    );

    json = read_file('warp_list', 'json');
    if (!json, json = []);
    json += {
        'name' -> name,
        'pos' -> pos,
        'dim' -> dim
    };
    write_file('warp_list', 'json', json);
);

delete_warp(name) -> (
    json = read_file('warp_list', 'json');
    if (!json, exit(print(format('w [', 'd Warp', 'w ] ', 'y No warps exist to delete!'))));

    found = false;
    for(json, if (lower(_:'name') == lower(name),
        delete(json, _i);
        print(format('w [', 'd Warp', 'w ] ', 'y Successfully deleted warp ', 'wb ' + _:'name'));
        found = true;
        break;
    ));
    
    if (!found, exit(print(format('w [', 'd Warp', 'w ] ', 'y No such warp ', 'wb ' + name))));
    
    write_file('warp_list', 'json', json);
);