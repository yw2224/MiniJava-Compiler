package minijava.minijava2piglet;

import java.io.*;

import minijava.parser.*;
import minijava.symboltable.*;
import minijava.syntaxtree.*;
import minijava.typecheck.ErrorPrinter;
import minijava.visitor.*;

public class Minijava2Piglet {
	public static String translate(InputStream in) {
		
		try {
			
			Node root = new MiniJavaParser(in).Goal();
			MClassList allClassList = new MClassList();
			
			root.accept(new BuildSymbolTableVisitor(), allClassList);
			Minijava2PigletVisitor newvis = new Minijava2PigletVisitor();
			
			allClassList.updateVarAndMethodTable(); 
			
			newvis.setAllClass(allClassList);
			newvis.setCurrentTemp(allClassList.alloc(20));
			
			/*
			for (MClass mclass : allClassList.classList){
				
				System.out.println(mclass.getName() + " " );
				for(MMethod mm : mclass.methodList.values()) {
					System.out.print(mm.getName() + " " + mm.getParentType() + mm.getOffset() + ", ");
				}
				for(MVar mv : mclass.varList.values()) {
					System.out.print(mv.getName() + " " + mv.getParentType() + mv.getOffset() + ", ");
				}
				System.out.println("");
			}*/
			
			MPiglet mp = root.accept(newvis, new MIdentifier());
			
			return mp.toString();
			
		} catch(ParseException e) {
			e.printStackTrace();
		}
		return "?????";
	}
}
