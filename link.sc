__config() -> {
   'commands' -> {
     '<url>' -> 'link',
   },
   'arguments' -> {
      'url' -> {
         'type' -> 'text',
         'suggest' -> ['https://www.example.com']
      }
   },
   ['stay_loaded','true']
};

link(url) -> (
  url = replace(url, '"');
  run('tellraw @a [{"selector":"@s"},{"text":" wants to show you "},{"text":"' + url + '","underlined":true,"color":"aqua","clickEvent":{"action":"open_url","value":"' + url + '"}}]');
  exit();
);