package crux;

import ast.*;
import ast.Error;

import java.util.ArrayList;
import java.util.List;

public class Parser
{
    public static String studentName = "Gabriel Blanco";
    public static String studentID = "81443241";
    public static String uciNetID = "gblanco1";

    // Grammar Rule Reporting ==========================================

    //SYMBOL TABLE STUFF================================================

    SymbolTable symbolTable;

    private void initSymbolTable()
    {
        symbolTable = new SymbolTable();
        symbolTable.insert("readInt");
        symbolTable.insert("readFloat");
        symbolTable.insert("printBool");
        symbolTable.insert("printInt");
        symbolTable.insert("printFloat");
        symbolTable.insert("println");
    }

    private void enterScope()
    {
        symbolTable = new SymbolTable(symbolTable);
    }

    private void exitScope()
    {
        symbolTable = symbolTable.getParent();
    }

    private Symbol tryResolveSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.lookup(name);
        } catch (SymbolNotFoundError e) {
            String message = reportResolveSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportResolveSymbolError(String name, int lineNum, int charPos)
    {
        String message = "ResolveSymbolError(" + lineNum + "," + charPos + ")[Could not find " + name + ".]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }

    private Symbol tryDeclareSymbol(Token ident)
    {
        assert(ident.is(Token.Kind.IDENTIFIER));
        String name = ident.lexeme();
        try {
            return symbolTable.insert(name);
        } catch (RedeclarationError re) {
            String message = reportDeclareSymbolError(name, ident.lineNumber(), ident.charPosition());
            return new ErrorSymbol(message);
        }
    }

    private String reportDeclareSymbolError(String name, int lineNum, int charPos)
    {
        String message = "DeclareSymbolError(" + lineNum + "," + charPos + ")[" + name + " already exists.]";
        errorBuffer.append(message + "\n");
        errorBuffer.append(symbolTable.toString() + "\n");
        return message;
    }

    // Helper Methods ==========================================

    private Token expectRetrieve(Token.Kind kind)
    {
        Token tok = currentToken;
        if (accept(kind))
            return tok;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }

    private Token expectRetrieve(NonTerminal nt)
    {
        Token tok = currentToken;
        if (accept(nt))
            return tok;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return ErrorToken(errorMessage);
    }



    // Error Reporting ==========================================
    private StringBuffer errorBuffer = new StringBuffer();

    private String reportSyntaxError(NonTerminal nt)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected a token from " + nt.name() + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }

    private String reportSyntaxError(Token.Kind kind)
    {
        String message = "SyntaxError(" + lineNumber() + "," + charPosition() + ")[Expected " + kind + " but got " + currentToken.kind() + ".]";
        errorBuffer.append(message + "\n");
        return message;
    }

    public String errorReport()
    {
        return errorBuffer.toString();
    }

    public boolean hasError()
    {
        return errorBuffer.length() != 0;
    }

    private class QuitParseException extends RuntimeException
    {
        private static final long serialVersionUID = 1L;

        public QuitParseException(String errorMessage)
        {
            super(errorMessage);
        }
    }

    private int lineNumber()
    {
        return currentToken.lineNumber();
    }

    private int charPosition()
    {
        return currentToken.charPosition();
    }

    // Parser ==========================================
    private Scanner scanner;
    private Token currentToken;

    public Parser(Scanner scanner)
    {
        this.scanner = scanner;
        this.currentToken = scanner.next();
    }

        public ast.Command parse()
        {
            initSymbolTable();
            try {
               return program();
            } catch (QuitParseException q) {
                return new ast.Error(lineNumber(), charPosition(), "Could not complete parsing.");
            }
        }


    // Helper Methods ==========================================
    private boolean have(Token.Kind kind)
    {
        return currentToken.is(kind);
    }

    private boolean have(NonTerminal nt)
    {
        return nt.firstSet().contains(currentToken.kind());
    }

    private boolean accept(Token.Kind kind)
    {
        if (have(kind))
        {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }

    private boolean accept(NonTerminal nt)
    {
        if (have(nt))
        {
            currentToken = scanner.next();
            return true;
        }
        return false;
    }

    private boolean expect(Token.Kind kind)
    {
        if (accept(kind))
            return true;
        String errorMessage = reportSyntaxError(kind);
        throw new QuitParseException(errorMessage);
        //return false;
    }

    private boolean expect(NonTerminal nt)
    {
        if (accept(nt))
            return true;
        String errorMessage = reportSyntaxError(nt);
        throw new QuitParseException(errorMessage);
        //return false;
    }

// Grammar Rules =====================================================

    // literal := INTEGER | FLOAT | TRUE | FALSE .
    public ast.Expression literal()
    {
        ast.Expression expr;
//        enterRule(NonTerminal.LITERAL);

        Token tok = expectRetrieve(NonTerminal.LITERAL);
        expr = Command.newLiteral(tok);

//        exitRule(NonTerminal.LITERAL);
        return expr;
    }

    // designator := IDENTIFIER { "[" expression0 "]" } .
    public ast.Expression designator()
    {
        int line = lineNumber();
        int charPos = charPosition();

        Token token = expectRetrieve(Token.Kind.IDENTIFIER);
        Symbol s = tryResolveSymbol(token);

        ast.Expression idx = new AddressOf(line, charPos, s);
        while (accept(Token.Kind.OPEN_BRACKET))
        {
            idx  = new ast.Index(lineNumber(), charPosition(), idx, expression0());
            expect(Token.Kind.CLOSE_BRACKET);
        }

        return idx;
    }

    //        program := declaration-list EOF .

    //    type := IDENTIFIER .
    public void type()
    {
        expect(Token.Kind.IDENTIFIER);
    }


    // ALL OPS ARE EXPECTING A TOKEN FOR COMMAND.newExperssion
    //    op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
    public Token op0()
    {
//        if(have(Token.Kind.GREATER_EQUAL))
//            expect(Token.Kind.GREATER_EQUAL);
//        else if(have(Token.Kind.LESSER_EQUAL))
//            expect(Token.Kind.LESSER_EQUAL);
//        else if(have(Token.Kind.NOT_EQUAL))
//            expect(Token.Kind.NOT_EQUAL);
//        else if(have(Token.Kind.EQUAL))
//            expect(Token.Kind.EQUAL);
//        else if(have(Token.Kind.GREATER_THAN))
//            expect(Token.Kind.GREATER_THAN);
//        else if(have(Token.Kind.LESS_THAN))
//            expect(Token.Kind.LESS_THAN);
//        else
//        {
//            String error = reportSyntaxError(NonTerminal.OP2);
//        }

        return expectRetrieve(NonTerminal.OP0);

    }

    //    op1 := "+" | "-" | "or" .
    public Token op1()
    {
//        if(have(Token.Kind.ADD))
//            expect(Token.Kind.ADD);
//        else if(have(Token.Kind.SUB))
//            expect(Token.Kind.SUB);
//        else if(have(Token.Kind.OR))
//            expect(Token.Kind.OR);

        return expectRetrieve(NonTerminal.OP1);

    }

    //    op2 := "*" | "/" | "and" .
    public Token op2()
    {
//        if(have(Token.Kind.MUL))
//            expect(Token.Kind.MUL);
//        else if(have(Token.Kind.DIV))
//            expect(Token.Kind.DIV);
//        else if(have(Token.Kind.AND))
//            expect(Token.Kind.AND);
//        else
//        {
//            String error = reportSyntaxError(NonTerminal.OP2);
//        }

        return expectRetrieve(NonTerminal.OP2);
    }

    //  expression0 := expression1 [ op0 expression1 ] .
    public ast.Expression expression0()
    {
        ast.Expression result = expression1();

        if(have(NonTerminal.OP0))
        {
            Token op = op0();
            ast.Expression toAdd = expression1();
            result = Command.newExpression(result, op, toAdd);
        }

        return result;
    }

    // expression1 := expression2 { op1  expression2 } .
    public ast.Expression expression1()
    {
        ast.Expression result = expression2();

        while(have(NonTerminal.OP1))
        {
            Token op = op1();
            ast.Expression toAdd = expression2();
            result = Command.newExpression(result, op, toAdd);
        }

        return result;
    }

    //    expression2 := expression3 { op2 expression3 } .
    public ast.Expression expression2()
    {
        ast.Expression result = expression3();

        while(have(NonTerminal.OP2))
        {
            Token op = op2();
            ast.Expression toAdd = expression3();
            result = Command.newExpression(result, op, toAdd);
        }

        return result;

    }

    //    expression3 := "not" expression3
    //    | "(" expression0 ")"
    //            | designator
    //    | call-expression
    //    | literal .
    public ast.Expression expression3()
    {
        int line = lineNumber();
        int charPos = charPosition();

        ast.Expression expression;
        if(accept(Token.Kind.NOT))
        {
            expression = expression3();
            expression = new LogicalNot(line, charPos, expression);
        }
        else if(accept(Token.Kind.OPEN_PAREN))
        {
            expression = expression0();
            expect(Token.Kind.CLOSE_PAREN);
        }
        else if(have(NonTerminal.DESIGNATOR))
        {
            ast.Expression toAdd = designator();
            expression = new ast.Dereference(line, charPos, toAdd);

        }
        else if(have(NonTerminal.CALL_EXPRESSION))
            expression = call_expression();
        else if(have(NonTerminal.LITERAL))
            expression = literal();
        else
        {
            String error = reportSyntaxError(NonTerminal.EXPRESSION3);
            expression = new ast.Error(lineNumber(), charPosition(), error);
        }

        return expression;
    }

    //call-expression := "::" IDENTIFIER "(" expression-list ")" .
    public ast.Call call_expression()
    {
        int line = lineNumber();
        int charPos = charPosition();
        expect(Token.Kind.CALL);
        Token token = expectRetrieve(Token.Kind.IDENTIFIER);
        Symbol symbol = tryResolveSymbol(token);
        expect(Token.Kind.OPEN_PAREN);
        ast.ExpressionList list = expression_list();
        expect(Token.Kind.CLOSE_PAREN);

        ast.Call call = new ast.Call(line, charPos, symbol, list);
        return call;

    }

    //expression-list := [ expression0 { "," expression0 } ] .
    public ast.ExpressionList expression_list()
    {
        ast.ExpressionList list = new ast.ExpressionList(lineNumber(), charPosition());
        if (have(NonTerminal.EXPRESSION0))
        {
            list.add(expression0());
            while (accept(Token.Kind.COMMA))
            {
                list.add(expression0());
            }
        }

        return list;

    }

    //parameter := IDENTIFIER ":" type .
    public Symbol parameter()
    {
        return declare_helper();

    }

    //parameter-list := [ parameter { "," parameter } ] .
    public List<Symbol> parameter_list()
    {
        List<Symbol> params = new ArrayList<Symbol>();
        if (have(NonTerminal.PARAMETER))
        {
            params.add(parameter());
            while(accept(Token.Kind.COMMA))
            {
                params.add(parameter());
            }
        }

        return params;
    }

    //variable-declaration := "var" IDENTIFIER ":" type ";"
    public ast.VariableDeclaration variable_declaration()
    {
        int line = lineNumber();
        int charPos = charPosition();
        expect(Token.Kind.VAR);
        Symbol symbol = declare_helper();
        ast.VariableDeclaration var = new ast.VariableDeclaration(line,
                charPos,
                symbol);
        expect(Token.Kind.SEMICOLON);

        return var;

    }

    // array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
    public ast.ArrayDeclaration array_declaration()
    {
        int line = lineNumber();
        int charPos = charPosition();
        expect(Token.Kind.ARRAY);
        Symbol symbol = declare_helper();
        ast.ArrayDeclaration arr = new ast.ArrayDeclaration(line,
                charPos,
                symbol);
        expect(Token.Kind.OPEN_BRACKET);
        array_declare_closer();
        while (accept(Token.Kind.OPEN_BRACKET))
        {
            array_declare_closer();
        }
        expect(Token.Kind.SEMICOLON);

        return arr;
    }

    //function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
    public ast.FunctionDefinition function_definition()
    {
        int line = lineNumber();
        int charPos = charPosition();
        expect(Token.Kind.FUNC);
        Token token = expectRetrieve(Token.Kind.IDENTIFIER);
        Symbol symbol = tryDeclareSymbol(token);
        expect(Token.Kind.OPEN_PAREN);
        enterScope();
        List<Symbol> params = parameter_list();
        expect(Token.Kind.CLOSE_PAREN);
        expect(Token.Kind.COLON);
        type();
        ast.StatementList statements = statement_block();
        exitScope();

        ast.FunctionDefinition func = new ast.FunctionDefinition(line,
                charPos,
                symbol,
                params,
                statements);

        return func;


    }

    // declaration := variable-declaration | array-declaration | function-definition .
    public ast.Declaration declaration()
    {
        ast.Declaration declaration;

        if (have(NonTerminal.VARIABLE_DECLARATION))
            declaration = variable_declaration();
        else if (have(NonTerminal.ARRAY_DECLARATION))
            declaration =  array_declaration();
        else if (have(NonTerminal.FUNCTION_DEFINITION))
            declaration = function_definition();
        else
        {
            String error = reportSyntaxError(NonTerminal.DECLARATION);
            declaration = new ast.Error(lineNumber(), charPosition(), error);
        }

        return declaration;

    }

    // declaration-list := { declaration } .
    public ast.DeclarationList declaration_list()
    {
        ast.DeclarationList list = new ast.DeclarationList(lineNumber(), charPosition());
        while(have(NonTerminal.DECLARATION))
        {
            list.add(declaration());
        }

        return list;

    }

    //
    //assignment-statement := "let" designator "=" expression0 ";"
    public ast.Assignment assignment_statement()
    {
        int line = lineNumber();
        int charPos = charPosition();
        expect(Token.Kind.LET);
        ast.Expression des = designator();
        expect(Token.Kind.ASSIGN);
        ast.Expression expr = expression0();
        expect(Token.Kind.SEMICOLON);

        return new ast.Assignment(line, charPos, des, expr);

    }

    // call-statement := call-expression ";"
    public ast.Call call_statement()
    {
        ast.Call call =  call_expression();

        expect(Token.Kind.SEMICOLON);

        return call;
    }

    //  if-statement := "if" expression0 statement-block [ "else" statement-block ] .
    public ast.IfElseBranch if_statement()
    {
        int line = lineNumber();
        int charPos = charPosition();
        expect(Token.Kind.IF);
        ast.Expression cond = expression0();
        enterScope();
        ast.StatementList list = statement_block();
        exitScope();
        ast.StatementList block = new StatementList(lineNumber(), charPosition());
        if (accept(Token.Kind.ELSE))
        {
            enterScope();
            block = statement_block();
            exitScope();
        }

        return new ast.IfElseBranch(line, charPos, cond, list, block);
    }

    //while-statement := "while" expression0 statement-block .
    public ast.WhileLoop while_statement()
    {
        int line = lineNumber();
        int charPos = charPosition();
        expect(Token.Kind.WHILE);
        ast.Expression cond = expression0();
        enterScope();
        ast.StatementList body = statement_block();
        exitScope();

        return new ast.WhileLoop(line, charPos, cond, body);
    }

    //    return-statement := "return" expression0 ";" .
    public ast.Return return_statement()
    {
        int line = lineNumber();
        int charPos = charPosition();
        expect(Token.Kind.RETURN);
        ast.Expression result = expression0();
        expect(Token.Kind.SEMICOLON);

        return new ast.Return(line, charPos, result);
    }

    //    statement := variable-declaration
    //    | call-statement
    //    | assignment-statement
    //    | if-statement
    //    | while-statement
    //    | return-statement .
    public ast.Statement statement()
    {
        ast.Statement statement;

        if (have(NonTerminal.VARIABLE_DECLARATION))
            statement = variable_declaration();
        else if (have(NonTerminal.CALL_STATEMENT))
            statement = call_statement();
        else if (have(NonTerminal.ASSIGNMENT_STATEMENT))
            statement = assignment_statement();
        else if (have(NonTerminal.IF_STATEMENT))
            statement = if_statement();
        else if (have(NonTerminal.WHILE_STATEMENT))
            statement = while_statement();
        else if (have(NonTerminal.RETURN_STATEMENT))
            statement = return_statement();
        else
        {
            String error = reportSyntaxError(NonTerminal.STATEMENT);
            statement = new ast.Error(lineNumber(), charPosition(), error);
        }

        return statement;
    }

    // statement-list := { statement } .
    public ast.StatementList statement_list()
    {
        ast.StatementList list = new ast.StatementList(lineNumber(), charPosition());
        while (have(NonTerminal.STATEMENT))
        {
            list.add(statement());
        }

        return list;
    }

    public ast.DeclarationList program()
    {

        ast.DeclarationList dec = declaration_list();
        expect(Token.Kind.EOF);

        return dec;

    }

    //    statement-block := "{" statement-list "}" .
    public ast.StatementList statement_block()
    {

        expect(Token.Kind.OPEN_BRACE);
        ast.StatementList list = statement_list();
        expect(Token.Kind.CLOSE_BRACE);

        return list;
    }

    //helper function for declarations
    private Symbol declare_helper()
    {
        Token token  = expectRetrieve(Token.Kind.IDENTIFIER);
        Symbol s = tryDeclareSymbol(token);
        expect(Token.Kind.COLON);
        type();

        return s;
    }

    private void array_declare_closer()
    {
        expect(Token.Kind.INTEGER);
        expect(Token.Kind.CLOSE_BRACKET);
    }

}