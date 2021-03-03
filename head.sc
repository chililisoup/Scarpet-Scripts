__config() -> {
   'commands' -> {
     '<username>' -> 'giveHead',
   },
   'arguments' -> {
      'username' -> {
         'type' -> 'term',
         'suggest' -> ['Steve']
      }
   },
   ['stay_loaded','true']
};

giveHead(name) -> (
   if (name != replace(name, '[^A-Za-z0-9_]') || length(name) > 16,
      print(format('w [','d Head','w ] ','y Invalid username!'));
      exit(),
      for([
         'opticality_',
         'oranjejoose',
         'rebot333',
         'av8cdan',
         'a1yk',
         'fortune8flea',
         'davie2tone',
         'sound_barrier',
         'eclolray'],
         if (_ == lower(name),
            print(format('w [','d Head','w ] ','y That username is blacklisted!'));
            exit();
         );
      );
   );
   plr = player();
   hand = query(plr, 'holds');
   if (hand:0 == 'player_head',
      if (!hand:2,
         inventory_set(plr, query(plr, 'selected_slot'), hand:1, 'player_head', '{SkullOwner:"' + name + '"}'),
         print(format('w [','d Head','w ] ','y That head is not blank!'));
      ),
      print(format('w [','d Head','w ] ','y You must be holding a blank player head!'));
   );
   exit();
);