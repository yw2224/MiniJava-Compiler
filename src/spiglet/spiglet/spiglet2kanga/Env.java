package spiglet.spiglet2kanga;

import java.util.*;

public class Env {
	protected HashMap<String, NMethod> MethodTable;
	public NMethod currentMethod;
	public NStmt currentStmt;
	public boolean isInStmt = false, isInExp = false;
	public StringBuilder KangaCode;
	public int vReg = 0, isPassingPara = -1, moveToReg = -1;
	
	public Env(){
		MethodTable = new HashMap<String, NMethod>();
		KangaCode = new StringBuilder();
	}
	public void addMethod(){MethodTable.put(currentMethod.name, currentMethod);}
	public void setMethod(String name){currentMethod = MethodTable.get(name);}
	public void append(String str){KangaCode.append(str + "\n");}
	
	public void alloc(){
		for (NMethod NMethod : MethodTable.values()) {
			NMethod.allocReg();
			ArrayList<NStmt> sList = NMethod.stmtList;
			//System.out.println(NMethod.name + ": ");
			//System.out.println("tempSet: " + NMethod.tempSet);
			/*for(NStmt tmp : sList) {
				System.out.println(tmp.type + ":  ");

				System.out.println("usedTempList: " + tmp.usedTempList);
				System.out.println("outSet: " + tmp.outSet);
			
				System.out.println();
			}*/
				
		}
			
	}
}
