// Lets players use the shadow function of /player without allowing access to the other functions
// Does not (currently) work with nick name mods. YMMV on this though

__config() -> {
	'commands' -> {
		'' -> 'shadow',
		'schedule <action> continuous' -> ['shadow_schedule', 'continuous'],
		'schedule <action> interval <ticks>' -> 'shadow_schedule',
		'schedule cancel' -> 'shadow_schedule_cancel'
	},
	'arguments' -> {
		'action' -> {
			'type' -> 'term',
			'options' -> [
				'use',
				'attack'
			]
		},
		'ticks' -> {
			'type' -> 'int',
			'min' -> 0,
			'suggest' -> [20]
		}
	}
};

shadow() -> (
	run('player ' + player() + ' shadow');
	exit();
);

shadow_schedule(action, period) -> (
	if (period == 'continuous',
		run('player ' + player() + ' ' + action + ' continuous');
		print(format('w [', 'd Shadow', 'w ] ', 'y Scheduled ', 'wb ' + action, 'y  to be done ', 'wb continuously', 'y .')),

		run('player ' + player() + ' ' + action + ' interval ' + period);
		print(format('w [', 'd Shadow', 'w ] ', 'y Scheduled ', 'wb ' + action, 'y  to be done every ', 'wb ' + period + ' ticks', 'y .'));
	);
	
	exit();
);

shadow_schedule_cancel() -> (
	run('player ' + player() + ' stop');
	print(format('w [', 'd Shadow', 'w ] ', 'y Canceled any scheduled actions.'));
	exit();
);