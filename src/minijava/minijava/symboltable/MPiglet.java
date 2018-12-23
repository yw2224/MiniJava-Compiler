package minijava.symboltable;

public class MPiglet {
	protected StringBuilder pigCode;
	protected boolean isHead = false;
	protected MVar var;
	protected MClass mclass;
	
	public MPiglet(){}
	public MPiglet(String str){ this.pigCode = new StringBuilder(str); }
	public MPiglet(String str, int nCurrentTab) {
		this.isHead = true;
		this.pigCode = new StringBuilder("");
		for (int i = 0; i < nCurrentTab; ++i) {
			this.pigCode.append("\t");
		}
		this.pigCode.append(str);
	}
	
	public void append(String str) {
		if(this.pigCode == null)
			this.pigCode = new StringBuilder("");
		this.pigCode.append(str);
	}
	public void append(MPiglet mp) {
		if(mp == null || mp.pigCode == null) return;
		if(this.pigCode == null)
			this.pigCode = new StringBuilder("");
		if(mp.isHead)
			this.pigCode.append("\n");
		else 
			this.pigCode.append(" ");
		this.pigCode.append(mp.pigCode.toString());
	}
	
	public StringBuilder getPigCode() { return this.pigCode; }
	public void setPigCode(StringBuilder _pigCode) { this.pigCode = _pigCode; }
	public MVar getVar() { return var; }
	public void setVar(MVar _var) { this.var = _var; }
	public MClass getMclass(){ return this.mclass; }
	public void setMclass(MClass _mclass) { this.mclass = _mclass; }
	
	@Override
	public String toString(){return pigCode.toString();}
	
}
