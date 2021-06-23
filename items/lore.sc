//Takes an item's custom name and moves it to the next lore line

__config() -> {
   ['stay_loaded','true']
};

__command() -> (
   plr = player();
   item = query(plr, 'holds');
   if (item,
      name = item:2:'display':'Name';
      if (name,
         lore = item:2:'display':'Lore';
         name = replace(name, '\'', '\\\\\'');
         if (lore,
            lore = replace(lore, '.{1}$');
            lore += ',\'' + name + '\']',
            lore = '[\'' + name + '\']';
         );
         item:2:'display' = '{Lore: ' + lore + '}';
         inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, item:2),
         print(format('w [','d Lore','w ] ','y Your item isn\'t renamed!'));
      ),
      print(format('w [','d Lore','w ] ','y You aren\'t holding anything!'));
   );
   exit();
);

reset() -> (
   plr = player();
   item = query(plr, 'holds');
   if (item,
      name = item:2:'display':'Name';
      if (name,
         item:2:'display' = '{Name: \'' + name + '\'}',
         delete(item:2, 'display');
      );
      inventory_set(plr, query(plr, 'selected_slot'), item:1, item:0, item:2),
      print(format('w [','d Lore','w ] ','y You aren\'t holding anything!'));
   );
   exit();
);
