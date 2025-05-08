// Takes an item's custom name and moves it to the next lore line
// Creative mode version, for survival version see items/lore.sc
// Requires the format_text library (util/format_text.scl)

// Requires Carpet LAB Addition (https://modrinth.com/mod/carpet-lab-addition)
// Check file history for versions not requiring that mod

import('format_text', 'format_text');

__config() -> {
	'commands' -> {
		'add <lore>' -> 'lore',
		'clear' -> ['clear', 0],
		'clear <lines>' -> 'clear'
	},
	'arguments' -> {
		'lore' -> {
			'type' -> 'text',
			'suggest' -> []
		},
		'lines' -> {
			'type' -> 'int',
			'min' -> 1,
			'suggest' -> [1]
		}
	}
};

lore(text) -> (
	plr = player();

	item = query(plr, 'holds');
	if (!item, exit(print(format('w [', 'd Lore', 'w ] ', 'y You aren\'t holding anything!'))));
	
	text = format_text('<head:italic=false />' + text);

	item_nbt = parse_nbt(item:2);
	if (!item_nbt:'components', item_nbt:'components' = {});

	lore = item_nbt:'components':'minecraft:lore';
	if (lore,
		lore += text,
		lore = [text];
	);

	item_nbt:'components':'minecraft:lore' = lore;
	item_nbt = encode_snbt(item_nbt);
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
	item_nbt = encode_snbt(item_nbt);
	inventory_set(plr, plr ~ 'selected_slot', item:1, item:0, item_nbt);
);