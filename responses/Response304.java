package responses;

import configurations.MimeTypes;

import java.io.File;

public class Response304 extends Response {
  public Response304(File file, MimeTypes mimeTypes, String httpVersion, String user) {
    super(file, mimeTypes, httpVersion, user);
    this.setStarLine(304, "Not Modified");
    this.setCommonHeaders();
    this.addFileInfo();
  }
}
