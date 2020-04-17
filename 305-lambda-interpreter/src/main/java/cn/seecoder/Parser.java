package cn.seecoder;

import java.util.ArrayList;
  //识别出三个抽象
//将后面的抽象按照顺序带入到前面的抽象中
public class Parser {
	Lexer lexer;
	ArrayList<String> ctx;

	public Parser(Lexer l) {
		lexer = l;
	}

	public AST parse() {
		this.lexer.index = 0;
		ctx = new ArrayList<>(); // 声明一个列表，用来存储出现的parma
		AST result = this.term();
		// 从term开始搜索
		return result;

	}
//	Application
//	有lhs和rhs两个属性，都是AST类
//	Abstraction
//	有param和body两个属性，分别是Identifier类和AST类
//	Identifier
//	有name和value两个属性，分别是String和int类
	// Term ::= Application| LAMBDA LCID DOT Term
	//
	// Application ::= Application Atom| Atom
	//
	// Atom ::= LPAREN Term RPAREN| LCID
	
	

	private AST term() {
		if (this.lexer.getNextTokenType() == TokenType.LAMBDA) {
			this.lexer.moveNext();
			if (this.lexer.getNextTokenType() == TokenType.LCID) {
				this.lexer.moveNext();
				String id = this.lexer.getLCID();
				if (this.lexer.getNextTokenType() == TokenType.DOT) {
					this.lexer.moveNext();
					ctx.add(0, id);
					AST term = this.term();// 递归
					Identifier newId = new Identifier(id, String.valueOf(ctx.indexOf(id)));
					Abstraction abs = new Abstraction(newId, term);
					ctx.remove(ctx.indexOf(id));
					return abs;
				}
			}
		} else {
			return this.application();
		}
		return null;
	}

	private AST application() {
		AST lhs = this.atom(); // //左枝搜索atom | ε
		while (true) {
			AST rhs = this.atom(); // 右枝搜索atom
			if (rhs != null) {
				lhs = new Application(lhs, rhs);
			} else {
				return lhs;
			}
		}
		// 不断搜索atom到找不到为止
		// Application ::= Application Atom| Atom

	}

	// Atom ::= LPAREN Term RPAREN| LCID
	private AST atom() {
		if (this.lexer.getNextTokenType() == TokenType.LPAREN) {
			this.lexer.moveNext();
			AST term = this.term();
			if (this.lexer.getNextTokenType() == TokenType.RPAREN) {
				this.lexer.moveNext();
				return term;
			}
		} else if (this.lexer.getNextTokenType() == TokenType.LCID) {
			this.lexer.moveNext();
			String id = this.lexer.getLCID();
			return new Identifier(id,
					ctx.indexOf(id) == -1 ? String.valueOf(ctx.size()) : String.valueOf(ctx.indexOf(id)));
		}

		return null;
	}
	// if (this.lexer.skip(Token.Type.LPAREN)) {
	// AST term = this.term(ctx);
	// this.lexer.match(Token.Type.RPAREN);
	// return term;
	// }
	// else if (this.lexer.next(Token.Type.LCID)) {
	// String id = this.lexer.token(Token.Type.LCID);
	//
	//
	// //System.out.println(ctx);
	// return new Identifier(id,ctx.indexOf(id) ==
	// -1?ctx.size():ctx.indexOf(id));
	// } else {
	// return null;
	// }
	// }

}
