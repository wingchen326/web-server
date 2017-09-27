package configurations;

// Huiliang & Yingjing on 9/26/2017

import java.util.HashMap;
import java.util.Base64;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.io.IOException;

public class Htpassword extends ConfigurationReader {
  private HashMap<String, String> passwords;

  public Htpassword( String filename ) {
    super( filename );
    //System.out.println( "Password file: " + filename );
    this.passwords = new HashMap<String, String>();
    this.load();
  }

  @Override
  public void load() {
    while (this.hasMoreLines()) {
      String currLine = this.nextLine().trim();
      if (!currLine.equals("") && currLine.charAt(0) != '#') {
        parseLine(currLine);
      }
    }
  }
  protected void parseLine( String line ) {
    String[] tokens = line.split( ":" );
    if( tokens.length == 2 ) {
      passwords.put( tokens[ 0 ], tokens[ 1 ].replace( "{SHA}", "" ).trim() );
    }
  }

  public boolean isAuthorized( String username, String password ) {
    // call encryptClearPassword to encrypt the users input password,
    // then compare to the SHA key store in password file
    return passwords.containsKey(username) && passwords.get(username).equals(encryptTextPassword(password));
      
  }

  private String encryptTextPassword( String password ) {
    // Encrypt the cleartext password using the SHA-1 encryption algorithm
    try {
      MessageDigest mDigest = MessageDigest.getInstance( "SHA-1" );
      byte[] result = mDigest.digest( password.getBytes() );

     return Base64.getEncoder().encodeToString( result );
    } catch( Exception e ) {
      return "";
    }
  }
}
