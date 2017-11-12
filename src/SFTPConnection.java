import java.io.File;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPConnection implements Connection {
	private String host;
	private String login;
	private String password;
	private Session session;
	private ChannelSftp channel;
	
	public SFTPConnection(String host, String login, String password) {
		this.host = host;
		this.login = login;
		this.password = password;
	}

	public void connect() throws JSchException {
		Controller.updateMessage("Connection to SFTP server...");
		JSch jsch = new JSch();
		this.session = jsch.getSession(this.login, this.host, 22);
		this.session.setConfig("StrictHostKeyChecking", "no");
		this.session.setPassword(this.password);
		this.session.connect();

		this.channel = (ChannelSftp) this.session.openChannel("sftp");
		this.channel.connect();
	}
	
	public void createDirectoryTree(String dirName) throws SftpException {
		Controller.updateMessage("Creating remote directory...");
		try {
			this.channel.mkdir(dirName);
		} catch(SftpException e) {
			if(e.id != 4) // directory already exists == 4
				throw e;
		}
		this.channel.cd(dirName);
	}
	
	public boolean fileExists(String filePath) {
		try {
			this.channel.lstat(filePath);
			return true;
		}
		catch(SftpException e) {
			return false;
		}
	}
	
	public String[] uploadFiles(File[] files, boolean forceUpload) throws SftpException {
		String currentDirectory = this.channel.pwd();
		if(!currentDirectory.endsWith("/"))
			currentDirectory += '/';
		String[] remotePaths = new String[files.length];
		
		for(int i = 0; i < files.length; ++i) {
			Controller.updateMessage("Uploading files... " + i + "/" + files.length + " files uploaded.");
			remotePaths[i] = (currentDirectory + files[i].getName());
			if(!forceUpload && fileExists(remotePaths[i])) // file already exists
				continue;
			this.channel.put(files[i].getAbsolutePath(), remotePaths[i]);
		}
		
		Controller.updateMessage("Upload complete.");
		return remotePaths;
	}

	public void deconnect() {
		this.channel.exit();
		this.session.disconnect();
	}
}