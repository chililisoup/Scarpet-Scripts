// Copy of warp.sc for private use warps

// Requires Carpet LAB Addition (https://modrinth.com/mod/carpet-lab-addition)
// Check file history for versions not requiring that mod

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
				json = read_file('warp_list_' + player() ~ 'uuid', 'json');
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
                json = read_file('warp_list_' + player() ~ 'uuid', 'json');
                if (!json || length(json) < 1, return([]));
                return([1, floor(length(json) / 8) + 1]);
            );
        }
    }
};

find_warp_index(warp) -> (
    json = read_file('warp_list_' + player() ~ 'uuid', 'json');
    if (json, for(json, if (lower(_:'name') == lower(warp:'name'), return(_i))));
);

find_warp(name) -> (
    json = read_file('warp_list_' + player() ~ 'uuid', 'json');
    if (json, for(json, if (lower(_:'name') == lower(name), return(_))));
);

help() -> (
    print(format('y This is the private warp command. To use public warps, do /warp'));
    print('/pwarp OR /pwarp menu - Open warp menu');
    print('/pwarp <destination> - Warp to destination');
    print('/pwarp list <page> - List available warps');
    print('/pwarp create <name> - Create a new warp at your location');
    print('/pwarp delete <name> - Delete an existing warp');
    print('/pwarp help - Display this message');
);

execute_warp(warp) -> (
    run('execute in ' + warp:'dim' + ' run tp ' + warp:'pos':0 + ' ' + warp:'pos':1 + ' ' + warp:'pos':2);
    print(format('w [', 'd PWarp', 'w ] ', 'y Successfully warped to ', 'wb ' + warp:'name'));
);

warp(destination) -> (
    warp = find_warp(destination);
    if (!warp, exit(print(format('w [', 'd PWarp', 'w ] ', 'y PWarp ', 'wb ' + destination, 'y  does not exist!'))));
    execute_warp(warp);
);

open_anvil_screen(item_nbt, name, callback) -> (
    screen = create_screen(player(), 'anvil', name, _(screen, player, action, data, outer(callback)) -> (
        if (!screen_property(screen, 'open'), return('cancel'));
        screen_property(screen, 'level_cost', 1);
        screen_property(screen, 'level_cost', 0);
        
        if (action == 'pickup' && data:'slot' == 2, call(callback, screen));

        return('cancel');
    ));

    inventory_set(screen, 0, null, null, encode_snbt(item_nbt));
    screen_property(screen, 'level_cost', 0);
);

open_warp_creation_menu() -> (
    inventory_set(screen, bottom_row_slot + 5, null, null, encode_snbt({'count' -> 1, 'id' -> 'oak_sign', 'components' -> {'minecraft:enchantment_glint_override' -> true, 'minecraft:item_name' -> 'Create new warp'}}));
    open_anvil_screen({'count' -> 1, 'id' -> 'oak_sign', 'components' -> {'minecraft:item_name' -> ''}}, 'Enter PWarp Name...', _(screen) -> (
        item = inventory_get(screen, 2);
        if (!item:2:'components':'minecraft:custom_name',
            close_screen(screen);
            return();
        );

        name = item_display_name(item);
        create_warp(name);
        close_screen(screen);
        open_warp_edit_menu(find_warp(name));
    ));
);

open_warp_rename_menu(warp) -> (
    item_nbt = {
        'count' -> 1,
        'id' -> warp:'item' || 'oak_sign',
        'components' -> {}
    };
    if (warp:'components',
        item_nbt:'components' = copy(warp:'components');
    );
    item_nbt:'components':'minecraft:item_name' = {'text' -> warp:'name', 'color' -> 'white'};
    delete(item_nbt:'components':'minecraft:custom_name');

    open_anvil_screen(item_nbt, 'Rename PWarp...', _(screen, outer(warp)) -> (
        item = inventory_get(screen, 2);
        if (!item:2:'components':'minecraft:item_name',
            close_screen(screen);
            open_warp_edit_menu(warp);
            return();
        );

        name = item_display_name(item);
        if (find_warp(name),
            print(format('w [', 'd PWarp', 'w ] ', 'y A warp with that name already exists!'));
            close_screen(screen);
            open_warp_edit_menu(warp);
            return();
        );

        old = warp:'name';
        warp:'name' = decode_json(encode_json(name));
        edit_warp({'name' -> old}, warp);
        close_screen(screen);
        print(format('w [', 'd PWarp', 'w ] ', 'y Successfully renamed warp ', 'wb ' + old, 'y  to ', 'wb ' + name));
        open_warp_edit_menu(warp);
    ));
);

move_warp(warp, index, target) -> (
    json = read_file('warp_list_' + player() ~ 'uuid', 'json');
    delete(json, index);
    put(json, target, warp, 'insert');
    write_file('warp_list_' + player() ~ 'uuid', 'json', json);
);

edit_warp(original, edit) -> (
    json = read_file('warp_list_' + player() ~ 'uuid', 'json');
    if (json, for(json, if (lower(_:'name') == lower(original:'name'),
        json:_i = edit;
        write_file('warp_list_' + player() ~ 'uuid', 'json', json);
        return();
    )));
);

open_warp_edit_menu(warp) -> (
    screen = create_screen(player(), 'generic_9x3', 'PWarp Edit Menu', _(screen, player, action, data, outer(warp)) -> (
        if (action != 'pickup', return('cancel'));

        if (data:'slot' == 4,
            slot = inventory_find(player, null);
            if (slot != null,
                item_nbt = {
                    'count' -> 1,
                    'id' -> warp:'item' || 'oak_sign',
                };
                if (warp:'components',
                    item_nbt:'components' = warp:'components';
                );
                inventory_set(player, slot, null, null, encode_snbt(item_nbt));
            );
            return('cancel');
        );

        if (data:'slot' == 9,
            index = find_warp_index(warp);
            move_warp(warp, index, max(index - 1, 0));
            return('cancel');
        );
        
        if (data:'slot' == 10,
            json = read_file('warp_list_' + player() ~ 'uuid', 'json');
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
                    'w [', 'd PWarp', 'w ] ', 'y Successfully moved warp ', 'wb ' + warp:'name', 'y  to ', 'wb ' +
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
                inventory_set(screen, 25, null, null, encode_snbt({
                    'count' -> 1,
                    'id' -> 'red_terracotta',
                    'components' -> {
                        'minecraft:item_name' -> {
                            'text' -> 'Click 2 more times to delete',
                            'color' -> 'red'
                        }
                    }
                }));
                return('cancel');
            );

            if (item == 'red_terracotta',
                inventory_set(screen, 25, null, null, encode_snbt({
                    'count' -> 1,
                    'id' -> 'bedrock',
                    'components' -> {
                        'minecraft:item_name' -> {
                            'text' -> 'Click 1 more time to delete',
                            'color' -> 'red'
                        }
                    }
                }));
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

            item_nbt = {
                'count' -> 1,
                'id' -> item:0,
                'components' -> {}
            };

            components = parse_nbt(item:2):'components';
            if (components,
                warp:'components' = copy(components);
                delete(warp:'components':'minecraft:item_name');
                item_nbt:'components' = components,

                delete(warp:'components');
            );
            item_nbt:'components':'minecraft:item_name' = {'text' -> warp:'name', 'color' -> 'white'};

            inventory_set(screen, 4, null, null, encode_snbt(item_nbt));
            edit_warp(warp, warp);
        );

        return('cancel');
    ));

    for (range(9), inventory_set(screen, _, null, null, encode_snbt({'count' -> 1, 'id' -> 'white_stained_glass_pane', 'components' -> {'minecraft:item_name' -> ''}})));
    for (range(9), inventory_set(screen, _ + 18, null, null, encode_snbt({'count' -> 1, 'id' -> 'white_stained_glass_pane', 'components' -> {'minecraft:item_name' -> ''}})));

    item_nbt = {
        'count' -> 1,
        'id' -> warp:'item' || 'oak_sign',
        'components' -> {}
    };
    if (warp:'components',
        item_nbt:'components' = copy(warp:'components');
    );
    item_nbt:'components':'minecraft:item_name' = {'text' -> warp:'name', 'color' -> 'white'};
    inventory_set(screen, 4, null, null, encode_snbt(item_nbt));
    
    inventory_set(screen, 9, null, null, encode_snbt({'count' -> 1, 'id' -> 'iron_ingot', 'components' -> {'minecraft:item_name' -> 'Push up in list'}}));
    inventory_set(screen, 10, null, null, encode_snbt({'count' -> 1, 'id' -> 'iron_nugget', 'components' -> {'minecraft:item_name' -> 'Push down in list'}}));

    inventory_set(screen, 12, null, null, encode_snbt({'count' -> 1, 'id' -> 'ender_pearl', 'components' -> {'minecraft:item_name' -> 'Reposition warp'}}));
    inventory_set(screen, 13, null, null, encode_snbt({'count' -> 1, 'id' -> 'glass_pane', 'components' -> {'minecraft:item_name' -> 'Click an inventory item to set custom item'}}));
    inventory_set(screen, 14, null, null, encode_snbt({'count' -> 1, 'id' -> 'name_tag', 'components' -> {'minecraft:item_name' -> 'Rename warp'}}));

    inventory_set(screen, 16, null, null, encode_snbt({'count' -> 1, 'id' -> 'gold_ingot', 'components' -> {'minecraft:item_name' -> 'Push to front of list'}}));
    inventory_set(screen, 17, null, null, encode_snbt({'count' -> 1, 'id' -> 'gold_nugget', 'components' -> {'minecraft:item_name' -> 'Push to back of list'}}));

    inventory_set(screen, 19, null, null, encode_snbt({'count' -> 1, 'id' -> 'arrow', 'components' -> {'minecraft:item_name' -> 'Back'}}));
    inventory_set(screen, 25, null, null, encode_snbt({'count' -> 1, 'id' -> 'barrier', 'components' -> {'minecraft:item_name' -> { 'text' -> 'Delete PWarp', 'color' -> 'red' }}}));
);

set_warp_menu_edit_mode(screen, slot, enabled) -> (
    item_nbt = {};

    if (enabled,
        item_nbt = {
            'id' -> 'diamond_pickaxe',
            'components' -> {
                'minecraft:enchantment_glint_override' -> true,
                'minecraft:item_name' -> 'Edit Mode ON'
            }
        },

        item_nbt = {
            'id' -> 'wooden_pickaxe',
            'components' -> {
                'minecraft:item_name' -> 'Edit Mode OFF'
            }
        }
    );

    item_nbt:'count' = 1;

    inventory_set(screen, slot, null, null, encode_snbt(item_nbt));
);

open_warp_menu(page, edit_mode) -> (
    json = slice(read_file('warp_list_' + player() ~ 'uuid', 'json'), 45 * page);
    json_rows = ceil(length(json) / 9);
    screen_size = min(json_rows + 1, 6);
    bottom_row_slot = min(json_rows * 9, 45);

    screen = create_screen(player(), 'generic_9x' + screen_size, 'PWarp Menu', _(screen, player, action, data, outer(json), outer(bottom_row_slot), outer(json_rows), outer(page)) -> (
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
        item_nbt = {
            'count' -> '1',
            'id' -> _:'item' || 'oak_sign',
            'components' -> {}
        };

        if (_:'components',
            item_nbt:'components' = copy(_:'components');
        );
        item_nbt:'components':'minecraft:item_name' = {'text' -> _:'name', 'color' -> 'white'};

        inventory_set(screen, _i, null, null, encode_snbt(item_nbt));
    );

    for (range(9), inventory_set(screen, _ + bottom_row_slot, null, null, encode_snbt({'count' -> 1, 'id' -> 'white_stained_glass_pane', 'components' -> {'minecraft:item_name' -> ''}})));
    if (page > 0, inventory_set(screen, bottom_row_slot + 1, null, null, encode_snbt({'count' -> 1, 'id' -> 'arrow', 'components' -> {'minecraft:item_name' -> 'Previous Page'}})));
    if (length(json) > 45, inventory_set(screen, bottom_row_slot + 7, null, null, encode_snbt({'count' -> 1, 'id' -> 'arrow', 'components' -> {'minecraft:item_name' -> 'Next Page'}})));
    set_warp_menu_edit_mode(screen, bottom_row_slot + 3, edit_mode);
    inventory_set(screen, bottom_row_slot + 5, null, null, encode_snbt({'count' -> 1, 'id' -> 'oak_sign', 'components' -> {'minecraft:enchantment_glint_override' -> true, 'minecraft:item_name' -> 'Create new warp'}}));
);

list_warps(page) -> (
    json = read_file('warp_list_' + player() ~ 'uuid', 'json');
    if (!json, return(print(format('w [', 'd PWarp', 'w ] ', 'y No warps found!'))));

    max_page = floor(length(json) / 8) + 1;
    if ((page - 1) * 8 > length(json), page = max_page);
    print(format('db Available warps', 'w :'));
    c_for (i = (page - 1) * 8, i < page * 8 && i < length(json), i += 1,
        print(format('y  ' + json:i:'name', '^w Click here to warp', '!/warp ' + json:i:'name'))
    );
    print(format('w [', 'cb <', '!/warp list ' + max(page - 1, 1), 'w ] ', 'l ' + page, 'w /', 'l ' + max_page, 'w  [', 'cb >', '!/warp list ' + (page + 1), 'w ] '))
);

create_warp(name) -> (
    if (find_warp(name), exit(print(format('w [', 'd PWarp', 'w ] ', 'y A warp with that name already exists!'))));

    pos = player() ~ 'pos';
    pos:0 = round(pos:0 * 10) / 10;
    pos:1 = round(pos:1 * 10) / 10;
    pos:2 = round(pos:2 * 10) / 10;

    dim = player() ~ 'dimension';

    print(
        format(
            'w [', 'd PWarp', 'w ] ', 'y Successfully created warp ', 'wb ' + name, 'y  at ', 'wb ' +
            pos:0 + ', ' + 
            pos:1 + ', ' + 
            pos:2,
            'y  in ', 'wb ' + dim
        )
    );

    json = read_file('warp_list_' + player() ~ 'uuid', 'json');
    if (!json, json = []);
    json += {
        'name' -> name,
        'pos' -> pos,
        'dim' -> dim
    };
    write_file('warp_list_' + player() ~ 'uuid', 'json', json);
);

delete_warp(name) -> (
    json = read_file('warp_list_' + player() ~ 'uuid', 'json');
    if (!json, exit(print(format('w [', 'd PWarp', 'w ] ', 'y No warps exist to delete!'))));

    found = false;
    for(json, if (lower(_:'name') == lower(name),
        delete(json, _i);
        print(format('w [', 'd PWarp', 'w ] ', 'y Successfully deleted warp ', 'wb ' + _:'name'));
        found = true;
        break;
    ));
    
    if (!found, exit(print(format('w [', 'd PWarp', 'w ] ', 'y No such warp ', 'wb ' + name))));
    
    write_file('warp_list_' + player() ~ 'uuid', 'json', json);
);
