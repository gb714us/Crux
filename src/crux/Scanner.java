package crux;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

public class Scanner implements Iterable<Token> {
	public static String studentName = "Gabriel Blanco";
	public static String studentID = "81443241";
	public static String uciNetID = "gblanco1";
	
	private int lineNum;  // current line count
	private int charPos;  // character offset for current line
	private int nextChar; // contains the next char (-1 == EOF)
	private Reader input;
	
	Scanner(Reader reader)
	{
		// TODO: initialize the Scanner
		this.lineNum = 1;
		this.charPos = 0;
		this.input = reader;
		this.nextChar = readChar();

	}	
	
	// OPTIONAL: helper function for reading a single char from input
	//           can be used to catch and handle any IOExceptions,
	//           advance the charPos or lineNum, etc.



	private int readChar()
	{
		int next = -1;   //init to -1 for EOF
		try
		{
			next = input.read();
			++charPos;
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return next;
	}

		

	/* Invariants:
	 *  1. call assumes that nextChar is already holding an unread character
	 *  2. return leaves nextChar containing an untokenized character
	 */
	public Token next()
	{
		return generateToken();
	}

	private Token generateToken()
	{
		while(Character.isWhitespace(nextChar))  //Ignore whitespace
		{
			if (nextChar == '\n')
			{
				++lineNum;
				charPos = 0;
			}
			nextChar = readChar();
		}

		if (nextChar == -1)   //EOF
		{
			try
			{
				input.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return Token.EOF(lineNum, charPos);
		}


		StringBuilder stringBuilder;
		switch(nextChar)  //Handle special cases  (Comments, 2-char tokens)
		{
			case '/':
				stringBuilder = initialize();
				if (nextChar == '/')
				{
					while(nextChar != '\n' && nextChar != -1)
						advance(stringBuilder);
					if (nextChar == '\n')
						++lineNum;
					charPos = 0;
					nextChar = readChar();
					return next();
				}
				else
					return new Token(stringBuilder.toString(), lineNum, charPos - stringBuilder.length());
			case '<':
			case '>':
			case '=':
				stringBuilder = initialize();
				if (nextChar == '=')
					advance(stringBuilder);
				return new Token (stringBuilder.toString(), lineNum, charPos - stringBuilder.length());

			case ':':
				stringBuilder = initialize();
				if (nextChar == ':')
					advance(stringBuilder);
				return new Token (stringBuilder.toString(), lineNum, charPos - stringBuilder.length());
			case '!':
				stringBuilder = initialize();
				if (nextChar == '=')
				{
					advance(stringBuilder);
					return new Token(stringBuilder.toString(), lineNum, charPos - stringBuilder.length());
				}
				return Token.ERROR(stringBuilder.toString(), lineNum, charPos - stringBuilder.length());
			default:
				break;

		}

		if (Character.isDigit(nextChar))   //INT OR FLOAT
		{
			stringBuilder = initialize();

			while (Character.isDigit(nextChar))
				advance(stringBuilder);

			if (nextChar == '.')
			{
				advance(stringBuilder);

				while (Character.isDigit(nextChar))
					advance(stringBuilder);

				return Token.FLOAT(stringBuilder.toString(), lineNum, charPos - stringBuilder.length());
			}

			return Token.INTEGER(stringBuilder.toString(), lineNum, charPos - stringBuilder.length());

		}
		else if (Character.isLetter(nextChar) || nextChar == '_')  //IDENTIFIER
		{
			stringBuilder = initialize();

			while (Character.isLetter(nextChar) || nextChar == '_' || Character.isDigit(nextChar))
				advance(stringBuilder);

			for (Token.Kind kind : Token.Kind.values())
			{
				if (kind.matches(stringBuilder.toString()))
					return new Token(stringBuilder.toString(), lineNum, charPos - stringBuilder.length());
			}

			return Token.IDENTIFIER(stringBuilder.toString(), lineNum, charPos - stringBuilder.length());
		}
		else
		{
			stringBuilder = initialize();

			for (Token.Kind kind : Token.Kind.values())
			{
				if (kind.matches(stringBuilder.toString()))
					return new Token(stringBuilder.toString(), lineNum, charPos - stringBuilder.length());
			}

			return Token.ERROR(stringBuilder.toString(), lineNum, charPos - stringBuilder.length());
		}

	}

	private StringBuilder initialize()
	{
		StringBuilder stringBuilder = new StringBuilder();
		advance(stringBuilder);
		return stringBuilder;
	}

	private void advance(StringBuilder s)
	{
		s.append((char)nextChar);
		nextChar = readChar();
	}

	// OPTIONAL: any other methods that you find convenient for implementation or testing

	@Override
	public Iterator<Token> iterator() {
		return null;
	}
}
