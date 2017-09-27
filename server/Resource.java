package server;


import configurations.HttpdConf;
import exceptions.BadRequestException;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;


public class Resource {
  private HttpdConf config;
  private String url;
  private boolean isScript;

  public Resource(HttpdConf config, String url) {
    this.config = config;
    this.url = url;
    this.isScript = false;
  }

  public String absPath() {
    String[] splittedUrl = this.url.split("\\/");
    String[] realURLComponent = null;
    if (splittedUrl.length > 0) {
      realURLComponent = Arrays.copyOfRange(splittedUrl, 1, splittedUrl.length);
    } else {
      realURLComponent = new String[]{"/"};
    }

    String tobeAliased = null;
    if (realURLComponent[0].equals("/")) {
      tobeAliased = realURLComponent[0];
    } else {
      tobeAliased = "/" + realURLComponent[0] + "/";
    }

    if (this.config.getAliases().containsKey(tobeAliased)) {
      String realRoot = this.config.getAliases().get(tobeAliased);
      if (realRoot.charAt(realRoot.length() - 1) == File.separatorChar) {
        realURLComponent[0] = realRoot.substring(0, realRoot.length() - 1);
      } else {
        realURLComponent[0] = realRoot;
      }
      String realFilePath = this.arrayToString(realURLComponent);
      if (isFolder(realFilePath)) {
        realFilePath = realFilePath + config.getDirectoryIndex();
      }
      return realFilePath;
    }

    if (this.config.getScriptAliases().containsKey(tobeAliased)) {
      this.isScript = true;
      String realRoot = this.config.getScriptAliases().get(tobeAliased);
      if (realRoot.charAt(realRoot.length() - 1) == File.separatorChar) {
        realURLComponent[0] = realRoot.substring(0, realRoot.length() - 1);
      } else {
        realURLComponent[0] = realRoot;
      }
      String realFilePath = this.arrayToString(realURLComponent);
      if (isFolder(realFilePath)) {
        realFilePath = realFilePath + config.getDirectoryIndex();
      }
      return realFilePath;
    }

    if (realURLComponent[0].equals("/")) {
      realURLComponent = new String[]{};
    }
    String realFilePath = this.config.getDocumentRoot() + this.arrayToString(realURLComponent);
    if (isFolder(realFilePath)) {
      realFilePath = realFilePath + config.getDirectoryIndex();
    }
    return realFilePath;
  }

  public boolean isScript() {
    return isScript;
  }

  private boolean isFolder(String path) {
    File filePath = new File(path);
    return filePath.isDirectory();
  }

  private boolean isFile(String path) {
    File filePath = new File(path);
    return filePath.isFile();
  }

  private String arrayToString(String[] array) {
    StringBuilder buffer = new StringBuilder();
    for (String item : array) {
      buffer.append(item).append(File.separator);
    }
    return buffer.toString();
  }
}
