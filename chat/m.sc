//Sends private messages
//Requires message.scl
//r.sc strongly suggested, allows for replying

import('message', 'send_message');

__config() -> {
   'commands' -> {
     '<player> <message>' -> 'message'
   },
   'arguments' -> {
      'player' -> {
         'type' -> 'players',
         'single' -> true
      },
      'message' -> {
         'type' -> 'text',
         'suggest' -> []
      }
   },
   ['stay_loaded','true']
};

message(to, msg) -> (
   send_message(player(), to, msg);
   exit();
);