__config() -> {
   'commands' -> {
     '<hex>' -> 'stylize',
   },
   'arguments' -> {
      'hex' -> {
         'type' -> 'term',
         'suggest' -> ['ffffff']
      }
   },
   ['stay_loaded','true']
};

stylize(hex) -> (
   if (length(replace(hex, '^[a-fA-F0-9]{6}$')) == 0 && length(hex) == 6,
      plr = player();
      item = query(plr, 'holds');
      if (item,
         name = item:2:'display':'Name';
         if (name,
            name = '[' + join('"},{"italic":"false","color":"#' + hex + '","text":"', split('#', name)) + ']';
            itemNBT = parse_nbt(item:2:'display');
            itemNBT:'Name' = name;
            item:2:'display' = encode_nbt(itemNBT);
            inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, item:2),
            print(format('w [','d Stylize','w ] ','y Your item isn\'t renamed!'));
         ),
         print(format('w [','d Stylize','w ] ','y You aren\'t holding anything!'));
      ),
      print(format('w [','d Stylize','w ] ','y You must input a valid hex color!'));
   );
   exit();
);