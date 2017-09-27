package server;

import exceptions.BadRequestException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Request {
  private String url = null;
  private byte[] body = null;
  private String verb = null;
  private String httpVersion = null;
  private HashMap<String,String> queryString;
  private String longQueryString = null;
  private HashMap<String, List<String>> headers;
  private BufferedReader bufferedReader = null;

  public Request(String testResques) {
    this.bufferedReader = new BufferedReader(new StringReader(testResques));
    this.headers = new HashMap<String, List<String>>();
    this.queryString = new HashMap<String, String>();
  }

  public Request(InputStream client) {
    this.bufferedReader = new BufferedReader(new InputStreamReader(client));
    this.queryString = new HashMap<String, String>();
    this.headers = new HashMap<String, List<String>>();
  }

  public void parseRequest() throws BadRequestException {
    try {
      // Start Line
      String startLine = bufferedReader.readLine();
      while (startLine == null) {
        startLine = bufferedReader.readLine();
      }
      this.parseStartLine(startLine);

      // Header Line
      String headerLine = null;
      while ((headerLine = bufferedReader.readLine()) != null) {
        if (!headerLine.isEmpty()) {
          this.parseHeaderLine(headerLine);
        } else {
          break;
        }
      }
      if (this.verb.equals("GET") || this.verb.equals("DELETE") || this.verb.equals("HEAD")) {
        return;
      }
        
      // body
      this.parseBody();

    } catch (IOException e) {
      throw new BadRequestException();
    } catch (Exception e) {
      throw new BadRequestException();
    }
  }

  private void parseStartLine(String requestLine) throws BadRequestException {
    String startLinePattern = "^(GET|POST|PUT|DELETE|HEAD) (\\/.*) (HTTP\\/(1\\.1|1\\.0|0\\.9))$";
    Pattern pattern = Pattern.compile(startLinePattern);
    Matcher matcher = null;
    matcher = pattern.matcher(requestLine);
    if (matcher.find()) {
      this.verb = matcher.group(1);
      this.url = matcher.group(2);
      this.httpVersion = matcher.group(3);
      if(this.url.contains("?")) {
        String[] splittedUrl = this.url.split("\\?");
        this.url = splittedUrl[0];
        this.longQueryString = splittedUrl[1];
        this.parseQueryString(splittedUrl[1]);
      }
    } else {
      throw new BadRequestException();
    }
  }

  private void parseQueryString(String queryStrings) throws BadRequestException {
    String[] pairs = queryStrings.split("&");
    for (String pair : pairs) {
      String[] keyValue = pair.split("=");
      this.queryString.put(keyValue[0],keyValue[1]);
    }
  }

  private void parseHeaderLine(String requestLine) throws BadRequestException {
    String headLinePattern = "^([^:]*)(:\\s*)(.*)$";
    Pattern pattern = Pattern.compile(headLinePattern);
    Matcher matcher = pattern.matcher(requestLine);
    if (matcher.find()) {
      String key = matcher.group(1);
      String[] values = matcher.group(3).split(",\\s*");
      if (!headers.containsKey(key)) {
        headers.put(key, new ArrayList<String>());
        for (String value : values) {
          headers.get(key).add(value);
        }
      } else {
        for (String value : values) {
          headers.get(key).add(value);
        }
      }
    } else {
      throw new BadRequestException();
    }
  }

  private void parseBody() throws BadRequestException {
    try {
      StringBuilder bodyContent = new StringBuilder();
      int length = 0;
      int contentLength = Integer.parseInt(headers.get("Content-Length").get(0));
      while (length < contentLength) {
        char content = (char) bufferedReader.read();
        bodyContent.append(content);
        length = bodyContent.length();
      }
      this.body = bodyContent.toString().getBytes();
      
    } catch (Exception e) {
      throw new BadRequestException();
    }
  }

  public String getUrl() {
    return url;
  }

  public byte[] getBody() {
    return body;
  }

  public HashMap<String, String> getQueryString() {
    return queryString;
  }
  
  public String getLongQueryString() {
    return longQueryString;
  }

  public String getVerb() {
    return verb;
  }

  public String getHttpVersion() {
    return httpVersion;
  }

  public HashMap<String, List<String>> getHeaders() {
    return headers;
  }
  
  void setBody(byte[] body) {
    this.body = body;
  }

}

