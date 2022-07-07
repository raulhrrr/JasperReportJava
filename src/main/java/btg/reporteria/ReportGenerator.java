package btg.reporteria;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.io.FileUtils;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.util.Base64;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class ReportGenerator {

    static final String outFile = "/tmp/export.pdf";

    private LambdaLogger logger;

    public ReportGenerator(LambdaLogger logger) {
        this.logger = logger;
    }

    public String generateBase64EncodedReport(InputStream logo, InputStream template, ResultSet resultSet) throws JRException, IOException, SQLException {
        try {
            File file = new File(outFile);
            // String outputStream = outFile;
            OutputStream outputStream = new FileOutputStream(file);
            generateReport(logo, template, outputStream, resultSet);
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

    public void generateReport(InputStream logo, InputStream template, OutputStream outputStream, ResultSet resultSet) throws JRException, SQLException {

        // JasperDesign jasperDesign = JRXmlLoader.load(template);

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(template);
                // JasperCompileManager.compileReport(jasperDesign);

        JRParameter[] templateParameters = jasperReport.getParameters();
        
        Map<String, Object> parameters = null;

        resultSet.next();

        switch (resultSet.getString("numComprobante")) {
            case "1":
                parameters = mockDataComprobante(logo, resultSet, templateParameters);
                break;
            case "2":
                parameters = mockDataFormularioUno(logo, resultSet, templateParameters);
                break;
            case "3":
                parameters = mockDataFormularioDos(logo, resultSet, templateParameters);
                break;
            case "4":
                parameters = mockDataFormularioTres(logo, resultSet, templateParameters);
                break;
        }

        /* Using jasperReport object to generate PDF */
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);

        this.logger.log("File generated successfully");
    }

    private Map<String, Object> mockDataComprobante(InputStream logo, ResultSet resultSet, JRParameter[] templateParameters) throws SQLException {
        List<TableComprobante> listItems = new ArrayList<>();

        TableComprobante coc1 = new TableComprobante("COSTO TRANSFERENCIA AL EXTERIOR", "150.000,00", "28.500,00", "178.500,00");

        listItems.add(coc1);

        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(listItems);
        Map<String, Object> parameters = new HashMap<>();

        for (JRParameter jrParameter : templateParameters) {
            if (!jrParameter.isSystemDefined()) {
                switch (jrParameter.getValueClassName()) {
                    case "java.lang.String":
                        parameters.put(jrParameter.getName(), resultSet.getString(jrParameter.getName()));
                        break;
                    case "net.sf.jasperreports.engine.data.JRBeanCollectionDataSource":
                        parameters.put(jrParameter.getName(), itemsJRBean);
                        break;
                    case "java.io.InputStream":
                        parameters.put(jrParameter.getName(), logo);
                        break;
                    default:
                        logger.log("Este tipo de dato no está implementado: " + jrParameter.getValueClassName());
                        logger.log("Nombre del parámetro: " + jrParameter.getName());
                }
            }
        }

        return parameters;
    }

    private Map<String, Object> mockDataFormularioUno(InputStream logo, ResultSet resultSet, JRParameter[] templateParameters) throws SQLException {
        List<TableFormUno> listItems = new ArrayList<>();

        TableFormUno dc1 = new TableFormUno(1, "");

        listItems.add(dc1);

        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(listItems);
        Map<String, Object> parameters = new HashMap<>();

        for (JRParameter jrParameter : templateParameters) {
            if (!jrParameter.isSystemDefined()) {
                switch (jrParameter.getValueClassName()) {
                    case "java.lang.String":
                        parameters.put(jrParameter.getName(), resultSet.getString(jrParameter.getName()));
                        break;
                    case "net.sf.jasperreports.engine.data.JRBeanCollectionDataSource":
                        parameters.put(jrParameter.getName(), itemsJRBean);
                        break;
                    case "java.io.InputStream":
                        parameters.put(jrParameter.getName(), logo);
                        break;
                    default:
                        logger.log("Este tipo de dato no está implementado: " + jrParameter.getValueClassName());
                        logger.log("Nombre del parámetro: " + jrParameter.getName());
                }
            }
        }

        return parameters;
    }

    private Map<String, Object> mockDataFormularioDos(InputStream logo, ResultSet resultSet, JRParameter[] templateParameters) throws SQLException {
        Map<String, Object> parameters = new HashMap<>();

        for (JRParameter jrParameter : templateParameters) {
            if (!jrParameter.isSystemDefined()) {
                switch (jrParameter.getValueClassName()) {
                    case "java.lang.String":
                        parameters.put(jrParameter.getName(), resultSet.getString(jrParameter.getName()));
                        break;
                    case "java.io.InputStream":
                        parameters.put(jrParameter.getName(), logo);
                        break;
                    default:
                        logger.log("Este tipo de dato no está implementado: " + jrParameter.getValueClassName());
                        logger.log("Nombre del parámetro: " + jrParameter.getName());
                }
            }
        }

        return parameters;
    }

    private Map<String, Object> mockDataFormularioTres(InputStream logo, ResultSet resultSet, JRParameter[] templateParameters) throws SQLException {
        Map<String, Object> parameters = new HashMap<>();

        for (JRParameter jrParameter : templateParameters) {
            if (!jrParameter.isSystemDefined()) {
                switch (jrParameter.getValueClassName()) {
                    case "java.lang.String":
                        parameters.put(jrParameter.getName(), resultSet.getString(jrParameter.getName()));
                        break;
                    case "java.io.InputStream":
                        parameters.put(jrParameter.getName(), logo);
                        break;
                    default:
                        logger.log("Este tipo de dato no está implementado: " + jrParameter.getValueClassName());
                        logger.log("Nombre del parámetro: " + jrParameter.getName());
                }
            }
        }

        return parameters;
    }
}
