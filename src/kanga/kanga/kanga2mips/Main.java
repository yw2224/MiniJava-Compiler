package kanga.kanga2mips;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


public class Main{
	public static void main(String args[]){
		
    try {
        	//InputStream in = new FileInputStream(args[0]);
    		InputStream in = new FileInputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/kanga/BinaryTree.kg.java");
    			
    		String rawcode = Kanga2Mips.translate(in);
    		System.out.println(rawcode);
    		
    		OutputStream output = new FileOutputStream("/Users/Yixue/Desktop/pku/3/编译实习/utility/测试用例/kanga/out.s");
    		//OutputStream output = new FileOutputStream(args[1]);
    		output.write(rawcode.getBytes());
    	}
    	catch (Exception e) {
    		e.printStackTrace();
    	}    		
    		
   }
}