package com.samples.functions;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.ParsePosition;
import java.util.Base64;
import java.util.Base64.Decoder;

import sun.nio.cs.SingleByte;
import uk.co.vodafone.itcp.vss2.protocol.ConnectionRequestMessage;
import uk.co.vodafone.itcp.vss2.protocol.ConnectionResponseMessage;
import uk.co.vodafone.itcp.vss2.protocol.DisconnectRequestMessage;
import uk.co.vodafone.itcp.vss2.protocol.DisconnectResponseMessage;
import uk.co.vodafone.itcp.vss2.protocol.ExecuteCommandRequestMessage;
import uk.co.vodafone.itcp.vss2.protocol.ExecuteCommandResponseMessage;
import uk.co.vodafone.itcp.vss2.protocol.LcmParameters;
import uk.co.vodafone.itcp.vss2.protocol.Message;
import uk.co.vodafone.itcp.vss2.protocol.ProtocolException;
import uk.co.vodafone.itcp.vss2.protocol.RcmParameters;
import uk.co.vodafone.itcp.vss2.protocol.ResponseMessage;
import uk.co.vodafone.itcp.vss2.protocol.Vss2Parameters;

import java.util.*;
public class ThreadServer extends Thread {

	private Socket socket;
	private LcmParameters lcmParameters = null;
	private RcmParameters rcmParameters = null;
	private static short MAX_PACKET_LENGTH = 5120;
	boolean emulator;
	
	private DataInputStream dis;
//	ByteArrayOutputStream finalArrayOutputStream = new ByteArrayOutputStream();
//	DataOutputStream finaDataOutputStream = new DataOutputStream(finalArrayOutputStream);
	DataOutputStream finaDataOutputStream;
	

	public ThreadServer(DataInputStream dis, DataOutputStream finaDataOutputStream) {
//		this.socket = socket;
		this.dis=dis;
		this.finaDataOutputStream=finaDataOutputStream;
		try {
			rcmParameters = receiveResponse();
			// Byte Array
			writeConnectionResMsg(rcmParameters);
			
			//?Debug RCM Parameters
			System.out.println("\n----------------------------------------");
			System.out.println("             RCM Parameter");
			System.out.println("----------------------------------------");
			System.out.println("Compression Type :\t"+rcmParameters.getCompressionType());
			System.out.println("Encryption Type :\t"+rcmParameters.getEncryptionType());
			System.out.println("Node Name :\t"+rcmParameters.getNodeName());
			System.out.println("Protocol Version :\t"+rcmParameters.getProtocolVersion());
			System.out.println("Session ID :\t"+rcmParameters.getSessionId());
			System.out.println("----------------------------------------\n");
			//?Debug RCM Parameters
			
			emulator = true;
		} catch (Exception e) {
			System.out.println("rcmExceptionParams :" + e.getMessage());
		}
	}
	
//	public ThreadServer(Socket socket) {
//		this.socket = socket;
//		try {
//			rcmParameters = receiveResponse();
//			// Byte Array
//			writeConnectionResMsg(rcmParameters);
//			
//			//?Debug RCM Parameters
//			System.out.println("\n----------------------------------------");
//			System.out.println("             RCM Parameter");
//			System.out.println("----------------------------------------");
//			System.out.println("Compression Type :\t"+rcmParameters.getCompressionType());
//			System.out.println("Encryption Type :\t"+rcmParameters.getEncryptionType());
//			System.out.println("Node Name :\t"+rcmParameters.getNodeName());
//			System.out.println("Protocol Version :\t"+rcmParameters.getProtocolVersion());
//			System.out.println("Session ID :\t"+rcmParameters.getSessionId());
//			System.out.println("----------------------------------------\n");
//			//?Debug RCM Parameters
//			
//			emulator = true;
//		} catch (Exception e) {
//			System.out.println("rcmExceptionParams :" + e.getMessage());
//		}
//	}

	@Override
	public void run() {
		try {
			while (emulator) {
				// runtime API will get appropriate class to invoke clerk
				// ExecuteCommandRequestMessage commandReqMsg = receiveCommandResponse();
				Object object = receiveCommandResponse();
				if (object instanceof ExecuteCommandRequestMessage) {
					ExecuteCommandRequestMessage commandReqMsg = (ExecuteCommandRequestMessage) object;
					System.out.println("Command :" + commandReqMsg.getCommand());

					ExecuteCommandResponseMessage commandResponseMessage = fetchExecuteCommandResponse();
					System.out.println("Response : " + commandResponseMessage.getResponse());
				}
				
				if (object instanceof DisconnectRequestMessage) {
					DisconnectResponseMessage drs = disconnectResponse(rcmParameters);
					System.out.println("Disconnecting the VS22 and Emulator connectivity");
					emulator = false;
				}

//	          // once connected, send a command to invoke and fetch the result
//	            ExecuteCommandRequestMessage commandRequestMessage = receiveCommandResponse();
//	            System.out.println("VSS2 Command :" + commandRequestMessage.getCommand());
//	 
//	            ExecuteCommandResponseMessage responseMessage = fetchExecuteCommandResponse(commandReqMsg.getCommand());
//	            System.out.println("VSS2 Response : " +  responseMessage.getResponse());
			}
		} catch (Exception e) {
			System.out.println("Exception:" + e.getMessage());
		}

		// to disconnect the connection between VS22 and Emulator.
//				receiveDisconnectCommandResponse();
//				DisconnectResponseMessage disconnectResponse = disconnectResponse(rcmParameters);
//				if (disconnectResponse.getResponseCode() == 0) {
//					System.out
//							.println("succesfully disconnected VS22 and Emulator");
//				}

	}

	/**
	 * Once after the data is received to emulator from VSS2 clients, emulator
	 * method will return an proper response. The response will be send back to VS22
	 * client for successful connection.
	 * 
	 * @return
	 * @throws Exception
	 */
	private RcmParameters receiveResponse() throws Exception {
		{
			try {
				// ?What is received message here ?
				byte[] arrayOfByte = receiveMessage();
				//?Debug Changed Data Input Stream
				DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
//				DataInputStream dataInputStream = this.dis;
				//?Debug
				//?Debug arrayOfByte - Input?
//				System.out.println("input Byte Array :\t");
//				for (byte temp : arrayOfByte) {
//					System.out.print(temp + ",");
//				}

//				InputStreamReader isr=new InputStreamReader(dataInputStream);
//				isr.read();
//				BufferedReader br=new BufferedReader(isr);
//				String op=br.readLine();
				Message localMessage = Message.CreateMessage(dataInputStream);

//				System.out.println("\nInput Stream Reader :\t"+op+"\nLocal Message :\t"+localMessage.toString());
				// System.out.println(localMessage.getClass());
//				br.close();
				dataInputStream.close();
				ConnectionRequestMessage connectionRequestMessage = (ConnectionRequestMessage) localMessage;
				lcmParameters = connectionRequestMessage.getLcmParameters();

				//?Debug LCM Parameter
				System.out.println("\n----------------------------------------");
				System.out.println("             LCM Parameter");
				System.out.println("----------------------------------------");
				System.out.println("Host Name :\t"+lcmParameters.getHostName());
				System.out.println("Login Name :\t"+lcmParameters.getLoginName());
				System.out.println("Local Address :\t"+lcmParameters.getLocalAddress());
				System.out.println("LCM Version :\t"+lcmParameters.getLcmVersion());
				System.out.println("Clerk Name :\t"+lcmParameters.getClerkName());
				System.out.println("Clerk Password :\t"+lcmParameters.getClerkPassword());
				System.out.println("Compression Type :\t"+lcmParameters.getCompressionType());
				System.out.println("Encryption Type :\t"+lcmParameters.getEncryptionType());
				System.out.println("OpSys :\t"+lcmParameters.getOpSys());
				System.out.println("Protocol Version :\t"+lcmParameters.getProtocolVersion());
				System.out.println("Server List :\t"+lcmParameters.getServerList());
				System.out.println("Server Port :\t"+lcmParameters.getServerPort());
				System.out.println("Usage Description :\t"+lcmParameters.getUsageDescription());
				System.out.println("View Switch :\t"+lcmParameters.getViewSwitch());
				System.out.println("----------------------------------------\n");
				//?Debug LCM Parameter
				
				// System.out.println("" lcmParameters.getServerList() + " "
				// + lcmParameters.getServerPort() + " "
				// + lcmParameters.getViewSwitch());
//				System.out.println("Host Name: " + lcmParameters.getHostName());
//				System.out.println("Port: " + lcmParameters.getServerPort());
//				System.out.println("LCM Version: " + lcmParameters.getLcmVersion());
				rcmParameters = new RcmParameters(lcmParameters, "12345678901234567890");
				return rcmParameters;

			} catch (ClassCastException localClassCastException) {
				throw new Exception("A VSS2 protocol error has occurred. Unexpected message type sent by RCM.");
			} catch (ProtocolException localProtocolException) {
				throw new Exception("A VSS2 protocol error has occurred. " + localProtocolException.getMessage());
			} catch (IOException localIOException) {
				throw new Exception("A VSS2 protocol error has occurred. " + localIOException.toString());
			}
		}
	}

	/**
	 * ReceiveMessage method will process the client sent data in to byte arrays ,
	 * packets and gets converted short and int length. Once after the proper
	 * transformation , length shouldn't exceeded more than the restricted value.
	 * 
	 * @return
	 * @throws Exception
	 */
	private byte[] receiveMessage() throws Exception {

		// ?Increasing this will it help ?
		byte[] arrayOfByte1 = new byte[1024];

		short s = 0;
		short[] arrayOfShort1 = new short[1];
		short[] arrayOfShort2 = new short[1];
		int i = 0;
		boolean[] arrayOfBoolean = new boolean[1];
		do {
			receivePacket(arrayOfByte1, s, arrayOfShort1, arrayOfShort2, arrayOfBoolean);
			s = (short) (s + arrayOfShort1[0]);
			if (arrayOfShort2[0] != i + 1) {
				throw new Exception("A VSS2 protocol error has occurred. Packet received out of sequence.");
			}
			i = arrayOfShort2[0];
		} while (arrayOfBoolean[0] != false);
		byte[] arrayOfByte2 = new byte[s];
		System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, s);
		
		//?Debug arrayOfByte2
//		System.out.println("arrayOfByte2");
//		for(byte temp : arrayOfByte2) {
//			System.out.print(temp+",");
//		}
//		String op=new String(arrayOfByte2,StandardCharsets.US_ASCII);
//		System.out.println("\nString Output :\t"+op);
		//?Debug
		
		return arrayOfByte2;
	}

	/**
	 * This method receives the transformed data through byte arrays and check the
	 * packets are restricted to limited value or not. if the packets are with in
	 * the range or limited restriction, it copies all the coming packets into a
	 * proper new array to transform to proper values.
	 * 
	 * @param paramArrayOfByte
	 * @param paramShort
	 * @param paramArrayOfShort1
	 * @param paramArrayOfShort2
	 * @param paramArrayOfBoolean
	 * @throws Exception
	 */
	private void receivePacket(byte[] paramArrayOfByte, short paramShort, short[] paramArrayOfShort1,
			short[] paramArrayOfShort2, boolean[] paramArrayOfBoolean) throws Exception {

		byte[] arrayOfByte11 = new byte[Packets.LENGTH];
		int i = 0;
		while (i < Packets.LENGTH) {
			i += receiveData(arrayOfByte11, i, Packets.LENGTH - i);
		}
		Packets localPacketHeader = new Packets(arrayOfByte11);
		if (localPacketHeader.getLength() > MAX_PACKET_LENGTH) {
			throw new Exception(
					"A VSS2 protocol error has occurred. Packet received is longer than the maximum size packet length.");
		}
		paramArrayOfShort1[0] = localPacketHeader.getLength();
		paramArrayOfShort2[0] = localPacketHeader.getSequenceNumber();
		paramArrayOfBoolean[0] = localPacketHeader.getMorePackets();

		byte[] arrayOfByte2 = new byte[paramArrayOfShort1[0]];
		i = 0;
		while (i < paramArrayOfShort1[0]) {
			i += receiveData(arrayOfByte2, i, paramArrayOfShort1[0] - i);
		}
		System.arraycopy(arrayOfByte2, 0, paramArrayOfByte, paramShort, paramArrayOfShort1[0]);

	}

	/**
	 * This method receives the data form input stream from socket and transforms
	 * into int value to validate the data and limit.
	 * 
	 * @param paramArrayOfByte
	 * @param paramInt1
	 * @param paramInt2
	 * @return
	 * @throws Exception
	 */
	//?Debug Input Data
	private int receiveData(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws Exception {

		try {
			if (paramArrayOfByte.length < paramInt1 + paramInt2) {
				throw new Exception(
						"An unknown error has occurred. Unable to receive the requested quantity of data because the buffer is not big enough.");
			}
			//?Debug InputData (Socket is removed)
//			DataInputStream localDataInputStream = new DataInputStream(socket.getInputStream());
			DataInputStream localDataInputStream = this.dis;
			int i = localDataInputStream.read(paramArrayOfByte, paramInt1, paramInt2);
			if (i < 1) {
				throw new Exception(
						"A network error has occurred. Unable to receive data because the end of the input stream has been reached.");
			}
			return i;
		} catch (InterruptedIOException localInterruptedIOException) {
			throw new Exception("Network operation failed due to timeout. Unable to receive data.");
		} catch (IOException localIOException) {
			throw new Exception("A network error has occurred. Unable to receive data. " + localIOException.toString());
		} catch (Exception localException) {
			throw new Exception("An unknown error has occurred. Unable to receive data. " + localException.toString());
		}
	}

	/**
	 * Once after the successful connection with client, emulator returns an
	 * response with received RCM parameters appended with session Id.
	 * 
	 * @param rcmParameters
	 * @throws ProtocolException
	 * @throws IOException
	 */
	private void writeConnectionResMsg(RcmParameters rcmParameters) throws ProtocolException, IOException {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(arrayOutputStream);
		ConnectionResponseMessage connectionResponseMessage = new ConnectionResponseMessage(rcmParameters, (short) 0);
		//?Debug (My assumption Output)
		System.out.println("\n----------------------------------------");
		System.out.println("         writeConnectionResMsg");
		System.out.println("----------------------------------------");
		System.out.println("Node Name :\t"+connectionResponseMessage.getNodeName());
		System.out.println("ResponseCode :\t"+connectionResponseMessage.getResponseCode());
		System.out.println("Session ID :\t"+connectionResponseMessage.getSessionId());
		System.out.println("----------------------------------------\n");
		//?Debug
		
		connectionResponseMessage.streamOut(dos);
		dos.close();
		sendMessage(arrayOutputStream.toByteArray());
	}

	/**
	 * Once the clerk details are received by emulator from VS22, it returns an
	 * success response with rcm parameters. This methods not only returns the
	 * clerks commands response.it will return other commands response, once clerk
	 * commands response is successful.
	 * 
	 * @param paramString
	 * @return
	 * @throws ProtocolException
	 * @throws IOException
	 */
	private ExecuteCommandResponseMessage fetchExecuteCommandResponse() throws ProtocolException, IOException {
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(arrayOutputStream);
		ExecuteCommandResponseMessage localExecuteCommandResponseMessage = new ExecuteCommandResponseMessage(
				rcmParameters, (short) 0, "RC4001", 0);
		localExecuteCommandResponseMessage.streamOut(dos);
		
		//?Debug (My assumption Output)
//		localExecuteCommandResponseMessage.streamOut(finaDataOutputStream);
		System.out.println("\n----------------------------------------");
		System.out.println("     ExecuteCommandResponseMessage");
		System.out.println("----------------------------------------");
		System.out.println("More Data :\t"+localExecuteCommandResponseMessage.getMoreData());
		System.out.println("Response :\t"+localExecuteCommandResponseMessage.getResponse());
		System.out.println("Response Code :\t"+localExecuteCommandResponseMessage.getResponseCode());
		System.out.println("----------------------------------------\n");
		
//		finaDataOutputStream.close();
		//?Debug
		
		//?Debug DOS OP
//		System.out.println("DOS OP :\t" + dos);
		dos.close();
		
		sendMessage(arrayOutputStream.toByteArray());
		return localExecuteCommandResponseMessage;

	}

	/**
	 * Send Message will return an emulator response to VS22 client.
	 * 
	 * @param paramArrayOfByte
	 */
	private void sendMessage(byte[] paramArrayOfByte) {

		{
			// ?Debug BStream
//			System.out.println("Byte Stream :\t");
//			for (byte temp : paramArrayOfByte) {
//				System.out.print(temp + ",");
//			}

//			System.out.println();
			int i = 0;
			short s = 1;
			int j = 0;
			while (paramArrayOfByte.length - i > MAX_PACKET_LENGTH) {
				sendPacket(paramArrayOfByte, i, MAX_PACKET_LENGTH, s, true);
				j = (short) (i + MAX_PACKET_LENGTH);
				s = (short) (s + 1);
			}
			
			sendPacket(paramArrayOfByte, j, (short) (paramArrayOfByte.length - j), s, false);
		}
	}

	/**
	 * Transformed Packets will be sent to VS22 client from emulator.
	 * 
	 * @param paramArrayOfByte
	 * @param paramShort1
	 * @param paramShort2
	 * @param paramShort3
	 * @param paramBoolean
	 */
	private void sendPacket(byte[] paramArrayOfByte, int paramShort1, short paramShort2, short paramShort3,
			boolean paramBoolean) {
		// ?included try and catch block
		try {
			byte[] arrayOfByte = new byte[paramShort2 + Packets.LENGTH];

			Packets localPacketHeader = new Packets(paramShort2, paramShort3, paramBoolean);
			localPacketHeader.writeToPacket(arrayOfByte);
			System.arraycopy(paramArrayOfByte, paramShort1, arrayOfByte, Packets.LENGTH, paramShort2);
			// ?Debug Printing all the parameters that are been sent
//			System.out.println("----------------------------------------------------");
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			bos.write(paramArrayOfByte);
//			System.out
//					.println("ParamArrayofByte (ISO) :\t" + new String(paramArrayOfByte, StandardCharsets.ISO_8859_1));
//			System.out
//					.println("ParamArrayofByte (Ascii) :\t" + new String(paramArrayOfByte, StandardCharsets.US_ASCII));
//			System.out.println("ParamArrayofByte (UTF16) :\t" + new String(paramArrayOfByte, StandardCharsets.UTF_16));
//			System.out.println(
//					"ParamArrayofByte (UTF 16BE) :\t" + new String(paramArrayOfByte, StandardCharsets.UTF_16BE));
//			System.out.println(
//					"ParamArrayofByte (UTF16LE) :\t" + new String(paramArrayOfByte, StandardCharsets.UTF_16LE));
//			System.out.println("ParamArrayofByte (UTF8) :\t" + new String(paramArrayOfByte, StandardCharsets.UTF_8));
//			System.out.println("paramShort1 :\t" + paramShort1);
//			System.out.println("arrayOfByte :\t" + arrayOfByte);
//			System.out.println("Packets.LENGTH :\t" + Packets.LENGTH);
//			System.out.println("paramShort2 :\t" + paramShort2);
			sendData(arrayOfByte);
		} catch (Exception exc) {
			exc.printStackTrace();
		}

	}

	/**
	 * it will write the transformed packets to streams, VS22 client will read using
	 * streams.
	 * 
	 * @param arrayOfByte
	 */
	private void sendData(byte[] arrayOfByte) {
		try {
			//?Debug OutputData Writing the data to output stream (Removed socked related content and directly writing the array of bytes to dataoutput stream object
//			DataOutputStream localDataOutputStream = new DataOutputStream(this.socket.getOutputStream());
			finaDataOutputStream.write(arrayOfByte);

			//?OriginalCode
//			localDataOutputStream.write(arrayOfByte);
//			localDataOutputStream.flush();
			//?OriginalCode
			
			//?Debug Writing the data to output stream 
			//so that it can be retrieved from the main method
//			finaDataOutputStream.write(arrayOfByte);
			
			//?Debug arrayOfByte - Output?
//			System.out.println("arrayOfByte");
//			for (byte temp : arrayOfByte) {
//				System.out.print(temp + ",");
//			}
			//?Debug

		} catch (InterruptedIOException localInterruptedIOException) {
			try {
				throw new Exception("Network operation failed due to timeout. Unable to send data.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();

			}
		} catch (IOException localIOException) {
			try {
				throw new Exception(
						"A network error has occurred. Unable to send data. " + localIOException.toString());
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
		}

	}

	/**
	 * Disconnect response sends an message to client to release the connection.
	 * 
	 * @param rcmParameters
	 * @return
	 * @throws ProtocolException
	 * @throws IOException
	 */
	private DisconnectResponseMessage disconnectResponse(RcmParameters rcmParameters)
			throws ProtocolException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		DisconnectResponseMessage disconectResponse = new DisconnectResponseMessage(rcmParameters, (short) 0);
		disconectResponse.streamOut(dos);
		dos.close();
		sendMessage(bos.toByteArray());
		return disconectResponse;
	}

	/**
	 * disconnect request class to receive the message from client and pick it
	 * appropriate class to release the connection.
	 * 
	 * @throws Exception
	 */
	private void receiveDisconnectCommandResponse() throws Exception {
		byte[] arrayOfbyte = receiveMessage();
		ByteArrayInputStream bis = new ByteArrayInputStream(arrayOfbyte);
		Message localMessage = Message.CreateMessage(new DataInputStream(bis));
		if (localMessage.getClass() == DisconnectRequestMessage.class) {
			DisconnectRequestMessage drs = (DisconnectRequestMessage) localMessage;
		}
	}

	/**
	 * Clerk command request class will be used to identify the proper class for
	 * processing of clerk commands. In this method other than clerk commands ,
	 * other operation classes will be processed.
	 * 
	 * @return
	 * @throws Exception
	 */
	private Object receiveCommandResponse() throws Exception {
		{
			try {
				byte[] arrayOfByte = receiveMessage();
				DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(arrayOfByte));
				Message localMessage = Message.CreateMessage(inputStream);
				inputStream.close();
				if (localMessage.getClass() == ExecuteCommandRequestMessage.class) {
					ExecuteCommandRequestMessage connectionRequestMessage = (ExecuteCommandRequestMessage) localMessage;
					connectionRequestMessage.getClerkName();
					connectionRequestMessage.getClerkPassword();
					connectionRequestMessage.getCharacterSet();
					connectionRequestMessage.getCommand();
					return connectionRequestMessage;
				}
				if (localMessage.getClass() == DisconnectRequestMessage.class) {
					return ((DisconnectRequestMessage) localMessage);
				}

			} catch (ClassCastException localClassCastException) {
				throw new Exception("A VSS2 protocol error has occurred. Unexpected message type sent by RCM.");
			} catch (ProtocolException localProtocolException) {
				throw new Exception("A VSS2 protocol error has occurred. " + localProtocolException.getMessage());
			} catch (IOException localIOException) {
				throw new Exception("A VSS2 protocol error has occurred. " + localIOException.toString());
			}
		}
		return null;
	}
}
