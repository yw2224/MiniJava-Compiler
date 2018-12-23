package spiglet.visitor;

import java.util.Enumeration;

import spiglet.spiglet2kanga.*;
import spiglet.syntaxtree.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class Spiglet2KangaVisitor extends GJDepthFirst<String, Env> {
   //
   // Auto class visitors--probably don't need to be overridden.
   //
   public String visit(NodeList n, Env argu) {
      String _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public String visit(NodeListOptional n, Env argu) {
      if ( n.present() ) {
         String _ret=null;
         int _count=0;
         for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
            e.nextElement().accept(this,argu);
            _count++;
         }
         return _ret;
      }
      else
         return null;
   }

   public String visit(NodeOptional n, Env argu) {
      if ( n.present() )
         return n.node.accept(this,argu);
      else
         return null;
   }

   public String visit(NodeSequence n, Env argu) {
      String _ret=null;
      int _count=0;
      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
         e.nextElement().accept(this,argu);
         _count++;
      }
      return _ret;
   }

   public String visit(NodeToken n, Env argu) { return null; }
   
   public String getReg(int t){return (t < 8) ? "s"+t : "t"+(t-8);}
   public void move(String str, Env argu){
	   if (argu.moveToReg == -1) return;
	   int t = argu.moveToReg;
	   argu.moveToReg = -1;
	   
	   String _ret = "";
	   if (t < 18){
		   _ret = getReg(t);
		   argu.append("MOVE " + _ret + " " + str);
	   }
	   else{
		   argu.append("MOVE v0 " + str);
		   argu.append("ASTORE SPILLEDARG " + (t+argu.currentMethod.nSpilledPara) + " v0");
	   }
   }
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
   public String visit(Goal n, Env argu) {
      String _ret = null;
      argu.setMethod("MAIN");
      argu.append("MAIN[0][0][30]");
      n.f1.accept(this, argu);
      argu.append("END\n");
      n.f3.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> ( ( Label() )? Stmt() )*
    */
   public String visit(StmtList n, Env argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Label()
    * f1 -> "["
    * f2 -> IntegerLiteral()
    * f3 -> "]"
    * f4 -> StmtExp()
    */
   public String visit(Procedure n, Env argu) {
	   String _ret=null;
	   argu.setMethod(n.f0.f0.tokenImage);
      
	   String stmt = n.f0.f0.tokenImage + "[" + argu.currentMethod.nParaSize + "]";
	   stmt += "[";
	   stmt += (argu.currentMethod.nSpilledPara + argu.currentMethod.nSpilledSize);
	   stmt += "][30]";
	   argu.append(stmt);
	   for (int i = 0; i < Math.min(18, argu.currentMethod.nSpilledSize); ++i)
		    argu.append("ASTORE SPILLEDARG " + (i+argu.currentMethod.nSpilledPara) + " " + getReg(i));
	  
	   for (int i = 0; i < argu.currentMethod.nParaSize; ++i){
		    if (argu.currentMethod.getReg(i) == -1) continue;
		    if (i < 4) 
			   argu.append("MOVE " + getReg(argu.currentMethod.getReg(i)) + " a" + i);
		    else
			   argu.append("ALOAD " + getReg(argu.currentMethod.getReg(i)) + " SPILLEDARG " + (i-4));
	   }
	  
	   n.f4.accept(this, argu);
	  
	   for (int i = 0; i < Math.min(18, argu.currentMethod.nSpilledSize); ++i)
		    argu.append("ALOAD " + getReg(i) + " SPILLEDARG " + (i+argu.currentMethod.nSpilledPara));
	  
	   argu.append("END\n");
	  
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
   public String visit(Stmt n, Env argu) {
      String _ret=null;
      argu.isInStmt = true;
      argu.vReg = 0;
	    argu.isPassingPara = -1;
      n.f0.accept(this, argu);
      argu.isInStmt = false;
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public String visit(NoOpStmt n, Env argu) {
      String _ret=null;
      argu.append("NOOP");
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public String visit(ErrorStmt n, Env argu) {
      String _ret=null;
      argu.append("ERROR");
      return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public String visit(CJumpStmt n, Env argu) {
      String _ret=null;
      String r1 = n.f1.accept(this, argu);
      argu.append("CJUMP " + r1 + " " + n.f2.f0.tokenImage);
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public String visit(JumpStmt n, Env argu) {
      String _ret=null;
      argu.append("JUMP " + n.f1.f0.tokenImage);
      return _ret;
   }

   /**
    * f0 -> "HSTORE"
    * f1 -> Temp()
    * f2 -> IntegerLiteral()
    * f3 -> Temp()
    */
   public String visit(HStoreStmt n, Env argu) {
      String _ret=null;
      String r1 = n.f1.accept(this, argu);
      String r2 = n.f3.accept(this, argu);
      
      argu.append("HSTORE " + r1 + " " + n.f2.f0.tokenImage + " " + r2);
      
      return _ret;
   }

   /**
    * f0 -> "HLOAD"
    * f1 -> Temp()
    * f2 -> Temp()
    * f3 -> IntegerLiteral()
    */
   public String visit(HLoadStmt n, Env argu) {
      String _ret=null;
      String r2 = n.f2.accept(this, argu);
      
      int t = Integer.valueOf(n.f1.f1.f0.tokenImage);
      t = argu.currentMethod.getReg(t);
      if (t < 18)
    	  argu.append("HLOAD " + this.getReg(t) + " " + r2 + " " + n.f3.f0.tokenImage);
      else{
    	  String rv = "v" + (argu.vReg++);
    	  argu.append("HLOAD " + rv + " " + r2 + " " + n.f3.f0.tokenImage);
    	  argu.append("ASTORE SPILLEDARG " + (t+argu.currentMethod.nSpilledPara) + " " + rv);
      }
      
      return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public String visit(MoveStmt n, Env argu) {
      String _ret=null;
      int t = Integer.valueOf(n.f1.f1.f0.tokenImage);
      t = argu.currentMethod.getReg(t);
      // t == -1: dead code, drop it
      if (t == -1) return _ret;
      argu.moveToReg = t;
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public String visit(PrintStmt n, Env argu) {
      String _ret=null;
      String r1 = n.f1.accept(this, argu);
      argu.append("PRINT " + r1);
      return _ret;
   }

   /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   // store result in v0
   public String visit(Exp n, Env argu) {
      String _ret = null;
      n.f0.accept(this, argu);
      
      return _ret;
   }

   /**
    * f0 -> "BEGIN"
    * f1 -> StmtList()
    * f2 -> "RETURN"
    * f3 -> SimpleExp()
    * f4 -> "END"
    */
   public String visit(StmtExp n, Env argu) {
      String _ret=null;
      n.f1.accept(this, argu);
      String r1 = n.f3.accept(this, argu);
      argu.append("MOVE v0 " + r1);
      return _ret;
   }

   /**
    * f0 -> "CALL"
    * f1 -> SimpleExp()
    * f2 -> "("
    * f3 -> ( Temp() )*
    * f4 -> ")"
    */
   public String visit(Call n, Env argu) {
	  String _ret=null;
	  argu.isPassingPara = 0;
	  n.f3.accept(this, argu);
      
	  int tmp = argu.moveToReg;
	  argu.moveToReg = -1;
	  String r1 = n.f1.accept(this, argu);
	  argu.moveToReg = tmp;
	  argu.append("CALL " + r1);
      move("v0", argu);
      
      return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public String visit(HAllocate n, Env argu) {
      String _ret=null;
      int tmp = argu.moveToReg;
	  argu.moveToReg = -1;
      String r1 = n.f1.accept(this, argu);
      argu.moveToReg = tmp;
      move("HALLOCATE " + r1, argu);
      
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public String visit(BinOp n, Env argu) {
      String _ret=null;
      String r1 = n.f1.accept(this, argu);
      int tmp = argu.moveToReg;
	  argu.moveToReg = -1;
      String r2 = n.f2.accept(this, argu);
      argu.moveToReg = tmp;
      String code = n.f0.accept(this, argu) + " " + r1 + " " + r2;
      if (argu.moveToReg < 18)
    	  move(code, argu);
      else{
    	  code = "MOVE v0 " + code;
    	  argu.append(code);
    	  move("v0", argu);
      }
      
      return _ret;
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public String visit(Operator n, Env argu) {
      String _ret = ((NodeToken)n.f0.choice).tokenImage;
      return _ret;
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public String visit(SimpleExp n, Env argu) {
      String _ret = n.f0.accept(this, argu);
      if (argu.moveToReg >= 0 )
    	  move(_ret, argu);
      return _ret;
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public String visit(Temp n, Env argu) {
      String _ret=null;
      int t = Integer.valueOf(n.f1.f0.tokenImage);
      t = argu.currentMethod.getReg(t);
      if (t < 18){
    	  _ret = getReg(t);
      }
      else{
    	  if (argu.isPassingPara >= 0)
    		  _ret = "v0";
    	  else
    		  _ret = "v" + (argu.vReg++); 
    	  argu.append("ALOAD " + _ret + " SPILLEDARG " + (t+argu.currentMethod.nSpilledPara));
      }
      if (argu.isPassingPara >= 0){
    	  if (argu.isPassingPara <= 3) 
    		  argu.append("MOVE a" + argu.isPassingPara + " " + _ret);
    	  else 
    		  argu.append("PASSARG " + (argu.isPassingPara-3) + " " + _ret);
    	  argu.isPassingPara++;
      }
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, Env argu) {
      String _ret = n.f0.tokenImage;
      return _ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Label n, Env argu) {
      String _ret = n.f0.tokenImage;
      if (argu.isInStmt == false)
    	  argu.append(_ret);
      return _ret;
   }
}
