import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Zip {

	public static Vector<File> unzip(String zipFile, String outputFolder) throws IOException {
		Vector<File> files = new Vector<File>();
		byte[] buffer = new byte[1024];
		int counter = 0;

		File folder = new File(outputFolder);
		if(!folder.exists())
			folder.mkdir();

		ZipInputStream inputStream = new ZipInputStream(new FileInputStream(zipFile));
		ZipEntry entry;
		String entryName;
		File file;

		while((entry = inputStream.getNextEntry()) != null) {
			
			entryName = entry.getName();
			if(entryName.equals("/"))
				continue;
			
			// create file
			file = new File(outputFolder + File.separator + entryName);

			// create arborescence of the new file if needed
			new File(file.getParent()).mkdirs();

			// write data into output file
			FileOutputStream fos = new FileOutputStream(file);
			int len;
			while ((len = inputStream.read(buffer)) > 0)
				fos.write(buffer, 0, len);
			fos.close();
			
			files.add(file);
			//Controller.updateMessage("File \"" + file.getPath() + "\" unzipped.");
			Controller.updateMessage("Unzipping archive... " + ++counter + " files unzipped.");
		}

		inputStream.closeEntry();
		inputStream.close();
		return files;
	}
}