package com.adiacent.menarini.menarinimaster.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.HistoryItem;
import com.adobe.granite.workflow.exec.WorkItem;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.AssetManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EFPIAXLSXLogger {


    private final String siteName;

    private String userId;
    private Session jcrSession;
    private Node jcrNode;

    private static final String ROOT_LOG_PATH_PATTERN = "/content/dam/efpia/{0}/log";

    private static int IDX_YEAR = 0;
    private static int IDX_VERSION = 1;
    private static int IDX_STATUS = 2;
    private static int IDX_NOTES = 3;
    private static int IDX_DATE_CREATED = 4;
    private static int IDX_CREATOR = 5;
    private static int IDX_DATE_APPROVED = 6;
    private static int IDX_APPROVER = 7;
    private static int IDX_DATE_REJECTED = 8;
    private static int IDX_REJECTER = 9;
    private static int IDX_REJECT_NOTES = 10;

    public static EFPIAXLSXLogger getLogger(String siteName, WorkItem workItem, WorkflowSession workflowSession) throws RepositoryException, WorkflowException, PersistenceException {
        EFPIAXLSXLogger logger = new EFPIAXLSXLogger(siteName);
        logger.jcrSession =  workflowSession.adaptTo(Session.class);
        List<HistoryItem> history = workflowSession.getHistory(workItem.getWorkflow());
        logger.userId = "nobody";
        if (workItem.getWorkflowData().getMetaDataMap() != null)
            if (workItem.getWorkflowData().getMetaDataMap().get("userId") != null)
                logger.userId = workItem.getWorkflowData().getMetaDataMap().get("userId").toString();
        if (history != null && !history.isEmpty()) {
            for (int i = history.size()-1; i >= 0; i--) {
                HistoryItem hi = history.get(i);
                if ("WorkflowCompleted".equals(hi.getAction())) {
                    logger.userId = hi.getUserId();
                    break;
                }
            }
        }
        //String instancePath = StringUtils.substringBeforeLast(StringUtils.substringAfter(workItem.getWorkflow().getId(), "/var/workflow/instances"), "/");
        String logFileName = "EFPIA_ReportHeaders.xlsx";
                //StringUtils.substringAfterLast(workItem.getWorkflow().getId(), "/") + ".log";
        String path = MessageFormat.format(ROOT_LOG_PATH_PATTERN, siteName);
        String[] nodeNames = StringUtils.split(path, "/");
        path = "";
        for (String nodeName : nodeNames) {
            path += "/" + nodeName;
            if (!logger.jcrSession.nodeExists(path)) {
                JcrUtil.createPath(path, "nt:folder", logger.jcrSession);
            }
        }
        path += "/" + logFileName;

        ResourceResolver resourceResolver = workflowSession.adaptTo(ResourceResolver.class);
        AssetManager assetManager = resourceResolver.adaptTo(AssetManager.class);
        if (!logger.jcrSession.nodeExists(path)) {
            byte[] data = new byte[0];
            InputStream is = new ByteArrayInputStream(data);

            final ValueFactory valueFactory = logger.jcrSession.getValueFactory();
            final Binary binary = valueFactory.createBinary(is);

            Asset fileAsset = assetManager.createOrReplaceAsset(path, binary, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", true);

            //Node fileNode = fileAsset.adaptTo(Node.class);

                    /*JcrUtil.createPath(path, "nt:file", logger.jcrSession);
            logger.jcrNode = fileNode.addNode("jcr:content", "nt:resource");
            logger.jcrNode.setProperty("jcr:mimeType", "text/plain");
            logger.jcrNode.setProperty("jcr:encoding", "utf-8");*/
            resourceResolver.commit();
        } //else {
        Resource r = resourceResolver.getResource(path + "/jcr:content/renditions/original/jcr:content");
        if (r != null)
            logger.jcrNode = r.adaptTo(Node.class);
        //}
        return logger;
    }

    public EFPIAXLSXLogger(String siteName) {
        this.siteName = siteName;
    }

    private Workbook read() {
        if (jcrNode != null) {
            try {
                Value value = null;
                byte[] fileContent = new byte[0];
                try {
                    Property data = jcrNode.getProperty("jcr:data");
                    if (data != null) {
                        value = (Value) data.getValue();
                        InputStream is = value.getBinary().getStream();
                        fileContent = new byte[is.available()];
                        is.read(fileContent);
                    }
                } catch (RepositoryException e) {
                }
                Workbook res = null;
                if (fileContent.length == 0) {
                    res = new XSSFWorkbook();
                    Sheet sheet = res.createSheet("ReportHeaders");
                    Row header = sheet.createRow(0);
                    //
                    header.createCell(IDX_YEAR).setCellValue("Year");
                    header.createCell(IDX_VERSION).setCellValue("Version");
                    header.createCell(IDX_STATUS).setCellValue("Status");
                    header.createCell(IDX_NOTES).setCellValue("Note");
                    header.createCell(IDX_DATE_CREATED).setCellValue("CreatedOnDate");
                    header.createCell(IDX_CREATOR).setCellValue("CreatedByUser");
                    header.createCell(IDX_DATE_APPROVED).setCellValue("ApprovedOnDate");
                    header.createCell(IDX_APPROVER).setCellValue("ApprovedByUser");
                    header.createCell(IDX_DATE_REJECTED).setCellValue("RejectedOnDate");
                    header.createCell(IDX_REJECTER).setCellValue("RejectedByUser");
                    header.createCell(IDX_REJECT_NOTES).setCellValue("RejectedNote");
                } else {
                    res = new XSSFWorkbook(new ByteArrayInputStream(fileContent));
                }
                return res;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }

    private void write(Workbook wb) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            wb.write(bos);
            wb.close();
            byte[] fileContent = bos.toByteArray();
            Value value = null;
            ValueFactory factory = jcrSession.getValueFactory();
            Binary binary = factory.createBinary(new ByteArrayInputStream(fileContent));
            value = factory.createValue(binary);
            jcrNode.setProperty("jcr:data", value);
            binary.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public double submitForApproval(Integer year, String note) {
        Workbook wb = this.read();
        Sheet sheet = wb.getSheetAt(0);
        int rowIndex = 1;
        double version = 0;
        Row row;
        while ((row = sheet.getRow(rowIndex)) != null) {
            if (row.getCell(IDX_VERSION).getNumericCellValue() > version) {
                version = row.getCell(1).getNumericCellValue();
            }
            rowIndex++;
        }
        version++;

        row = sheet.createRow(rowIndex);
        row.createCell(IDX_YEAR).setCellValue(year);
        row.createCell(IDX_VERSION).setCellValue(version);
        row.createCell(IDX_STATUS).setCellValue("DRAFT");
        row.createCell(IDX_NOTES).setCellValue(note);
        row.createCell(IDX_DATE_CREATED).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        row.createCell(IDX_CREATOR).setCellValue(this.userId);


        this.write(wb);

        return version;
    }

    public void approve(Integer year, Double version, String note) {
        Workbook wb = this.read();
        Sheet sheet = wb.getSheetAt(0);
        int rowIndex = 1;
        Row row;
        while ((row = sheet.getRow(rowIndex)) != null) {
            if (row.getCell(IDX_YEAR).getNumericCellValue() == (double)year && "APPROVED".equals(row.getCell(IDX_STATUS).getStringCellValue())) {
                row.getCell(IDX_STATUS).setCellValue("OUTDATED");
            }
            if (row.getCell(IDX_YEAR).getNumericCellValue() == (double)year && row.getCell(IDX_VERSION).getNumericCellValue() == version)
                break;
            rowIndex++;
        }

        row.getCell(IDX_STATUS).setCellValue("APPROVED");
        row.createCell(IDX_DATE_APPROVED).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        row.createCell(IDX_APPROVER).setCellValue(this.userId);
        row.createCell(IDX_NOTES).setCellValue(note);


        this.write(wb);
    }

    public void reject(Integer year, Double version, String note) {
        Workbook wb = this.read();
        Sheet sheet = wb.getSheetAt(0);
        int rowIndex = 1;
        Row row;
        while ((row = sheet.getRow(rowIndex)) != null) {
            if (row.getCell(IDX_YEAR).getNumericCellValue() == (double)year && row.getCell(IDX_VERSION).getNumericCellValue() == version)
                break;
            rowIndex++;
        }

        row.getCell(IDX_STATUS).setCellValue("REJECTED");
        row.createCell(IDX_DATE_REJECTED).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        row.createCell(IDX_REJECTER).setCellValue(this.userId);
        row.createCell(IDX_REJECT_NOTES).setCellValue(note);


        this.write(wb);
    }

}
