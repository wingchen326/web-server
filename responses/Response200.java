package responses;

import configurations.MimeTypes;

import java.io.File;


public class Response200 extends Response {

  public Response200(File targetFile, MimeTypes mimeTypes, String httpVersion, String user) {
    super(targetFile, mimeTypes, httpVersion, user);
    this.setStarLine(200, "OK");
    this.setCommonHeaders();
    this.addFileInfo();
  }
}
