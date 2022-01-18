//Lets you create and use warps!

__config() -> {
    'commands' -> {
        '' -> 'help',
        'help' -> 'help',
        '<destination>' -> 'warp',
        'list' -> 'listWarps',
        'create <name>' -> 'createWarp',
        'delete <name>' -> 'deleteWarp'
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
    },
    ['stay_loaded','true']
};

findWarp(name) -> (
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
    warp = findWarp(destination);
    if (warp,
        modify(player(), 'pos', warp:'pos');
        print(format('w [','d Warp','w ] ', 'y Successfully warped to ', 'wb ' + warp:'name')),
        print(format('w [','d Warp','w ] ', 'y Warp ', 'wb ' + destination, 'y  does not exist!'));
    );
    exit();
);

listWarps() -> (
    json = read_file('warp_list', 'json');
    if (!json, print(format('w [','d Warp','w ] ', 'y No warps found!')),
        print(format('db Available warps', 'w :'));
        for(json, print(format('y  ' + _:'name')));
    );
    exit();
);

createWarp(name) -> (
    if (!findWarp(name),
        pos = query(player(), 'pos');
        pos:0 = round(pos:0 * 10) / 10;
        pos:1 = round(pos:1 * 10) / 10;
        pos:2 = round(pos:2 * 10) / 10;

        print(format('w [','d Warp','w ] ', 'y Successfully created warp ', 'wb ' + name, 'y  at ', 'wb ' +
            pos:0 + ', ' + 
            pos:1 + ', ' + 
            pos:2));

        json = read_file('warp_list', 'json');
        if (!json, json = []);
        
        json:length(json) = {
            'name' -> name,
            'pos' -> pos
        };

        write_file('warp_list', 'json', json),


        print(format('w [','d Warp','w ] ', 'y A warp with that name already exists!'));
    );
    exit();
);

deleteWarp(name) -> (
    json = read_file('warp_list', 'json');
    if (json,
        found = false;
        for(json, if (lower(_:'name') == lower(name),
            delete(json, _i);
            print(format('w [','d Warp','w ] ', 'y Successfully deleted warp ', 'wb ' + _:'name'));
            found = true;
        ));
        
        if (found,
            write_file('warp_list', 'json', json),

            print(format('w [','d Warp','w ] ', 'y No such warp ', 'wb ' + name));
        ),

        print(format('w [','d Warp','w ] ', 'y No warps exist to delete!'));
    );
    exit();
);
