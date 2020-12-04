package com.vodafone.vss2;


public class Packets
{
  public static short LENGTH = 6;
  private byte[] m_bytes;
  private short m_length;
  private short m_sequenceNumber;
  private boolean m_morePackets;
  
public   Packets(byte[] paramArrayOfByte)
    throws Exception
  {
    if (paramArrayOfByte.length != LENGTH) {
      throw new Exception( "An unknown error has occurred. Invalid packet header size.");
    }
    this.m_bytes = paramArrayOfByte;
    
    this.m_length = ((short)unsignedBytesToInt(paramArrayOfByte[0], paramArrayOfByte[1]));
    this.m_sequenceNumber = ((short)unsignedBytesToInt(paramArrayOfByte[2], paramArrayOfByte[3]));
    this.m_morePackets = (unsignedBytesToInt(paramArrayOfByte[4], paramArrayOfByte[5]) != 0);
  }
  
 public Packets(short paramShort1, short paramShort2, boolean paramBoolean)
  {
    this.m_length = paramShort1;
    this.m_sequenceNumber = paramShort2;
    this.m_morePackets = paramBoolean;
    
    this.m_bytes = new byte[LENGTH];
    this.m_bytes[0] = ((byte)(paramShort1 / 256));
    this.m_bytes[1] = ((byte)(paramShort1 % 256));
    this.m_bytes[2] = ((byte)(paramShort2 / 256));
    this.m_bytes[3] = ((byte)(paramShort2 % 256));
    this.m_bytes[4] = 0;
    if (paramBoolean) {
      this.m_bytes[5] = 1;
    } else {
      this.m_bytes[5] = 0;
    }
  }
  
  public void writeToPacket(byte[] paramArrayOfByte)
  {
    System.arraycopy(this.m_bytes, 0, paramArrayOfByte, 0, LENGTH);
  }
  
  public short getLength()
  {
    return this.m_length;
  }
  
  public short getSequenceNumber()
  {
    return this.m_sequenceNumber;
  }
  
 public boolean getMorePackets()
  {
    return this.m_morePackets;
  }
  
  private static int unsignedBytesToInt(byte paramByte1, byte paramByte2)
  {
    int i = paramByte2 >= 0 ? paramByte2 : paramByte2 + 256;
    int j = paramByte1 >= 0 ? paramByte1 : paramByte1 + 256;
    return (j << 8) + i;
  }
}
