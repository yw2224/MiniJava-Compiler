package kanga.kanga2mips;

import java.io.*;

import kanga.parser.*;
import kanga.syntaxtree.*;
import kanga.visitor.*;

public class Kanga2Mips {
	public static String translate(InputStream is) {
		
		try {
			Node root = new KangaParser(is).Goal();
			Kanga2MipsVisitor v = new Kanga2MipsVisitor(); 
			Env env = new Env();
			root.accept(v, env);
			return env.mipsCode.toString();
	    }catch (ParseException e){
			e.printStackTrace();
		}
		return "fuck you";
	}
}
