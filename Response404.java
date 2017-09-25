package responses;

import configurations.MimeTypes;

import java.io.File;


public class Response404 extends Response {
  public Response404(File targetFile, MimeTypes mimeTypes, String httpVersion, String user) {
    super(targetFile, mimeTypes, httpVersion, user);
    this.setStarLine(404, "Not Found");
    this.buildBodyByString("<h1> 404 NOT FOUND </h1>");
    this.setCommonHeaders();
  }
}
