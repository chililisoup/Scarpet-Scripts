//Applies bukkit formatting to signs after clicked

global_sign_types = [
   'oak_sign',
   'spruce_sign',
   'birch_sign',
   'jungle_sign',
   'acacia_sign',
   'dark_oak_sign',
   'crimson_sign',
   'warped_sign'
];

__config() -> {
   ['stay_loaded','true']
};

__on_player_interacts_with_block(player, hand, block, face, hitvec) -> (
   if (is_in_array(global_sign_types, block),
      block_pos = pos(block);
      block_props = [];
      for (keys(block_state(block)),
         put(block_props, _i*2, _);
         put(block_props, _i*2 + 1, block_state(block, _));
      );
      print(block_props);
      block_nbt = block_data(block_pos);
      put(block_nbt, 'Text1', format_text(block_nbt:'Text1'));
      put(block_nbt, 'Text2', format_text(block_nbt:'Text2'));
      put(block_nbt, 'Text3', format_text(block_nbt:'Text3'));
      put(block_nbt, 'Text4', format_text(block_nbt:'Text4'));
      set(block_pos, 'air');
      set(block_pos, block, block_props, block_nbt);
   );
);

format_text(string) -> (
   string = join('"},{"color":"black","text":"', split('&0', string));
   string = join('"},{"color":"dark_blue","text":"', split('&1', string));
   string = join('"},{"color":"dark_green","text":"', split('&2', string));
   string = join('"},{"color":"dark_aqua","text":"', split('&3', string));
   string = join('"},{"color":"dark_red","text":"', split('&4', string));
   string = join('"},{"color":"dark_purple","text":"', split('&5', string));
   string = join('"},{"color":"gold","text":"', split('&6', string));
   string = join('"},{"color":"gray","text":"', split('&7', string));
   string = join('"},{"color":"dark_gray","text":"', split('&8', string));
   string = join('"},{"color":"blue","text":"', split('&9', string));
   string = join('"},{"color":"green","text":"', split('&a', string));
   string = join('"},{"color":"aqua","text":"', split('&b', string));
   string = join('"},{"color":"red","text":"', split('&c', string));
   string = join('"},{"color":"light_purple","text":"', split('&d', string));
   string = join('"},{"color":"yellow","text":"', split('&e', string));
   string = join('"},{"color":"white","text":"', split('&f', string));
   string = join('"},{"text":"', split('&r', string));
   
   while((length(replace(string, '("text":"&k)')) != 0
       || length(replace(string, '("text":"&l)')) != 0
       || length(replace(string, '("text":"&m)')) != 0
       || length(replace(string, '("text":"&n)')) != 0
       || length(replace(string, '("text":"&o)')) != 0
   ), 100,
      string = join('"obfuscated":"true","text":"', split('"text":"&k', string));
      string = join('"bold":"true","text":"', split('"text":"&l', string));
      string = join('"strikethrough":"true","text":"', split('"text":"&m', string));
      string = join('"underlined":"true","text":"', split('"text":"&n', string));
      string = join('"italic":"true","text":"', split('"text":"&o', string));
   );
   
   string = join('"},{"obfuscated":"true","text":"', split('&k', string));
   string = join('"},{"bold":"true","text":"', split('&l', string));
   string = join('"},{"strikethrough":"true","text":"', split('&m', string));
   string = join('"},{"underlined":"true","text":"', split('&n', string));
   string = join('"},{"italic":"true","text":"', split('&o', string));
   
   return('\'[' + string + ']\'');
);

is_in_array(arr, val) -> (
   for (arr,
      if (val == _,
         r = true;
         break();
      );
   );
   r
);
