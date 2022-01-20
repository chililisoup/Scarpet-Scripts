// Sends private messages
// r.sc strongly suggested, allows for replying
// Requires the message library (chat/message.scl)

import('message', 'send_message');

__config() -> {
	'commands' -> {
		'<player> <message>' -> 'message'
	},
	'arguments' -> {
		'player' -> {
			'type' -> 'players',
			'single' -> true
		},
		'message' -> {
			'type' -> 'text',
			'suggest' -> []
		}
	}
};

message(to, msg) -> (
	send_message(player(), to, msg);
	exit();
);