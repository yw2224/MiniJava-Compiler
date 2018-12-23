package spiglet.spiglet2kanga;

import java.util.*;


public class NMethod {
	protected String name;
	protected ArrayList<NStmt> stmtList;
	public int nParaSize, nSpilledPara, nSpilledSize, regNum = 18; 
	
	public NMethod(String _name){
		this.name = _name;
		stmtList = new ArrayList<NStmt>();
		regForTemp = new TreeMap<Integer, Integer>();
		linearAnalyse = new TreeMap<Integer, Integer>();
	}
	
	public void addStmt(NStmt _stmt) {stmtList.add(_stmt);}
	
	protected HashSet<Integer> tempSet;
	protected TreeMap<Integer, Integer> regForTemp;
	protected TreeMap<Integer, Integer> linearAnalyse;


	public boolean update(NStmt st1, NStmt st2){
		boolean _ret = false;
		if (st2 == null) return _ret;
		
		for (int temp : st2.usedTempList)
			if (!st1.outSet.contains(temp)){
				_ret = true;
				st1.outSet.add(temp);
			}
		for (int temp : st2.outSet)
			if (temp != st1.genTemp && !st1.outSet.contains(temp)){
				_ret = true;
				st1.outSet.add(temp);
			}
		
		return _ret;
	}
	
	private void getLiveSet() {
		boolean flag = true;
		HashSet<Integer> saveSet = new HashSet<Integer>();
		while(flag) {
			flag = false;
			for(NStmt stmt : stmtList) {
				saveSet.clear();
				for(int i : stmt.inSet) {
					saveSet.add(i);
				}
				
				//System.out.print(stmt.type + ": ");
				//System.out.println(saveSet);
				
				/*if(stmt.nextStmt1 != null)
					stmt.inSet.addAll(stmt.nextStmt1.inSet);
				if(stmt.nextStmt2 != null)
					stmt.inSet.addAll(stmt.nextStmt2.inSet);*/
				
				for(int i : stmt.outSet) {
					stmt.inSet.add(i);
				}
				stmt.inSet.remove(stmt.genTemp);
				stmt.inSet.addAll(stmt.usedTempList);
				
				if(stmt.nextStmt1 != null) {
				for(int i : stmt.nextStmt1.inSet) { // successor
					if(stmt.nextStmt2 != null && stmt.nextStmt2.inSet.contains(i))
						stmt.outSet.add(i);
					else if(stmt.nextStmt2 == null)
						stmt.outSet.add(i);
				}
				}
				//System.out.println(stmt.inSet);
				
				if(!stmt.inSet.equals(saveSet))
					flag = true; // someone has changed, need to circle
			}
		}	
		/*
		for(NStmt stmt : stmtList) {
			System.out.print(stmt.type + ": ");
			System.out.println(stmt.inSet);
		}*/
		
	}
	
	private void buildGraph(){
		HashMap<String, NStmt> labelStmt = new HashMap<String, NStmt>();
		for (NStmt stmt : stmtList) {
			String label = stmt.entryLabel;
			if (label != null) labelStmt.put(label, stmt);
		}
		
		for (int i = 0; i < stmtList.size(); ++i){
			NStmt stmt = stmtList.get(i);
			if (!stmt.isUnconditionJump && i+1 < stmtList.size())
				stmt.nextStmt1 = stmtList.get(i+1);
			
			if (stmt.jumpLabel != null)
				stmt.nextStmt2 = labelStmt.get(stmt.jumpLabel);
		}
			
		while (true){
			boolean flag = false;
			for (NStmt stmt : stmtList){
				flag |= update(stmt, stmt.nextStmt1);
				flag |= update(stmt, stmt.nextStmt2);
			}
			if (!flag) break; 
		}
		/*
		tempSet = new HashSet<Integer>();
		for (NStmt stmt : stmtList)
			tempSet.addAll(stmt.outSet);
			*/
	}
	
	public void allocReg(){
		buildGraph();	
		getLiveSet();
		//linearScan();	
		boolean toStack = true;
		boolean inReg = false;
		int nToStack = regNum;
		
		int j = 0;
		for(int i = 0; i < regNum; i ++) {
			if(i < Math.max(4, nParaSize)) {// restore reg for param
				linearAnalyse.put(i, j); 
				regForTemp.put(j, i);
				j ++;
			}		
			else 
				linearAnalyse.put(i, -1);
		}
		
		for(NStmt stmt : stmtList) {
			stmt.usedTempList.add(stmt.genTemp);

			for(int tmp : stmt.outSet) { // alloc for tmp	
				
				toStack = true;
				inReg = false;
				
				if(regForTemp.containsKey(tmp)) { // have allocated
					continue;
				}
				for(int i = 0; i < regNum; i ++) {
					
					if(linearAnalyse.get(i) == -1 || !stmt.inSet.contains(linearAnalyse.get(i)) ) { // no longer alive
						regForTemp.put(tmp, i);
						linearAnalyse.put(i, tmp);
						toStack = false; // found a reg, no need to store on stack
						break;
					}
				}
				if(toStack) {
					regForTemp.put(tmp, nToStack ++);
				}
			}
		}
		nSpilledSize = nToStack;
		nSpilledPara = Math.max(0, nParaSize-4);
		//System.out.println(regForTemp);
		
		/*
		tempSet = new HashSet<Integer>();
		for(NStmt stmt: stmtList) {
			if(!stmt.usedTempList.isEmpty())
				tempSet.addAll(stmt.usedTempList);
		}
		nSpilledPara = Math.max(0, nParaSize-4);
		regForTemp = new TreeMap<Integer, Integer>();
		nSpilledSize = 0;
		for (int temp1 : tempSet)
			regForTemp.put(temp1, nSpilledSize++);
			*/
	}
	
	public int getReg(int t){
		//if (tempSet.contains(t) == false) return -1;
		if(!regForTemp.containsKey(t)) {
			return -1;
		} 
		return regForTemp.get(t);
	}
}
