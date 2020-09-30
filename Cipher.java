import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import javax.crypto.*;

public class Cipher{

  HashMap<Character, Character> hmapebc = new HashMap<Character, Character>();
  HashMap<Character, Character> hmapcbc = new HashMap<Character, Character>();
  HashMap<Character, Integer> alphabetValue = new HashMap<Character, Integer>();


  public void readFile(String f) throws IOException{
    File file = new File(f);
    FileReader fr = new FileReader(file);
    BufferedReader br = new BufferedReader(fr);
    /*if (!f.exists()) {
      System.out.println(" does not exist.");
      return;
    }
    if (!(f.isFile() && f.canRead())) {
      System.out.println(file.getName() + " cannot be read from.");
      return;
    }*/   
    int n;
    char c;
    while ((n = br.read())!= -1){
      c = (char) n;
      System.out.print(c);
    }
  }

  public void keyebc(String c1, String p1) throws IOException{
    File cipher = new File(c1);
    BufferedReader br1 = new BufferedReader(new FileReader(cipher));

    File plain = new File(p1);
    BufferedReader br2 = new BufferedReader(new FileReader(plain));
    
    int cipherInt;
    int plainInt;
    char c;
    char p;

    //check length of cipher and plain text afterwards
    while((cipherInt = br1.read())!=-1)
    {
      c = (char) cipherInt;

      plainInt = br2.read();
      p = (char) plainInt;
      hmapebc.put(p,c);  
    }

    for(Map.Entry m : hmapebc.entrySet()){
   
      System.out.println(m.getKey()+"--->"+m.getValue()+ "  ");
    }

  } 

public void keycbc(String c1, String p1, int iv) throws IOException{
    File cipher = new File(c1);
    BufferedReader br1 = new BufferedReader(new FileReader(cipher));

    File plain = new File(p1);
    BufferedReader br2 = new BufferedReader(new FileReader(plain));
    
    int cipherInt;
    int plainInt;
    char[] c = new char[(int) cipher.length()];
    char[] p = new char[(int) plain.length()];
    int j=0, z=0;

    // stores lowercase alphabets as keys in hashmap with the value as int from 0 -25  
     for(int i = 0; i < 26; i++){
        char alpha = (char)(97 + i);
        alphabetValue.put(alpha,i);
     } 

      for(Map.Entry m : alphabetValue.entrySet()){
   
      System.out.println(m.getKey()+"--->"+m.getValue()+ "  ");
     }
     
    //add code 
    while((cipherInt = br1.read())!=-1){

      c[j] = (char) cipherInt;
      System.out.print(c[j]);
      j++;
    }

    System.out.println("\n");
      
    while((plainInt = br2.read())!=-1){

      p[z] = (char) plainInt;
      System.out.print(p[z]);
      z++;
      //System.out.print(p[z]);
    }

    for(int k=0; k < plain.length(); k++){
      if(k==0){
        int num = alphabetValue.get(p[k]) + iv;
        hmapcbc.put(pushKeycbc(num),c[k]);
      }
      else{
        int num = alphabetValue.get(p[k]) + alphabetValue.get(c[k-1]);
        hmapcbc.put(pushKeycbc(num),c[k]);
      }

    }

    System.out.println("\n");

    for(Map.Entry mm : hmapcbc.entrySet()){
   
      System.out.println(mm.getKey()+"--->"+mm.getValue()+ "  ");
    }
     
  }

  public String decipherebc(String s) throws IOException{
    File decipher = new File(s);
    BufferedReader br = new BufferedReader(new FileReader(decipher));

    int i;
    char c;

    // put this in a file 
    while((i = br.read())!=-1){

      c = (char) i;
      for(Map.Entry m : hmapebc.entrySet()){
        if(c==(char) m.getValue())
        {
          System.out.print(m.getKey());
        }
      }
    }
    return "";
  }

  public String deciphercbc(String s, int iv) throws IOException{
    File decipher = new File(s);
    BufferedReader br = new BufferedReader(new FileReader(decipher));

    int i;
    char[] c = new char[(int) decipher.length()];
    //add code
    int j=0, ran=0;
    String s2 =" ";

    while((i = br.read())!=-1){

      c[j] = (char) i;
      System.out.print(c[j]);
      j++;
    }

    System.out.println("\n");

    for(int k=0; k < decipher.length(); k++){
      if(k==0){
          
          for(Map.Entry m : hmapcbc.entrySet()){
            if(c[k]==(char) m.getValue())
            {
              ran = (int) alphabetValue.get((char)m.getKey()) - iv;
              s2 = s2 + push(ran);
            }            
          }
          
        }
        else{
          for(Map.Entry t : hmapcbc.entrySet()){
            if(c[k]==(char) t.getValue())
            {
              ran =  alphabetValue.get((char)t.getKey()) - alphabetValue.get((char) c[k-1]);
              s2 = s2 + push(ran);

            }
          }
        }

      }
     return  s2; 
  }

  public String push (int ran){
    String s1 = " ";
    if(ran< 0)
    {
      ran = ran + 26;
    }
    else if(ran > 26){
      ran = ran -26;
    }

    for(Map.Entry mm : alphabetValue.entrySet()){
      if(ran == (int) mm.getValue()){
          char x = (char) mm.getKey();
          System.out.print(x);
          s1 = s1 + x;
        }
    }

  return s1;
  }

  public char pushKeycbc(int num){
    char x = ' ';
    int mod = num % 26;
    for(Map.Entry a : alphabetValue.entrySet()){
      if(mod==(int) a.getValue())
      {
        x = (char) a.getKey();
        System.out.print(x);
      }
    }
    return x;
  }


  public static void main(String[] args){
    try{

     Cipher p = new Cipher(); 
     String mode = args[0];
     String c1 = args[1];
     String p1 = args[2];
     String c2 = args[3];
     String output = args[4];
     int iv1 = Integer.parseInt(args[5]);
     int iv2 = Integer.parseInt(args[6]);
     System.out.println(mode);

     if(mode.equals("EBC")){
      System.out.println("\n");
      p.keyebc(c1, p1);

      System.out.println("\n");
      //p.decipherebc(c2);
      PrintWriter out = new PrintWriter(output);
      out.println(p.decipherebc(c2));
     }
     else if(mode.equals("CBC")){
     //cbc
     System.out.println("\n");
     p.keycbc(c1, p1, iv1);
     System.out.println("\n");
     System.out.println(" ");
     //p.deciphercbc(c2,iv2);
     PrintWriter out = new PrintWriter(output);
     out.println(p.deciphercbc(c2,iv2));
     }
     else
     {
      System.out.println("PLease enter ECB or CBC mode");
     }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
