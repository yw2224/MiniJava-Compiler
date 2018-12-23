package spiglet.spiglet2kanga;

import java.io.*;

import spiglet.parser.*;
import spiglet.syntaxtree.*;
import spiglet.visitor.*;

public class Spiglet2Kanga {
	public static String translate(InputStream is) {
		try{
			Node root = new SpigletParser(is).Goal();
			LivenessVisitor v1 = new LivenessVisitor();
			Spiglet2KangaVisitor v2 = new Spiglet2KangaVisitor();
			Env env = new Env();
			root.accept(v1, env);
			env.alloc();
			root.accept(v2, env);
			
			return env.KangaCode.toString();
		}catch (ParseException e){
			e.printStackTrace();
		}
		return "???????";
	}
}
