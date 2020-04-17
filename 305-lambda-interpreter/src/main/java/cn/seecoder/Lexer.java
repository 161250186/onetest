package cn.seecoder;

import java.util.ArrayList;

public class Lexer {
   
	public String source;
	public int index;
	public TokenType token;
    public ArrayList<TokenType> arr;
    
	public Lexer(String s) {
		index = 0;
		source = s;
		arr = new ArrayList<>();
		this.allToken();
	}
	
	public boolean hasNext() {
		return index < this.source.length();
	}
	
	public void moveNext() {
		this.index = this.index + 1;
	}
	
	public String getLCID() {
		return this.source.replaceAll(" ", "").substring(this.index - 1, this.index);
	}
	
	public TokenType getNextTokenType() {
		return this.arr.get(this.index);
	}

	private void allToken() {
		while (index < source.length()) {
			// index = index + 1;
			this.nextToken();

		}
		this.addToken(TokenType.EOF);
	}

	// get next token
	private void nextToken() {
		String a = source.substring(index, index + 1);
		if (a.equals(".")) {
			this.addToken(TokenType.DOT);
			index = index + 1;
		} else if (a.equals("\\")) {
			this.addToken(TokenType.LAMBDA);
			index = index + 1;
		} else if (a.equals("(")) {
			this.addToken(TokenType.LPAREN);
			index = index + 1;
		} else if (a.equals(")")) {
			this.addToken(TokenType.RPAREN);
			index = index + 1;
		} else if (a.equals(" ")) {
			index = index + 1;
		} else {
			String b = source.substring(index - 1, index);
			if (b.equals(".") || b.equals("(") || b.equals(")") || b.equals("\\") || b.equals(" ")) {
				this.addToken(TokenType.LCID);
			}
			index = index + 1;
		}
	}
	
	private void addToken(TokenType token) {
		System.out.println(token);
		this.arr.add(token);
	}

}
