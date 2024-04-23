// Converts normal steve player heasd (minecraft:player_head w/ no nbt) into ones skinned to a username
// Survival mode version, for creative see creative/head.sc

global_blocked_names = [ // Prevents heads from being skinned to any name in the list
	//'name',
	//'other_name'
];

__config() -> {
	'commands' -> {
		'claim' -> 'claim_head',
		'paint <username>' -> ['paint_head', 1],
		'paint <username> <amount>' -> 'paint_head',
		'claim_oaken' -> 'claim_oaken_head'
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

claim_head() -> (
	plr = player();
	hand = query(plr, 'holds');
	if (hand:0 == 'paper' && hand:2:'components':'minecraft:custom_data':'CommunityToken',
		inventory_set(plr, query(plr, 'selected_slot'), hand:1 - 1);
		run('give @s player_head 16'),

		print(format('w [','d Head','w ] ','y You must be holding a Community Token!'));
	);
);

paint_head(name, amount) -> (
	if (name != replace(name, '[^A-Za-z0-9_]') || length(name) > 16,
		exit(print(format('w [', 'd Head', 'w ] ', 'y Invalid username!'))),
		for (global_blocked_names,
			if (_ == lower(name), exit(print(format('w [', 'd Head', 'w ] ', 'y That username is blacklisted!'))));
		);
	);

	plr = player();

	hand = query(plr, 'holds');
	if (hand:0 != 'player_head', exit(print(format('w [', 'd Head', 'w ] ', 'y You must be holding a blank player head!'))));
	if (hand:2:'components':'minecraft:profile', exit(print(format('w [', 'd Head', 'w ] ', 'y That head is not blank!'))));

	if (amount < hand:1,
		run('give @s minecraft:player_head[profile="' + name + '"] ' + amount);
		inventory_set(plr, plr ~ 'selected_slot', hand:1 - amount),
		inventory_set(plr, plr ~ 'selected_slot', null, null, '{count:' + hand:1 + ',id:"minecraft:player_head",components:{profile:"' + name + '"}}}');
	);
);

claim_oaken_head() -> (
	plr = player();
	hand = query(plr, 'holds');
	if (hand:0 == 'paper' && hand:2:'components':'minecraft:custom_data':'CommunityToken',
		inventory_set(plr, query(plr, 'selected_slot'), hand:1 - 1);
		run('give @s minecraft:player_head[custom_name=\'{"text":"§aOaken§r"}\',minecraft:profile={name:"",properties:[{name:"textures",value:"eyJ0aW1lc3RhbXAiOjE1MDUxNTg3Mjc3MTgsInByb2ZpbGVJZCI6IjMzMjg1ZjMwYzhlMDRmZTA5MWE4ODAzOTNmODQyMmZlIiwicHJvZmlsZU5hbWUiOiJPYWtlbl9fIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifSwidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jYmQ1MzA0NDI4NTY4Y2Q5NzJmMzhkYmViZDVlZWRjNmRiZjgxZTU0OTk3ZWMzMTZkYmRmNjBhZmRiOWQyNjgifX19"}],id:[I;-1096819554,444548884,-1917264242,-1866419970]}]'),
		print(format('w [','d Head','w ] ','y You must be holding a Community Token!'));
	);
);