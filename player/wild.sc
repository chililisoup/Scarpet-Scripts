// Teleports the user to a random location on the surface of the map.

global_teleport_radius = 5000; // Square radius in blocks around 0,0
global_teleport_timeout = 180; // Time in seconds between uses

__config() -> {};

__command() -> (
	plr = player();

	if (!read_file(plr, 'json'),
		write_file(plr, 'json', tick_time());
		run('/spreadplayers ~ ~ 0 '+global_teleport_radius+' false '+plr);
		print(format('w [', 'd Wild', 'w ] ', 'y Sent to random destination!'));
		schedule(global_teleport_timeout * 20, clearTimeout() -> (
			delete_file(player(), 'json');
		)),

		wait_time = floor(((global_teleport_timeout * 20) - tick_time()+read_file(plr, 'json')) / 20);
		if (wait_time < 0,
			delete_file(plr, 'json');
			wait_time = 0;
		);
		print(format('w [', 'd Wild', 'w ] ', 'y Please wait', 'b  ' + wait_time, 'y  more second(s) before doing this again!'));
	);

	exit();
);

__on_server_shuts_down() -> (
	delete_file(player(), 'json');
);