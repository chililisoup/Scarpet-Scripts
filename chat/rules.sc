//rules command for my server. If you want to use, just add/remove rules from the list.

__config() -> (
   m(
      l('stay_loaded','true')
   )
);

__command() ->
(
   print(format('db Rules', 'w :'));
   for([
      'Do not be America_Forever',
      'Your pranks shouldn\'t cause damage. Fix it if they do',
      'Play at least once a day',
      'Don\'t kill anyone for no reason',
      'Do not download RAM to the server. It has enough',
      'Make a Wither Skeleton Farm',
      'Do not use /head to get heads you can get through other means!'
   ],
      print(format('d '+str(_i+1),'w ) ','y '+_));
   );
   exit();
);
