package crux;

public class Token {


	public static enum Kind
	{
		AND("and"),
		OR("or"),
		NOT("not"),
		
		ADD("+"),
		SUB("-"),
		MUL("*"),
		DIV("/"),
		
		IDENTIFIER(),
		INTEGER(),
		FLOAT(),
		ERROR(),
		EOF(),

		OPEN_PAREN("("),
		CLOSE_PAREN(")"),
		OPEN_BRACE("{"),
		CLOSE_BRACE("}"),
		OPEN_BRACKET("["),
		CLOSE_BRACKET("]"),
		GREATER_EQUAL(">="),
		LESSER_EQUAL("<="),
		NOT_EQUAL("!="),
		EQUAL("=="),
		GREATER_THAN(">"),
		LESS_THAN("<"),
		ASSIGN("="),
		COMMA(","),
		SEMICOLON(";"),
		COLON(":"),
		CALL("::"),

		LET("let"),
		VAR("var"),
		ARRAY("array"),
		FUNC("func"),
		IF("if"),
		ELSE("else"),
		WHILE("while"),
		TRUE("true"),
		FALSE("false"),
		RETURN("return");
		
		private String default_lexeme;
		
		Kind() {default_lexeme = "";}
		
		Kind(String lexeme)
		{
			default_lexeme = lexeme;
		}
		
		public boolean hasStaticLexeme()
		{
			return default_lexeme != "";
		} //had to change to "" from null because of
																		 // object never being null
		
		// OPTIONAL: if you wish to also make convenience functions, feel free
		//           for example, boolean matches(String lexeme)
		//           can report whether a Token.Kind has the given lexeme

		public boolean matches(CharSequence lexeme)
		{
			return default_lexeme.equals(lexeme);
		}
	}
	
	private int lineNum;
	private int charPos;
	Kind kind;
	private String lexeme = "";
	
	
	// OPTIONAL: implement factory functions for some tokens, as you see fit
	/**/
	public static Token EOF(int linePos, int charPos)
	{
		Token token = new Token(linePos, charPos);
		token.kind = Kind.EOF;
		token.kind.default_lexeme = " ";
		return token;
	}

	public static Token IDENTIFIER(String name, int linePos, int charPos)
	{
		Token token = new Token(linePos, charPos);
		token.kind = Kind.IDENTIFIER;
		token.lexeme = name;

		return token;
	}

	public static Token ERROR(String error, int linePos, int charPos)
	{
		Token token = new Token(linePos, charPos);
		token.kind = Kind.ERROR;
		token.lexeme = "Unrecognized character: " + error;

		return token;
	}

	public static Token INTEGER(String number, int linePos, int charPos)
	{
		Token token = new Token(linePos, charPos);
		token.kind = Kind.INTEGER;
		token.lexeme = number;
		return token;
	}

	public static Token FLOAT(String number, int linePos, int charPos)
	{
		Token token = new Token(linePos, charPos);
		token.kind = Kind.FLOAT;
		token.lexeme = number;
		return token;
	}


	private Token(int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		
		// if we don't match anything, signal error
		this.kind = Kind.ERROR;
		this.lexeme = "No Lexeme Given";
	}
	
	public Token(String lexeme, int lineNum, int charPos)
	{
		this.lineNum = lineNum;
		this.charPos = charPos;
		
		// TODO: based on the given lexeme determine and set the actual kind
		for (Token.Kind kind : Token.Kind.values())
		{
			if (kind.matches(lexeme))
			{
				this.kind = kind;
				return;
			}
		}
		
		// if we don't match anything, signal error
		this.kind = Kind.ERROR;
		this.lexeme = "Unrecognized lexeme: " + lexeme;
	}
	
	public int lineNumber()
	{
		return lineNum;
	}
	
	public int charPosition()
	{
		return charPos;
	}

	public Kind kind()
	{
		return this.kind;
	}
	
	// Return the lexeme representing or held by this token
	public String lexeme()
	{
		return this.lexeme;
	}
	
	public String toString()
	{
		// TODO: implement this
		String extra = "";
		if (!kind.hasStaticLexeme())
			extra = "(" + this.lexeme + ")";
		return this.kind + extra + "(lineNum:" + this.lineNum + ", charPos:" + this.charPos + ")" ;
	}

	public boolean is(Token.Kind kind)
	{
		return kind == this.kind;
	}
	
	// OPTIONAL: function to query a token about its kind
	//           boolean is(Token.Kind kind)
	
	// OPTIONAL: add any additional helper or convenience methods
	//           that you find make for a clean design

}
