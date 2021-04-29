//Standard /hat command. Puts held item on your head armor slot

__config() -> (
   m(
      l('stay_loaded','true')
   )
);

__command() ->
(
   plr = player();
   old = inventory_get(plr, 39);
   new = query(plr, 'holds');
   if (new,
      if (!old || !old:2 || !(old:2 ~ 'binding_curse'),
         inventory_set(plr, 39, new:1, new:0, new:2);
         if (old,
            inventory_set(plr, query(plr, 'selected_slot'), old:1, old:0, old:2),
            inventory_set(plr, query(plr, 'selected_slot'), 0)
         );
         print(format('w [','d Hat','w ] ','y You put on a fancy hat!')),
         
         print(format('w [','d Hat','w ] ','y Your current hat was glued to your head by a rude!'))
      ),
      print(format('w [','d Hat','w ] ','y You aren\'t holding anything!'))
   );
   exit();
);
