// Applies text formatting to signs when they're edited

// Requires Carpet LAB Addition (https://modrinth.com/mod/carpet-lab-addition)
// Check file history for versions not requiring that mod

// Requires the format_text library file (util/format_text.scl)

import('format_text', 'format_text');

__config() -> {
    'scope' -> 'global'
};

__on_player_edits_sign(player, block) -> (
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
		front_text = front_messages:_;
		back_text = back_messages:_;

		if (length(front_text) && length(keys(front_text)) == 0,
			front_messages:_ = format_text(front_text);
		);
		if (length(back_text) && length(keys(back_text)) == 0,
			back_messages:_ = format_text(back_text);
		);
	);

	block_nbt:'front_text':'messages' = front_messages;
	block_nbt:'back_text':'messages' = back_messages;

	block_nbt = encode_snbt(block_nbt);

	without_updates(set(block_pos, 'air'));
	without_updates(set(block_pos, block, block_props, block_nbt));
);
