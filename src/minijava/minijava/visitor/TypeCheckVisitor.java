package minijava.visitor;

import java.util.Enumeration;
import java.util.HashSet;

import minijava.symboltable.*;
import minijava.syntaxtree.*;
import minijava.typecheck.*;

public class TypeCheckVisitor extends GJDepthFirst<MType, MType>
{
	   public MClassList allClassList;
	   private int paraNum = -1;
	   private MMethod callingMethod = null;
	   //
	   // Auto class visitors--probably don't need to be overridden.
	   //
	   public MType visit(NodeList n, MType argu) {
	      MType _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	         _count++;
	      }
	      return _ret;
	   }

	   public MType visit(NodeListOptional n, MType argu) {
	      if ( n.present() ) {
	         MType _ret=null;
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

	   public MType visit(NodeOptional n, MType argu) {
	      if ( n.present() )
	         return n.node.accept(this,argu);
	      else
	         return null;
	   }

	   public MType visit(NodeSequence n, MType argu) {
	      MType _ret=null;
	      int _count=0;
	      for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	         e.nextElement().accept(this,argu);
	         _count++;
	      }
	      return _ret;
	   }

	   public MType visit(NodeToken n, MType argu) { return null; }

	   //
	   // User-generated visitor methods below
	   //

	   //Check whether it has been defined:
	   public void checkTypeDeclared(MType type)
	   {
		   String typename = "";
		   if (type instanceof MIdentifier)
		   {
			   typename = ((MIdentifier)type).getName();
			   if (allClassList.containClass(typename))
				   return;   
		   }
		   else
		   {
			   typename = type.getType();
			   if (typename.equals("int") || typename.equals("int[]") || typename.equals("boolean"))
				   return;
		   }
		   ErrorPrinter.print("Undefined type: \'" + typename + "\'", type.getRow(), type.getCol());
	   }

	   //check {exp} is whether the type of {target}
	   public void checkExpEqual(MType exp, String target, String errmsg)
	   {
		   if (exp == null)
		   {
			   System.err.println("null exp in checkExpEqual?");
			   return;
		   }
		   if (target!= null)
		   {
			   if (!allClassList.classEqualsOrDerives(exp.getType(), target) && !exp.getType().equals(target))
				   ErrorPrinter.print(errmsg, exp.getRow(), exp.getCol());
		   }
	   }

	   //check {newVar} is whether the type of {target}
	   public boolean checkVarEqual(MIdentifier id, MVar newVar, String target, String errmsg)
	   {
		   if (newVar == null)
		   {
			   ErrorPrinter.print("Undefined variable: \'" + id.getName() + "\'", id.getRow(), id.getCol());
			   return false;
		   }
		   if (target != null)
		   {
			   if (!newVar.getType().equals(target) && !allClassList.classEqualsOrDerives(newVar.getName(), target))
			   {
				   ErrorPrinter.print(errmsg, id.getRow(), id.getCol());
				   return false;
			   }
		   }
		   return true;
	   }

	   /**
	    * f0 -> MainClass()
	    * f1 -> ( TypeDeclaration() )*
	    * f2 -> <EOF>
	    */
	   public MType visit(Goal n, MType argu) 
	   {
	      MType _ret=null;
	      allClassList=(MClassList)argu;
	      
	      n.f0.accept(this, allClassList);
	      n.f1.accept(this, allClassList);
	      n.f2.accept(this, allClassList);
	      return _ret;
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> "public"
	    * f4 -> "static"
	    * f5 -> "void"
	    * f6 -> "main"
	    * f7 -> "("
	    * f8 -> "String"
	    * f9 -> "["
	    * f10 -> "]"
	    * f11 -> Identifier()
	    * f12 -> ")"
	    * f13 -> "{"
	    * f14 -> ( VarDeclaration() )*
	    * f15 -> ( Statement() )*
	    * f16 -> "}"
	    * f17 -> "}"
	    */
	   public MType visit(MainClass n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      MIdentifier id = (MIdentifier)n.f1.accept(this, argu);
	      MClass newClass = allClassList.getClass(id.getName());
	      
	      n.f2.accept(this, argu);
	      n.f3.accept(this, argu);
	      n.f4.accept(this, argu);
	      n.f5.accept(this, argu);
	      n.f6.accept(this, argu);
	      
	      MMethod newMethod = newClass.getMethod("main");
	      n.f7.accept(this, newMethod);
	      n.f8.accept(this, newMethod);
	      n.f9.accept(this, newMethod);
	      n.f10.accept(this, newMethod);
	      n.f11.accept(this, newMethod);
	      n.f12.accept(this, newMethod);
	      n.f13.accept(this, newMethod);
	      n.f14.accept(this, newMethod);
	      n.f15.accept(this, newMethod);
	      n.f16.accept(this, newMethod);
	      n.f17.accept(this, newMethod);
	      return _ret;
	   }

	   /**
	    * f0 -> ClassDeclaration()
	    *       | ClassExtendsDeclaration()
	    */
	   public MType visit(TypeDeclaration n, MType argu) 
	   {
	      return n.f0.accept(this, argu);
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "{"
	    * f3 -> ( VarDeclaration() )*
	    * f4 -> ( MethodDeclaration() )*
	    * f5 -> "}"
	    */
	   public MType visit(ClassDeclaration n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      
	      MIdentifier id = (MIdentifier)n.f1.accept(this, argu);
	      MClass newClass = allClassList.getClass(id.getName());
	      
	      n.f2.accept(this, newClass);
	      n.f3.accept(this, newClass);
	      n.f4.accept(this, newClass);
	      n.f5.accept(this, newClass);
	      return _ret;
	   }

	   /**
	    * f0 -> "class"
	    * f1 -> Identifier()
	    * f2 -> "extends"
	    * f3 -> Identifier()
	    * f4 -> "{"
	    * f5 -> ( VarDeclaration() )*
	    * f6 -> ( MethodDeclaration() )*
	    * f7 -> "}"
	    */
	   // cycling inheritance
	   public MType visit(ClassExtendsDeclaration n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);

	      MIdentifier id = (MIdentifier)n.f1.accept(this, argu);
	      MClass newClass = allClassList.getClass(id.getName());

	      n.f2.accept(this, newClass);
	      n.f3.accept(this, newClass);

	      String basename = newClass.getBaseClassName();
	      if (!allClassList.containClass(basename))
	    	  ErrorPrinter.print("Undefined father class: \'" + basename + "\'", id.getRow(), id.getCol());
	      else
	      {
	    	  HashSet<String> baseNameSet = new HashSet<String>();
	    	  while (basename != null) 
	    	  {
	    		  if (basename.equals(newClass.getName())) 
	    		  {
	    			  ErrorPrinter.print("Circular extention of class: \'" + basename + "\'", id.getRow(), id.getCol());
	    			  break;
	    		  }
	    		  else if(baseNameSet.contains(basename)) // e.g. in case of: C extends from A which circularly extends from B
	    			  break;
	          
	    		  baseNameSet.add(basename);
	    		  MClass baseClass = allClassList.getClass(basename);
	    		  if (baseClass != null)
	    			  basename = baseClass.getBaseClassName();
	    		  else // e.g. grandfather doesn't exist, don't care
	    			  break;
	        }
	      }

	      n.f4.accept(this, newClass);
	      n.f5.accept(this, newClass);
	      n.f6.accept(this, newClass);
	      n.f7.accept(this, newClass);
	      return _ret;
	   }

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    * f2 -> ";"
	    */
	   public MType visit(VarDeclaration n, MType argu) 
	   {
	      MType _ret=null;
	      MType type = n.f0.accept(this, argu);
	      this.checkTypeDeclared(type);

	      MIdentifier id = (MIdentifier)n.f1.accept(this, argu);
	      if(argu.getType().equals("method"))
	      {
	    	 
	    	  MVar newVar = ((VarContainer)argu).getVar(id.getName());
	    	  if(newVar != null)
	    	  {
	    		  newVar.setDeclared(false);
	    		  newVar.setParentType("method");
	    	  }
	    		  
	      }

	      n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "public"
	    * f1 -> Type()
	    * f2 -> Identifier()
	    * f3 -> "("
	    * f4 -> ( FormalParameterList() )?
	    * f5 -> ")"
	    * f6 -> "{"
	    * f7 -> ( VarDeclaration() )*
	    * f8 -> ( Statement() )*
	    * f9 -> "return"
	    * f10 -> Expression()
	    * f11 -> ";"
	    * f12 -> "}"
	    */
	   public MType visit(MethodDeclaration n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);

	      MType type = n.f1.accept(this, argu);
	      checkTypeDeclared(type);
	      
	      MIdentifier id = (MIdentifier)n.f2.accept(this, argu);
	      MMethod newMethod = ((MClass)argu).getMethod(id.getName());

	      n.f3.accept(this, newMethod);
	      n.f4.accept(this, newMethod);
	      n.f5.accept(this, newMethod);
	      n.f6.accept(this, newMethod);
	      n.f7.accept(this, newMethod);
	      n.f8.accept(this, newMethod);
	      n.f9.accept(this, newMethod);

	      MType exp = n.f10.accept(this, newMethod);
	      checkExpEqual(exp, type.getType(), "Return expression does not match return type");

	      n.f11.accept(this, newMethod);
	      n.f12.accept(this, newMethod);
	      return _ret;
	   }

	   /**
	    * f0 -> FormalParameter()
	    * f1 -> ( FormalParameterRest() )*
	    */
	   public MType visit(FormalParameterList n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> Type()
	    * f1 -> Identifier()
	    */
	   public MType visit(FormalParameter n, MType argu) {
	      MType _ret=null;
	      MType type = n.f0.accept(this, argu);
	      this.checkTypeDeclared(type);
	      n.f1.accept(this, argu);

	      return _ret;
	   }

	   /**
	    * f0 -> ","
	    * f1 -> FormalParameter()
	    */
	   public MType visit(FormalParameterRest n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> ArrayType()
	    *       | BooleanType()
	    *       | IntegerType()
	    *       | Identifier()
	    */
	   public MType visit(Type n, MType argu) 
	   {
	      MType _ret = n.f0.accept(this, argu);
	      if (_ret instanceof MIdentifier){
	        _ret.setType(((MIdentifier)_ret).getName());
	      }
	      return _ret;
	   }

	   /**
	    * f0 -> "int"
	    * f1 -> "["
	    * f2 -> "]"
	    */
	   public MType visit(ArrayType n, MType argu) 
	   {
	      return new MType("int[]", n.f0.beginLine, n.f0.beginColumn);
	   }

	   /**
	    * f0 -> "boolean"
	    */
	   public MType visit(BooleanType n, MType argu) 
	   {
	      return new MType("boolean", n.f0.beginLine, n.f0.beginColumn);
	   }

	   /**
	    * f0 -> "int"
	    */
	   public MType visit(IntegerType n, MType argu) 
	   {
	      return new MType("int", n.f0.beginLine, n.f0.beginColumn);
	   }

	   /**
	    * f0 -> Block()
	    *       | AssignmentStatement()
	    *       | ArrayAssignmentStatement()
	    *       | IfStatement()
	    *       | WhileStatement()
	    *       | PrintStatement()
	    */
	   public MType visit(Statement n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "{"
	    * f1 -> ( Statement() )*
	    * f2 -> "}"
	    */
	   public MType visit(Block n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> Identifier()
	    * f1 -> "="
	    * f2 -> Expression()
	    * f3 -> ";"
	    */
	   public MType visit(AssignmentStatement n, MType argu) 
	   {
	      MIdentifier id = (MIdentifier)n.f0.accept(this, argu);
	      MVar newVar = ((VarContainer)argu).getVar(id.getName());
	   
	      if (newVar == null) 
	    	  ErrorPrinter.print("Undefined variable: '"+ id.getName() + "\'", id.getRow(), id.getCol());
	      else if(newVar != null)
	      {
	    	  MType exp = n.f2.accept(this, argu);
	    	  checkExpEqual(exp, newVar.getType(), "Type mismatch in assignment statement: \'" + id.getName() + " = " + exp.getType() + "\'");
	    	 
	    	  if(exp.getType().equals("int[]") && newVar.getType().equals("int[]"))// && exp.getDeclared()) // !!!
	    		  newVar.setArrayLen(exp.getArrayLen());
	    	  else if(exp.getType().equals("int") && newVar.getType().equals("int"))// && exp.getDeclared())
	    		  newVar.setIntValue(exp.getIntValue());
	    	  else if(exp.getType().equals("boolean") && newVar.getType().equals("boolean"))// && exp.getDeclared())
	    		  newVar.setBoolValue(exp.getBoolValue());
	    	  
	    	  newVar.setDeclared(true); //!!!
	    	  //newVar.setParentType(argu.getType());
	    	  //System.out.println(newVar.getName() + " " + newVar.getParentType());
	      }
	     
	      return null;
	   }

	   /**
	    * f0 -> Identifier()
	    * f1 -> "["
	    * f2 -> Expression()
	    * f3 -> "]"
	    * f4 -> "="
	    * f5 -> Expression()
	    * f6 -> ";"
	    */
	   public MType visit(ArrayAssignmentStatement n, MType argu) 
	   {
	      MIdentifier id = (MIdentifier)n.f0.accept(this, argu);
	      MVar newVar = ((VarContainer) argu).getVar(id.getName());
	      checkVarEqual(id, newVar, "int[]", "Type mismatch, not an array: \'" + id.getName() + "\'");
	    
	      MType exp1 = n.f2.accept(this, argu);
	      checkExpEqual(exp1, "int", "Type mismatch, need int in '[]' as index");
	      
	      MType exp2 = n.f5.accept(this, argu);
	      checkExpEqual(exp2, "int", "Type mismatch, assignment value is not an int" );
	      
	      if(newVar.getType().equals("int[]") && exp1.getType().equals("int"))// && newVar.getDeclared() && exp1.getDeclared()) // !!!
	      {
	    	  //System.out.println(newVar.getArrayLen());
	    	  if(exp1.getIntValue() >= newVar.getArrayLen() && newVar.getArrayLen() > 0)// && newVar.getParent().getType().equals("method"))
	    		  ErrorPrinter.print("Array index out of bounds: '"+ newVar.getName() + "\'", id.getRow(), id.getCol());
	      }
	      
	      n.f6.accept(this, argu);
	      return null;
	   }

	   /**
	    * f0 -> "if"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> Statement()
	    * f5 -> "else"
	    * f6 -> Statement()
	    */
	   public MType visit(IfStatement n, MType argu) 
	   {
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      
	      MType exp = n.f2.accept(this, argu);
	      checkExpEqual(exp, "boolean", "Type mismatch, need boolean in if-statement");
	      
	      n.f3.accept(this, argu);
	      n.f4.accept(this, argu);
	      n.f5.accept(this, argu);
	      n.f6.accept(this, argu);
	      return null;
	   }

	   /**
	    * f0 -> "while"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> Statement()
	    */
	   public MType visit(WhileStatement n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      
	      MType exp = n.f2.accept(this, argu);
	      checkExpEqual(exp, "boolean", "Type mismatch, need boolean in while-statement");
	      
	      n.f3.accept(this, argu);
	      n.f4.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "System.out.println"
	    * f1 -> "("
	    * f2 -> Expression()
	    * f3 -> ")"
	    * f4 -> ";"
	    */
	   public MType visit(PrintStatement n, MType argu) {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      
	      MType exp = n.f2.accept(this, argu);
	      checkExpEqual(exp, "int", "Type mismatch, need int in print-statement");
	      
	      n.f3.accept(this, argu);
	      n.f4.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> AndExpression()
	    *       | CompareExpression()
	    *       | PlusExpression()
	    *       | MinusExpression()
	    *       | TimesExpression()
	    *       | ArrayLookup()
	    *       | ArrayLength()
	    *       | MessageSend()
	    *       | PrimaryExpression()
	    */
	   public MType visit(Expression n, MType argu) 
	   {
	      MType _ret = n.f0.accept(this, argu);
	      if (this.paraNum >= 0)
	      {
	        MVar param = callingMethod.getParam(this.paraNum++);
	        if (param == null)
	        {
	        	ErrorPrinter.print("Parameter number in calling method: " + this.callingMethod.getName() + " number doesn't match.", _ret.getRow(), _ret.getCol());
	        }
	        else
	        {
	        	checkExpEqual(_ret, param.getType(), "Type mismatch, wrong parameter type \'" + _ret.getType() + "\' when calling method: \'" + this.callingMethod.getName()+ "\'");
	        }
	      }
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "&&"
	    * f2 -> PrimaryExpression()
	    */
	   public MType visit(AndExpression n, MType argu) 
	   {
	      MType exp1 = n.f0.accept(this, argu);
	      checkExpEqual(exp1, "boolean", "Type mismatch, need boolean at '&&' left");
	      MType exp2 = n.f2.accept(this, argu);
	      checkExpEqual(exp2, "boolean", "Type mismatch, need boolean at '&&' right");
	      
	      MType _ret = new MType("boolean", exp1.getRow(), exp1.getCol());
	     // _ret.setBoolValue(_value);
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "<"
	    * f2 -> PrimaryExpression()
	    */
	   public MType visit(CompareExpression n, MType argu) {
	      MType exp1 = n.f0.accept(this, argu);
	      MType exp2 = n.f2.accept(this, argu);
	      checkExpEqual(exp1, "int", "Type mismatch, need int at '<' left");
	      checkExpEqual(exp2, "int", "Type mismatch, need int at '<' right");
	      return new MType("boolean", exp1.getRow(), exp1.getCol());
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "+"
	    * f2 -> PrimaryExpression()
	    */
	   public MType visit(PlusExpression n, MType argu) 
	   {
	      MType exp1 = n.f0.accept(this, argu);
	      MType exp2 = n.f2.accept(this, argu);
	      checkExpEqual(exp1, "int", "Type mismatch, need int at '+' left");
	      checkExpEqual(exp2, "int", "Type mismatch, need int at '+' right");
	      
	      MType _ret = new MType("int", exp1.getRow(), exp1.getCol()); //!!!
	      if(exp1.getType().equals("int") && exp2.getType().equals("int"))// && exp1.getDeclared() && exp2.getDeclared())
	      {
	    	  _ret.setIntValue(exp1.getIntValue() + exp2.getIntValue());
	    	  //_ret.setDecalred(true);
	      }
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "-"
	    * f2 -> PrimaryExpression()
	    */
	   public MType visit(MinusExpression n, MType argu) {
	      MType exp1 = n.f0.accept(this, argu);
	      MType exp2 = n.f2.accept(this, argu);
	      checkExpEqual(exp1, "int", "Type mismatch, need int at '-' left");
	      checkExpEqual(exp2, "int", "Type mismatch, need int at '-' right");
	      
	      MType _ret = new MType("int", exp1.getRow(), exp1.getCol()); //!!!
	      if(exp1.getType().equals("int") && exp2.getType().equals("int"))// && exp1.getDeclared() && exp2.getDeclared())
	      {
	    	  _ret.setIntValue(exp1.getIntValue() - exp2.getIntValue());
	    	  //_ret.setDecalred(true);
	      }
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "*"
	    * f2 -> PrimaryExpression()
	    */
	   public MType visit(TimesExpression n, MType argu) {
	      MType exp1 = n.f0.accept(this, argu);
	      MType exp2 = n.f2.accept(this, argu);
	      checkExpEqual(exp1, "int", "Type mismatch, need int at '*' left");
	      checkExpEqual(exp2, "int", "Type mismatch, need int at '*' right");
	      
	      MType _ret = new MType("int", exp1.getRow(), exp1.getCol()); //!!!
	      if(exp1.getType().equals("int") && exp2.getType().equals("int"))// && exp1.getDeclared() && exp2.getDeclared())
	      {
	    	  _ret.setIntValue(exp1.getIntValue() * exp2.getIntValue());
	    	  //_ret.setDecalred(true);
	      }
	      return _ret;
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "["
	    * f2 -> PrimaryExpression()
	    * f3 -> "]"
	    */
	   public MType visit(ArrayLookup n, MType argu) {
	      MType id = n.f0.accept(this, argu);
	      MType exp2 = n.f2.accept(this, argu);
	      checkExpEqual(id, "int[]", "Type mismatch, not an array: '" + ((MIdentifier)id).getName() + "'");
	      checkExpEqual(exp2, "int", "Type mismatch, need int in '[]' as index");
	      
	      if(id.getType().equals("int[]") && exp2.getType().equals("int"))// && id.getDeclared() && exp2.getDeclared())
	      {
	    	  if(id.getArrayLen() <= exp2.getIntValue() && id.getArrayLen() > 0 && argu.getType().equals("method"))
	    		  ErrorPrinter.print("Array index out of bounds: '"+ ((MIdentifier)id).getName() + "\'", id.getRow(), id.getCol());
	      }
	    	  
	      return new MType("int", id.getRow(), id.getCol());
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "."
	    * f2 -> "length"
	    */
	   public MType visit(ArrayLength n, MType argu) 
	   {
	      MType exp1 = n.f0.accept(this, argu);
	      checkExpEqual(exp1, "int[]", "Type mismatch, need array at '.length' left");
	      
	      MType _ret = new MType("int", exp1.getRow(), exp1.getCol()); //!!!
	      if(exp1.getType().equals("int[]"))
	      {
	    	  _ret.setIntValue(exp1.getArrayLen());
	    	  //System.out.println(_ret.getIntValue());
	    	  //_ret.setDecalred(true);
	      }
	      return _ret;
	      
	   }

	   /**
	    * f0 -> PrimaryExpression()
	    * f1 -> "."
	    * f2 -> Identifier()
	    * f3 -> "("
	    * f4 -> ( ExpressionList() )?
	    * f5 -> ")"
	    */
	   public MType visit(MessageSend n, MType argu) 
	   {
	      MType _ret=null;

	      int tmpParaNum2 = this.paraNum;
	      this.paraNum = -1;
	      MType id = n.f0.accept(this, argu);
	      this.paraNum = tmpParaNum2;
	           
	      String methodName = ((MIdentifier)n.f2.accept(this, argu)).getName();
	      String className = "";
	          
	      if (id instanceof MIdentifier)
	      {
	        MVar var = ((VarContainer)argu).getVar(((MIdentifier)id).getName());
	        if (var == null)
	        {
	            ErrorPrinter.print("Undefined variable: \'" + ((MIdentifier)id).getName() + "\'", id.getRow(), id.getCol());
	            return new MType("undefined type", id.getRow(), id.getCol());
	        }
	        else{
	          className = var.getType();
	        }
	      }
	      else
	      {
	        className = id.getType();
	      }
	      MClass mclass = allClassList.getClass(className);
	      if (mclass == null)
	      {
	        ErrorPrinter.print("Undefined class: \'" + className + "\'", id.getRow(), id.getCol());
	        return new MType("undefined type", id.getRow(), id.getCol());   
	      }
	      MMethod method = (mclass).getMethod(methodName);
	      if (method == null)
	      {
	        ErrorPrinter.print("Undefined method: \'" + methodName + "\' in class " + className, id.getRow(), id.getCol());
	        return new MType("undefined type", id.getRow(), id.getCol());         
	      }
	    
	      _ret = new MType(method.getReturnType(), id.getRow(), id.getCol());
	   
	      int tmpParaNum = this.paraNum;
	      MMethod tmpMethod = this.callingMethod;
	      this.paraNum = 0;
	      this.callingMethod = method;
	      
	      n.f4.accept(this, argu);
	      if (this.paraNum != method.getParamSize()) {
	        ErrorPrinter.print("Number of param in MessageSend doesn't match ", _ret.getRow(), _ret.getCol());
	      }
	      this.paraNum = tmpParaNum;
	      this.callingMethod = tmpMethod;
	      
	      return _ret;
	   }

	   /**
	    * f0 -> Expression()
	    * f1 -> ( ExpressionRest() )*
	    */
	   public MType visit(ExpressionList n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> ","
	    * f1 -> Expression()
	    */
	   public MType visit(ExpressionRest n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> IntegerLiteral()
	    *       | TrueLiteral()
	    *       | FalseLiteral()
	    *       | Identifier()
	    *       | ThisExpression()
	    *       | ArrayAllocationExpression()
	    *       | AllocationExpression()
	    *       | NotExpression()
	    *       | BracketExpression()
	    */
	   public MType visit(PrimaryExpression n, MType argu) 
	   {
	      MType _ret = n.f0.accept(this, argu);
	      // only consider identifier: must be variable name
	      if (_ret instanceof MIdentifier)
	      {
	    	  MVar newVar = ((VarContainer)argu).getVar(((MIdentifier)_ret).getName());
	    	  if (newVar == null)
	    	  {
	    		  ErrorPrinter.print("Undefined variable: \'" + ((MIdentifier)_ret).getName() + "\'", _ret.getRow(), _ret.getCol());
	    		  ((MIdentifier)_ret).setType("undefined type");
	    	  }
	    	  else
	    	  {
	    		  ((MIdentifier)_ret).setType(newVar.getType());
	   
	    		  if(!newVar.getDeclared())// && newVar.getParentType().equals("method")) //!!!
	    			  ErrorPrinter.print("Use of undeclared variable: \'" + newVar.getName() + "\'", _ret.getRow(), _ret.getCol());
	    		  else 
	    		  {
	    			  if(newVar.getType().equals("int[]"))
	    				  _ret.setArrayLen(newVar.getArrayLen());
	    			  else if(newVar.getType().equals("int"))
	    				  _ret.setIntValue(newVar.getIntValue());
	    			  else if(newVar.getType().equals("boolean"))
	    				  _ret.setBoolValue(newVar.getBoolValue());
	    			  newVar.setDeclared(true);
	    		  }
	    	  }
	      }
	      return _ret;
	   }

	   /**
	    * f0 -> <INTEGER_LITERAL>
	    */
	   public MType visit(IntegerLiteral n, MType argu) 
	   {
		   MType _ret = new MType("int", n.f0.beginLine, n.f0.beginColumn);
		   _ret.setIntValue(Integer.parseInt(n.f0.toString()));
		   //_ret.setDecalred(true);
	       return _ret;
	   }

	   /**
	    * f0 -> "true"
	    */
	   public MType visit(TrueLiteral n, MType argu) 
	   {
		  MType _ret = new MType("boolean", n.f0.beginLine, n.f0.beginColumn);
		  _ret.setBoolValue(true);
		  //_ret.setDecalred(true);
	      return _ret;
	   }

	   /**
	    * f0 -> "false"
	    */
	   public MType visit(FalseLiteral n, MType argu) 
	   {
		   MType _ret = new MType("boolean", n.f0.beginLine, n.f0.beginColumn);
		   _ret.setBoolValue(false);
		   //_ret.setDecalred(true);
		   return _ret;
	   }

	   /**
	    * f0 -> <IDENTIFIER>
	    */
	   public MIdentifier visit(Identifier n, MType argu) 
	   {
		  
	      String idName = n.f0.toString();
	      MIdentifier _ret = null;
	      _ret = new MIdentifier(idName, null, n.f0.beginLine, n.f0.beginColumn); 

	      return _ret;
	     
	   }

	   /**
	    * f0 -> "this"
	    */
	   public MType visit(ThisExpression n, MType argu) 
	   {
	      MIdentifier parent = ((MIdentifier) argu).getParent();
	      if (!(parent instanceof MClass)) {
	        System.err.println(parent.getName() + " is not an MClass");
	      }
	      return new MType(parent.getName(), n.f0.beginLine, n.f0.beginColumn);
	   }

	   /**
	    * f0 -> "new"
	    * f1 -> "int"
	    * f2 -> "["
	    * f3 -> Expression()
	    * f4 -> "]"
	    */
	   public MType visit(ArrayAllocationExpression n, MType argu) 
	   {
	      n.f0.accept(this, argu);
	      n.f1.accept(this, argu);
	      n.f2.accept(this, argu);
	      MType exp = n.f3.accept(this, argu);
	      checkExpEqual(exp, "int", "Type mismatch, need int as array length");
	 
	      n.f4.accept(this, argu);
	      MType _ret = new MType("int[]", n.f0.beginLine, n.f0.beginColumn);

    	  //if(exp.getDeclared() && (exp.getIntValue() != 0)) // !!!
	      if((exp.getIntValue() != 0))
	      {
	    	  _ret.setArrayLen(exp.getIntValue());
	    	  
	    	  //_ret.setDecalred(true);
	      }
	      return _ret;
	   }

	   /**
	    * f0 -> "new"
	    * f1 -> Identifier()
	    * f2 -> "("
	    * f3 -> ")"
	    */
	   public MType visit(AllocationExpression n, MType argu) 
	   {
	      MType _ret=null;
	      n.f0.accept(this, argu);
	      
	      MIdentifier id = (MIdentifier)n.f1.accept(this, argu);
	      checkTypeDeclared(id);
	      _ret = new MType(id.getName(), id.getRow(), id.getCol());
	      
	      n.f2.accept(this, argu);
	      n.f3.accept(this, argu);
	      return _ret;
	   }

	   /**
	    * f0 -> "!"
	    * f1 -> Expression()
	    */
	   public MType visit(NotExpression n, MType argu) 
	   {
	      MType exp1 = n.f1.accept(this, argu);
	      checkExpEqual(exp1, "boolean", "Type mismatch, need boolean at '!' right");
	      
	      MType _ret = new MType("boolean", exp1.getRow(), exp1.getCol());
	      if(exp1.getType().equals("boolean")) // !!!
	      {
	    	  _ret.setBoolValue(!exp1.getBoolValue());
	    	  //_ret.setDecalred(true);
	      }

	      return _ret;
	   }

	   /**
	    * f0 -> "("
	    * f1 -> Expression()
	    * f2 -> ")"
	    */
	   public MType visit(BracketExpression n, MType argu) 
	   {
	      MType _ret = n.f1.accept(this, argu);
	      return _ret;
	   }

	}


