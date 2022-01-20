// Converts normal steve player heasd (minecraft:player_head w/ no nbt) into ones skinned to a username
// Survival mode version, for creative see creative/head.sc

global_blocked_names = [ // Prevents heads from being skinned to any name in the list
	//'name',
	//'other_name'
];

__config() -> {
	'commands' -> {
		'<username>' -> ['give_head', 1],
		'<username> <amount>' -> 'give_head'
	},
	'arguments' -> {
		'username' -> {
			'type' -> 'term',
			'suggest' -> ['Steve']
		},
		'amount' -> {
			'type' -> 'int',
			'min' -> 1,
			'max' -> 64,
			'suggest' -> [1, 64]
		}
	}
};

give_head(name, amount) -> (
	if (name != replace(name, '[^A-Za-z0-9_]') || length(name) > 16,
		exit(print(format('w [', 'd Head', 'w ] ', 'y Invalid username!'))),
		for (global_blocked_names,
			if (_ == lower(name), exit(print(format('w [', 'd Head', 'w ] ', 'y That username is blacklisted!'))));
		);
	);

	plr = player();

	hand = query(plr, 'holds');
	if (hand:0 != 'player_head', exit(print(format('w [', 'd Head', 'w ] ', 'y You must be holding a blank player head!'))));
	if (hand:2, exit(print(format('w [', 'd Head', 'w ] ', 'y That head is not blank!'))));

	if (amount < hand:1,
		run('give @s minecraft:player_head{SkullOwner:"' + name + '"} ' + amount);
		inventory_set(plr, query(plr, 'selected_slot'), hand:1 - amount),
		inventory_set(plr, query(plr, 'selected_slot'), hand:1, 'player_head', '{SkullOwner:"' + name + '"}');
	);

	exit();
);