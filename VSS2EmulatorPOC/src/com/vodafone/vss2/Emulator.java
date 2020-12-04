package com.vodafone.vss2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * 
 * @author shaikka Emulator connects to VS22 client for successful connection
 *         and data processing between emulator and VS22 client.
 */

public class Emulator {

	Socket socket = null;
	ServerSocket serverSocket = null;
	int port = 4030;
	String returnMsg = null;
	DataOutputStream dos = null;
	String message = null;
	private DataInputStream dis;
	private ByteArrayOutputStream finalArrayOutputStream = new ByteArrayOutputStream();
	private DataOutputStream finaDataOutputStream = new DataOutputStream(finalArrayOutputStream);

	public void runEmulatorServer() {

		try {

			serverSocket = new ServerSocket(port);
			System.out.println("Emulator started and listening to port : " + port);
			while (true) {
				socket = serverSocket.accept();
				System.out.println("Client connected to Emulator... " + port);
				ThreadServer ts = new ThreadServer(socket, dis, finaDataOutputStream);
				ts.start();
				System.out.println("Final Output :\t" );
				printOutput(finalArrayOutputStream);
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				serverSocket.close();
				socket.close();

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public void setDataInputStream(DataInputStream dis) {
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

	public ByteArrayOutputStream getFinalArrayOutputStream() {
		return finalArrayOutputStream;
	}

	public void setFinalArrayOutputStream(ByteArrayOutputStream finalArrayOutputStream) {
		this.finalArrayOutputStream = finalArrayOutputStream;
	}

	public DataOutputStream getFinaDataOutputStream() {
		return finaDataOutputStream;
	}

	public void setFinaDataOutputStream(DataOutputStream finaDataOutputStream) {
		this.finaDataOutputStream = finaDataOutputStream;
	}

	public void printOutput(ByteArrayOutputStream finaDataOutputStream) {
		for(byte temp:finaDataOutputStream.toByteArray()) {
			System.out.print(temp+",");
		}
		System.out.println();
	}

	public static void main(String[] args) {
		Emulator emulatorServer = new Emulator();
		int size;
		
		Scanner sc=new Scanner(System.in);
		size=sc.nextInt();
		byte[] byteArray=new byte[size];
		for(int i=0;i<size;i++) {
			byteArray[i]=sc.nextByte();
		}
		ByteArrayInputStream bais=new ByteArrayInputStream(byteArray);
		DataInputStream dataIpStream=new DataInputStream(bais);
		emulatorServer.setDataInputStream(dataIpStream);
		emulatorServer.runEmulatorServer();
		System.out.println();
	}

}
