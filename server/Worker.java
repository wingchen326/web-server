package server;


import configurations.HttpdConf;
import configurations.MimeTypes;
import exceptions.BadRequestException;
import log.Log;
import responses.Response;
import responses.Response400;
import responses.Response500;
import responses.ResponseFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Worker implements Runnable {
  private Socket client;
  private MimeTypes mimeTypes;
  private HttpdConf serverConfig;
  private HashMap<String, ArrayList<String>> accessFiles;
  private Log logFile;

  public Worker(Socket client, HttpdConf serverConfig, MimeTypes mimeTypes, HashMap<String, ArrayList<String>> accessFiles, Log logFile) {
    this.client = client;
    this.mimeTypes = mimeTypes;
    this.serverConfig = serverConfig;
    this.accessFiles = accessFiles;
    this.logFile = logFile;
  }

  @Override
  public void run() {
    try {
      Request request = new Request(this.client.getInputStream());
      request.parseRequest();
      Resource resource = new Resource(this.serverConfig, request.getUrl());
      Response response = ResponseFactory.getResponse(request, resource, this.mimeTypes, this.serverConfig, this.accessFiles);
      response.send(this.client.getOutputStream());
      logFile.writeLog(request, response, this.client);
      logFile.flush();
    } catch (BadRequestException e) {
      try {
        Response response = new Response400(null, null, "HTTP 1.1", null);
        response.send(this.client.getOutputStream());
        logFile.writeBadReqLod(this.client);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    } catch (Exception e) {
      try {
        Response response = new Response500(null, null, "HTTP 1.1", null);
        response.send(this.client.getOutputStream());
        logFile.writeIntErrLog(this.client);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    } finally {
      try {
        client.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
