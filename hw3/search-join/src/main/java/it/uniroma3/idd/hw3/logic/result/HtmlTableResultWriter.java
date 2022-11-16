package it.uniroma3.idd.hw3.logic.result;

import it.uniroma3.idd.entity.CellVO;
import it.uniroma3.idd.entity.ColumnVO;
import it.uniroma3.idd.entity.TableVO;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class HtmlTableResultWriter {

    private static final Logger logger = Logger.getLogger(HtmlTableResultWriter.class.toString());

    private static final String OUTPUT_FOLDER = "./target/html/";

    public void appendResultTable(TableVO tableVO, List<Long> columnsToShade, Long timestamp) {
        File file = new File(OUTPUT_FOLDER + timestamp + ".html");

        StringBuilder htmlTable = new StringBuilder();
        htmlTable.append("<table style='border: 1px solid #999;'>");
        for(int i = 0;i<tableVO.getMaxNumRows();i++) {
            String rowsHtml = generateRow(tableVO.getColumns(), columnsToShade, i);
            htmlTable.append(rowsHtml);
        }
        htmlTable.append("</table><br><br>");

        try {
            FileUtils.writeStringToFile(file, htmlTable.toString(), "UTF-8", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateRow(Map<Integer,ColumnVO> columns, List<Long> columnsToShade, int rowNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("<tr>");
        for(Map.Entry<Integer,ColumnVO> entry: columns.entrySet()) {
            long colNum = entry.getKey().longValue();
            ColumnVO columnVO = entry.getValue();

            if(columnVO.getCells().containsKey(rowNumber)) {
                CellVO cellVO = columnVO.getCells().get(rowNumber);
                if(columnsToShade.contains(colNum)) {
                    sb.append("<td style='background-color: #aaa;'>").append(cellVO.getContent()).append("</td>");
                } else {
                    sb.append("<td>").append(cellVO.getContent()).append("</td>");
                }
            } else {
                if(columnsToShade.contains(colNum)) {
                    sb.append("<td style='background-color: #aaa;'>").append("</td>");
                } else {
                    sb.append("<td>").append("</td>");
                }
            }
        }
        sb.append("</tr>");
        return sb.toString();
    }

}
