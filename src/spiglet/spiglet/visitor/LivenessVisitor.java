package spiglet.visitor;

import java.util.Enumeration;

import spiglet.spiglet2kanga.*;
import spiglet.syntaxtree.*;

/**
 * Provides default methods which visit each node in the tree in depth-first
 * order.  Your visitors may extend this class.
 */
public class LivenessVisitor extends GJDepthFirst<String, Env> {
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
      argu.currentMethod = new NMethod("MAIN");
      argu.currentMethod.nParaSize = 0;
      argu.currentMethod.addStmt(new NStmt("empty"));
      n.f1.accept(this, argu);
      argu.addMethod();
      n.f3.accept(this, argu);
      return null;
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
	    argu.currentMethod = new NMethod(n.f0.f0.tokenImage);
	    argu.currentMethod.nParaSize = Integer.valueOf(n.f2.f0.tokenImage);
	    argu.currentMethod.addStmt(new NStmt("empty"));
      n.f4.accept(this, argu);
      argu.addMethod();
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
      String _ret = null;
      argu.isInStmt = true;
      n.f0.accept(this, argu);
      argu.isInStmt = false;
      return _ret;
   }

   /**
    * f0 -> "NOOP"
    */
   public String visit(NoOpStmt n, Env argu) {
      String _ret=null;
      NStmt stmt = new NStmt("NoOpStmt");
      argu.currentMethod.addStmt(stmt);
      return _ret;
   }

   /**
    * f0 -> "ERROR"
    */
   public String visit(ErrorStmt n, Env argu) {
      String _ret=null;
      NStmt stmt = new NStmt("ErrorStmt");
      argu.currentMethod.addStmt(stmt);
      return _ret;
   }

   /**
    * f0 -> "CJUMP"
    * f1 -> Temp()
    * f2 -> Label()
    */
   public String visit(CJumpStmt n, Env argu) {
      String _ret=null;
      NStmt stmt = new NStmt("CJumpStmt");
      stmt.jumpLabel = n.f2.f0.tokenImage;
      stmt.addUsedTemp(n.f1.f1.f0.tokenImage);
      argu.currentMethod.addStmt(stmt);
      return _ret;
   }

   /**
    * f0 -> "JUMP"
    * f1 -> Label()
    */
   public String visit(JumpStmt n, Env argu) {
      String _ret=null;
      NStmt stmt = new NStmt("JumpStmt");
      stmt.jumpLabel = n.f1.f0.tokenImage;
      stmt.isUnconditionJump = true;
      argu.currentMethod.addStmt(stmt);
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
	   NStmt stmt = new NStmt("HStoreStmt");
	   stmt.addUsedTemp(n.f1.f1.f0.tokenImage);
	   stmt.addUsedTemp(n.f3.f1.f0.tokenImage);
	   argu.currentMethod.addStmt(stmt);
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
	   NStmt stmt = new NStmt("HLoadStmt");
	   stmt.addUsedTemp(n.f1.f1.f0.tokenImage);
	   stmt.addUsedTemp(n.f2.f1.f0.tokenImage);
	   argu.currentMethod.addStmt(stmt);
	   return _ret;
   }

   /**
    * f0 -> "MOVE"
    * f1 -> Temp()
    * f2 -> Exp()
    */
   public String visit(MoveStmt n, Env argu) {
	   String _ret=null;
	   argu.currentStmt = new NStmt("MoveStmt");
	   argu.currentStmt.genTemp = Integer.valueOf(n.f1.f1.f0.tokenImage);
	   n.f2.accept(this, argu);
	   argu.currentMethod.addStmt(argu.currentStmt);
	   return _ret;
   }

   /**
    * f0 -> "PRINT"
    * f1 -> SimpleExp()
    */
   public String visit(PrintStmt n, Env argu) {
	   String _ret=null;
	   argu.currentStmt = new NStmt("PrintStmt");
	   n.f1.accept(this, argu);
	   argu.currentMethod.addStmt(argu.currentStmt);
	   return _ret;
   }

   /**
    * f0 -> Call()
    *       | HAllocate()
    *       | BinOp()
    *       | SimpleExp()
    */
   public String visit(Exp n, Env argu) {
      String _ret=null;
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
      argu.currentStmt = new NStmt("Return"); 
      n.f3.accept(this, argu);
      argu.currentMethod.addStmt(argu.currentStmt);
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
      argu.currentStmt = new NStmt("Call"); 
      n.f1.accept(this, argu);
      n.f3.accept(this, argu);
      argu.currentMethod.addStmt(argu.currentStmt);
      return _ret;
   }

   /**
    * f0 -> "HALLOCATE"
    * f1 -> SimpleExp()
    */
   public String visit(HAllocate n, Env argu) {
      String _ret=null;
      n.f1.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Operator()
    * f1 -> Temp()
    * f2 -> SimpleExp()
    */
   public String visit(BinOp n, Env argu) {
      String _ret=null;
      n.f1.accept(this, argu);
      n.f2.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "LT"
    *       | "PLUS"
    *       | "MINUS"
    *       | "TIMES"
    */
   public String visit(Operator n, Env argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> Temp()
    *       | IntegerLiteral()
    *       | Label()
    */
   public String visit(SimpleExp n, Env argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> "TEMP"
    * f1 -> IntegerLiteral()
    */
   public String visit(Temp n, Env argu) {
      String _ret=null;
      argu.currentStmt.addUsedTemp(n.f1.f0.tokenImage);
      return _ret;
   }

   /**
    * f0 -> <INTEGER_LITERAL>
    */
   public String visit(IntegerLiteral n, Env argu) {
      String _ret=null;
      n.f0.accept(this, argu);
      return _ret;
   }

   /**
    * f0 -> <IDENTIFIER>
    */
   public String visit(Label n, Env argu) {
      String _ret=null;
      if (argu.isInStmt)
    	  argu.currentStmt.jumpLabel = n.f0.tokenImage;
      else{
    	  NStmt stmt = new NStmt("Entry Label");
    	  stmt.entryLabel = n.f0.tokenImage;
    	  argu.currentMethod.addStmt(stmt);
      }
      return _ret;
   }

}
