package al.atis.supermarket.service.rs;

import al.atis.api.service.RsRepositoryService;
import al.atis.supermarket.model.Bill;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.hibernate.Session;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.security.Principal;
import java.util.List;

import static al.atis.supermarket.managment.AppConstants.BILLS_PATH;

@RestController
@RequestMapping(BILLS_PATH)
public class BillController extends RsRepositoryService<Bill, String> {

    public BillController() {
        super(Bill.class);
    }

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected String getDefaultOrderBy() {
        return "created_date desc";
    }

    @Override
    public void applyFilters() throws Exception {

        if (notNull("gt.total_price")) {
            getEntityManager().unwrap(Session.class).enableFilter("gt.total_price").setParameter("total_price", getUiDoubleValue("gt.total_price"));
        }

        if (notNull("lt.total_price")) {
            getEntityManager().unwrap(Session.class).enableFilter("lt.total_price").setParameter("total_price", getUiDoubleValue("lt.total_price"));
        }

        if (notNull("from.created_date")) {
            getEntityManager().unwrap(Session.class).enableFilter("from.created_date").setParameter("created_date", getUiDateTimeValue("from.created_date"));
        }

        if (notNull("to.created_date")) {
            getEntityManager().unwrap(Session.class).enableFilter("to.created_date").setParameter("created_date", getUiDateTimeValue("to.created_date"));
        }

        if (notNull("like.created_by")) {
            getEntityManager().unwrap(Session.class).enableFilter("like.created_by").setParameter("created_by", likeParamToLowerCase("like.created_by"));
        }

        KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        Principal principal = (Principal) authentication.getPrincipal();
        KeycloakPrincipal kPrincipal = (KeycloakPrincipal) principal;
        String name = kPrincipal.getKeycloakSecurityContext().getToken().getPreferredUsername();

        boolean hasUserRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("ROLE_cashier"));
        logger.info("username: " + name);
        if (authentication != null && hasUserRole) {
            getEntityManager().unwrap(Session.class).enableFilter("obj.created_by").setParameter("created_by", name);
        }
    }

    @Override
    protected void prePersist(Bill object) throws Exception {
    }

    @Override
    protected void postPersist(Bill bill) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStream outputStream = new FileOutputStream("/home/eraldo/Projects/Project 1 - " +
                "supermarket/supermarket/src/main/java/al/atis/api/service/schedules/bills/pdf_"+bill.getUuid()+"report.pdf");
        try {
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(60);
            table.setWidths(new int[]{1, 3, 3});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);

            PdfPCell hcell;
            hcell = new PdfPCell(new Phrase("Id", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Created By", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Created Date ", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Total Price", headFont));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            PdfPCell cell;

            cell = new PdfPCell(new Phrase(bill.getUuid()));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(bill.getCreated_by()));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(bill.getCreated_date())));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPaddingRight(5);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(bill.getTotal_price())));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell.setPaddingRight(5);
            table.addCell(cell);
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(table);
            outputStream.write(out.toByteArray());
            document.close();
            logger.info("sas");
        } catch (DocumentException ex) {
            logger.error("Error occurred: {0}", ex);
        }


    }

    @Override
    protected void postList(List<Bill> list) throws Exception {
        super.postList(list);
    }
}
