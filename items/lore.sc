// Takes an item's custom name and moves it to the next lore line
// Survival mode version, for creative version see creative/lore.sc

__config() -> {
	'commands' -> {
	  '' -> 'lore',
	  'clear' -> ['clear', 0],
	  'clear <lines>' -> 'clear'
	},
	'arguments' -> {
		'lines' -> {
			'type' -> 'int',
			'min' -> 1,
			'suggest' -> [1]
		}
	}
};

lore() -> (
	plr = player();

	item = plr ~ 'holds';
	if (!item, exit(print(format('w [', 'd Lore', 'w ] ', 'y You aren\'t holding anything!'))));


	item_nbt = parse_nbt(item:2);

	name = item_nbt:'components':'minecraft:custom_name';
	if (!name, exit(print(format('w [', 'd Lore', 'w ] ', 'y Your item isn\'t renamed!'))));

	lore = item_nbt:'components':'minecraft:lore';
	if (lore,
		lore += name,
		lore = [name];
	);

	delete(item_nbt:'components':'minecraft:custom_name');
	item_nbt:'components':'minecraft:lore' = lore;
	item_nbt = encode_nbt(item_nbt);
	inventory_set(plr, plr ~ 'selected_slot', item:1, item:0, item_nbt);
);

clear(lines) -> (
	plr = player();

	item = plr ~ 'holds';
	if (!item, exit(print(format('w [', 'd Lore', 'w ] ', 'y You aren\'t holding anything!'))));

	item_nbt = parse_nbt(item:2);
	if (!item_nbt:'components':'minecraft:lore', exit(print(format('w [', 'd Lore', 'w ] ', 'y No lore to clear.'))));

	if (lines,
		for (range(0, lines),
			delete(item_nbt:'components':'minecraft:lore', -1);
			if (!length(item_nbt:'components':'minecraft:lore'),
				delete(item_nbt:'components':'minecraft:lore');
				break();
			);
		),
		delete(item_nbt:'components':'minecraft:lore');
	);
	
	if (!item_nbt:'components':'minecraft:lore', delete(item_nbt:'components':'minecraft:lore'));
	item_nbt = encode_nbt(item_nbt);
	inventory_set(plr, plr ~ 'selected_slot', item:1, item:0, item_nbt);
);