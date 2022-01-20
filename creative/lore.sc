// Takes an item's custom name and moves it to the next lore line
// Creative mode version, for survival version see items/lore.sc
// Requires the format_text library (util/format_text.scl)

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

	text = format_text(text);
	text = decode_json(text);
	text:0 = {'italic' -> 'false', 'text' -> ''};
	text = encode_json(text);

	nbt = parse_nbt(item:2);
	if (nbt != 'null',
		if (nbt:'display',
			if (nbt:'display':'Lore',
				nbt:'display':'Lore' += text,
				nbt:'display':'Lore' = [text];
			),
			nbt:'display' = {'Lore' -> [text]};
		),
		nbt = {'display' -> {'Lore' -> [text]}};
	);
	
	inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, encode_nbt(nbt));

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