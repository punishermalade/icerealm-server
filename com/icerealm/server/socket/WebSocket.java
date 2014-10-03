package com.icerealm.server.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;

/**
 * An implementation of the WebSocket protocol based on a example found at this
 * website: <a href="http://keraj.net/websocket.html">http://keraj.net/websocket.html</a><br><br>
 *
 * This implementation receives the input/output of the client and provides the handshake
 * mechanism<br><br>
 * 
 * The read method is blocking and it is possible to send a message asynchronisely to 
 * the client. 
 * 
 * Keep this object in memory to send message to the client during the server
 * execution time.
 * 
 * @author punisher
 *
 */
public class WebSocket {
	
	/**
	 * The input stream from the client, used to read the message from it
	 */
    private InputStream input;  
    
    /**
     * The output stream to the client, used to send message to it
     */
    private OutputStream output;
    
    /**
     * The key used to do the handshake
     */
    private String _key = null;
  
    /**
     * Default constructor, use it to establish a WebSocket connection
     * @param in Input stream from the client
     * @param out Output stream to the client
     * @exception Exception Thrown if not possible to get the stream
     */
    public WebSocket(Socket socket, String key) throws Exception {
    	input = socket.getInputStream();
    	output = socket.getOutputStream();
    	_key = key;
    }
    
    /**
     * Returns the key sent by the client
     * @return The key sent by the client
     */
    public String getKey() {
    	return _key;
    }
    
    /**
     * Handshake mechanism that needs to be done according to the
     * protocol. Web browser that implements the WebSocket needs to 
     * receive the result of this handshake
     * @param key the key that is sent from the client
     * @throws Exception Exception throw if the key cannot be encrypted
     */
    public void handshake() throws Exception {  
          
        // add key and magic value  
        String accept = _key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";  
          
        // sha1  
        byte[] digest = MessageDigest.getInstance("SHA-1")  
                        .digest(accept.getBytes("UTF8"));  
        // and base64  
        accept = DatatypeConverter.printBase64Binary(digest);
        
        // use this output stream to make it platform independent
        output.write("HTTP/1.1 101 Switching Protocols\r\n".getBytes());
        output.write("Upgrade: websocket\r\n".getBytes());
        output.write("Connection: Upgrade\r\n".getBytes());
        output.write(("Sec-WebSocket-Accept: " + accept + "\r\n").getBytes());
        output.write("\r\n".getBytes());
        output.flush();
    }  
      
    /**
     * Read the array of byte entierly
     * @param b the array of byte to be read
     * @throws IOException Thrown if the read operation fails
     */
    private void readFully(byte[] b) throws IOException {  
          
        int readen = 0;  
        while(readen<b.length)  
        {  
            int r = input.read(b, readen, b.length-readen);  
            if(r==-1)  
                break;  
            readen+=r;  
        }  
    }  
      
    /**
     * Read the message sent by the client, this is a blocking operation
     * @return the String that was sent to the server from the client
     * @throws Exception Thrown if the OP code is wrong
     */
    public String read() throws Exception {  
  
        int opcode = input.read(); 

        @SuppressWarnings("unused")
        boolean whole = (opcode & Integer.parseInt("10000000", 2)) !=0;
        opcode = opcode & 0xF;  
          
        if(opcode!=1)  
            throw new IOException("Wrong opcode: " + opcode);  
       
        int len = input.read();  
        boolean encoded = (len >= 128);  
          
        if(encoded)  
            len -= 128;  
          
        if(len == 127) {  
            len = (input.read() << 16) | (input.read() << 8) | input.read();  
        }  
        else if(len == 126) {  
            len = (input.read() << 8) | input.read();  
        }  
          
        byte[] key = null;  
          
        if(encoded) {  
            key = new byte[4];  
            readFully(key);  
        }  
          
        byte[] frame = new byte[len];  
          
        readFully(frame);  
          
        if(encoded) {  
            for(int i=0; i<frame.length; i++) {  
                frame[i] = (byte) (frame[i] ^ key[i%4]);  
            }  
        }  
          
        return new String(frame, "UTF8");
    }  
      
    /**
     * Send a message to the client
     * @param message The message to be sent
     * @throws Exception Thrown if the stream 
     */
    public void send(String message) throws Exception {
          
        byte[] utf = message.getBytes("UTF8");  
          
        output.write(129);  
          
        if(utf.length > 65535) {  
        	output.write(127);  
        	output.write(utf.length >> 16);  
        	output.write(utf.length >> 8);  
        	output.write(utf.length);  
        }  
        else if(utf.length>125) {  
        	output.write(126);  
        	output.write(utf.length >> 8);  
        	output.write(utf.length);  
        }  
        else {  
        	output.write(utf.length);  
        }  
          
        output.write(utf);  
    }
}
