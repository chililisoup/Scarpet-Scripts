// Applies bukkit formatting to signs after clicked
// Requires the format_text library (util/format_text.scl)

import('format_text', 'format_text');

global_sign_types = [
	'oak_sign',
	'spruce_sign',
	'birch_sign',
	'jungle_sign',
	'acacia_sign',
	'dark_oak_sign',
	'crimson_sign',
	'warped_sign',
	'oak_wall_sign',
	'spruce_wall_sign',
	'birch_wall_sign',
	'jungle_wall_sign',
	'acacia_wall_sign',
	'dark_oak_wall_sign',
	'crimson_wall_sign',
	'warped_wall_sign'
];

__config() -> {};

__on_player_interacts_with_block(player, hand, block, face, hitvec) -> (
	if (!is_in_array(global_sign_types, block), exit());

	block_pos = pos(block);
	block_props = [];
	for (keys(block_state(block)),
		block_props:(_i * 2) = _;
		block_props:(_i * 2 + 1) = block_state(block, _);
	);

	block_nbt = block_data(block_pos);
	for (range(1, 5),
		text_nbt = decode_json(block_nbt:('Text' + _));
		if (text_nbt:'text' && length(keys(text_nbt)) == 1,
			block_nbt:('Text' + _) = '\'' + replace(format_text(text_nbt:'text'), '\\\\', '\\\\\\\\') + '\'';
		);
	);

	set(block_pos, 'air');
	set(block_pos, block, block_props, block_nbt);
);

is_in_array(arr, val) -> (
	for (arr, if (val == _, return(true)));
	return(false);
);