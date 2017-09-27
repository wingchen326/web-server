package responses;

import configurations.MimeTypes;

import java.io.File;


public class Response201 extends Response {
  public Response201(File file, MimeTypes mimeTypes, String httpVersion, String user) {
    super(file, mimeTypes, httpVersion, user);
    this.setStarLine(201, "Created");
    this.setCommonHeaders();
  }
}
