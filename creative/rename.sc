// Renames held item using Bukkit formatting
// Requires the format_text library (util/format_text.scl)

// Requires Carpet LAB Addition (https://modrinth.com/mod/carpet-lab-addition)
// Check file history for versions not requiring that mod

import('format_text', 'format_text');

__config() -> {
	'commands' -> {
        '' -> 'clear_name',
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

    item_nbt = parse_nbt(item:2);
	if (!item_nbt:'components', item_nbt:'components' = {});

	text = format_text('<head:italic=false />' + text);
	
	item_nbt:'components':'minecraft:custom_name' = text;
	item_nbt = encode_snbt(item_nbt);
	inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, item_nbt);
);

clear_name() -> (
    plr = player();

	item = query(plr, 'holds');
	if (!item, exit(print(format('w [', 'd Rename', 'w ] ', 'y You aren\'t holding anything!'))));

    item_nbt = parse_nbt(item:2);

	delete(item_nbt:'components':'minecraft:custom_name');

    if (!item_nbt || item_nbt == {} || item_nbt == 'null',
        inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0),
        inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, encode_snbt(item_nbt));
    );
);
