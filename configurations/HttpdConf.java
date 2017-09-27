package configurations;

//  Huiliang & Yingjing on 9/26/2017

import java.util.HashMap;

public class HttpdConf extends ConfigurationReader {
  private HashMap<String, String> aliases;
  private HashMap<String, String> scriptAliases;
  private String serverRoot = null;
  private String documentRoot = null;
  private int port = 0;
  private String logFileName = null;
  private String accessFileName;
  private String directoryIndex;


  public HttpdConf(String fileName) {
    super(fileName);
    this.aliases = new HashMap<String, String>();
    this.scriptAliases = new HashMap<String, String>();
    this.accessFileName = ".htaccess";
    this.directoryIndex = "index.html";
  }

  @Override
  public void load() {
    while (this.hasMoreLines()) {
      String currLine = this.nextLine().trim();
      if (!currLine.equals("") && currLine.charAt(0) != '#') {
        String[] params = currLine.split("\\s+");
        if (params[0].toLowerCase().equals("serverroot")) {
          this.serverRoot = params[1].substring(1, params[1].length() - 1);
        } else if (params[0].toLowerCase().equals("documentroot")) {
          this.documentRoot = params[1].substring(1, params[1].length() - 1);
        } else if (params[0].toLowerCase().equals("listen")) {
          this.port = Integer.parseInt(params[1]);
        } else if (params[0].toLowerCase().equals("logfile")) {
          this.logFileName = params[1].substring(1, params[1].length() - 1);
        } else if (params[0].toLowerCase().equals("alias")) {
          this.aliases.put(params[1], params[2].substring(1, params[2].length() - 1));
        } else if (params[0].toLowerCase().equals("scriptalias")) {
          this.scriptAliases.put(params[1], params[2].substring(1, params[2].length() - 1));
        } else if (params[0].toLowerCase().equals("accessfilename")) {
          if (params[1].charAt(0) == '"' && params[1].charAt(params[1].length()-1) == '"') {
            this.accessFileName = params[1].substring(1, params[1].length() - 1);
          } else {
            this.accessFileName = params[1];
          } 
        } else if (params[0].toLowerCase().equals("directoryindex")) {
          if (params[1].charAt(0) == '"' && params[1].charAt(params[1].length()-1) == '"') {
            this.directoryIndex = params[1].substring(1, params[1].length() - 1);
          } else {
            this.directoryIndex = params[1];
          }
        }else{
          System.err.println("wrongparams at line: " + this.getLineNum());
          return;
        }

      }
    }

    if (this.serverRoot == null
            || this.documentRoot == null
            || this.logFileName == null
            || this.port == 0) {
      System.err.println("Incomplete configure file!");
    }

  }

  public HashMap<String, String> getAliases() {
    return aliases;
  }

  public HashMap<String, String> getScriptAliases() {
    return scriptAliases;
  }

  public String getServerRoot() {
    return serverRoot;
  }

  public String getDocumentRoot() {
    return documentRoot;
  }

  public int getPort() {
    return port;
  }

  public String getLogFileName() {
    return logFileName;
  }

  public String getAccessFileName() {
    return accessFileName;
  }

  public String getDirectoryIndex() {
    return directoryIndex;
  }
    
    /*  Testing
    public static void main(String[] args) {
        HttpdConf httpdConf = new HttpdConf("conf/httpd.conf");
        httpdConf.load();
        System.out.println(httpdConf.getAliases().toString());
        System.out.println(httpdConf.getScriptAliases().toString());
        System.out.println(httpdConf.getDirectoryIndex());
        System.out.println(httpdConf.getAccessFileName());
        
    }
     */
}
