//Allows non opped players to display scoreboard statistics for 5 seconds.
//Change time on line 21 (time is in ticks)

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
   if (!read_file('statShowing', 'json'),
      scoreboard_display('sidebar', stat:0);
      write_file('statShowing', 'json', true);
      schedule(100, clear() -> (
         scoreboard_display('sidebar', null);
         delete_file('statShowing', 'json');
      )),
      print(format('w [','d Stat','w ] ','y A stat is already being displayed!'))
   );
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
