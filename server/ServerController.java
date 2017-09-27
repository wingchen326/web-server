package server;

// Huiliang & Yingjing 9/26/2017

import configurations.HttpdConf;
import configurations.MimeTypes;
import log.Log;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerController {
  private static String HTTPD_CONF_PATH = "conf/httpd.conf";
  private static String MIME_TYPES_PATH = "conf/mime.types";
  public static String SERVER_NAME = "Huang_Chen__Server_0.1";
  private HttpdConf serverConfig;
  private MimeTypes mimeTypes;
  private ServerSocket serverSocket;
  private Log logFile;
  public HashMap<String, ArrayList<String>> accessFiles;

  public ServerController() {
  try{
    this.loadHttpdConf();
    this.loadMimeTypes();
    this.initAccessFilesList();
    this.loadServerSocket();
    this.initLogFile();
  } catch (Exception e){
      e.printStackTrace();
    }
      // should not be throw 400 or 500, if Exception, should not start server!
  }

  public void start() {
    while (true) {
      try {
        Socket client = this.serverSocket.accept();
        Thread thread = new Thread(new Worker(client, this.serverConfig, this.mimeTypes, this.accessFiles, this.logFile));
        thread.start();
      } catch (IOException e) {
          e.printStackTrace(); // if socket down, code 500
      }
    }
  }

  private void loadServerSocket() {
    try {
      this.serverSocket = new ServerSocket(this.serverConfig.getPort());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void loadHttpdConf() {
    this.serverConfig = new HttpdConf(HTTPD_CONF_PATH);
    this.serverConfig.load();
  }

  private void loadMimeTypes() {
    this.mimeTypes = new MimeTypes(MIME_TYPES_PATH);
    mimeTypes.load();
  }

  private void initLogFile() {
    try {
      this.logFile = Log.getLogInstance(this.serverConfig.getLogFileName());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initAccessFilesList() {
    this.accessFiles = new HashMap<String, ArrayList<String>>();
    this.accessFiles.put(serverConfig.getAccessFileName(), new ArrayList<String>());
    String dumpRootPath = "/";
    File docRootFolder = new File(this.serverConfig.getDocumentRoot());
    this.checkAccessFile(docRootFolder, dumpRootPath);

  }

  private void checkAccessFile(File file, String parentPath) {
    String targetFileName = serverConfig.getAccessFileName();
    if (file == null) return;

    if (file.isFile() && file.getName().equals(targetFileName)) {
      String[] pathComponents = parentPath.split("/");
      String[] realComponent = Arrays.copyOfRange(pathComponents, 1, pathComponents.length);
      String docRoot = serverConfig.getDocumentRoot();
      if (docRoot.charAt(docRoot.length() - 1) == File.separatorChar) {
        realComponent[0] = docRoot.substring(0, docRoot.length() - 1);
      } else {
        realComponent[0] = docRoot;
      }
      StringBuilder buffer = new StringBuilder();
      for (String path : realComponent) {
        buffer.append(path).append(File.separator);
      }
      this.accessFiles.get(targetFileName).add(buffer.toString());
      return;
    }

    if (file.isDirectory()) {
      String nextLevePath = parentPath + file.getName() + "/";
      for (File subFile : file.listFiles()) {
        checkAccessFile(subFile, nextLevePath);
      }
    }
  }
    
  /* Test
    public static void main(String[] args) {
        Server server = new Server();
        server.start();
        //    System.out.println(server.accessFiles.toString());
        //    System.out.println(server.mimeTypes.types.toString());
    }
   */
}
