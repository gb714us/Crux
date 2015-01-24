package crux;

import java.util.*;

//        literal := INTEGER | FLOAT | TRUE | FALSE .
//
//        designator := IDENTIFIER { "[" expression0 "]" } .
//        type := IDENTIFIER .
//
//        op0 := ">=" | "<=" | "!=" | "==" | ">" | "<" .
//        op1 := "+" | "-" | "or" .
//        op2 := "*" | "/" | "and" .
//
//        expression0 := expression1 [ op0 expression1 ] .
//        expression1 := expression2 { op1  expression2 } .
//        expression2 := expression3 { op2 expression3 } .
//        expression3 := "not" expression3
//        | "(" expression0 ")"
//        | designator
//        | call-expression
//        | literal .
//        call-expression := "::" IDENTIFIER "(" expression-list ")" .
//        expression-list := [ expression0 { "," expression0 } ] .
//
//        parameter := IDENTIFIER ":" type .
//        parameter-list := [ parameter { "," parameter } ] .
//
//        variable-declaration := "var" IDENTIFIER ":" type ";"
//        array-declaration := "array" IDENTIFIER ":" type "[" INTEGER "]" { "[" INTEGER "]" } ";"
//        function-definition := "func" IDENTIFIER "(" parameter-list ")" ":" type statement-block .
//        declaration := variable-declaration | array-declaration | function-definition .
//        declaration-list := { declaration } .
//
//        assignment-statement := "let" designator "=" expression0 ";"
//        call-statement := call-expression ";"
//        if-statement := "if" expression0 statement-block [ "else" statement-block ] .
//        while-statement := "while" expression0 statement-block .
//        return-statement := "return" expression0 ";" .
//        statement := variable-declaration
//        | call-statement
//        | assignment-statement
//        | if-statement
//        | while-statement
//        | return-statement .
//        statement-list := { statement } .
//        statement-block := "{" statement-list "}" .
//
//        program := declaration-list EOF .

public enum NonTerminal
{

    // TODO: mention that we are not modeling the empty string
    // TODO: mention that we are not doing a first set for every line in the grammar
    //       some lines have already been handled by the CruxScanner

    DESIGNATOR(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.IDENTIFIER);
        }
    }),
    TYPE(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.IDENTIFIER);
        }
    }),
    LITERAL(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;
        {
            add(Token.Kind.INTEGER);
            add(Token.Kind.FLOAT);
            add(Token.Kind.TRUE);
            add(Token.Kind.FALSE);
        }
    }),
    CALL_EXPRESSION(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.IDENTIFIER);
        }
    }),
    OP0(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.LESSER_EQUAL);
            add(Token.Kind.GREATER_EQUAL);
            add(Token.Kind.NOT_EQUAL);
            add(Token.Kind.EQUAL);
            add(Token.Kind.LESS_THAN);
            add(Token.Kind.GREATER_THAN);
        }
    }),
    OP1(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.ADD);
            add(Token.Kind.SUB);
            add(Token.Kind.OR);
        }
    }),
    OP2(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.MUL);
            add(Token.Kind.DIV);
            add(Token.Kind.AND);
        }
    }),
    EXPRESSION3(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.NOT);
            add(Token.Kind.OPEN_PAREN);
            addAll(DESIGNATOR.firstSet());
            addAll(CALL_EXPRESSION.firstSet());
            addAll(LITERAL.firstSet());
        }
    }),
    EXPRESSION2(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
           addAll(EXPRESSION3.firstSet());
        }
    }),
    EXPRESSION1(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(EXPRESSION2.firstSet());
        }
    }),
    EXPRESSION0(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(EXPRESSION1.firstSet());
        }
    }),
    EXPRESSION_LIST(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(EXPRESSION0.firstSet());
        }
    }),
    PARAMETER(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.IDENTIFIER);
        }
    }),
    PARAMETER_LIST(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(PARAMETER.firstSet());
        }
    }),
    VARIABLE_DECLARATION(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.VAR);
        }
    }),
    ARRAY_DECLARATION(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.ARRAY);
        }
    }),
    FUNCTION_DEFINITION(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.FUNC);
        }
    }),
    DECLARATION(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(VARIABLE_DECLARATION.firstSet());
            addAll(ARRAY_DECLARATION.firstSet());
            addAll(FUNCTION_DEFINITION.firstSet());
        }
    }),
    DECLARATION_LIST(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(DECLARATION.firstSet());
        }
    }),
    ASSIGNMENT_STATEMENT(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.LET);
        }
    }),
    CALL_STATEMENT(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(CALL_EXPRESSION.firstSet());
        }
    }),
    IF_STATEMENT(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.IF);
        }
    }),
    WHILE_STATEMENT(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.WHILE);
        }
    }),
    RETURN_STATEMENT(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.RETURN);
        }
    }),
    STATEMENT_BLOCK(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(Token.Kind.OPEN_BRACE);
        }
    }),
    STATEMENT(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(VARIABLE_DECLARATION.firstSet());
            addAll(ASSIGNMENT_STATEMENT.firstSet());
            addAll(IF_STATEMENT.firstSet());
            addAll(WHILE_STATEMENT.firstSet());
            addAll(RETURN_STATEMENT.firstSet());
            addAll(CALL_STATEMENT.firstSet());
        }
    }),
    STATEMENT_LIST(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(STATEMENT.firstSet());
        }
    }),
    PROGRAM(new HashSet<Token.Kind>()
    {
        private static final long serialVersionUID = 1L;

        {
            addAll(DECLARATION_LIST.firstSet());
        }
    });

    public final HashSet<Token.Kind> firstSet = new HashSet<Token.Kind>();

    NonTerminal(HashSet<Token.Kind> t)
    {
        firstSet.addAll(t);
    }

    public final Set<Token.Kind> firstSet()
    {
        return firstSet;
    }
}
