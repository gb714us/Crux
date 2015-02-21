package crux;

import java.util.Vector;

public class SymbolTable {

    private Vector<Symbol> symbols;
    int depth;
    SymbolTable parent;
    
    public SymbolTable()
    {
        symbols = new Vector<Symbol>();
        depth = 0;
        parent = null;
    }

    public SymbolTable(SymbolTable parent)
    {
        symbols = new Vector<Symbol>();
        depth = parent.depth + 1;
        this.parent = parent;
    }
    
    public Symbol lookup(String name) throws SymbolNotFoundError
    {
        boolean found = false;
        for (Symbol s : symbols)
        {
            if (s.name().equals(name))
            {
                return s;
            }
        }

        if (!found && parent != null)
            return parent.lookup(name);
        throw new SymbolNotFoundError(name);
    }

       
    public Symbol insert(String name) throws RedeclarationError
    {
        if (!contains(name))
        {
            Symbol symbol = new Symbol(name);
            symbols.add(symbol);
            return symbol;
        }
        throw new RedeclarationError(name);
    }


    public boolean contains(String name)
    {
        for (Symbol s : symbols)
        {
            if (s.name().equals(name))
            {
                return true;
            }
        }
        return false;

    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (parent != null)
            sb.append(parent.toString());
        
        String indent = new String();
        for (int i = 0; i < depth; i++) {
            indent += "  ";
        }
        
        for (Symbol s: symbols)
        {
            sb.append(indent + s.toString() + "\n");
        }
        return sb.toString();
    }

    public int getDepth()
    {
        return depth;
    }

    public SymbolTable getParent()
    {
        return parent;
    }
}

class SymbolNotFoundError extends Error
{
    private static final long serialVersionUID = 1L;
    private String name;
    
    SymbolNotFoundError(String name)
    {
        this.name = name;
    }
    
    public String name()
    {
        return name;
    }
}

class RedeclarationError extends Error
{
    private static final long serialVersionUID = 1L;

    public RedeclarationError(String sym)
    {
        super("Symbol " + sym + " being redeclared.");
    }
}
