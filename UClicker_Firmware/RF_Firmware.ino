

#define NETWORK_SIG_SIZE 3

#define VAL_SIZE         4
#define CHECKSUM_SIZE    1
#define PACKET_SIZE      (NETWORK_SIG_SIZE + VAL_SIZE + CHECKSUM_SIZE)

// The network address byte and can be change if you want to run different devices in proximity to each other without interfearance
#define NET_ADDR 5

const byte g_network_sig[NETWORK_SIG_SIZE] = {0x8F, 0xAA, NET_ADDR};  // Few bytes used to initiate a transfer

// Sends an unsigned int over the RF network
void writeUInt(byte* data)
{
  unsigned int val = data[0] * pow(256,3) + data[1] * pow(256,2) + data[2] * 256 + data[4];
  byte checksum = (val/(int)pow(256,3)) ^ (val&0xFF);
  Serial.write(0xF0);  // This gets reciever in sync with transmitter
  Serial.write(g_network_sig, NETWORK_SIG_SIZE);
  Serial.write(data, VAL_SIZE);
  Serial.write(checksum); //CHECKSUM_SIZE
}

// Receives an unsigned int over the RF network
void readUInt(bool wait)
{
  int pos = 0;          // Position in the network signature
  unsigned int val;     // Value of the unsigned int
  byte c = 0;           // Current byte
  
  if((Serial.available() < PACKET_SIZE) && (wait == false))
  {
   
  }
  
  while(pos < NETWORK_SIG_SIZE)
  { 
    while(Serial.available() == 0); // Wait until something is avalible
    c = Serial.read();

    if (c == g_network_sig[pos])
    {
      if (pos == NETWORK_SIG_SIZE-1)
      {
        byte checksum;
        byte buf[5];

        while(Serial.available() < VAL_SIZE + CHECKSUM_SIZE); // Wait until something is avalible
        buf[0]      =  0xF0;
        buf[1]      =  Serial.read();
        buf[2]      =  Serial.read();
        buf[3]      =  Serial.read();
        buf[4]      =  Serial.read();
        val      = buf[1] * pow(256,3) + buf[2] * pow(256,2) + buf[3] * 256 + buf[4];
        checksum =  Serial.read();
        
        if (checksum != ((val/(int)pow(256,3)) ^ (val&0xFF)))
        {
          // Checksum failed
          pos = -1;
        }else{
            Serial.write(buf, 5);
        }
      }
      ++pos;
    }
    else if (c == g_network_sig[0])
    {
      pos = 1;
    }
    else
    {
      pos = 0;
      if (!wait)
      {
        
      }
    }
  }

}



