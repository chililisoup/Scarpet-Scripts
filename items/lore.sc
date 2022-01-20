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

	item = query(plr, 'holds');
	if (!item, exit(print(format('w [', 'd Lore', 'w ] ', 'y You aren\'t holding anything!'))));

	name = item:2:'display':'Name';
	if (!name, exit(print(format('w [', 'd Lore', 'w ] ', 'y Your item isn\'t renamed!'))));

	lore = item:2:'display':'Lore';
	name = replace(name, '\'', '\\\\\'');
	if (lore,
		lore = replace(lore, '.{1}$');
		lore += ',\'' + name + '\']',
		lore = '[\'' + name + '\']';
	);
	item:2:'display' = '{Lore: ' + lore + '}';

	inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, item:2);

	exit();
);

clear(lines) -> (
	plr = player();

	item = query(plr, 'holds');
	if (!item, exit(print(format('w [', 'd Lore', 'w ] ', 'y You aren\'t holding anything!'))));

	nbt = parse_nbt(item:2);
	if (!nbt:'display':'Lore', exit(print(format('w [', 'd Lore', 'w ] ', 'y No lore to clear.'))));

	if (lines,
		for (range(0, lines),
			delete(nbt:'display':'Lore', -1);
			if (!length(nbt:'display':'Lore'),
				delete(nbt:'display':'Lore');
				break();
			);
		),
		delete(nbt:'display':'Lore');
	);
	
	if (!nbt:'display', delete(nbt:'display'));
	nbt = encode_nbt(nbt);
	if (!nbt, nbt = null);

	inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, nbt);
	
	exit();
);