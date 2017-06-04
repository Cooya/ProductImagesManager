import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class UITable {
	private ObservableList<Room> table;
	
	public UITable() {
		this.table = FXCollections.observableArrayList();
	}
	
	public ObservableList<Room> getObservableTable() {
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

		rowIterator.next(); // skip the header
		while(rowIterator.hasNext()) {
			row = rowIterator.next();
			
			cellIterator = row.cellIterator();
			while(cellIterator.hasNext()) {
				if(cellIterator.next().getCellTypeEnum() != CellType.BLANK) {
					this.table.add(new Room(row));
					Controller.updateMessage("Loading spreadsheet file... " + ++counter + " rows loaded.");
					break;
				}
			}
		}
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