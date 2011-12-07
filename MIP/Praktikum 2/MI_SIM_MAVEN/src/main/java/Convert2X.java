

/**
 * ToDo:
 * - Apache POI entlocken, wie man Charts erstellt (kann es, allerdings noch im Beta Stadium und deshalb gar nicht (bzw. sehr schlecht) dokumentiert) * 
 */


// Java Libs
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.csvreader.CsvWriter;

public class Convert2X implements Names{
	
	// quantity of layer
	private static int quanLayer = 4;

	// head titles
    private static String[] titles = {"Turn/Zeit", EB_SECURITY, EB_FUN, EB_SOZIOLOGY, EB_PRODUCTIVITY};
    
	public static void Convert2Excel(List<Map<String,Double>> data, String filename) throws Exception
	{		
		// Create new Workbook with Excel 2007 format
		Workbook wb = new XSSFWorkbook();

		// Create new sheet for data
		Sheet sheet = wb.createSheet("Data");

        // Header row
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(titles[i]);
        }
        
        // Copy data into sheet
        Row row;
        Cell cell;
        int rownum = 1;
        for(int i = 0; i < data.size(); i++, rownum++)
        {
            row = sheet.createRow(rownum);
            if(data.get(i) == null) continue; 
            
            for (int j = 0; j < quanLayer + 1; j++)
            {
            	double value = 0;
            	switch(j)
            	{
            	    case 0:
            	    	value = i + 1.0;
            	    	break;
            	    case 1:
            	    	value = data.get(i).get(EB_SECURITY);
            	    	break;
            	    case 2:
            	    	value = data.get(i).get(EB_FUN);
            	    	break;
            	    case 3:
            	    	value = data.get(i).get(EB_SOZIOLOGY);
            	    	break;
            	    case 4:
            	    	value = data.get(i).get(EB_PRODUCTIVITY);
            	    	break;
            	}
                cell = row.createCell(j);
                cell.setCellValue(value);
            }
        }
        
        // Write the output to a file
        FileOutputStream out = new FileOutputStream(filename);
        wb.write(out);
        out.close();        
	}
	
	public static void Convert2CSV(List<Map<String,Double>> data, String filename) throws Exception
	{		
		CsvWriter writer = new CsvWriter(filename, ',', Charset.forName("ISO-8859-1"));
		
		// Header row
        for (int i = 0; i < titles.length; i++) {
        	writer.write(titles[i]);
        }
        writer.endRecord();
        
        // copy data
        for(int i = 0; i < data.size(); i++)
        {
            if(data.get(i) == null) continue; 
            
            for (int j = 0; j < quanLayer + 1; j++)
            {
            	double value = 0;
            	switch(j)
            	{
            	    case 0:
            	    	value = i + 1.0;
            	    	break;
            	    case 1:
            	    	value = data.get(i).get(EB_SECURITY);
            	    	break;
            	    case 2:
            	    	value = data.get(i).get(EB_FUN);
            	    	break;
            	    case 3:
            	    	value = data.get(i).get(EB_SOZIOLOGY);
            	    	break;
            	    case 4:
            	    	value = data.get(i).get(EB_PRODUCTIVITY);
            	    	break;
            	}
            	
                writer.write(String.valueOf(value));
            }
            
            // new line
            writer.endRecord();
        }
        
        // save csv data
     	writer.close();
	}
}
