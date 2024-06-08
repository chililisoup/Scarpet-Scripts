// Allows you to make use of formatting in item names
// Usage:
//   /stylize
//     Will update the item name using Bukkit formatting
//   /stylize <hex>
//     Ex: /stylize FFFFFF
//     Will use the symbol "#" as a marker to use that hex color
//
// Requires the format_text library (util/format_text.scl)

import('format_text', 'format_text', 'json_to_markup');

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

	item = plr ~ 'holds';
	if (!item, exit(print(format('w [', 'd Stylize', 'w ] ', 'y You aren\'t holding anything!'))));

	name = item:2:'components':'minecraft:custom_name';
	if (!name, exit(print(format('w [', 'd Stylize', 'w ] ', 'y Your item isn\'t renamed!'))));

	name = json_to_markup(name);

	if (hex, name = replace(name, '(?<!\\\\)(?<![^\\\\]<[^>]+)(?<!^<[^>]+)#', '<c:#' + hex + '>'));

	name = format_text('<head:italic=false />' + name);
	
	item_nbt = parse_nbt(item:2);
	item_nbt:'components':'minecraft:custom_name' = name;
	item_nbt = encode_nbt(item_nbt);
	inventory_set(plr, plr ~ 'selected_slot', item:1, item:0, item_nbt);
);