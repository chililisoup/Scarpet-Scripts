// Standard /hat command. Puts held item on your head armor slot

__config() -> {};

__command() -> (
	plr = player();

	new_held = query(plr, 'holds'); //Unable to put print inside exit like other scripts, prints twice. Don't know why
	if (!new_held, print(format('w [', 'd Hat', 'w ] ', 'y You aren\'t holding anything!')); exit());

	old_hat = inventory_get(plr, 39);
	if (old_hat:2 ~ 'binding_curse', print(format('w [', 'd Hat', 'w ] ', 'y Your current hat was glued to your head by a rude!')); exit());

	inventory_set(plr, 39, new_held:1, new_held:0, new_held:2);
	if (old_hat,
		inventory_set(plr, query(plr, 'selected_slot'), old_hat:1, old_hat:0, old_hat:2),
		inventory_set(plr, query(plr, 'selected_slot'), 0);
	);

	print(format('w [', 'd Hat', 'w ] ', 'y You put on a fancy hat!'));
	exit();
);