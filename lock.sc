__config() -> {
   'commands' -> {
     '<key>' -> 'lockBlock',
   },
   'arguments' -> {
      'key' -> {
         'type' -> 'term',
         'suggest' -> ['password']
      }
   },
   ['stay_loaded','true']
};

lockBlock(key) -> (
   p = player();
   current_gamemode = p~'gamemode';
   blok = query(p, 'trace', 4.5, 'blocks');
   if (blok,
      if (isInArray([
         'chest',
         'trapped_chest',
         'barrel',
         'hopper',
         'furnace',
         'blast_furnace',
         'smoker'
      ], blok),
         if (name != replace(name, '[^A-Za-z0-9-_+.]'),
            print(format('w [','d Lock','w ] ', 'y Invalid characters in key!'));
            exit();
         );
         blok_pos = pos(blok);
         blok_props = [];
         for (keys(block_state(blok_pos)),
            put(blok_props, _i*2, _);
            put(blok_props, _i*2 + 1, block_state(blok_pos, _));
         );
         blok_data = block_data(blok_pos);
         if (blok_data:'Lock',
            if (blok_data:'Lock' == key,
               delete(blok_data, 'Lock');
               set(blok_pos, blok, blok_props, blok_data);
               print(format('w [','d Lock','w ] ', 'y Successfully unlocked ', 'wb ' + blok, 'y .')),
               print(format('w [','d Lock','w ] ', 'y This ', 'wb ' + blok, 'y  is locked! Input correct password to unlock.'));
            ),
            put(blok_data, 'Lock', key);
            set(blok_pos, blok, blok_props, blok_data);
            print(format('w [','d Lock','w ] ', 'y Successfully locked ', 'wb ' + blok, 'y  with key ', 'wb ' + key, 'y .'));
         ),
         print(format('w [','d Lock','w ] ', 'y This block can\'t be locked!'));
      ),
      print(format('w [','d Lock','w ] ', 'y You\'re not looking at anything in range.'));
   );
   exit();
);

isInArray(arr, val) -> (
   for (arr,
      if (val == _,
         r = true;
         break();
      );
   );
   r
)