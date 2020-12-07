package com.samples.functions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Vector;

import com.ghc.ghTester.expressions.Function;
import com.samples.functions.ThreadServer;

public class VSS2Plugin {

	String returnMsg = null;
	DataOutputStream dos = null;
	String message = null;
	
	private InputStream bArray;
	private DataInputStream dis=new DataInputStream(bArray);
	
	private ByteArrayOutputStream finalArrayOutputStream = new ByteArrayOutputStream();
	private DataOutputStream finaDataOutputStream = new DataOutputStream(finalArrayOutputStream);

	public VSS2Plugin() {
	}

//	public Function create(int size, Vector<DataInputStream> params) {
//		Function outputFormat = null;
//		for (int i = 0; i < size; i++) {
//			
//		}
//		return "aa";
//	}

	public Function runVss2Plugin() {

		try {
			while (true) {
				
				ThreadServer ts = new ThreadServer(dis, finaDataOutputStream);
				
				ts.start();
				System.out.println("Final Output :\t");
				printOutput(finalArrayOutputStream);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public Function setDataInputStream(DataInputStream dis) {
		this.dis = dis;
		System.out.println("Input Steam (Input Stream -> byte Array)");
		try {
			for (byte temp : dis.readAllBytes()) {
				System.out.print(temp + ",");
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public Function getFinalArrayOutputStream() {
		return finalArrayOutputStream;
	}

	public Function setFinalArrayOutputStream(ByteArrayOutputStream finalArrayOutputStream) {
		this.finalArrayOutputStream = finalArrayOutputStream;
	}

	public Function getFinaDataOutputStream() {
		return finaDataOutputStream;
	}

	public Function setFinaDataOutputStream(DataOutputStream finaDataOutputStream) {
		this.finaDataOutputStream = finaDataOutputStream;
	}

	public Function printOutput(ByteArrayOutputStream finaDataOutputStream) {
		for (byte temp : finaDataOutputStream.toByteArray()) {
			System.out.print(temp + ",");
		}
		System.out.println();
	}

	public Function getbArray() {
		return bArray;
	}

	public Function setbArray(InputStream bArray) {
		this.bArray = bArray;
	}

	public Function getDis() {
		return dis;
	}

	public Function setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public Object evaluate(Object data) {

		VSS2Plugin vss2Plugin = new VSS2Plugin();
		int size;

		// ?Debug
//		Scanner sc = new Scanner(System.in);
//		size = sc.nextInt();
//		byte[] byteArray = new byte[size];
//		for (int i = 0; i < size; i++) {
//			byteArray[i] = sc.nextByte();
//		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		DataInputStream dataIpStream = new DataInputStream(bais);
		vss2Plugin.setDataInputStream(dataIpStream);
		// ?Debug

		vss2Plugin.runVss2Plugin();
		System.out.println();
		return "\"" + "VSS2response" + "\"";
	}

}
