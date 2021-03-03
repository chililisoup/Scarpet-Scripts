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