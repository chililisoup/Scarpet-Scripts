// Fork of warp.sc for private use warps

__config() -> {
    'commands' -> {
        '' -> 'list_warps',
        'help' -> 'help',
        '<destination>' -> 'warp',
        'list' -> 'list_warps',
        'create <name>' -> 'create_warp',
        'delete <destination>' -> 'delete_warp'
    },
    'arguments' -> {
        'destination' -> {
            'type' -> 'term',
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
            'type' -> 'term',
            'suggest' -> []
        }
    }
};

find_warp(name) -> (
    json = read_file('warp_list_' + player() ~ 'uuid', 'json');
    warp = null;
    if (json, for(json, if (lower(_:'name') == lower(name), warp = _)));
    return(warp);
);

help() -> (
    print(format('y This is the private warp command. To use public warps, do /warp'));
    print('/pwarp <destination> - Warp to destination');
    print('/pwarp list - List available warps');
    print('/pwarp create <name> - Create a new warp at your location');
    print('/pwarp delete <name> - Delete an existing warp');
    print('/pwarp help - Display this message');
    exit();
);

warp(destination) -> (
    warp = find_warp(destination);
    if (!warp, exit(print(format('w [', 'd PWarp', 'w ] ', 'y Warp ', 'wb ' + destination, 'y  does not exist!'))));

    run('execute in ' + warp:'dim' + ' run tp ' + warp:'pos':0 + ' ' + warp:'pos':1 + ' ' + warp:'pos':2);
    exit(print(format('w [', 'd PWarp', 'w ] ', 'y Successfully warped to ', 'wb ' + warp:'name')));
);

list_warps() -> (
    json = read_file('warp_list_' + player() ~ 'uuid';, 'json');
    if (!json, print(format('w [', 'd PWarp', 'w ] ', 'y No warps found!')),
        print(format('db Available warps', 'w :'));
        for(json, print(format('y  ' + _:'name', '^w Click here to warp', '!/pwarp ' + _:'name')));
    );
    exit();
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

    exit();
);

delete_warp(name) -> (
    json = read_file('warp_list_' + player() ~ 'uuid', 'json');
    if (!json, exit(print(format('w [', 'd PWarp', 'w ] ', 'y No warps exist to delete!'))));

    found = false;
    for(json, if (lower(_:'name') == lower(name),
        delete(json, _i);
        print(format('w [', 'd PWarp', 'w ] ', 'y Successfully deleted warp ', 'wb ' + _:'name'));
        found = true;
    ));
    
    if (!found, exit(print(format('w [', 'd PWarp', 'w ] ', 'y No such warp ', 'wb ' + name))));
    
    write_file('warp_list_' + player() ~ 'uuid', 'json', json);

    exit();
);