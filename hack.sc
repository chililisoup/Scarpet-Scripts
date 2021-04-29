//sends a message to the server saying the executor has hacked the server. Joke command

__config() -> (
   m(
      l('stay_loaded','true')
   )
);

__command() ->
(
   run('tellraw @a [{"selector":"@s"},{"text":" has hacked the server! WTF!","color":"red","bold":"true"}]');
   exit();
);
