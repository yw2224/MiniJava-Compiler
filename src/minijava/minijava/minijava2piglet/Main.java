package minijava.minijava2piglet;

import java.io.*;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class Main {

	public static void main(String[] args)  {
		
		try {
			//InputStream input = new FileInputStream(args[0]);
			InputStream input = new FileInputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/minijava/test20.java");
		
			String input1 = Minijava2Piglet.translate(input);
			//System.out.println(input1);
			
			//OutputStream output = new FileOutputStream(args[1]);
			OutputStream output = new FileOutputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/out.java");
			output.write(input1.getBytes());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
