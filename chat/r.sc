// Replies to messages sent through /m or /r
// Requires m (chat/m.sc)
// Requires the message library (chat/message.scl)

import('message', 'send_message');

__config() -> {
	'commands' -> {
		'<message>' -> 'message'
	},
	'arguments' -> {
		'message' -> {
			'type' -> 'text',
			'suggest' -> []
		}
	}
};

message(msg) -> (
	send_message(player(), read_file('msg_' + player(), 'shared_text'):0, msg);
	exit();
);