import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONConfig {
	private String host;
	private int port;
	private String login;
	private String password;
	private String localImagesFolder;
	private String remoteImagesFolder;
	private String imagesUrlPrefix;
	
	public JSONConfig(String filePath) throws JSONException, IOException {
		try {
			String json = readFile(filePath, Charset.forName("UTF-8"));
			JSONObject obj = new JSONObject(json);
			this.host = obj.getString("host");
			this.port = obj.getInt("port");
			this.login = obj.getString("login");
			this.password = obj.getString("password");
			this.localImagesFolder = obj.getString("localImagesFolder");
			this.remoteImagesFolder = obj.getString("remoteImagesFolder");
			this.imagesUrlPrefix = obj.getString("imagesUrlPrefix");
		}
		catch(JSONException e) {
			throw e;
		}
	}
	
	public String getHost() {
		return this.host;
	}
	
	public int getPort() {
		return this.port;
	}
	
	public String getLogin() {
		return this.login;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getLocalImagesFolder() {
		return this.localImagesFolder;
	}
	
	public String getRemoteImagesFolder()  {
		return this.remoteImagesFolder;
	}
	
	public String getImagesUrlPrefix() {
		return this.imagesUrlPrefix;
	}
	
	private static String readFile(String filePath, Charset encoding) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)), encoding);
	}
}