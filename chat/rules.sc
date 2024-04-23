// A simple command that lists the rules from the global_rules array

global_rules = [
	'No using any kind of hacks. Some client side mods are allowed, such as optifine, replay mod, minihud, litematica, etc. Ask if unsure!',
	'No griefing/stealing. Obviously can be stretched, but only to a limit. Just treat others how you would want to be treated.',
	'No starting drama (rp is fine). Just ask questions and work through problems like an adult.',
	'PvP is only allowed when both parties consent.',
	'Stick to build theme in themed areas. If you are unsure if an area is themed, just ask! We are always happy to help!',
	'No politics. Please keep it to yourself.',
	'Cussing is okay, but stay mature. No name calling or anything related.',
	'No using slurs. This includes racism/sexism/etc..',
	'Please don\'t spam. It is not nice'
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