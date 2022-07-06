// Displays a GUI of all the color & format codes
// Design by xMelvinnx https://www.spigotmc.org/resources/authors/xmelvinnx.235787/

__config() -> {};

__command() -> (
	print(format('d ==============================='));
	print(format('w &0 = ', 'k Black            ', 'w &1 = ', 'v Dark Blue'));
	print(format('w &2 = ', 'e Dark Green    ', 'w &3 = ', 'q Dark Aqua'));
	print(format('w &4 = ', 'n Dark Red       ', 'w &5 = ', 'p Dark Purple'));
	print(format('w &6 = ', 'd Gold             ', 'w &7 = ', 'g Gray'));
	print(format('w &8 = ', 'f Dark Gray      ', 'w &9 = ', 't Blue'));
	print(format('w &a = ', 'l Green           ', 'w &b = ', 'c Aqua'));
	print(format('w &c = ', 'r Red              ', 'w &d = ', 'm Light Purple'));
	print(format('w &e = ', 'y Yellow           ', 'w &f = White'));
	print(format('d ==============================='));
	print(format('w &k = ', 'o Obfuscate      ', 'w &l = ', 'b Bold'));
	print(format('w &m = ', 's Strikethrough', 'w  &n = ', 'u Underline'));
	print(format('w &o = ', 'i Italic            ', 'w &r = Reset'));
	print(format('d ==============================='));
	print(format('w *text*     = ', 'i Italic        ', 'w _text_     = ', 'i Italic'));
	print(format('w **text**   = ', 'b Bold      ', 'w ***text***  = ', 'bi Bold Italic'));
	print(format('w __text__ = ', 'u Underline', 'w   ~~text~~ = ', 's Strikethrough'));
	print(format('d ==============================='));
	exit();
);