package responses;

import configurations.MimeTypes;

import java.io.File;

public class Response204 extends Response {
  public Response204(File file, MimeTypes mimeTypes, String httpVersion, String user) {
    super(file, mimeTypes, httpVersion, user);
    this.setStarLine(204, "No Content!");
    this.setCommonHeaders();
    this.deleteFile();
  }
}
