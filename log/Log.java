package log;

// Huiliang & Yingjing on 9/26/2017

import responses.Response;
import server.Request;
import date.*;

import java.io.*;
import java.net.Socket;

public class Log {
  private static Log logInstance;
  private PrintWriter logWriter;

  public static synchronized Log getLogInstance(String logFileName) throws IOException {
    if (logInstance == null) {
      logInstance = new Log(logFileName);
    }
    return logInstance;
  }

  private Log(String logFileName) throws IOException {
    logWriter = new PrintWriter(new FileWriter(new File(logFileName), true));
  }

  public void writeLog(Request request, Response response, Socket client) {
    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(this.getRemoteAddress(client)).append(" ");
    logBuilder.append(this.getUser(response)).append(" ");
    logBuilder.append(this.getCurrentTime()).append(" ");
    logBuilder.append(this.getRequestInfo(request)).append(" ");
    logBuilder.append(this.getResponseInfo(response)).append(" ");
    if (request.getHeaders().containsKey("User-Agent")) {
      logBuilder.append(request.getHeaders().get("User-Agent"));
    }
    logWriter.println(logBuilder.toString());
    logWriter.flush();
  }


  public void writeLine(String logLine) {
    StringBuilder logBuilder = new StringBuilder();
    logBuilder.append(this.getCurrentTime()).append(" ");
    logBuilder.append(logLine);
    logWriter.println(logBuilder.toString());
    logWriter.flush();
  }

  public void flush() {
    logWriter.flush();
  }

  public void close() {
    logWriter.close();
  }

  private String getCurrentTime() {
    return "[" + Dateinfo.getCurrentTimeGMT() + "]";
  }

  private String getRemoteAddress(Socket client) {
    return client.getRemoteSocketAddress().toString();
  }

  private String getUser(Response response) {
    if (response.getUser() != null) {
      return response.getUser();
    } else {
      return "--";
    }
  }

  private String getRequestInfo(Request request) {
    StringBuilder requestInfo = new StringBuilder();
    requestInfo.append("\"").append(request.getVerb()).append(" ").append(request.getUrl()).append(" ");
    requestInfo.append(request.getHttpVersion()).append("\"");
    return requestInfo.toString();
  }

  private String getResponseInfo(Response response) {
    StringBuilder responseInfo = new StringBuilder();
    responseInfo.append(response.getCode()).append(" ").append(response.getContentLength());
    return responseInfo.toString();
  }


}
