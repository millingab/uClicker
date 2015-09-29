import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.InputStream;

public class JavaApplication1
{
public static byte Data[] = new byte[4];
public static String Answers[][] = new String[2][1000];
public static String strings[] = { "A", "B", "C", "D", "E"};

    private static void UpdateStats() {
            int quantity[] = {0, 0, 0, 0, 0 };
            String MAC = Integer.toHexString(Data[0] * (int)Math.pow(256,2) + Data[1] * 256 + Data[2]);
//            System.out.println(MAC);
            String answer = Integer.toHexString(Data[3]);
//            System.out.println(answer);
            for(int i = 0; i<1000; i++){
                if(Answers[0][i] == null){
                    Answers[0][i] = MAC;
                    Answers[1][i] = answer;
                    break;
                }else if(Answers[0][i].equals(MAC)){
                    Answers[1][i] = answer;
                    break;
                }
            }
            for(String s : Answers[1]){
                for( int i = 0; i<5; i++){
                    if(s != null&&s.equalsIgnoreCase(strings[i])){
                        quantity[i] ++;
                        break;
                    }
                }
            }
            System.out.println("ANSWERS\t\tA\tB\tC\tD\tE");
            System.out.println("QUANTITY\t"+quantity[0]+"\t"+quantity[1]+"\t"+quantity[2]+"\t"+quantity[3]+"\t"+quantity[4]+"\n\n\n\n");
    }
    public JavaApplication1()
    {
        super();
    }
    
    void connect ( String portName ) throws Exception
    {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if ( portIdentifier.isCurrentlyOwned() )
        {
            System.out.println("Error: Port is currently in use");
        }
        else
        {
            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);
            
            if ( commPort instanceof SerialPort )
            {
                SerialPort serialPort = (SerialPort) commPort;
                serialPort.setSerialPortParams(1200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
                
                InputStream in = serialPort.getInputStream();
                
                (new Thread(new SerialReader(in))).start();

            }
            else
            {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }     
    }
    
    /** */
    public static class SerialReader implements Runnable 
    {
        InputStream in;
        
        public SerialReader ( InputStream in )
        {
            this.in = in;
        }
        
        public void run ()
        {
            byte[] buffer = new byte[1024];
            int len = -1;
            try
            {
                while ( ( len = this.in.read(buffer)) > -1 )
                {
                    Byte b = buffer[0];
                    if(b.intValue() == -16){
                       while(this.in.available() < 4);
                       this.in.read(Data);
                       UpdateStats();
                       buffer = new byte[1024];
                    }
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }
    
    public static void main ( String[] args )
    {
        try
        {
            (new JavaApplication1()).connect("COM5");
        }
        catch ( Exception e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}