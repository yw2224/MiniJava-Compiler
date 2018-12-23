package minijava.symboltable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;


//Class Table that contains all symbol in a class
public class MClass extends MIdentifier implements VarContainer, Cloneable{
	protected boolean declared = false; // whether it is declared -- for inside-symboltable check

	// For inheritance check
	protected String baseClassName;
	protected MClass baseClass;

	// All Variables and Methods are sorted within a hashtable
	protected HashMap<String, MMethod> methodList = new HashMap<String, MMethod>();
	protected HashMap<String, MVar> varList = new HashMap<String, MVar>();
	protected HashMap<String, Integer> parentsList = new HashMap<String, Integer>();

	public MClass(String _name, int _row, int _col){
		super(_name, "class", _row, _col); // type: "class"
	}/*
	public void updateParentsList() {
		while(true) {
			if (this.baseClass != null) {
				
			}
		}
	}*/
	// Clone
	public Object clone() { 
		MClass o = null;	 
		try{
			o = (MClass)super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return o;
	}
		
	@Override // implements interface
	public String insertVar(MVar newVar){
		if (this.varList.containsKey(newVar.getName())){
			// in table; report error
			return "Redundant Variable Declaration: \'" + newVar.getName() + "\'";
		}
		else{
			this.varList.put(newVar.getName(), newVar);
			return null;
		}
	}

	public String insertMethod(MMethod newMethod){
		if (this.methodList.containsKey(newMethod.getName())){
			// in table; report error
			return "Redundant Variable Declaration: \'" + newMethod.getName() + "\'";
		}
		else{
			this.methodList.put(newMethod.getName(), newMethod);
			return null;
		}
	}
	void updateVarAndMethodList(){
		if (this.baseClass == null){
			return;
		}
		this.baseClass.updateVarAndMethodList(); // recursive update
		//update the varlist from baseclass
		///*
		for (String varName : this.baseClass.varList.keySet()){
			if (this.varList.containsKey(varName))
				continue;
			MVar newVar = this.baseClass.varList.get(varName); // !!!
			this.varList.put(varName, newVar);
		}
		//*/
		//update the methodlist from baseclass
		for (String methodName : this.baseClass.methodList.keySet()){
			if (this.methodList.containsKey(methodName))
				continue;
			MMethod newMethod = this.baseClass.methodList.get(methodName);
			this.methodList.put(methodName, newMethod);
		}
	}
	//QUERY
	public int getVarSize() {
		if (this.baseClass != null)
			return this.varList.size() * 4 + this.baseClass.getVarSize();
		else
			return this.varList.size() * 4;
	}
	public int getOwnVarSize() {
		return this.varList.size() * 4;
	}
	public int getMethodSize() {
		return this.methodList.size() * 4;
	}
	public MMethod getMethod(String methodName) { 
		
		MMethod res =  this.methodList.get(methodName);
		if (res != null) // !!!
			res.setParentType(this.name);
		if (res == null && this.baseClass != null)
			res = this.baseClass.getMethod(methodName);
		return res;
	}
	public boolean isDeclared(){return this.declared;}
	public String getBaseClassName(){return this.baseClassName;}
	public Collection<MMethod> getMethodSet(){return this.methodList.values();}
	public Collection<MVar> getVarSet(){return this.varList.values();}
	public MClass getBaseClass(){return this.baseClass;}
	@Override
	public MVar getVar(String name){
		MVar res = this.varList.get(name);
		if (res != null) // !!!
			res.setParentType(this.name);
		if (res == null && this.baseClass != null)
			res = this.baseClass.getVar(name);
		return res;
	}
	// CHANGE
	public void setDeclared(boolean _declared){this.declared = _declared;}
	public void setBaseClassName(String _baseClassName) {this.baseClassName = _baseClassName;}
	public void setBaseClass(MClass _baseClass) {this.baseClass = _baseClass;}
	
	public int pigletAddName(String name, MMethod mmethod, HashSet<String> pigletNameSet) {
		for (int i = 0;;++i){
			if (i >= 2) name = name + "_" + i; // repeat name
			if (!pigletNameSet.contains(name)){
				pigletNameSet.add(name);
				mmethod.setPigletName(name);
				break;
			}
		}
		return 0;
	}
	
	// MINIJAVA 2 PIGLET
	public int alloc(int currentReg, HashSet<String> pigletNameSet) {
		int currentOffset = 4; // save 4 for method table pointer
		//every var + 4
		if (this.baseClass != null) {
			// first inherit
			for (MVar mvar : this.baseClass.varList.values()) {
				mvar.setOffset(currentOffset);
				currentOffset += 4;
				//System.out.println("VARinherit " + mvar.getName() + " offset = " + mvar.getOffset());
			}
			// second append
			for (MVar mvar : varList.values()) {
				if (!this.baseClass.varList.containsValue(mvar)) {
					mvar.setOffset(currentOffset);
					currentOffset += 4;
					//System.out.println("VARappend " + mvar.getName() + " offset = " + mvar.getOffset());
				}
			}
		} else {
			for (MVar mvar : varList.values()) {
				mvar.setOffset(currentOffset);
				currentOffset += 4;
				//System.out.println("VARpure " + mvar.getName() + " offset = " + mvar.getOffset());
			}
		}
		
		currentOffset = 0;
		if (this.baseClass != null) {
			// first inherit
			for (MMethod mmethod : this.baseClass.methodList.values()){
				//mmethod.setOffset(currentOffset);
				//pigletAddName(this.getName() + "_" + mmethod.getName(), mmethod, pigletNameSet);
				currentOffset += 4;
				//currentReg = mmethod.alloc(currentReg);
				//System.out.println("inerhit " + mmethod.getName() + " offset = " + mmethod.getOffset());
			}
			// second override and append
			Map<String, MMethod> ML = methodList;
			for (Entry<String, MMethod> entry : ML.entrySet()) {
				String mname = entry.getKey();
				MMethod mmethod = entry.getValue();
				// override
				if (this.baseClass.methodList.containsKey(mname) && !this.baseClass.methodList.containsValue(mmethod)) {
					int tmpOffset = this.baseClass.methodList.get(mname).getOffset();
					mmethod.setOffset(tmpOffset);
					pigletAddName(this.getName() + "_" + mmethod.getName(), mmethod, pigletNameSet);
					currentReg = mmethod.alloc(currentReg);
					//System.out.println("override " + mmethod.getName() + " offset = " + mmethod.getOffset());
				}
				// append
				if (!this.baseClass.methodList.containsKey(mname) && !this.baseClass.methodList.containsValue(mmethod)) {
					mmethod.setOffset(currentOffset);
					pigletAddName(this.getName() + "_" + mmethod.getName(), mmethod, pigletNameSet);
					currentOffset += 4;
					currentReg = mmethod.alloc(currentReg);
					//System.out.println("append " + mmethod.getName() + " offset = " + mmethod.getOffset());
				}
			}
		} else {
			for (MMethod mmethod : methodList.values()){
				mmethod.setOffset(currentOffset);
				pigletAddName(this.getName() + "_" + mmethod.getName(), mmethod, pigletNameSet);
				currentOffset += 4;
				currentReg = mmethod.alloc(currentReg);
			}
		}
		
		
		return currentReg;
	}
}

