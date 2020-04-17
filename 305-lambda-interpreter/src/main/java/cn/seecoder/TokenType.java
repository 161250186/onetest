package cn.seecoder;

public enum TokenType {
     EOF("EOF"),// 结束符 空
     LAMBDA("LAMBDA"),//lambda \
     LPAREN("LPAREN"),//左括号
     RPAREN("RPAREN"),//右括号
     LCID("LCID"),
     DOT("DOT");//点
     //枚举类
     
     private String str;
	
	private TokenType(String s) {
		this.str = s;
	}
	
	@Override
	public String toString() {
		return this.str;
	}
}
