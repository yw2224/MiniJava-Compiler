package piglet.piglet2spiglet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import piglet.syntaxtree.Node;

public class Main{
	public static void main(String args[]){
		//read the code and output the translation
		
		try {
			//InputStream in = new FileInputStream(args[0]);
			InputStream in = new FileInputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/piglet/TreeVisitor.pg.java");
			
			String rawcode = Piglet2Spiglet.translate(in);
			//System.out.println(rawcode);
		
			OutputStream output = new FileOutputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/piglet/outSpiglet.java");
			//OutputStream output = new FileOutputStream(args[1]);
			output.write(rawcode.getBytes());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}