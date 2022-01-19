// Gives you a player head with specified username
// Creative mode version, for survival version see items/head.sc

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
   );
   run('give @s minecraft:player_head{SkullOwner:"' + name + '"} ' + amount);
   exit();
);
