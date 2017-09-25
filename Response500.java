package responses;

import configurations.MimeTypes;

import java.io.File;

public class Response500 extends Response {
  public Response500(File file, MimeTypes mimeTypes, String httpVersion, String user) {
    super(file, mimeTypes, httpVersion, user);
    this.setStarLine(500, "Internal Server Error");
    this.setCommonHeaders();
    this.buildBodyByString("<h1> 500 Interenal Server Error! </h1>");
    this.finishResponseBuild();
  }
}
