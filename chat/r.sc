//Replies to messages sent through /m or /r
//Requires m.sc, message.scl

import('message', 'send_message');

__config() -> {
   'commands' -> {
     '<message>' -> 'message'
   },
   'arguments' -> {
      'message' -> {
         'type' -> 'text',
         'suggest' -> []
      }
   },
   ['stay_loaded','true']
};

message(msg) -> (
   send_message(player(), read_file('msg_' + player(), 'shared_text'):0, msg);
   exit();
);