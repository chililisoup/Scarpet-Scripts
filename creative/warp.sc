// Lets you create and use warps!

__config() -> {
    'commands' -> {
        '' -> 'list_warps',
        'help' -> 'help',
        '<destination>' -> 'warp',
        'list' -> 'list_warps',
        'create <name>' -> 'create_warp',
        'delete <name>' -> 'delete_warp'
    },
    'arguments' -> {
        'destination' -> {
            'type' -> 'term',
            'suggest' -> []
        },
        'name' -> {
            'type' -> 'term',
            'suggest' -> []
        }
    }
};

find_warp(name) -> (
    json = read_file('warp_list', 'json');
    warp = null;
    if (json, for(json, if (lower(_:'name') == lower(name), warp = _)));
    return(warp);
);

help() -> (
    print('/warp <destination> - Warp to destination');
    print('/warp list - List available warps');
    print('/warp create <name> - Create a new warp at your location');
    print('/warp delete <name> - Delete an existing warp');
    print('/warp help - Display this message');
    exit();
);

warp(destination) -> (
    warp = find_warp(destination);
    if (!warp, exit(print(format('w [', 'd Warp', 'w ] ', 'y Warp ', 'wb ' + destination, 'y  does not exist!'))));

    modify(player(), 'pos', warp:'pos');
    exit(print(format('w [', 'd Warp', 'w ] ', 'y Successfully warped to ', 'wb ' + warp:'name')));
);

list_warps() -> (
    json = read_file('warp_list', 'json');
    if (!json, print(format('w [', 'd Warp', 'w ] ', 'y No warps found!')),
        print(format('db Available warps', 'w :'));
        for(json, print(format('y  ' + _:'name')));
    );
    exit();
);

create_warp(name) -> (
    if (find_warp(name), exit(print(format('w [', 'd Warp', 'w ] ', 'y A warp with that name already exists!'))));

    pos = query(player(), 'pos');
    pos:0 = round(pos:0 * 10) / 10;
    pos:1 = round(pos:1 * 10) / 10;
    pos:2 = round(pos:2 * 10) / 10;

    print(format('w [', 'd Warp', 'w ] ', 'y Successfully created warp ', 'wb ' + name, 'y  at ', 'wb ' +
        pos:0 + ', ' + 
        pos:1 + ', ' + 
        pos:2)
    );

    json = read_file('warp_list', 'json');
    if (!json, json = []);
    json += {
        'name' -> name,
        'pos' -> pos
    };
    write_file('warp_list', 'json', json);

    exit();
);

delete_warp(name) -> (
    json = read_file('warp_list', 'json');
    if (!json, exit(print(format('w [', 'd Warp', 'w ] ', 'y No warps exist to delete!'))));

    found = false;
    for(json, if (lower(_:'name') == lower(name),
        delete(json, _i);
        print(format('w [', 'd Warp', 'w ] ', 'y Successfully deleted warp ', 'wb ' + _:'name'));
        found = true;
    ));
    
    if (!found, exit(print(format('w [', 'd Warp', 'w ] ', 'y No such warp ', 'wb ' + name))));
    
    write_file('warp_list', 'json', json);

    exit();
);