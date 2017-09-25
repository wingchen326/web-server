package responses;

import java.io.File;

import configurations.MimeTypes;

public class Response403 extends Response {
  public Response403(File file, MimeTypes mimeTypes, String httpVersion, String user) {
    super(file, mimeTypes, httpVersion, user);
    this.setStarLine(403, "Forbidden");
    this.setCommonHeaders();
  }
}
