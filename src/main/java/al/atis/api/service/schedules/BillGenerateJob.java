package al.atis.api.service.schedules;

import al.atis.supermarket.model.Bill;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Configuration
public class BillGenerateJob {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<Bill> bills;

    @Autowired
    EntityManager entityManager;

    //runs every day in 00:00
//    @Scheduled(cron = "0 * * ? * *")
    @Scheduled(cron = "0 0 0 ? * *")
    public void generateReportJob() throws IOException {
        workbook = new XSSFWorkbook();

        bills = getBills();

        for (Bill bill : bills) {
            logger.info(String.format("bill id: %s created date: %s",bill.getUuid(),bill.getCreated_date()));
        }
        LocalDateTime currentDateTime = getCurrentDateTime();
        OutputStream fileOut = new FileOutputStream("/home/eraldo/Projects/Project 1 " + "- " +
                "supermarket/supermarket/src/main/java/al/atis/api/service/timer/reports/" + currentDateTime + ".xlsx");

        sheet = workbook.createSheet("Bill report ");
        export(sheet);
        logger.info("Sheets Has been Created successfully");
        workbook.write(fileOut);
        workbook.close();
    }

    public void export(Sheet sheet) throws IOException {
        writeHeaderLine(sheet);
        writeDataLines();
    }

    private void writeHeaderLine(Sheet sheet) {
        Row row = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);

        createCell(row, 0, "Bill Id", style);
        createCell(row, 1, "Price", style);
        createCell(row, 2, "Created Date", style);
        createCell(row, 3, "Created By", style);

    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(13);
        style.setFont(font);

        int totalPrice = 0;
        for (Bill bill : bills) {
            logger.info(bill.getUuid());
            int columnCount = 0;
            sheet.autoSizeColumn(columnCount);
            Row row = sheet.createRow(rowCount++);
            totalPrice += bill.getTotal_price();

            createCell(row, columnCount++, bill.getUuid(), style);
            createCell(row, columnCount++, bill.getTotal_price().toString(), style);
            createCell(row, columnCount++, bill.getCreated_date().toString(), style);
            createCell(row, columnCount++, bill.getCreated_by(), style);
        }
        CellStyle style2 = workbook.createCellStyle();
        XSSFFont font2 = workbook.createFont();
        font2.setColor(Font.COLOR_RED);
        font2.setFontHeight(14);
        font2.setBold(true);
        style2.setFont(font2);

        Row row = sheet.createRow(1 + rowCount++);
        createCell(row, 0, "Total Price: ", style2);
        createCell(row, 1, totalPrice, style2);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }


    private List<Bill> getBills() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Bill> criteriaQuery = criteriaBuilder.createQuery(Bill.class);
        Root<Bill> root = criteriaQuery.from(Bill.class);
        criteriaQuery.select(root);

        LocalDateTime startDateTime = getStartCurrentDateTime();
        LocalDateTime endDateTime = getEndCurrentDateTime();

        criteriaQuery.select(root).where(criteriaBuilder.between(root.get("created_date"),
                startDateTime, endDateTime));
        TypedQuery<Bill> search = entityManager.createQuery(criteriaQuery);

        return search.getResultList();
    }

    private LocalDateTime getCurrentDateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        return dateTime;
    }

    private LocalDateTime getStartCurrentDateTime() {
        LocalDate date = LocalDate.now();
        return date.atStartOfDay();
    }

    private LocalDateTime getEndCurrentDateTime() {
        LocalDate date = LocalDate.now();
        return date.atTime(LocalTime.MAX);
    }

}
