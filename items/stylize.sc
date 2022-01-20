// Allows you to use hex color codes in item names by replacing the character '#' with a color switch
// Usage: /stylize <hex>
// Ex:    /stylize ffffff

__config() -> {
	'commands' -> {
		'<hex>' -> 'stylize'
	},
	'arguments' -> {
		'hex' -> {
			'type' -> 'term',
			'suggest' -> ['ffffff']
		}
	}
};

stylize(hex) -> (
	if (length(replace(hex, '^[a-fA-F0-9]{6}$')) != 0 || length(hex) != 6,
		exit(print(format('w [', 'd Stylize', 'w ] ', 'y You must input a valid hex color!')))
	);

	plr = player();

	item = query(plr, 'holds');
	if (!item, exit(print(format('w [', 'd Stylize', 'w ] ', 'y You aren\'t holding anything!'))));

	name = item:2:'display':'Name';
	if (!name, exit(print(format('w [', 'd Stylize', 'w ] ', 'y Your item isn\'t renamed!'))));

	name = '[' + join('"},{"italic":"false","color":"#' + hex + '","text":"', split('#', name)) + ']';
	itemNBT = parse_nbt(item:2:'display');
	itemNBT:'Name' = name;
	item:2:'display' = encode_nbt(itemNBT);
	inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, item:2);
	
	exit();
);