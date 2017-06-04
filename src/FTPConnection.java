import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FTPConnection implements Connection {
	private String host;
	private String login;
	private String password;
	private FTPClient client;

	public FTPConnection(String host, String login, String password) {
		this.host = host;
		this.login = login;
		this.password = password;
		this.client = new FTPClient();
	}

	public void connect() throws IOException {
		Controller.updateMessage("Connection to FTP server...");
		this.client.connect(this.host);
		this.client.enterLocalPassiveMode(); // do not move
		this.client.login(this.login, this.password);
		this.client.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
		this.client.setFileTransferMode(FTP.BINARY_FILE_TYPE);
	}

	public void createDirectoryTree(String dirTree) throws IOException {
		Controller.updateMessage("Creating remote directory...");
		boolean dirExists = true;
		String[] directories = dirTree.split("/");
		for(String dir : directories) {
			if(!dir.isEmpty()) {
				if(dirExists)
					dirExists = this.client.changeWorkingDirectory(dir);
				if(!dirExists) {
					if(!this.client.makeDirectory(dir))
						throw new IOException("Unable to create remote directory '" + dir + "'.  error='" + this.client.getReplyString() + "'");
					if(!this.client.changeWorkingDirectory(dir))
						throw new IOException("Unable to change into newly created remote directory '" + dir + "'.  error='" + this.client.getReplyString() + "'");
				}
			}
		}     
	}

	public boolean fileExists(String filePath) throws IOException {
		return this.client.listFiles(filePath).length != 0;
	}

	public String[] uploadFiles(File[] files, boolean forceUpload) throws IOException {
		String currentDirectory = this.client.printWorkingDirectory() + "/";
		String[] remotePaths = new String[files.length];
		FileInputStream fis;

		for(int i = 0; i < files.length; ++i) {
			Controller.updateMessage("Uploading files... " + i + "/" + files.length + " files uploaded.");
			remotePaths[i] = (currentDirectory + files[i].getName());
			if(!forceUpload && fileExists(remotePaths[i])) // file already exists
				continue;
			fis = new FileInputStream(files[i]);
			System.out.println("Uploading file \"" + files[i] + "\"...");
			if(!this.client.storeFile(remotePaths[i], fis)) {
				System.out.println(remotePaths[i]);
				throw new IOException("The upload has failed.");
			}
			fis.close();
		}

		Controller.updateMessage("Upload complete.");
		return remotePaths;
	}

	public void deconnect() throws IOException {
		this.client.logout();
		this.client.disconnect();
	}
}