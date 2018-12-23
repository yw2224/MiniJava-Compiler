package minijava.typecheck;

import java.util.*;

import minijava.parser.*;
import minijava.symboltable.*;
import minijava.syntaxtree.*;
import minijava.visitor.*;

import java.io.*;
 
public class Main
{
    public static void main (String[] args)
    {  
		try {
			InputStream in = new FileInputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/Inheritance.java");
			Node root = new MiniJavaParser(in).Goal();
			MType allClassList = new MClassList();
			
			root.accept(new BuildSymbolTableVisitor(), allClassList);
			root.accept(new TypeCheckVisitor(), allClassList);
			
			if(ErrorPrinter.getsize() == 0)
				System.out.println("Program type checked successfully!");
			else 
				System.out.println("Type error!");
			
			ErrorPrinter.printAll();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    }
}
