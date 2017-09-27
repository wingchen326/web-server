package server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;



public class ScriptResponseHandler {

  public byte[] executeScript(String path, Request request) throws Exception {
    if (path.charAt(path.length()-1) == '/') {
      path = path.substring(0, path.length()-1);
    }
    ProcessBuilder pb = new ProcessBuilder(path);
    this.setEnvironment(request, pb);
    Process process = pb.start();
    if ((request.getVerb().equals("PUT") || request.getVerb().equals("POST"))
                                                && request.getBody() != null) {
      this.writeToOutputStream(process, request);
    }

    int errCode = process.waitFor();
    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
    String line = null;
    while ((line = reader.readLine())!= null) {
      System.out.println(line);
    }
    if (errCode != 0) {
      throw new Exception("Fail to execute the script.");
    }
    byte[] bytes = this.convertStreamToByteArray(process.getInputStream());
    return bytes;
  }

  private void writeToOutputStream(Process process, Request request) throws IOException {
    OutputStream out = process.getOutputStream();
    out.write(request.getBody());
    out.close();
  }

  private void setEnvironment(Request request, ProcessBuilder pb) {
    for (String key : request.getHeaders().keySet()) {
      StringBuilder sb = new StringBuilder();
      for (String value : request.getHeaders().get(key)) {
        sb.append(value).append(",");
      }
      sb.deleteCharAt(sb.length() - 1);
      pb.environment().put("HTTP_" + key.toUpperCase(), sb.toString());
    }
    pb.environment().put("SERVER_PROTOCOL", request.getHttpVersion());
    if (request.getLongQueryString() != null) {
      pb.environment().put("QUERY_STRING", request.getLongQueryString());
    }

    /**The server will not send an EOF on the end of the stdin data, instead you
    * must use the environment variable CONTENT_LENGTH to determine how much data
    *  you should read from stdin
    */
    if (request.getBody() != null) {
      pb.environment().put("CONTENT_LENGTH", request.getBody().length + "");
    }
  }

  private byte[] convertStreamToByteArray(InputStream inputStream)
          throws Exception {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    int readByte = inputStream.read();
    while (readByte != -1) {
      outputStream.write(readByte);
      readByte = inputStream.read();
    }
    byte[] bytes = outputStream.toByteArray();
    return bytes;
  }
}
