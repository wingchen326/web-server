package configurations;

import java.io.*;

//  Huiliang & Yingjing  on 9/26/2017

public abstract class ConfigurationReader {
  private File file;
  private BufferedReader bufferedReader;
  private String nextLineContent;
  private int lineNum;

  public ConfigurationReader(String fileName) {
    try {
      this.file = new File(fileName);
      this.bufferedReader = new BufferedReader(new FileReader(this.file));
      this.lineNum = 0;
    } catch (FileNotFoundException e) {
      System.err.println("FileNotFoundException:" + e.toString());
    }
  }

  public boolean hasMoreLines() {
    boolean hasMore = false;
    try {
      nextLineContent = this.bufferedReader.readLine();
      this.lineNum++;
      if (nextLineContent != null) {
        hasMore = true;
      } else {
        hasMore = false;
        bufferedReader.close();
      }
    } catch (IOException e) {
        e.printStackTrace();
      //System.err.println("IOException at hasMoreLine.");
      try {
        bufferedReader.close();
      } catch (IOException e2) {
          e.printStackTrace();
        //System.err.println("IOException at file closing");
      }
    }
    return hasMore;
  }

  public String nextLine() {
    return this.nextLineContent;
  }

  public int getLineNum() {
    return this.lineNum;
  }

  public abstract void load();

}
