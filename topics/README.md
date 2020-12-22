##Topics模式下有两个符号：

- 符号 # 匹配零个或多个词。（0个或多个）

- 符号 * 匹配不多不少一个词。（必须是一个，0个不行）

例子：
red.green.green			1/2/3/4
green.green				3/4	
green.red.blue
green.green.green		3/4		
green.red.green			3

red.#.green		
red.green.*			
#.green	
*.green.#