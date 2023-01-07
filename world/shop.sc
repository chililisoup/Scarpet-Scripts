// Safe shops script based on vanilla honor-based shops (no need for honor here!)

global_shoppables = [
	'chest',
	'trapped_chest',
	'barrel',
	'hopper',
	'furnace',
	'blast_furnace',
	'smoker',
    'shulker_box',
    'white_shulker_box',
    'light_gray_shulker_box',
    'gray__shulker_box',
    'black_shulker_box',
    'brown_shulker_box',
    'red_shulker_box',
    'orange_shulker_box',
    'yellow_shulker_box',
    'lime_shulker_box',
    'green_shulker_box',
    'cyan_shulker_box',
    'light_blue_shulker_box',
    'blue_shulker_box',
    'purple_shulker_box',
    'magenta_shulker_box',
    'pink_shulker_box'
];

__config() -> {
    'commands' -> {
        'create <price> <icon> hand' -> ['create', 'hand'],
		'create <price> <icon> <currency>' -> 'create',
        'remove' -> 'remove'
	},
    'arguments' -> {
        'price' -> {
			'type' -> 'int',
			'min' -> 1,
            'max' -> 64,
			'suggest' -> [1, 64]
		},
        'icon' -> {
            'type' -> 'term',
			'options' -> [
				'diamond',
                'dollar'
			]
        },
		'currency' -> {
			'type' -> 'item',
			'suggest' -> ['diamond']
		}
	}
};

create(price, icon, currency) -> (
    plr = player();
    block = query(plr, 'trace', 4.5, 'blocks');
    shop_data = {};

    if (currency == 'hand', currency = plr ~ 'holds');
    if (!currency, exit(print(format('w [', 'd Shop', 'w ] ', 'y You\'re not holding anything'))));
    if (price > stack_limit(currency:0),  exit(print(format('w [', 'd Shop', 'w ] ', 'y Price too high!'))));

    if (!block, exit(print(format('w [', 'd Shop', 'w ] ', 'y You\'re not looking at anything in range.'))));
	if (!is_in_array(global_shoppables, block), exit(print(format('w [', 'd Shop', 'w ] ', 'y This block can\'t be made a shop!'))));

    block_pos = pos(block);
    block_data = block_data(block_pos);

	block_props = [];
	for (keys(block_state(block_pos)),
		put(block_props, _i*2, _);
		put(block_props, _i*2 + 1, block_state(block_pos, _));
	);

    if (block_data:'Lock', exit(print(format('w [', 'd Shop', 'w ] ', 'y Cannot create a shop from a locked container!'))));

    if (block_data:'CustomName',
		shop_data = read_shop_data(block_data:'CustomName');
        if (!shop_data, exit(print(format('w [', 'd Shop', 'w ] ', 'y Unable to create shop. Please clear container name.'))));
        if (shop_data:'owner' != plr ~ 'uuid', exit(print(format('w [', 'd Shop', 'w ] ', 'y You do not own this shop.'))));
	);

    key = encode_nbt('{
        "extra":[
            {"text":"' + currency:0 + '"},
            {"text":"' + price + '"},
            {"text":"' + replace(currency:2, '"', '\\\\"') + '"},
            {"text":"' + icon + '"}
        ],
        "text":"' + plr ~ 'uuid' + '"
    }');

	block_data:'CustomName' = key;
    set(block_pos, 'air');
    set(block_pos, block, block_props, block_data);
    exit(print(format('w [', 'd Shop', 'w ] ', 'y Successfully created shop!')));
);

remove() -> (
    plr = player();
    block = query(plr, 'trace', 4.5, 'blocks');

    if (!block, exit(print(format('w [', 'd Shop', 'w ] ', 'y No shop.'))));
	if (!is_in_array(global_shoppables, block), exit(print(format('w [', 'd Shop', 'w ] ', 'y No shop.'))));

    block_pos = pos(block);
    block_data = block_data(block_pos);
    if (!block_data:'CustomName', exit(print(format('w [', 'd Shop', 'w ] ', 'y No shop.'))));

	block_props = [];
	for (keys(block_state(block_pos)),
		put(block_props, _i*2, _);
		put(block_props, _i*2 + 1, block_state(block_pos, _));
	);

    shop_data = read_shop_data(block_data:'CustomName');
    if (!shop_data, exit(print(format('w [', 'd Shop', 'w ] ', 'y No shop.'))));
    if (shop_data:'owner' != plr ~ 'uuid', exit(print(format('w [', 'd Shop', 'w ] ', 'y You do not own this shop.'))));
    
    delete(block_data, 'CustomName');
    set(block_pos, 'air');
    set(block_pos, block, block_props, block_data);
    exit(print(format('w [', 'd Shop', 'w ] ', 'y Successfully removed shop.')));
);

read_shop_data(data) -> (
    data = parse_nbt(data);

    if (!data:'extra', return(null));
    if (length(data:'extra') != 4, return(null));

    owner = data:'text';
    if (replace_first(owner, '[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}') != '', return(null));

    currency = data:'extra':0:'text';
    try(stack_limit(currency), 'unknown_item', return(null));

    price = number(data:'extra':1:'text');
    if (price == null, return(null));
    if (price < 1 || price > 64, return(null));

    nbt = data:'extra':2:'text';

    icon = data:'extra':3:'text';
    if (replace_first(icon, '(diamond)|(dollar)') != '', return(null));

    return({
        'owner' -> owner,
        'currency' -> currency,
        'price' -> price,
        'nbt' -> nbt,
        'icon' -> icon
    });
);

shop_screen_callback(screen, player, action, data, block, cart, shop_data, sounds, paid_slots, bought_items) -> (
    extra = '';
    if (bool(shop_data:'nbt'), extra = shop_data:'nbt');
    
    if (action == 'close',
        sound(sounds:1, pos(block), 0.5, 1, 'block');

        for(paid_slots, //Validation loop
            if (inventory_get(block, _) == bought_items:_i, continue());
            run('give @s ' + shop_data:'currency' + extra + ' ' + length(paid_slots) * shop_data:'price');
            close_screen(screen);
            print(format('w [', 'd Shop', 'w ] ', 'y Could not complete transaction. Your items have been refunded.'));
            return('cancel');
        );

        for(paid_slots, //Checkout loop
            item = inventory_get(block, _);
            if (extra,
                inventory_set(block, _, shop_data:'price', shop_data:'currency', extra),
                inventory_set(block, _, shop_data:'price', shop_data:'currency');
            );
            nbt = '';
            if (item:2, nbt = item:2);
            run('give @s ' + item:0 + nbt + ' ' + item:1);
        );

        close_screen(screen);
        return('cancel');
    );

    if (action != 'pickup', return('cancel'));

    if (data:'slot' >= 27,
        item = inventory_get(screen, data:'slot');
        if (!item, return('cancel'));

        slots_left = length(cart) - length(paid_slots);
    
        if (item:0 != shop_data:'currency', return('cancel'));
        if (item:1 < shop_data:'price', return('cancel'));
        if (str(item:2) != shop_data:'nbt', return('cancel'));
        if (slots_left < 1, return('cancel'));

        paid_for = floor(item:1 / shop_data:'price');
        if (paid_for > slots_left, paid_for = slots_left);
        inventory_set(screen, data:'slot', item:1 - (paid_for * shop_data:'price'));
        sound('entity.experience_orb.pickup', player ~ 'pos');

        for (cart,
            if (paid_for < 1, break());
            cart_slot = _;
            free = true;
            for (paid_slots, if (_ == cart_slot,
                free = false;
                break();
            ));
            if (free,
                paid_slots += _;
                bought_items += inventory_get(block, _);
                paid_for += -1;
            );
        );

        for (paid_slots,
            item = inventory_get(block, _);
            nbt = '{}';
            if (extra, nbt = extra);
            nbt = parse_nbt(nbt(nbt));
            nbt:'CurrencyItem' = true;
            nbt:'display' = {'Name' -> '{"italic":"false","text":"' + item:1 + 'x ' + item:0 + '"}\''};
            inventory_set(screen, _, shop_data:'price', shop_data:'currency', encode_nbt(nbt));
        );

        return('cancel');
    );

    item = inventory_get(block, data:'slot');
    if (!item, return('cancel'));
    if (item:0 == shop_data:'currency' && str(item:2) == shop_data:'nbt' && !item:2:'CurrencyItem', return('cancel'));

    if (inventory_get(screen, data:'slot'):2:'CurrencyItem',
        inventory_set(screen, data:'slot', item:1, item:0, item:2);
        for (paid_slots, if(_ == data:'slot', delete(paid_slots, _i); delete(bought_items, _i); break()));
        for (cart, if(_ == data:'slot', delete(cart, _i); break()));
        sound('item.bundle.remove_one', player ~ 'pos');
        run('give @s ' + shop_data:'currency' + extra + ' ' + shop_data:'price');
        return('cancel');
    );

    if (inventory_get(screen, data:'slot'):2:'ShopItem',
        inventory_set(screen, data:'slot', item:1, item:0, item:2);
        for (cart, if(_ == data:'slot', delete(cart, _i); break()));
        sound('item.bundle.remove_one', player ~ 'pos');
        return('cancel');
    );
    
    custom_marker = '';
    if (extra, custom_marker = ' (custom)');
    model_data = 1;
    if (shop_data:'icon' == 'dollar', model_data = 2);
    inventory_set(
        screen, data:'slot', 1, 'bundle',
        nbt('
        {
            ShopItem:1b,
            display:{
                Name:\'{"italic":"false","color":"aqua","text":"Cost: ' + shop_data:'price' + 'x ' + shop_data:'currency' + custom_marker + '"}\'
            },
            CustomModelData:' + model_data + ',
            Items:[{
                Count:' + item:1 + 'b,
                id:"minecraft:' + item:0 + '",
                tag:' + item:2 + '
            }]
        }')
    );
    cart += data:'slot';
    sound('item.bundle.insert', player ~ 'pos');

    return('cancel');
);

__on_player_right_clicks_block(player, item_tuple, hand, block, face, hitvec) -> (
	if (!is_in_array(global_shoppables, block), exit());

    block_data = block_data(pos(block));
    if (!block_data, exit());
    if (!block_data:'CustomName', exit());
    if (block_data:'Lock', exit());

    shop_data = read_shop_data(block_data:'CustomName');
    if (!shop_data, exit());
    if (shop_data:'owner' == player ~ 'uuid', exit());

    cart = [];
    paid_slots = [];
    bought_items = [];

    sounds = ['block.chest.open', 'block.chest.close', 'Chest'];
    if (is_in_array(block_tags(block), 'shulker_boxes'), sounds = ['block.shulker_box.open', 'block.shulker_box.close', 'Shulker Box']);
    if (block == 'barrel', sounds = ['block.barrel.open', 'block.barrel.close', 'Barrel']);
    
    sound(sounds:0, pos(block), 0.5, 1, 'block');
    
    shop_screen = create_screen(
        player,
        'generic_9x3',
        sounds:2 + ' Shop',
        _(screen, player, action, data, outer(block), outer(cart), outer(shop_data), outer(sounds), outer(paid_slots), outer(bought_items)) -> (
            shop_screen_callback(screen, player, action, data, block, cart, shop_data, sounds, paid_slots, bought_items);
        );
    );

    loop(27,
        slot = inventory_get(block, _);
        inventory_set(shop_screen, _, slot:1, slot:0, slot:2);
    );
    return('cancel');
);

__on_player_breaks_block(player, block) -> (
	if (!is_in_array(global_shoppables, block), exit());

    block_data = block_data(pos(block));
    if (!block_data:'CustomName', exit());

    shop_data = read_shop_data(block_data:'CustomName');
    if (!shop_data, exit());
    if (shop_data:'owner' != player ~ 'uuid', return('cancel'));
);

is_in_array(arr, val) -> (
	for (arr, if (val == _, return(true)));
	return(false);
);