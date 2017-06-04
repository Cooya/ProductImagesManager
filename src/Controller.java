import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public class Controller {
	private static final String JSON_CONFIG_FILE = "config.json";
	private static final String HASH = "cffd022da1b52339734c683f47bbe70c";
	
	private static JSONConfig config;
	private static Connection connection;
	private static Thread currentProcess;
	//private static File zipArchive;
	private static File workbookFile;
	private static ProductTable table;
	private static Map<String, String[]> filePaths;
	private static SimpleStringProperty message;

	public static void main(String[] args) {
		try {
			config = new JSONConfig(JSON_CONFIG_FILE);
		}
		catch(IOException e) {
			e.printStackTrace();
			View.displayView("Missing JSON config file.");
			return;
		}
		catch(JSONException e) {
			e.printStackTrace();
			View.displayView("Malformated JSON config file.");
			return;
		}
		int port = config.getPort();
		if(port == 21)
			connection = new FTPConnection(config.getHost(), config.getLogin(), config.getPassword());
		else if(port == 22)
			connection = new SFTPConnection(config.getHost(), config.getLogin(), config.getPassword());
		else {
			View.displayView("Invalid port. Please select the port 21 (FTP) or 22 (SFTP).");
			return;
		}
		table = new ProductTable();
		filePaths = new HashMap<String, String[]>();
		message = new SimpleStringProperty();
		View.displayView(null);
	}
	
	public static ObservableList<Product> getObservableTable() {
		return table.getObservableTable();
	}
	
	public static ObservableValue<String> getMessage() {
		return message;
	}

	/*
	public static File getZipArchive() {
		return zipArchive;
	}

	public static void setZipArchive(File file) {
		zipArchive = file;
	}
	*/

	public static File getWorkbookFile() {
		return workbookFile;
	}

	public static void setWorkbookFile(File file) {
		workbookFile = file;
	}
	
	public static void updateMessage(String newMessage) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				message.setValue(newMessage);
			}
		});
		System.out.println(newMessage);
	}
	
	protected static boolean isValidPassword(String str) {
		return hashString(str).equals(HASH);
	}
	
	private static String hashString(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes(), 0 ,str.length());
			return new BigInteger(1, md.digest()).toString(16);
		} catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void runProcess(String processName) {
		View.displayMessagePopUp();
		currentProcess = new Thread() {
			@Override
			public void run() {
				if(processName.equals("load"))
					try {
						table.addEntriesFromWorkbook(workbookFile.getAbsolutePath());
						if(!filePaths.isEmpty())
							table.updateTable(filePaths);
					} catch(Exception e) {
						updateMessage(e.getMessage());
						e.printStackTrace();
						return;
					}
				else if(processName.equals("upload"))
					try {
						processZipArchive();
						if(!table.isEmpty())
							table.updateTable(filePaths);
					} catch(Exception e) {
						updateMessage(e.getMessage());
						e.printStackTrace();
						return;
					}
				else if(processName.equals("save"))
					try {
						table.saveTable();
					} catch(Exception e) {
						updateMessage(e.getMessage());
						e.printStackTrace();
						return;
					}
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						View.dismissMessagePopUp();
					}
				});
			}
		};
		currentProcess.start();
	}
	
	@SuppressWarnings("deprecation")
	public static void cancelCurrentProcess() {
		if(currentProcess != null)
			currentProcess.stop(); // naughty boy
	}
	
	private static void processZipArchive() throws Exception {
		// old version (extract images from a zip archive)
		/*
		if(!tmpDir.exists() && !tmpDir.mkdir())
			throw new Exception("An error has occurred when trying to create the temporary directory.");
		
		// extract files into the temporary directory
		Vector<File> localFiles = Zip.unzip(zipArchive.getAbsolutePath(), tmpDir.getAbsolutePath());
		*/
		
		// new version (read from a defined folder)
		File[] imageFiles = formatImages(); 
		String[] remotePaths = uploadImages(imageFiles);
		
		String localDir = config.getLocalImagesFolder();
		String imagesUrlPrefix = config.getImagesUrlPrefix();
		String fileName;
		String key;
		String[] urlArray;
		int index;
		for(int i = 0; i < remotePaths.length; ++i) {
			fileName = remotePaths[i].substring(remotePaths[i].lastIndexOf('/') + 1);
			key = remotePaths[i].substring(remotePaths[i].lastIndexOf('/') + 1, remotePaths[i].lastIndexOf('.'));
			if(key.indexOf("_F") != -1)
				index = 1;
			else if(key.indexOf("_B") != -1)
				index = 2;
			else if(key.indexOf("_E3") != -1)
				index = 3;
			else if(key.indexOf("_E4") != -1)
				index = 4;
			else
				index = 5;
			key = key.substring(0, key.lastIndexOf("_"));
			if(filePaths.get(key) == null) {
				urlArray = new String[6];
				urlArray[0] = localDir + File.separator + fileName;
				urlArray[index] = imagesUrlPrefix + fileName;
				filePaths.put(key, urlArray);
			}
			else
				filePaths.get(key)[index] = imagesUrlPrefix + fileName;
		}
	}
	
	private static File[] formatImages() throws Exception {
		File imagesDir = new File(config.getLocalImagesFolder());
		if(!imagesDir.exists() || !imagesDir.isDirectory())
			throw new Exception("Local images folder does not exist.");
		
		File[] imageFiles = imagesDir.listFiles();
		String fileName;
		String key;
		String extension;
		String newPath;
		for(int i = 0; i < imageFiles.length; ++i) {
			fileName = imageFiles[i].getName();
			key = fileName.substring(0, fileName.lastIndexOf('.')).toUpperCase();
			extension = fileName.substring(key.length()).toLowerCase(); // with the dot
			newPath = imageFiles[i].getParent() + File.separator + key + (fileName.indexOf('_') == -1 ? "_F" : "") + extension;
			if(!imageFiles[i].renameTo(new File(newPath)))
				;//throw new Exception("Renaming file has failed.");
			imageFiles[i] = new File(newPath); // required
		}
			
		return imageFiles;
	}
	
	private static String[] uploadImages(File[] imageFiles) throws Exception {
		connection.connect();
		connection.createDirectoryTree(config.getRemoteImagesFolder());
		String[] remotePaths = connection.uploadFiles(imageFiles, false); // does not upload if file already exists
		connection.deconnect();
		return remotePaths;
	}
}