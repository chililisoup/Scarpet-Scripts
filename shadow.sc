__config() -> {
   'commands' -> {
     '' -> 'shadow',
     'schedule <action> continuous' -> ['sSchedule', 'continuous'],
     'schedule <action> interval <ticks>' -> 'sSchedule',
     'schedule cancel' -> 'sCancel'
   },
   'arguments' -> {
      'action' -> {
         'type' -> 'term',
         'options' -> [
            'use',
            'attack'
         ]
      },
      'ticks' -> {
         'type' -> 'int',
         'min' -> 0,
         'suggest' -> [20]
      }
   },
   ['stay_loaded','true']
};

shadow() -> (
   run('player ' + player() + ' shadow');
   exit();
);

sSchedule(action, period) -> (
   if (period == 'continuous',
      run('player ' + player() + ' ' + action + ' continuous');
      print(format('w [','d Shadow','w ] ', 'y Scheduled ', 'wb ' + action, 'y  to be done ', 'wb continuously', 'y .')),
      run('player ' + player() + ' ' + action + ' interval ' + period);
      print(format('w [','d Shadow','w ] ', 'y Scheduled ', 'wb ' + action, 'y  to be done every ', 'wb ' + period + ' ticks', 'y .'));
   );
   exit();
);

sCancel() -> (
   run('player ' + player() + ' stop');
   print(format('w [','d Shadow','w ] ', 'y Canceled any scheduled actions.'));
   exit();
);