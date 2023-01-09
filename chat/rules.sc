// A simple command that lists the rules from the global_rules array

global_rules = [
	'Do not steal',
	'Do not grief',
	'Do not be rude',
	'Obey the rules',
	'Pay me $5 every other Tuesday'
];

__config() -> {};

__command() -> (
	print(format('db Rules', 'w :'));
	for (global_rules,
		color = 'y ';
		if (_i % 2, color = '#FFFABF ');
		print(format('d ' + str(_i + 1), 'w ) ', color + _));
	);
	exit();
);