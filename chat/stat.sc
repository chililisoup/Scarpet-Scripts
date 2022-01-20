// Allows non-opped players to display scoreboard statistics for a few seconds

global_display_duration = 10; // Number of seconds to display for

__config() -> {
	'commands' -> {
	  'help' -> 'help',
	  'show <statistic>' -> 'show'
	},
	'arguments' -> {
		'statistic' -> {
			'type' -> 'objective'
		}
	}
};

show(stat) -> (
	if (read_file('stat_showing', 'json'), exit(print(format('w [', 'd Stat', 'w ] ', 'y A stat is already being displayed!'))));

	scoreboard_display('sidebar', stat:0);
	write_file('stat_showing', 'json', true);
	schedule(global_display_duration * 20, clear() -> (
		scoreboard_display('sidebar', null);
		delete_file('stat_showing', 'json');
	));

	exit();
);

help() -> (
	print('g.  -> General statistics');
	print('b.  -> Times broken (tools)');
	print('c.  -> Times crafted');
	print('q.  -> Times dropped');
	print('p.  -> Times picked up');
	print('cu. -> Custom stats (more general stuff)');
	print('d.  -> Times killed by');
	print('k.  -> Times killed');
	print('m.  -> Times mined');
	print('u.  -> Times used');
	exit();
);

__on_server_shuts_down() -> (
	scoreboard_display('sidebar', null);
	delete_file('stat_showing', 'json');
);