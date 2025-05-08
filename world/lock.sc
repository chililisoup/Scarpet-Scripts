// Locks/unlocks a container with /lock <password>

global_lockables = [
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
		'<key>' -> 'lock_block'
	},
	'arguments' -> {
		'key' -> {
			'type' -> 'term',
			'suggest' -> ['password']
		}
	}
};

lock_block(key) -> (
	plr = player();
	current_block = query(plr, 'trace', 4.5, 'blocks');

	if (!current_block, exit(print(format('w [', 'd Lock', 'w ] ', 'y You\'re not looking at anything in range.'))));
	if (!is_in_array(global_lockables, current_block), exit(print(format('w [', 'd Lock', 'w ] ', 'y This block can\'t be locked!'))));
	if ('' != replace(name, '[A-Za-z0-9-_+.]'), exit(print(format('w [', 'd Lock', 'w ] ', 'y Invalid characters in key!'))));

	block_pos = pos(current_block);
	block_props = [];
	for (keys(block_state(block_pos)),
		put(block_props, _i*2, _);
		put(block_props, _i*2 + 1, block_state(block_pos, _));
	);

	block_data = block_data(block_pos);
	if (block_data:'lock',
		if (block_data:'lock':'components':'minecraft:custom_name' == key,
			delete(block_data, 'lock');
			set(block_pos, current_block, block_props, block_data);
			print(format('w [', 'd Lock', 'w ] ', 'y Successfully unlocked ', 'wb ' + current_block, 'y .')),
			
			print(format('w [', 'd Lock', 'w ] ', 'y This ', 'wb ' + current_block, 'y  is locked! Input correct password to unlock.'));
		),

		put(block_data, 'lock', encode_nbt({'components' -> {'minecraft:custom_name' -> key}}));
		set(block_pos, current_block, block_props, block_data);
		print(format('w [', 'd Lock', 'w ] ', 'y Successfully locked ', 'wb ' + current_block, 'y  with key ', 'wb ' + key, 'y .'));
	);
	exit();
);

__on_player_breaks_block(player, block) -> (
	if (block_data(pos(block)):'Lock', return('cancel'));
);

is_in_array(arr, val) -> (
	for (arr, if (val == _, return(true)));
	return(false);
);
