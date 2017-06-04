import java.io.File;

public interface Connection {
	void connect() throws Exception;
	void createDirectoryTree(String dirTree) throws Exception;
	boolean fileExists(String filePath) throws Exception;
	String[] uploadFiles(File[] files, boolean forceUpload) throws Exception;
	void deconnect() throws Exception;
}