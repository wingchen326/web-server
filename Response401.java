package responses;

import configurations.MimeTypes;

import java.io.File;


public class Response401 extends Response {
  public Response401(File targetFile, MimeTypes mimeTypes, String httpVersion, String user) {
    super(targetFile, mimeTypes, httpVersion, user);
    this.setStarLine(401, "Unauthorized");
    String authHeader = "WWW-Authenticate: Basic realm=\"I Challenge You\"";
    this.addHeaderLine(authHeader);
    this.setCommonHeaders();
  }
}
