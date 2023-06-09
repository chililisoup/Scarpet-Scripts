// Applies bukkit formatting to signs after clicked
// Requires the format_text library (util/format_text.scl)

import('format_text', 'format_text');

__config() -> {};

__on_player_right_clicks_block(player, item_tuple, hand, block, face, hitvec) -> (
	if (item_tuple, exit());
	if (!block_tags(block, 'all_signs'), exit());
	if (!player ~ 'sneaking', exit());

	block_pos = pos(block);
	block_props = [];
	for (keys(block_state(block)),
		block_props:(_i * 2) = _;
		block_props:(_i * 2 + 1) = block_state(block, _);
	);

	block_nbt = parse_nbt(block_data(block_pos));
	front_messages = block_nbt:'front_text':'messages';
	back_messages = block_nbt:'back_text':'messages';

	for (range(0, 4),
		front_text = decode_json(front_messages:_);
		back_text = decode_json(back_messages:_);

		if (length(front_text:'text') && length(keys(front_text)) == 1,
			front_messages:_ = format_text(front_text:'text');
		);
		if (length(back_text:'text') && length(keys(back_text)) == 1,
			back_messages:_ = format_text(back_text:'text');
		);
	);

	block_nbt:'front_text':'messages' = front_messages;
	block_nbt:'back_text':'messages' = back_messages;

	block_nbt = encode_nbt(block_nbt);

	without_updates(set(block_pos, 'air'));
	without_updates(set(block_pos, block, block_props, block_nbt));
	
	return('cancel');
);

is_in_array(arr, val) -> (
	for (arr, if (val == _, return(true)));
	return(false);
);
