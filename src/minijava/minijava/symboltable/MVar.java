package minijava.symboltable;

import java.util.*;


public class MVar extends MIdentifier implements Cloneable
{
	protected boolean declared = true;
	protected boolean hidden = false;
	protected boolean arrayBound = true;
	@Override
	public MVar clone() {
		 
		MVar o = null;	 
		try{
			o = (MVar)super.clone();
		 
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return o;
	}
	public MVar(String _name, String _type, MIdentifier _parent, int _row, int _col)
	{
		super(_name, _type, _row, _col);
		this.setParent(_parent);
	}
	
	public String toString(){ return this.getType() + " " + this.getName(); }
	public void setDeclared(boolean _declared) { this.declared = _declared; }
	public void setHidden(boolean _hidden) { this.hidden = _hidden; }
	public boolean getHidden() { return this.hidden; }
	public void setArrayBound(boolean _arrayBound) { this.arrayBound = _arrayBound; }
	public boolean getDeclared() { return this.declared; }
	//public void setUsed(boolean _used) { this.used = _used; }
	//public boolean getUsed() { return this.used; }
	public boolean getArrayBound() { return this.arrayBound; }
	
	//public int getOffset(String callType) { return this.offset; }
}
