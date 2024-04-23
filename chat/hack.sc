// Sends a message to the server saying the executor has hacked the server. Joke command. Ha!

__config() -> {};

__command() -> (
	run('tellraw @a [{"selector":"@s"},{"text":" has hacked the server! WTF!","color":"red","bold":true}]');
);