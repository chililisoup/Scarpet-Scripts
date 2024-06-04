// Prints formatted text (debug tool for format_text.scl)

import('format_text', 'format_text');

__config() -> {
    'commands' -> {
        '' -> ['print_message', ''],
		'<message>' -> 'print_message'
	},
    'arguments' -> {
		'message' -> {
			'type' -> 'text',
			'suggest' -> ['']
		}
	}
};

print_message(message) -> (
    message = format_text(message);
    print(format('w ' + message, '^w Click to copy', '&' + message));
    run('tellraw @s ' + message);
);