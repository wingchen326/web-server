package responses;

// Huiliang & Yingjing on 9/26/2017

import configurations.Htaccess;
import configurations.HttpdConf;
import configurations.MimeTypes;
import date.Dateinfo;
import server.Request;
import server.Resource;
import server.ScriptResponseHandler;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

public class ResponseFactory {
  public static Response getResponse(Request request, Resource resource, MimeTypes mimeTypes,
                                     HttpdConf httpdConf, HashMap<String, ArrayList<String>> accessFile) throws Exception {
    String resourcePath = resource.absPath();
    File resourceFile = new File(resourcePath);
    String httpVersion = request.getHttpVersion();
    String user = null;

    // Code 401 & 403
    if (accessFile.get(httpdConf.getAccessFileName()).size() > 0) {
      String assessFileRoot = accessFile.get(httpdConf.getAccessFileName()).get(0);
      File assessFile = new File(assessFileRoot + File.separator + httpdConf.getAccessFileName());
        //    System.out.println(assessFile.toString()); //todo check assessfile name
        if (resourcePath.contains(assessFileRoot)) {
        Htaccess ha = new Htaccess(assessFile.getAbsolutePath());
        if (!request.getHeaders().containsKey("Authorization")) {
          Response response = new Response401(resourceFile, mimeTypes, httpVersion, user);
          response.finishResponseBuild();
          return response;
        } else {
        /*if fail to pass, return 403,else keepgoing*/
          String[] str = request.getHeaders().get("Authorization").get(0).split("\\s");
          String encode = str[1];
          String credentials = new String(Base64.getDecoder().decode(encode), Charset.forName("UTF-8"));
          // The string is the key:value pair username:password
          String[] tokens = credentials.split(":");
          user = tokens[0];
            
          if (!ha.isAuthorized(tokens[0], tokens[1])) {
            Response response = new Response403(resourceFile, mimeTypes, httpVersion, user); //403 new
            response.buildBodyByString("<h1> 403 Forbidden </h1>\n<h2>You don't have authorization to view this page.</h2>");
            response.finishResponseBuild();
            return response;
          }
        }
      }
    }

    // Code  404
    if ((!request.getVerb().equals("PUT")) && (!resourceFile.exists())) {
      Response response = new Response404(resourceFile, mimeTypes, httpVersion, user);
      response.finishResponseBuild();
      return response;
    }

    // Script
    if (resource.isScript()) {
      Response response = new Response200(resourceFile, mimeTypes, httpVersion, user);
      ScriptResponseHandler scriptResponseHandler = new ScriptResponseHandler();
      String sciptOutput = new String(scriptResponseHandler.executeScript(resourcePath, request));
      response.addHeaderLine(sciptOutput);
      response.finishResponseBuild();
      return response;
    }

    // Code 201 - PUT, CREATE
    if (request.getVerb().equals("PUT")) {
      Response response = new Response201(resourceFile, mimeTypes, httpVersion, user);
      response.createNewFile(request.getBody());
      response.finishResponseBuild();
      return response;
    }

    // Code 204 Delete
    if (request.getVerb().equals("DELETE")) {
      Response response = new Response204(resourceFile, mimeTypes, httpVersion, user);
      response.finishResponseBuild();
      return response;
    }
     
      
    // Code 200 & 304 - GET, POST, HEAD
    if (request.getVerb().equals("GET") || request.getVerb().equals("POST") ||
        request.getVerb().equals("HEAD")) {
      if (request.getHeaders().containsKey("If-Modified-Since")) {
        Date modifiedSince = Dateinfo.stringToDate(request.getHeaders().get("If-Modified-Since").get(0));
        Date fileLastModifed = Dateinfo.stringToDate(Dateinfo.fileLastModified(resourceFile));
        if (fileLastModifed.compareTo(modifiedSince) <= 0) { //304 return not modified
          Response response = new Response304(resourceFile, mimeTypes, httpVersion, user);
          response.finishResponseBuild();
          return response;
        } else {
          Response response = new Response200(resourceFile, mimeTypes, httpVersion, user);
          response.buildBodyByFile();
          response.finishResponseBuild();
          return response;
        }
      } else {
        Response response = new Response200(resourceFile, mimeTypes, httpVersion, user);
        response.buildBodyByFile();
        response.finishResponseBuild();
        return response;
      }
    }

    return null;
  }
}
