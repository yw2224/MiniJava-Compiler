package spiglet.spiglet2kanga;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class Main{
	public static void main(String[] args){
    	
    	try {
    		InputStream in = new FileInputStream(args[0]);
			//InputStream in = new FileInputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/spiglet/MyTreeVisitor.spg.java");
			
			String rawcode = Spiglet2Kanga.translate(in);
			//System.out.println(rawcode);
		
			//OutputStream output = new FileOutputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/spiglet/out.java");
			OutputStream output = new FileOutputStream(args[1]);
			output.write(rawcode.getBytes());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
    	
    	
    }
}