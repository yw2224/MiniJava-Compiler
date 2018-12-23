package minijava.symboltable;


//Identifier, can be a variable
public class MIdentifier extends MType
{
	protected MIdentifier parent = null; // marks the parent
	protected String name = "";
	protected String parentType = "";
	
	protected int temp = 0;
	protected int offset;

	public MIdentifier(){}
	public MIdentifier(String _name, int _row, int _col)
	{
		super(null, _row, _col);
		this.name = _name;
	}
	public MIdentifier(String _name, String _type, int _row, int _col)
	{
		super(_type, _row, _col);
		this.name = _name;
	}
/*public MIdentifier(String _name, String _type, int _row, int _col, String _parent)
	{
		super(_type, _row, _col);
		this.name = _name;
		this.parentType = _parent;
	}*/

	//query
	public String getName()
	{
		return this.name;
	}
	public MIdentifier getParent()
	{
		return this.parent;
	}

	//change
	public void setName(String _name)
	{
		this.name = _name;
	}
	public void setParent(MIdentifier _parent){ this.parent = _parent; }
	public String getParentType() { return this.parentType; }
	public void setParentType(String _type){ this.parentType = _type; }
	
	public int getTemp() { return this.temp; }
	public void setTemp(int _temp) { this.temp = _temp; }
	public int getOffset() { return this.offset; }
	public void setOffset(int _offset) { this.offset = _offset; }
	public boolean isTemp() { return temp > 0; }
}