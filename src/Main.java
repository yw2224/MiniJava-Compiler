import minijava.minijava2piglet.*;
import piglet.piglet2spiglet.*;
import spiglet.spiglet2kanga.*;
import kanga.kanga2mips.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class Main{
	public static void main(String args[]) {
		try {
			InputStream input = new FileInputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/minijava/binarytree.java");
		    
			String str = Minijava2Piglet.translate(input);
			InputStream in = new ByteArrayInputStream(str.getBytes());   
		
			str = Piglet2Spiglet.translate(in);
			in = new ByteArrayInputStream(str.getBytes());
		
			str = Spiglet2Kanga.translate(in);
			in = new ByteArrayInputStream(str.getBytes());
		
			str = Kanga2Mips.translate(in);
		
			System.out.println(str);
			OutputStream output = new FileOutputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/out.s");
			output.write(str.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
