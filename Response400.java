package responses;

import configurations.MimeTypes;

import java.io.File;

public class Response400 extends Response{
  public Response400(File file, MimeTypes mimeTypes, String httpVersion, String user) {
    super(file, mimeTypes, httpVersion, user);
    this.setStarLine(400, "Bad Request");
    this.setCommonHeaders();
    this.buildBodyByString("<h1> 400 Bad Request! </h1>");
    this.finishResponseBuild();
  }
}
