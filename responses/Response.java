package responses;

import configurations.MimeTypes;
import date.Dateinfo;
import server.Resource;
import server.ServerController;

import java.io.*;


public class Response {
  private int code;
  private String httpVersion = null;
  private Resource resource;
  private File targetFile;
  private int contentLength = 0;
  private String user = null;
  private StringBuilder responseHeader;
  private byte[] body = null;
  private MimeTypes mimeTypes;


  public Response(File file, MimeTypes mimeTypes, String httpVersion, String user) {
    this.targetFile = file;
    this.responseHeader = new StringBuilder();
    this.mimeTypes = mimeTypes;
    this.httpVersion = httpVersion;
    this.user = user;
  }

  public void setStarLine(int code, String reasonPhrase) {
    this.code = code;
      
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(this.httpVersion).append(" ").append(code).append(" ").append(reasonPhrase);
    if (this.responseHeader.length() == 0) {
      this.addHeaderLine(stringBuilder.toString());
    }
  }

  public void setCommonHeaders() {
    this.addHeaderLine("Server: " + ServerController.SERVER_NAME);
    this.addHeaderLine("Connection: close");
    this.addHeaderLine("Content-Language: en");
    this.addHeaderLine("Date: " + Dateinfo.getCurrentTimeGMT());
  }

  public void addHeaderLine(String headerLine) {
    this.responseHeader.append(headerLine).append("\r\n");
  }

  public void addFileInfo() {
    String contentType = mimeTypes.lookUP(Response.getFileExternsion(this.targetFile));
    if (contentType != null) {
      this.addHeaderLine("Content-Type: " + contentType);
    }
    this.addHeaderLine("Last-Modified: " + Dateinfo.fileLastModified(this.targetFile));

  }

  public void finishResponseBuild() {
    this.addHeaderLine("");
  }

  public void buildBodyByString(String stringBody) {
    this.body = stringBody.getBytes();
    this.addHeaderLine("Content-Length: " + this.body.length); //add length
    this.addHeaderLine("Content-Type: text/html");
    this.contentLength = this.body.length;
  }

  public void buildBodyByByte(byte[] inputByte) {
    this.body = inputByte;
    this.contentLength = this.body.length;
  }

  public void buildBodyByFile() throws IOException {
    BufferedInputStream bufferInputStream = new BufferedInputStream(new FileInputStream(this.targetFile));
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    byte[] buffer = new byte[1024];
    int len = 0;
    while (-1 != (len = bufferInputStream.read(buffer))) {
      bos.write(buffer, 0, len);
    }
    bos.flush();
    this.body = bos.toByteArray();
    bos.close();
    bufferInputStream.close();

    this.addHeaderLine("Content-Length: " + this.body.length);
    this.contentLength = this.body.length;
  }

  public void deleteFile() {
    if (targetFile.isFile() && targetFile.exists()) {
      boolean deleteAction = targetFile.delete();
    }
  }

  public void createNewFile(byte[] body) throws IOException {
    if (targetFile.exists()) {
      this.deleteFile();
    }
    targetFile.createNewFile();

    if (body == null) return;

    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFile));
    InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(body));
    byte[] buffer = new byte[1024];
    int len = 0;
    while (-1 != (len = inputStream.read(buffer))) {
      outputStream.write(buffer, 0, len);
    }
    outputStream.flush();
    outputStream.close();
    inputStream.close();
  }

  public void send(OutputStream outputStream) throws IOException {
    BufferedOutputStream output = new BufferedOutputStream(outputStream);
    output.write(this.responseHeader.toString().getBytes());

    if (this.body != null && body.length > 0) {
      output.write(this.body);
    }
    output.flush();
  }

  public int getCode() {
    return code;
  }

  public String getUser() {
    return user;
  }

  public int getContentLength() {
    return contentLength;
  }

  private static String getFileExternsion(File file) {
    String extension = "";
    String fileName = file.getName();
    int indexOfDot = fileName.lastIndexOf('.');
    if (indexOfDot > 0) {
      extension = fileName.substring(indexOfDot + 1);
    }
    return extension;
  }
}
