import java.net.*;
import java.io.*;
import java.math.*;
import java.security.*;
import java.lang.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class dh{

  private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
  private static final SecureRandom RANDOM = new SecureRandom();		

	public static void main(String[] args) throws IOException{
		
		InetAddress addr = InetAddress.getByName("127.0.0.1");
		System.out.println("addr = " + addr);
        int port = Integer.parseInt(args[0]);
		Socket socket = new Socket(addr, port);
	    System.out.println("Connected to Server!!!" );
			
		try{
			
			BufferedReader in = new BufferedReader(
					new InputStreamReader( socket.getInputStream()));
			
			PrintWriter out = new PrintWriter(
					new BufferedWriter( new OutputStreamWriter( socket.getOutputStream() )), true);	
						
            BigInteger p = new BigInteger(args[1]);
			BigInteger g = new BigInteger(args[2]);
			BigInteger secret = new BigInteger(args[3]);


			//format
			String start = "**DHA**";
			BigInteger A = g.modPow(secret,p);		
			String end = "****";	
			
			System.out.println(start+A+end);
			out.println(start+A+end);
			
			// recieve from server									
			String value = in.readLine();	//read from socket
			System.out.println(value);

			value = value.replace("DHB","");
			value = value.replace("*","");
			BigInteger B = new BigInteger(value);
			System.out.println(B);	

		    //compute
			BigInteger DH = B.modPow(secret,p);   
			System.out.println(DH);

			// random 4letter string					
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i <4 ; ++i) {
				sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
			}
			
			String rand4Letters = sb.toString();
			System.out.println(rand4Letters);
 
			String key = DH.toString();
			int len = key.length();
			System.out.println("Computed value length before padding 0's--->"+len +"\n");
		
			//add 0 to string if length<12 o make it 12char string
			if(len<12){
				while(len<12){
					key = "0" + key;
					len = key.length();
				}
			} 
			System.out.println("12bytes key---> "+key +"\n");

			// 16bit key by concaenating 4letter random sring with 12bit key string 
			key = rand4Letters + key;
			System.out.println("16char string--->"+key+"\n");
						
			//Using MD5 Hashing
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(key.getBytes());
			
			byte[] hashBytes = md.digest();
			byte[] hashByte16 = Arrays.copyOfRange(hashBytes, 0, 16);
			System.out.println("SessionKey ---> " + hashByte16 +"\n");
			System.out.println("Key: " +Base64.getEncoder().encodeToString(hashByte16)+"\n");
            

            out.println("**NONCE**" + key +"****");				  //send session key to server
			
			out.println("**REQ****");                             //Request
            String returnEncryptedFile = in.readLine();					  //Receive encrypted file 
            returnEncryptedFile = returnEncryptedFile.replace("ENC","");
			returnEncryptedFile = returnEncryptedFile.replace("*","");
			System.out.println("Response from server encoded---> "+ returnEncryptedFile + "\n");
	   
            
			byte[] byteArray = Base64.getDecoder().decode(returnEncryptedFile.getBytes());
			System.out.println("Return Encrypted hash ---> " + byteArray +"\n");			
			
			//Decrypt
			String decryptedText = decrypt(byteArray, hashByte16 , key);
            System.out.println("DeCrypted Text : "+decryptedText);
            
            //MD5 hashing of file
            decryptedText = decryptedText.replace("FILE:","");
            System.out.println("Stripped DeCrypted Text : "+decryptedText);
             
            MessageDigest msg = MessageDigest.getInstance("MD5");
			byte[] hashBytesFile = msg.digest(decryptedText.getBytes());
			
			//Encrypt File Content
            byte[] encryptedText = encrypt(hashBytesFile ,hashByte16 , key);
            System.out.println("EnCrypted Text : "+encryptedText);

            //Verify from Server
            out.println("**VERIFY**"+ Base64.getEncoder().encodeToString(encryptedText)+"****");
            String response = in.readLine();
            System.out.println("\n"+response); 
            
		}catch( Exception e ){
			e.printStackTrace();
				
		}finally{
				System.out.println("closing Socket..");
				socket.close(); //close connection 
		}
	
	}
	
	public static String decrypt (byte[] cipherText, byte[] key, String IV) throws Exception
    {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        
        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        
        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
        
        //Initialize Cipher for DECRYPT_MODE
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        
        //Perform Decryption
        String decryptedText = new String(cipher.doFinal(cipherText));
        
        return decryptedText;
    }
    
    public static byte[] encrypt (byte[] plaintext,byte[] key, String IV ) throws Exception
    {
        //Get Cipher Instance
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        
        //Create SecretKeySpec
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        
        //Create IvParameterSpec
        IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes());
        
        //Initialize Cipher for ENCRYPT_MODE
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        
        //Perform Encryption
        byte[] cipherText = cipher.doFinal(plaintext);
        
        return cipherText;
    }

}
