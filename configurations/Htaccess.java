package configurations;

//  Huiliang & Yingjing  on 9/26/2017

import java.util.ArrayList;

public class Htaccess extends ConfigurationReader {
  private Htpassword authUserFile;
  private String authType;
  private String authName;
  private String require;
  private ArrayList<String> authUsers;

  public Htaccess(String fileName) {
    super(fileName);
    this.load();
  }

  @Override
  public void load() {
    while (this.hasMoreLines()) {
      String currLine = this.nextLine().trim();
      if (!currLine.equals("") && currLine.charAt(0) != '#') {
        String[] params = currLine.split("\\s+");

        if (params[0].equals("AuthUserFile")) {
          String htpasswordFilePath = params[1].substring(1,
                  params[1].length() - 1);
          this.authUserFile = new Htpassword(htpasswordFilePath);
        } else if (params[0].equals("AuthType")) {
          if (params[1].equals("Basic")) {
            this.authType = params[1];
          } else {
            throw new RuntimeException();
          }
        } else if (params[0].equals("AuthName")) {
          this.authName = params[1].substring(1, params[1].length() - 1);

        } else if (params[0].equals("Require")) {
          if (params[1].equals("valid-user")) {
            this.require = params[1];
          } else if (params[1].equals("user")) {
            this.require = params[1];
            this.authUsers = new ArrayList<String>();
            for (int i = 2; i < params.length; i++) {
              this.authUsers.add(params[i]);
            }
          }
        } else {
          System.out.println("Unsupported params at line: " + this.getLineNum());
          return;
        }
      }
    }
  }

  public boolean isAuthorized(String decodedUsername, String decodedPassword) {
    boolean isAuthorized = false;
    if (this.require.equals("valid-user")
            || (this.require.equals("user") && authUsers.contains(decodedUsername))) {
      isAuthorized = this.authUserFile.isAuthorized(decodedUsername, decodedPassword);
    }
    return isAuthorized;
  }

  public String getAuthName() {
    return authName;
  }

    /***************************************************************
            Testing the for our individual username and password
     ***************************************************************
     public static void main(String[] args) {
        Htaccess ha = new Htaccess("../access/.htaccess");
        System.out.println(ha.authUsers.toString());
        System.out.println(ha.isAuthorized("HHuan", "nicepassword"));
     }
    ****************************************************************/
}
