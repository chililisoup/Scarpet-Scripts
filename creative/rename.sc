// Renames held item using Bukkit formatting
// Requires the format_text library (util/format_text.scl)

import('format_text', 'format_text');

__config() -> {
	'commands' -> {
        '' -> ['rename', false],
		'<name>' -> 'rename'
	},
	'arguments' -> {
		'name' -> {
			'type' -> 'text',
			'suggest' -> []
		}
	}
};

rename(text) -> (
	plr = player();

	item = query(plr, 'holds');
	if (!item, exit(print(format('w [', 'd Rename', 'w ] ', 'y You aren\'t holding anything!'))));

    nbt = parse_nbt(item:2);

	if (!text,
        delete(nbt:'display':'Name');
        if (nbt:'display' == {}, delete(nbt:'display'));
        if (!nbt || nbt == {} || nbt == 'null',
            inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0),
            inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, encode_nbt(nbt));
        );
        exit();
    );

	text = format_text(text);
    text = decode_json(text);
    text:0 = {'italic' -> 'false', 'text' -> ''};
    text = encode_json(text);

	if (nbt != 'null',
		if (nbt:'display',
			if (nbt:'display':'Name',
				nbt:'display':'Name' = text;
			),
			nbt:'display' = {'Name' -> text};
		),
		nbt = {'display' -> {'Name' -> text}};
	);
	
	inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, encode_nbt(nbt));

	exit();
);