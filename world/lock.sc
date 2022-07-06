// Locks/unlocks a container with /lock <password>

global_lockables = [
	'chest',
	'trapped_chest',
	'barrel',
	'hopper',
	'furnace',
	'blast_furnace',
	'smoker'
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
	current_gamemode = plr ~ 'gamemode';
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
	if (block_data:'Lock',
		if (block_data:'Lock' == key,
			delete(block_data, 'Lock');
			set(block_pos, current_block, block_props, block_data);
			print(format('w [', 'd Lock', 'w ] ', 'y Successfully unlocked ', 'wb ' + current_block, 'y .')),
			
			print(format('w [', 'd Lock', 'w ] ', 'y This ', 'wb ' + current_block, 'y  is locked! Input correct password to unlock.'));
		),

		put(block_data, 'Lock', key);
		set(block_pos, current_block, block_props, block_data);
		print(format('w [', 'd Lock', 'w ] ', 'y Successfully locked ', 'wb ' + current_block, 'y  with key ', 'wb ' + key, 'y .'));
	);
	exit();
);

is_in_array(arr, val) -> (
	for (arr, if (val == _, return(true)));
	return(false);
);