import server.ServerController;

/*
    Huiliang Huang & Yingjing Chen
*/

public class WebServer {
  public static void main(String[] args) {
    ServerController serverController = new ServerController();
    serverController.start();
  }
}
