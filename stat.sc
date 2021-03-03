__config() -> {

   'commands' -> {

     'help' -> 'help',

     'show <statistic>' -> 'show'

   },

   'arguments' -> {

      'statistic' -> {

         'type' -> 'objective'

      }

   },

   ['stay_loaded','true']

};



show(stat) -> (

   scoreboard_display('sidebar', stat:0);

   schedule(200, clear() -> (scoreboard_display('sidebar', null)));

   exit();

);



help() -> (

   print('g.  -> General statistics');

   print('b.  -> Times broken (tools)');

   print('c.  -> Times crafted');

   print('q.  -> Times dropped');

   print('p.  -> Times picked up');

   print('cu. -> Custom stats (more general stuff)');

   print('d.  -> Times killed by');

   print('k.  -> Times killed');

   print('m.  -> Times mined');

   print('u.  -> Times used');

   exit();

);