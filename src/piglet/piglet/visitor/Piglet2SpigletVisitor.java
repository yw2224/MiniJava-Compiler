package piglet.visitor;

import java.util.*;

import piglet.piglet2spiglet.*;
import piglet.syntaxtree.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class Piglet2SpigletVisitor extends GJNoArguDepthFirst<MSpiglet>{
	protected final int TAB_NUM = 1;
	protected int currentTemp;
	public void setTempNum(int x){this.currentTemp = x;}
	public String getNextTemp(){return "TEMP " + (currentTemp++);}
   //
   // Auto class visitors--probably don't need to be overridden.
   //
   public MSpiglet visit(NodeList n) {
	  MSpiglet _ret= new MSpiglet();
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         _ret.append(e.nextElement().accept(this));
         _count++;
      }
      return _ret;
   }

   public MSpiglet visit(NodeListOptional n) {
      if ( n.present() ) {
    	  MSpiglet _ret= new MSpiglet();
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
        	MSpiglet sp1 = e.nextElement().accept(this);
        	_ret.append(sp1);
        	if (sp1 != null && sp1.getTemp() != null)
        		_ret.addTemp(sp1.getTemp());
            _count++;
         }
         return _ret;
      }
      else
         return null;
   }

   public MSpiglet visit(NodeOptional n) {
      if ( n.present() ){
    	  if (n.node instanceof Label)
    		  return new MSpiglet(n.node.toString(), 0);
         return n.node.accept(this);
      }
      else
         return null;
   }

   public MSpiglet visit(NodeSequence n) {
	   MSpiglet _ret= new MSpiglet();
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         _ret.append(e.nextElement().accept(this));
         _count++;
      }
      return _ret;
   }

   public MSpiglet visit(NodeToken n) { return null; }

   //
   // User-generated visitor methods below
   //

   /**
    * f0 -> "MAIN"
    * f1 -> StmtList()
    * f2 -> "END"
    * f3 -> ( Procedure() )*
    * f4 -> <EOF>
    */
   public MSpiglet visit(Goal n) {
	   MSpiglet _ret= new MSpiglet();
      _ret.append(new MSpiglet("MAIN", 0));
      _ret.append(n.f1.accept(this));
      _ret.append(new MSpiglet("END", 0));
      _ret.append(n.f3.accept(this));
      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public MSpiglet visit(StmtList n) {
	   //System.out.println("???" + n.f0.toString());
	   MSpiglet _ret=n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public MSpiglet visit(Procedure n) {
	   MSpiglet _ret=new MSpiglet(n.f0.f0.tokenImage + "[" + n.f2.f0.tokenImage + "]", 0);
	   _ret.append(new MSpiglet("BEGIN", 0));
	   MSpiglet sp1 = n.f4.accept(this);
	   _ret.append(sp1);
	   _ret.append(new MSpiglet("RETURN " + sp1.getTemp(), TAB_NUM));
	   _ret.append(new MSpiglet("END", 0));
      return _ret;
   }

   /**
    * f0 -> NoOpStmt()
    *       | ErrorStmt()
    *       | CJumpStmt()
    *       | JumpStmt()
    *       | HStoreStmt()
    *       | HLoadStmt()
    *       | MoveStmt()
    *       | PrintStmt()
    */
   public MSpiglet visit(Stmt n) {
	   MSpiglet _ret=n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public MSpiglet visit(NoOpStmt n) {
	  MSpiglet _ret= new MSpiglet("NOOP", TAB_NUM);
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public MSpiglet visit(ErrorStmt n) {
	  MSpiglet _ret= new MSpiglet("ERROR", TAB_NUM);
      return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Exp()
    * f2 -> Label()
    */
   public MSpiglet visit(CJumpStmt n) {
	   MSpiglet _ret = new MSpiglet();
	   MSpiglet sp1 = n.f1.accept(this);
	   _ret.append(sp1);
	   _ret.append(new MSpiglet("CJUMP " + sp1.getTemp() + " " + n.f2.f0.tokenImage, TAB_NUM));
	   return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public MSpiglet visit(JumpStmt n) {
	  MSpiglet _ret = new MSpiglet("JUMP " + n.f1.f0.tokenImage, TAB_NUM);
	  //System.out.println("???" + n.f1.f0.tokenImage);
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Exp()
    * f2 -> IntegerLiteral()
    * f3 -> Exp()
    */
   public MSpiglet visit(HStoreStmt n) {
	  MSpiglet _ret=new MSpiglet();
	  MSpiglet sp1 = n.f1.accept(this);
	  MSpiglet sp2 = n.f3.accept(this);
	  _ret.append(sp1);
	  _ret.append(sp2);
	  _ret.append(new MSpiglet("HSTORE " + sp1.getTemp() + " " + n.f2.f0.tokenImage + " " + sp2.getTemp(), TAB_NUM));
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Exp()
    * f3 -> IntegerLiteral()
    */
   public MSpiglet visit(HLoadStmt n) {
	   MSpiglet _ret=new MSpiglet();
	   MSpiglet sp1 = n.f1.accept(this);
	   MSpiglet sp2 = n.f2.accept(this);
	   _ret.append(sp1);
	   _ret.append(sp2);
	   _ret.append(new MSpiglet("HLOAD " + sp1.getTemp() + " " + sp2.getTemp() + " " + n.f3.f0.tokenImage, TAB_NUM));
	   return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public MSpiglet visit(MoveStmt n) {
	   MSpiglet _ret=new MSpiglet();
	   MSpiglet sp1 = n.f1.accept(this);
	   MSpiglet sp2 = n.f2.accept(this);
	   _ret.append(sp1);
	   _ret.append(sp2);
	   _ret.append(new MSpiglet("MOVE " + sp1.getTemp() + " " + sp2.getTemp(), TAB_NUM));
	   return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> Exp()
    */
   public MSpiglet visit(PrintStmt n) {
	   MSpiglet  _ret = new MSpiglet();
	   MSpiglet sp1 = n.f1.accept(this);
	   _ret.append(sp1);
	   _ret.append(new MSpiglet("PRINT " + sp1.getTemp(), TAB_NUM));
	   return _ret;
   }

   /**
    * f0 -> StmtExp()
    *       | Call()
    *       | HAllocate()
    *       | BinOp()
    *       | Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public  MSpiglet visit(Exp n) {
	   MSpiglet _ret= n.f0.accept(this);
      return _ret;
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> Exp()
    * f4 -> "END"
    */
   public MSpiglet visit(StmtExp n) {
	   MSpiglet _ret=new MSpiglet();
	   MSpiglet sp1 = n.f1.accept(this);
	   MSpiglet sp2 = n.f3.accept(this);
	   _ret.append(sp1);
	   _ret.append(sp2);
	   _ret.setTemp(sp2.getTemp());
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> Exp()
    * f2 -> "("
    * f3 -> ( Exp() )*
    * f4 -> ")"
    */
   public MSpiglet visit(Call n) {
	   MSpiglet _ret = new MSpiglet();
	   MSpiglet sp1 = n.f1.accept(this);
	   MSpiglet sp2 = n.f3.accept(this);
	   
	   String temp1 = this.getNextTemp();
		  
	   _ret.append(sp1);
	   _ret.append(sp2);
	   String code = "CALL " + sp1.getTemp() + "(";
	   for (String temp : sp2.getTempList())
			  code += temp + " ";
	   code += ")";
	   _ret.append(new MSpiglet("MOVE " + temp1 + " " + code, TAB_NUM));
	   _ret.setTemp(temp1);
	   return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> Exp()
    */
   public MSpiglet visit(HAllocate n) {
	   MSpiglet _ret = new MSpiglet();
	   MSpiglet sp1 = n.f1.accept(this);
	   String temp1 = this.getNextTemp();
	      
	   _ret.append(sp1);
	   _ret.append(new MSpiglet("MOVE " + temp1 + " HALLOCATE " + sp1.getTemp(), TAB_NUM));
	   _ret.setTemp(temp1);
	      
	   return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Exp()
    * f2 -> Exp()
    */
   public MSpiglet visit(BinOp n) {
	   MSpiglet _ret = new MSpiglet();
	   MSpiglet sp0 = n.f0.accept(this);
	   MSpiglet sp1 = n.f1.accept(this);
	   MSpiglet sp2 = n.f2.accept(this);
	   String temp1 = this.getNextTemp();
	   
	   _ret.append(sp1);
	   _ret.append(sp2);
	   _ret.append(new MSpiglet("MOVE " + temp1 + " " + sp0.getOp() + " " + sp1.getTemp() + " " + sp2.getTemp(), TAB_NUM));
	   _ret.setTemp(temp1);
	   return _ret;
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public MSpiglet visit(Operator n) {
	   MSpiglet _ret = new MSpiglet();
	   String op;
	   if (n.f0.which == 0) op = "LT";
	   else if (n.f0.which == 1) op = "PLUS";
	   else if (n.f0.which == 2) op = "MINUS";
	   else op = "TIMES";
	   _ret.setOp(op);
	   return _ret;
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public MSpiglet visit(Temp n) {
	   MSpiglet _ret = new MSpiglet();
	   String temp1 = "TEMP " + n.f1.f0.tokenImage;
	   _ret.setTemp(temp1);
	   return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public MSpiglet visit(IntegerLiteral n) {
	   String temp1 = this.getNextTemp();
	   MSpiglet _ret = new MSpiglet("MOVE " + temp1 + " " + n.f0.tokenImage, TAB_NUM);
	   _ret.setTemp(temp1);
	   _ret.setSimpleExp(n.f0.tokenImage);
	   return _ret;
   } 

   /**
    * f0 -> <IDENTIFIER>
    */
   public MSpiglet visit(Label n) {
	   // System.out.println("???" + n.f0.toString());
	    String temp1 = this.getNextTemp();
	    MSpiglet _ret = new MSpiglet("MOVE " + temp1 + " " + n.f0.tokenImage, TAB_NUM);
	    _ret.setTemp(temp1);
	    _ret.setSimpleExp(n.f0.tokenImage);
	    return _ret;
   }

}
