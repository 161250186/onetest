package cn.seecoder;

public class Interpreter {
	Parser parser;
	AST astAfterParser;

	public Interpreter(Parser p) {
		parser = p;
		astAfterParser = p.parse();
		// System.out.println("After parser:"+astAfterParser.toString());
	}

	private boolean isAbstraction(AST ast) {
		return ast instanceof Abstraction;
	}// java 中的instanceof 运算符是用来在运行时指出对象是否是特定类的一个实例。

	private boolean isApplication(AST ast) {
		return ast instanceof Application;
	}

	private boolean isIdentifier(AST ast) {
		return ast instanceof Identifier;
	}

	private int Type(AST ast) {
		if (isAbstraction(ast) == true)
			return 1;
		if (isApplication(ast) == true)
			return 2;
		if (isIdentifier(ast) == true)
			return 3;
		return 0;
	}

	// 首先，定义一个抽象的概念，value。
	// 所谓的value就是一段不能再求值，也即是已经最简了的lambda表达式。
	// 通过简单的分析可以确定以下规则：
	//
	// Identifier一定是value
	// Application分情况
	// 2.1 lhs为Identifier，看rhs
	// 2.2 lhs为abstraction，不是value
	// Abstraction比较复杂
	// 3.1 每个body都是Abstraction，是value
	// 3.2 body中有application 且lhs为Identifier，看rls
	// 3.3 body中有application 且lhs为abstraction，不是value
	private boolean isValue(AST node) {
		if (Type(node) == 3) {
			return true;
		} else if (Type(node) == 2) {
			if (Type(((Application) node).lhs) == 1) {
				return false;
			} else {
				return isValue(((Application) node).rhs) && isValue(((Application) node).lhs);
			}
		} else {
			return isValue(((Abstraction) node).body);
		}
	}

	// 如果application的lhs和rhs都是value，把rhs带入lhs
	// 如果application的rhs不是value，而lhs是value，化简rhs
	// 如果application的rhs和value都不是value，化简lhs
	// 如果abstraction不是value，化简它的body
	private AST eval(AST ast) {
		while (!isValue(ast)) {
			if (ast instanceof Application) {
				Application subast = new Application(((Application) ast).lhs, ((Application) ast).rhs);
				if ((isValue(((Application) ast).rhs) && isValue(((Application) ast).lhs))) {
					ast = substitute(((Abstraction) ((Application) subast).lhs).body, ((Application) subast).rhs);
					// lhs rhs都是value 把rhs带入lhs
				} else if (isValue(((Application) ast).lhs)) {

					((Application) ast).rhs = eval(((Application) subast).rhs);
				} else {
					((Application) ast).lhs = eval(((Application) subast).lhs);
				}
			}

			if (ast instanceof Abstraction) {
				return new Abstraction(((Abstraction) ast).param, eval(((Abstraction) ast).body));
			}

		}

		return ast;
	}

	public AST eval() {

		return evalAST(astAfterParser);
	}

	private AST evalAST(AST ast) {

		return eval(ast);

	}

	private AST substitute(AST node, AST value) {

		return shift(-1, subst(node, shift(1, value, 0), 0), 0);

	}

	/**
	 * value替换node节点中的变量： 如果节点是Applation，分别对左右树替换；
	 * 如果node节点是abstraction，替入node.body时深度得+1； 如果node是identifier，则替换De Bruijn
	 * index值等于depth的identifier（替换之后value的值加深depth）
	 * 
	 * @param value
	 *            替换成为的value
	 * @param node
	 *            被替换的整个节点
	 * @param depth
	 *            外围的深度
	 * 
	 *                     
	 * @return AST
	 * @exception  (方法有异常的话加)
	 * 
	 * 
	 */
	//替换成为的value 被替换的节点 外围的深度
	private AST subst(AST node, AST value, int depth) {
		 if(isApplication(node)){
	        return new Application(subst(((Application) node).getLhs(),value,depth),subst(((Application) node).getRhs(),value,depth));
	        }
	        else if(isAbstraction(node)){
	        return new Abstraction(((Abstraction) node).param,subst(((Abstraction) node).body,value,depth+1));
	        }
	        else{
	       if(depth==((Identifier)node).getDBindex()) {
	       return shift(depth,value,0);
	            }
	       else return node;
	        }

	}

	/**
	 * 
	 * De Bruijn index值位移 如果节点是Applation，分别对左右树位移；
	 * 如果node节点是abstraction，新的body等于旧node.body位移by（from得+1）；
	 * 如果node是identifier，则新的identifier的De Bruijn
	 * index值如果大于等于from则加by，否则加0（超出内层的范围的外层变量才要shift by位）.
	 * 
	 *    *@param by 位移的距离
	 * 
	 * @param node
	 *            位移的节点
	 * @param from
	 *            内层的深度
	 * 
	 *                     
	 * @return AST
	 * @exception  (方法有异常的话加)
	 * 
	 * 
	 */
//位移距离 位移节点 内层深度
	private AST shift(int by, AST node, int from) {
		if(isApplication(node)){

            return new Application(shift(by,((Application)(node)).getLhs(),from), shift(by,((Application)(node)).getRhs(),from));
        }
        else if(isAbstraction(node)){
            return new Abstraction(((Abstraction) node).param,shift(by,((Abstraction) node).body,from+1));
        }
        else{
;
            if((((Identifier) node).getDBindex() >= from)) System.out.println("Using");
            Identifier identifier = new Identifier(((Identifier) node).name, String.valueOf(((Identifier) node).getDBindex() + (((Identifier) node).getDBindex() >= from ? by : 0)));
            return identifier;

        }

	}

	static String ZERO = "(\\f.\\x.x)";
	static String SUCC = "(\\n.\\f.\\x.f (n f x))";
	static String ONE = app(SUCC, ZERO);
	static String TWO = app(SUCC, ONE);
	static String THREE = app(SUCC, TWO);
	static String FOUR = app(SUCC, THREE);
	static String FIVE = app(SUCC, FOUR);
	static String PLUS = "(\\m.\\n.((m " + SUCC + ") n))";
	static String POW = "(\\b.\\e.e b)"; // POW not ready
	static String PRED = "(\\n.\\f.\\x.n(\\g.\\h.h(g f))(\\u.x)(\\u.u))";
	static String SUB = "(\\m.\\n.n" + PRED + "m)";
	static String TRUE = "(\\x.\\y.x)";
	static String FALSE = "(\\x.\\y.y)";
	static String AND = "(\\p.\\q.p q p)";
	static String OR = "(\\p.\\q.p p q)";
	static String NOT = "(\\p.\\a.\\b.p b a)";
	static String IF = "(\\p.\\a.\\b.p a b)";
	static String ISZERO = "(\\n.n(\\x." + FALSE + ")" + TRUE + ")";
	static String LEQ = "(\\m.\\n." + ISZERO + "(" + SUB + "m n))";
	static String EQ = "(\\m.\\n." + AND + "(" + LEQ + "m n)(" + LEQ + "n m))";
	static String MAX = "(\\m.\\n." + IF + "(" + LEQ + " m n)n m)";
	static String MIN = "(\\m.\\n." + IF + "(" + LEQ + " m n)m n)";

	private static String app(String func, String x) {
		return "(" + func + x + ")";
	}

	private static String app(String func, String x, String y) {
		return "(" + "(" + func + x + ")" + y + ")";
	}

	private static String app(String func, String cond, String x, String y) {
		return "(" + func + cond + x + y + ")";
	}

	public static void main(String[] args) {
		// write your code here

		String[] sources = { ZERO, // 0
				ONE, // 1
				TWO, // 2
				THREE, // 3
				app(PLUS, ZERO, ONE), // 4
				app(PLUS, TWO, THREE), // 5
				app(POW, TWO, TWO), // 6
				app(PRED, ONE), // 7
				app(PRED, TWO), // 8
				app(SUB, FOUR, TWO), // 9
				app(AND, TRUE, TRUE), // 10
				app(AND, TRUE, FALSE), // 11
				app(AND, FALSE, FALSE), // 12
				app(OR, TRUE, TRUE), // 13
				app(OR, TRUE, FALSE), // 14
				app(OR, FALSE, FALSE), // 15
				app(NOT, TRUE), // 16
				app(NOT, FALSE), // 17
				app(IF, TRUE, TRUE, FALSE), // 18
				app(IF, FALSE, TRUE, FALSE), // 19
				app(IF, app(OR, TRUE, FALSE), ONE, ZERO), // 20
				app(IF, app(AND, TRUE, FALSE), FOUR, THREE), // 21
				app(ISZERO, ZERO), // 22
				app(ISZERO, ONE), // 23
				app(LEQ, THREE, TWO), // 24
				app(LEQ, TWO, THREE), // 25
				app(EQ, TWO, FOUR), // 26
				app(EQ, FIVE, FIVE), // 27
				app(MAX, ONE, TWO), // 28
				app(MAX, FOUR, TWO), // 29
				app(MIN, ONE, TWO), // 30
				app(MIN, FOUR, TWO),// 31
		};

		for (int i = 0; i < sources.length; i++) {

			String source = sources[i];

			System.out.println(i + ":" + source);

			Lexer lexer = new Lexer(source);

			Parser parser = new Parser(lexer);

			Interpreter interpreter = new Interpreter(parser);

			AST result = interpreter.eval();

			System.out.println(i + ":" + result.toString());

		}

	}
}
