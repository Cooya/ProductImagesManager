import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WorkbookManager {
	private static WorkbookManager workbookManager;
	
	private Vector<Workbook> workbooks;
	private Vector<String> workbookFiles;
	
	public static WorkbookManager getInstance() {
		if(workbookManager == null)
			workbookManager = new WorkbookManager();
		return workbookManager;
	}
	
	private WorkbookManager() {
		this.workbooks = new Vector<Workbook>();
		this.workbookFiles = new Vector<String>();
	}
	
	public int addWorkbook(String filePath) throws Exception {
		Controller.updateMessage("Opening workbook file...");
		File file = new File(filePath);
		if(!file.exists())
			throw new Exception("The file does not exist.");
		if(!file.isFile())
			throw new Exception("The file is a directory.");
		
		this.workbooks.add(new XSSFWorkbook(file));
		this.workbookFiles.add(filePath);
		return this.workbooks.size() - 1;
	}
	
	public Sheet retrieveSheet(int workbookId, int sheetId) {
		return this.workbooks.get(workbookId).getSheetAt(sheetId);
	}
	
	public void saveSheet(int workbookId, int sheetId, Row[] rows) throws Exception {
		Controller.updateMessage("Saving data update into spreadsheet file...");
		
		Workbook workBook = this.workbooks.get(workbookId);
		Sheet sheet = workBook.getSheetAt(sheetId);
		String fileName = workbookFiles.get(workbookId);
		
		Iterator<Cell> cellIterator;
		Cell cell;
		Cell cellToPut;
		
		// update each cell of the existing sheet
		for(int i = 0; i < rows.length; ++i) {
			cellIterator = sheet.getRow(i + 1).cellIterator(); // skip the header
			for(int j = 0; cellIterator.hasNext(); ++j) {
				cell = cellIterator.next();
				cellToPut = rows[i].getCell(j);
				switch(cell.getCellTypeEnum()) {
					case BOOLEAN : cell.setCellValue(cellToPut.getBooleanCellValue()); break;
					case NUMERIC : cell.setCellValue(cellToPut.getNumericCellValue()); break;
					case STRING : cell.setCellValue(cellToPut.getStringCellValue()); break;
					case FORMULA : cell.setCellValue(cellToPut.getCellFormula()); break;
					case BLANK : break;
					case ERROR : cell.setCellValue(cellToPut.getErrorCellValue()); break;
					case _NONE : break;
				}
			}
		}
		
		// write into a new file
		File tmpFile = new File(fileName + ".tmp");
		OutputStream os = new FileOutputStream(tmpFile);
		workBook.write(os);
		os.close();
		
		// remove the old file
		workBook.close();
		File oldFile = new File(fileName);
		oldFile.delete();
		
		// rename the new one and reload the workbook
		if(!tmpFile.renameTo(oldFile))
			throw new Exception("Impossible to rename the temporary file.");
		this.workbooks.set(workbookId, new XSSFWorkbook(oldFile)); // tmpFile does not work
		
		Controller.updateMessage("Spreadsheet file updated successfully.");
	}
	
	public void closeWorkbook(int workBookId) throws IOException {
		this.workbooks.get(workBookId).close();
		this.workbooks.remove(workBookId);
	}
	
	public void closeAll() throws IOException {
		for(Workbook workbook : this.workbooks)
			workbook.close();
		this.workbooks.clear();
	}

	@Deprecated
	public static void writeSpreadSheet(String filePath, Vector<Vector<Object>> table) throws IOException {
		Workbook workBook = new XSSFWorkbook();
		Sheet sheet = workBook.createSheet();
		
		int tableSize = table.size();
		int rowSize;
		Row row;
		Cell cell;
		Object obj;
		
		for(int i = 0; i < tableSize; ++i) {
			row = sheet.createRow(i);
			rowSize = table.get(i).size();
			for(int j = 0; j < rowSize; ++j) {
				cell = row.createCell(j);
				obj = table.get(i).get(j);
				if(obj instanceof String)
					cell.setCellValue((String) obj);
				else if(obj instanceof Boolean)
					cell.setCellValue((Boolean) obj);
				else if(obj instanceof Date)
					cell.setCellValue((Date) obj);
				else if(obj instanceof Integer)
					cell.setCellValue((int) obj);
				else if(obj instanceof Double)
					cell.setCellValue((Double) obj);
			}
		}

		FileOutputStream os = new FileOutputStream(filePath);
		workBook.write(os);
		workBook.close();
		os.close();
	}
	
	@Deprecated
	public static void updateSpreadSheet(String filePath, Map<String, Object> data) throws IOException {
		Workbook workBook = new XSSFWorkbook(filePath);
		Sheet firstSheet = workBook.getSheetAt(0);
		Set<String> keys = data.keySet();
		String[] coords;
		Object obj;
		Cell cell;
		
		for(String key : keys) {
			obj = data.get(key);
			coords = key.split("/");
			cell = firstSheet.getRow(Integer.parseInt(coords[0])).getCell(Integer.parseInt(coords[1]));
			if(obj instanceof String)
				cell.setCellValue((String) obj);
			else if(obj instanceof Boolean)
				cell.setCellValue((Boolean) obj);
			else if(obj instanceof Date)
				cell.setCellValue((Date) obj);
			else if(obj instanceof Integer)
				cell.setCellValue((int) obj);
			else if(obj instanceof Double)
				cell.setCellValue((Double) obj);
		}
		
		workBook.cloneSheet(0);
		workBook.close();
	}
}