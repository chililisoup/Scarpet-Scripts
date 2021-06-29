//Converts normal steve player heasd (minecraft:player_head w/ no nbt) into ones skinned to a username

global_blocked_names = [ //Can block names here, or make empty to block none
   'name',
   'other_name'
];

__config() -> {
   'commands' -> {
     '<username>' -> ['giveHead', 1],
     '<username> <amount>' -> 'giveHead',
   },
   'arguments' -> {
      'username' -> {
         'type' -> 'term',
         'suggest' -> ['Steve']
      },
      'amount' -> {
         'type' -> 'int',
         'min' -> 1,
         'max' -> 64,
         'suggest' -> [1, 64]
      }
   },
   ['stay_loaded','true']
};

giveHead(name, amount) -> (
   if (name != replace(name, '[^A-Za-z0-9_]') || length(name) > 16,
      print(format('w [','d Head','w ] ','y Invalid username!'));
      exit(),
      for(global_blocked_names,
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
         if (amount < hand:1,
            run('give @s minecraft:player_head{SkullOwner:"' + name + '"} ' + amount);
            inventory_set(plr, query(plr, 'selected_slot'), hand:1 - amount),
            inventory_set(plr, query(plr, 'selected_slot'), hand:1, 'player_head', '{SkullOwner:"' + name + '"}');
         ),
         print(format('w [','d Head','w ] ','y That head is not blank!'));
      ),
      print(format('w [','d Head','w ] ','y You must be holding a blank player head!'));
   );
   exit();
);
