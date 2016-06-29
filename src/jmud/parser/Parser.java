
//----------------------------------------------------
// The following code was generated by CUP v0.10j
// Tue Oct 30 22:48:46 GMT-02:00 2001
//----------------------------------------------------

package jmud.parser;

import java_cup.runtime.*;
import jgp.container.Vector;
import jmud.parser.Lexer;
import jmud.parser.SynAna;

/** CUP v0.10j generated parser.
  * @version Tue Oct 30 22:48:46 GMT-02:00 2001
  */
public class Parser extends java_cup.runtime.lr_parser {

  /** Default constructor. */
  public Parser() {super();}

  /** Constructor which sets the default scanner. */
  public Parser(java_cup.runtime.Scanner s) {super(s);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\021\000\002\002\004\000\002\003\003\000\002\004" +
    "\004\000\002\004\005\000\002\005\003\000\002\005\005" +
    "\000\002\006\005\000\002\011\004\000\002\011\005\000" +
    "\002\012\003\000\002\012\005\000\002\007\003\000\002" +
    "\007\003\000\002\007\003\000\002\010\003\000\002\010" +
    "\003\000\002\010\003" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\033\000\004\012\004\001\002\000\016\005\020\006" +
    "\015\007\017\011\010\012\004\014\014\001\002\000\004" +
    "\002\000\001\002\000\004\002\007\001\002\000\004\002" +
    "\001\001\002\000\012\002\ufffa\011\ufffa\013\ufffa\015\ufffa" +
    "\001\002\000\010\011\ufff2\013\ufff2\015\ufff2\001\002\000" +
    "\010\011\ufff1\013\ufff1\015\ufff1\001\002\000\006\011\033" +
    "\015\034\001\002\000\012\005\020\006\015\007\017\013" +
    "\023\001\002\000\012\010\ufff4\011\ufff4\013\ufff4\015\ufff4" +
    "\001\002\000\006\011\ufff8\015\ufff8\001\002\000\012\010" +
    "\ufff5\011\ufff5\013\ufff5\015\ufff5\001\002\000\012\010\ufff6" +
    "\011\ufff6\013\ufff6\015\ufff6\001\002\000\010\011\ufff3\013" +
    "\ufff3\015\ufff3\001\002\000\004\010\031\001\002\000\010" +
    "\011\uffff\013\uffff\015\uffff\001\002\000\006\013\ufffd\015" +
    "\ufffd\001\002\000\006\013\026\015\027\001\002\000\010" +
    "\011\ufffe\013\ufffe\015\ufffe\001\002\000\010\005\020\006" +
    "\015\007\017\001\002\000\006\013\ufffc\015\ufffc\001\002" +
    "\000\014\005\020\006\015\007\017\012\004\014\014\001" +
    "\002\000\006\013\ufffb\015\ufffb\001\002\000\012\002\ufff9" +
    "\011\ufff9\013\ufff9\015\ufff9\001\002\000\014\005\020\006" +
    "\015\007\017\012\004\014\014\001\002\000\006\011\ufff7" +
    "\015\ufff7\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\033\000\006\003\005\011\004\001\001\000\014\004" +
    "\010\007\020\010\015\011\011\012\012\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\010\005\024\006\023\007\021\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\002\001\001" +
    "\000\002\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001\000\002\001\001\000\002\001\001\000\006" +
    "\006\027\007\021\001\001\000\002\001\001\000\012\004" +
    "\010\007\020\010\031\011\011\001\001\000\002\001\001" +
    "\000\002\001\001\000\012\004\010\007\020\010\034\011" +
    "\011\001\001\000\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$Parser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$Parser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$Parser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 0;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}



        /*
         * BEGIN - Codigo nesta secao estara presente na classe Parser.
         */

	static SyntaxNode progTree;

	public static void setTree(SyntaxNode tree) {
		progTree = tree;
	}	

	public static SyntaxNode getTree() {
		return progTree;
	}	

        /*
         * END - Codigo nesta secao estara presente na classe Parser.
         */

}

/** Cup generated class to encapsulate user supplied action code.*/
class CUP$Parser$actions {


	/*
	 * BEGIN
	 * Codigo nesta secao estara presente na classe
         * que executa as acoes das regras de producao.
	 */

	Vector nodeStack = new Vector();

	/*
	 * END
	 * Codigo nesta secao estara presente na classe
         * que executa as acoes das regras de producao.
	 */

  private final Parser parser;

  /** Constructor */
  CUP$Parser$actions(Parser parser) {
    this.parser = parser;
  }

  /** Method with the actual generated action code. */
  public final java_cup.runtime.Symbol CUP$Parser$do_action(
    int                        CUP$Parser$act_num,
    java_cup.runtime.lr_parser CUP$Parser$parser,
    java.util.Stack            CUP$Parser$stack,
    int                        CUP$Parser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$Parser$result;

      /* select the action based on the action number */
      switch (CUP$Parser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 16: // rvalue ::= enum 
            {
              SyntaxNode RESULT = null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		SyntaxList e = (SyntaxList)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		 RESULT = e; 
              CUP$Parser$result = new java_cup.runtime.Symbol(6/*rvalue*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 15: // rvalue ::= map 
            {
              SyntaxNode RESULT = null;
		int mleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int mright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		SyntaxMap m = (SyntaxMap)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		 RESULT = m; 
              CUP$Parser$result = new java_cup.runtime.Symbol(6/*rvalue*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 14: // rvalue ::= lvalue 
            {
              SyntaxNode RESULT = null;
		int lleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int lright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		SyntaxLValue l = (SyntaxLValue)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		 RESULT = l; 
              CUP$Parser$result = new java_cup.runtime.Symbol(6/*rvalue*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 13: // lvalue ::= TK_NUMBER 
            {
              SyntaxLValue RESULT = null;
		int numleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int numright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		Object num = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		 RESULT = new SyntaxNumber((String) num); 
              CUP$Parser$result = new java_cup.runtime.Symbol(5/*lvalue*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 12: // lvalue ::= TK_STRING 
            {
              SyntaxLValue RESULT = null;
		int strleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int strright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		Object str = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		 RESULT = new SyntaxString((String) str); 
              CUP$Parser$result = new java_cup.runtime.Symbol(5/*lvalue*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 11: // lvalue ::= TK_IDENT 
            {
              SyntaxLValue RESULT = null;
		int idleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int idright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		Object id = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		 RESULT = new SyntaxIdent((String) id); 
              CUP$Parser$result = new java_cup.runtime.Symbol(5/*lvalue*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 10: // enum_list ::= enum_list TK_SCOLON rvalue 
            {
              Object RESULT = null;
		int rleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int rright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		SyntaxNode r = (SyntaxNode)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		
	        ((SyntaxList) nodeStack.last()).add(r);
	      
              CUP$Parser$result = new java_cup.runtime.Symbol(8/*enum_list*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // enum_list ::= rvalue 
            {
              Object RESULT = null;
		int rleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int rright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		SyntaxNode r = (SyntaxNode)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		
		SyntaxList lst = new SyntaxList();
		lst.add(r);
	        nodeStack.insert(lst);
	      
              CUP$Parser$result = new java_cup.runtime.Symbol(8/*enum_list*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // enum ::= TK_LPAR enum_list TK_RPAR 
            {
              SyntaxList RESULT = null;
		
                RESULT = (SyntaxList) nodeStack.remove();
         
              CUP$Parser$result = new java_cup.runtime.Symbol(7/*enum*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // enum ::= TK_LPAR TK_RPAR 
            {
              SyntaxList RESULT = null;
		
	 	RESULT = new SyntaxList(); 
         
              CUP$Parser$result = new java_cup.runtime.Symbol(7/*enum*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // map_item ::= lvalue TK_EQ rvalue 
            {
              Object[] RESULT = null;
		int lleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left;
		int lright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).right;
		SyntaxLValue l = (SyntaxLValue)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-2)).value;
		int rleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int rright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		SyntaxNode r = (SyntaxNode)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		
	        Object[] pair = new Object[2];
                pair[0] = l;
                pair[1] = r;
	        RESULT = pair;
             
              CUP$Parser$result = new java_cup.runtime.Symbol(4/*map_item*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // map_list ::= map_list TK_SCOLON map_item 
            {
              Object RESULT = null;
		int ileft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		Object[] i = (Object[])((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		
	        ((SyntaxMap) nodeStack.last()).add((SyntaxLValue) i[0], (SyntaxNode) i[1]);
             
              CUP$Parser$result = new java_cup.runtime.Symbol(3/*map_list*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // map_list ::= map_item 
            {
              Object RESULT = null;
		int ileft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int iright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		Object[] i = (Object[])((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		
		SyntaxMap map = new SyntaxMap();
	        map.add((SyntaxLValue) i[0], (SyntaxNode) i[1]);
	        nodeStack.insert(map);
             
              CUP$Parser$result = new java_cup.runtime.Symbol(3/*map_list*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // map ::= TK_LBRA map_list TK_RBRA 
            {
              SyntaxMap RESULT = null;
		
		RESULT = (SyntaxMap) nodeStack.remove();
        
              CUP$Parser$result = new java_cup.runtime.Symbol(2/*map*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-2)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // map ::= TK_LBRA TK_RBRA 
            {
              SyntaxMap RESULT = null;
		
	  	RESULT = new SyntaxMap();
        
              CUP$Parser$result = new java_cup.runtime.Symbol(2/*map*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // first ::= enum 
            {
              Object RESULT = null;
		int eleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left;
		int eright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right;
		SyntaxList e = (SyntaxList)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-0)).value;
		
		SynAna.setTree(e);
          
              CUP$Parser$result = new java_cup.runtime.Symbol(1/*first*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          return CUP$Parser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // $START ::= first EOF 
            {
              Object RESULT = null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).right;
		Object start_val = (Object)((java_cup.runtime.Symbol) CUP$Parser$stack.elementAt(CUP$Parser$top-1)).value;
		RESULT = start_val;
              CUP$Parser$result = new java_cup.runtime.Symbol(0/*$START*/, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-1)).left, ((java_cup.runtime.Symbol)CUP$Parser$stack.elementAt(CUP$Parser$top-0)).right, RESULT);
            }
          /* ACCEPT */
          CUP$Parser$parser.done_parsing();
          return CUP$Parser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number found in internal parse table");

        }
    }
}

