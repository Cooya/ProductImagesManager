import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Room {
	protected static final int IMAGE_WIDTH = 80;
	protected static final int IMAGE_HEIGHT = 120;
	
	private Row row;
	private ImageView thumbnail;
	private String styleNumber;
	private String name;
	private String size;
	private String frontImageUrl;
	private String backImageUrl;
	private String firstExtraImageUrl;
	private String secondExtraImageUrl;
	private String thirdExtraImageUrl;
	
	public Room(Row row) throws FileNotFoundException {
		this.row = row;
		this.thumbnail = null;
		this.styleNumber = row.getCell(0).getStringCellValue();
		this.name = row.getCell(1).getStringCellValue();
		this.size = row.getCell(5).getStringCellValue();
	}
	
	public Room update(Map<String, String[]> filePaths) throws FileNotFoundException {
		String[] urlArray = filePaths.get(this.styleNumber);
		if(urlArray != null) {
			this.thumbnail = new ImageView(new Image(new FileInputStream(urlArray[0])));
			this.thumbnail.setFitWidth(IMAGE_WIDTH);
			this.thumbnail.setFitHeight(IMAGE_HEIGHT);
			
			this.frontImageUrl = urlArray[1];
			this.backImageUrl = urlArray[2];
			this.firstExtraImageUrl = urlArray[3];
			this.secondExtraImageUrl = urlArray[4];
			this.thirdExtraImageUrl = urlArray[5];
			
			this.row.getCell(7).setCellValue(urlArray[1]);
			this.row.getCell(8).setCellValue(urlArray[2]);
			this.row.getCell(9).setCellValue(urlArray[3]);
			this.row.getCell(10).setCellValue(urlArray[4]);
			this.row.getCell(11).setCellValue(urlArray[5]);
		}
		return this;
	}
	
	public Row getRow() {
		return this.row;
	}

	public ImageView getImage() {
		return this.thumbnail;
	}

	public String getStyleNumber() {
		return this.styleNumber;
	}

	public String getName() {
		return this.name;
	}

	public String getSize() {
		return this.size;
	}

	public String getFrontImageUrl() {
		return this.frontImageUrl;
	}

	public String getBackImageUrl() {
		return this.backImageUrl;
	}
	
	public String getFirstExtraImageUrl() {
		return this.firstExtraImageUrl;
	}
	
	public String getSecondExtraImageUrl() {
		return this.secondExtraImageUrl;
	}
	
	public String getThirdExtraImageUrl() {
		return this.thirdExtraImageUrl;
	}
}