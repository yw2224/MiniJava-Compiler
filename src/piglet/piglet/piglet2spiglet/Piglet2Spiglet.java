package piglet.piglet2spiglet;

import java.io.*;

import piglet.parser.*;
import piglet.syntaxtree.*;
import piglet.visitor.*;

public class Piglet2Spiglet{
	public static String translate(InputStream is) {

		try{
			Node root = new PigletParser(is).Goal();
			GetMaxTempVisitor v1 = new GetMaxTempVisitor();
			root.accept(v1);
			Piglet2SpigletVisitor v2 = new Piglet2SpigletVisitor();
			v2.setTempNum(v1.maxTempNum + 1);
			MSpiglet ans = root.accept(v2);
			return ans.toString();
	    }catch (ParseException e){
			e.printStackTrace();
		}
		return "?????";
	}
}
