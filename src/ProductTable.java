import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ProductTable {
	private ObservableList<Product> table;
	
	public ProductTable() {
		this.table = FXCollections.observableArrayList();
	}
	
	public ObservableList<Product> getObservableTable() {
		return this.table;
	}
	
	public boolean isEmpty() {
		return this.table.isEmpty();
	}
	
	public void addEntriesFromWorkbook(String filePath) throws Exception {
		int workbookId = WorkbookManager.getInstance().addWorkbook(filePath);
		Sheet sheet = WorkbookManager.getInstance().retrieveSheet(workbookId, 0);
		
		Iterator<Row> rowIterator = sheet.iterator();
		Iterator<Cell> cellIterator;
		Row row;
		int counter = 0;

		int[] posArray = readHeader(rowIterator.next());
		if(posArray == null)
			throw new Exception("Invalid spreadsheet header.");
		
		while(rowIterator.hasNext()) {
			row = rowIterator.next();
			
			cellIterator = row.cellIterator();
			while(cellIterator.hasNext()) {
				if(cellIterator.next().getCellTypeEnum() != CellType.BLANK) {
					this.table.add(new Product(row, posArray));
					Controller.updateMessage("Loading spreadsheet file... " + ++counter + " rows loaded.");
					break;
				}
			}
		}
	}
	
	private int[] readHeader(Row row) {
		int styleNumberPos = -1;
		int namePos = -1;
		int sizePos = -1;
		int firstImageUrlPos = -1;
		Iterator<Cell> cellIterator = row.iterator();
		Cell cell;
		while(cellIterator.hasNext()) {
			cell = cellIterator.next();
			switch(cell.getStringCellValue().trim().toLowerCase()) {
				case "style number": styleNumberPos = cell.getColumnIndex(); break;
				case "name": namePos = cell.getColumnIndex(); break;
				case "size": sizePos = cell.getColumnIndex(); break;
				case "image 1": firstImageUrlPos = cell.getColumnIndex(); break;
			}
		}
		if(styleNumberPos == -1 || namePos == -1 || sizePos == -1 || firstImageUrlPos == -1)
			return null;
		int[] posArray = {styleNumberPos, namePos, sizePos, firstImageUrlPos};
		return posArray;
	}
	
	public void updateTable(Map<String, String[]> filePaths) throws FileNotFoundException {
		for(int i = 0; i < this.table.size(); ++i)
			this.table.set(i, table.get(i).update(filePaths));
	}
	
	public void saveTable() throws Exception {
		int nbRows = table.size();
		Row[] rows = new Row[nbRows];
		for(int i = 0; i < nbRows; ++i)
			rows[i] = table.get(i).getRow();
		WorkbookManager.getInstance().saveSheet(0, 0, rows);
	}
}