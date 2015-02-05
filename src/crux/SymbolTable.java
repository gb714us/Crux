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
        depth = parent.getDepth() + 1;
        this.parent = parent;
    }
    
    public Symbol lookup(String name) throws SymbolNotFoundError
    {
        SymbolTable current = this;
        do
        {
            for (Symbol s: symbols)
            {
                if (s.name().equals(name))
                    return s;
            }

            current = current.getParent();
        } while (current != null);

        throw new SymbolNotFoundError(name);
    }

       
    public Symbol insert(String name) throws RedeclarationError
    {
        if (!this.contains(name))
        {
            symbols.add(new Symbol(name));
            return symbols.lastElement();
        }
        throw new RedeclarationError(name);
    }


    public boolean contains(String name)
    {
        SymbolTable current = this;
        do
        {
            for (Symbol s: symbols)
            {
                if (s.name().equals(name))
                    return true;
            }

            current = current.getParent();
        } while (current != null);

        return false;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if (symbols.size() > 1)
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
