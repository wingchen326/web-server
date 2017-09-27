package configurations;

//  Huiliang & Yingjing on 9/26/2017

import java.util.HashMap;


public class MimeTypes extends ConfigurationReader {
  private HashMap<String, String> types;

  public MimeTypes(String fileName) {
    super(fileName);
    this.types = new HashMap<String, String>();
  }

  @Override
  public void load() {
    while (this.hasMoreLines()) {
      String currLine = this.nextLine().trim();
      if (!currLine.equals("") && currLine.charAt(0) != '#') {
        String[] params = currLine.split("\\s+");
        if (params.length > 1) {
          for (int i = 1; i < params.length; i++) {
            types.put(params[i],params[0]);
          }
        }
      }
    }
  }

  public String lookUP(String fileExtension) {
    return types.get(fileExtension);
  }

    /*      Testing
     
    public static void main(String[] args) {
        MimeTypes mimeTypes = new MimeTypes("conf/mime.types");
        mimeTypes.load();
        System.out.println(mimeTypes.lookUP("js"));
    }

     */
}
