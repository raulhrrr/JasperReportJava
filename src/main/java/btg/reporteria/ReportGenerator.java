package btg.reporteria;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.Base64;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportGenerator {

    private String outFile = "/tmp/export.pdf";
    private Map<String, Object> parameters = null;
    private LambdaLogger logger;

    public void setParameter(String key, String value) {
        this.parameters.put(key, value);
    }
    
    public ReportGenerator(LambdaLogger logger) {
        this.logger = logger;
    }

    public String generateBase64EncodedReport() throws JRException, IOException, SQLException {
        try {
            File file = new File(this.outFile);
            byte[] encoded = Base64.encode(FileUtils.readFileToByteArray(file));
            return new String(encoded, StandardCharsets.US_ASCII);
        } catch (FileNotFoundException e) {
            logger.log("It was not possible to access the output file: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            logger.log("It was not possible to read and encode the report: " + e.getMessage());
            throw e;
        }
    }

    public void generateReport(InputStream logo, InputStream template, Connection connection) throws JRException, SQLException {

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(template);
         
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, this.parameters, connection);

        if ( jasperPrint != null && !connection.isClosed() ) {
            connection.close();
        }
        
        JasperExportManager.exportReportToPdfFile(jasperPrint, this.outFile);

        this.logger.log("File generated successfully");
    }
}
