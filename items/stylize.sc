// Allows you to make use of formatting in item names
// Usage:
//   /stylize
//     Will update the item name using Bukkit formatting
//   /stylize <hex>
//     Ex: /stylize FFFFFF
//     Will use the symbol "#" as a marker to use that hex color
//
// Requires the format_text library (util/format_text.scl)

import('format_text', 'format_text', 'json_to_markdown');

__config() -> {
	'commands' -> {
		'' -> ['stylize', false],
		'<hex>' -> 'stylize'
	},
	'arguments' -> {
		'hex' -> {
			'type' -> 'term',
			'suggester' -> _(args) -> (
				if (args:'hex' ~ '[^0-9A-Fa-f]' || length(args:'hex') > 6, return([args:'hex' + 'INVALID']));
				if (!length(args:'hex'),     return(['ffffff']));
				if (length(args:'hex') == 1, return([args:'hex'*6]));
				if (length(args:'hex') == 2, return([args:'hex'*3]));
				if (length(args:'hex') == 3, return([args:'hex'*2]));
				if (length(args:'hex') == 4, return([args:'hex'+'00']));
				if (length(args:'hex') == 5, return([args:'hex'+'0']));
			)
		}
	}
};

stylize(hex) -> (
	if (hex && (length(replace(hex, '^[a-fA-F0-9]{6}$')) != 0 || length(hex) != 6),
		exit(print(format('w [', 'd Stylize', 'w ] ', 'y You must input a valid hex color!')));
	);

	plr = player();

	item = query(plr, 'holds');
	if (!item, exit(print(format('w [', 'd Stylize', 'w ] ', 'y You aren\'t holding anything!'))));

	name = item:2:'display':'Name';
	if (!name, exit(print(format('w [', 'd Stylize', 'w ] ', 'y Your item isn\'t renamed!'))));

	itemNBT = parse_nbt(item:2);
	name = json_to_markdown(name);

	if (hex, name = replace(name, '(?<!\\\\)(?<![^\\\\]<[^>]+)(?<!^<[^>]+)#', '<c:#' + hex + '>'));

	name = format_text(name);
	name = decode_json(name);
	put(name, 0, {'italic' -> false, 'text' -> ''}, 'insert');
	name = encode_json(name);

	itemNBT:'display':'Name' = name;
	item:2 = encode_nbt(itemNBT);
	inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, item:2);
	
	exit();
);
