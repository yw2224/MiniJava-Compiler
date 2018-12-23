package minijava.symboltable;


//Father of all symbol table's subclass
public class MType{
	// Symbol info:
	protected String type = "";
	protected int row = 0, col = 0;
	protected int intValue;
	protected boolean boolValue;
	protected int arrayLen;
	//protected boolean declared = true;
	protected String parentType;
	
	//Constructor
	public MType(){}
	public MType(String _type){ this.type = _type; }
	public MType(String _type, int _row, int _col)
	{
		this.type = _type;
		this.row = _row;
		this.col = _col;
	}
	
	//Interface to query
	public int getRow(){ return this.row; }
	public int getCol(){ return this.col; }
	public String getType(){ return this.type; }
	public int getIntValue() { return this.intValue; }
	public int getArrayLen() { return this.arrayLen; }
	public boolean getBoolValue() { return this.boolValue; }
	//public boolean getDeclared() { return this.declared; }
	public String getParentType() { return this.parentType; }
	
	//Interface to change
	public void setRow(int _row){ this.row = _row; }
	public void setCol(int _col){ this.col = _col; }
	public void setType(String _type){ this.type = _type; }
	public void setIntValue(int _value){ this.intValue = _value; }
	public void setArrayLen(int _value){ this.arrayLen = _value; }
	public void setBoolValue(boolean _value){ this.boolValue = _value; }
	//public void setDecalred(boolean _value){ this.declared = _value; }
	public void setParentType(String _type){ this.parentType = _type; }
}
