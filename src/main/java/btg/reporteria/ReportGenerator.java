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
        // Get mock data

        Map<String, Object> parameters = null;

        //read jrxml file and creating jasperdesign object
        JasperDesign jasperDesign = JRXmlLoader.load(template);

        /*compiling jrxml with help of JasperReport class*/
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

        // Report parameters
        JRParameter[] templateParameters = jasperReport.getParameters();

        resultSet.next();

        switch (resultSet.getString("numComprobante")) {
            case "267675":
                parameters = mockDataComprobante(logo, resultSet, templateParameters);
                break;
            case "2":
                parameters = mockDataFormularioUno(logo);
                break;
            case "3":
                parameters = mockDataFormularioDos(logo);
                break;
            case "4":
                parameters = mockDataFormularioTres(logo);
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
                    default:
                        logger.log("Este tipo de dato no está implementado: " + jrParameter.getValueClassName());
                        logger.log("Nombre del parámetro: " + jrParameter.getName());
                }
            }
        }

        return parameters;
    }

    private Map<String, Object> mockDataFormularioUno(InputStream logo) {
        List<TableFormUno> listItems = new ArrayList<>();

        TableFormUno dc1 = new TableFormUno(1, "");
        //DeclaracionCambio dc2 = new TableFormUno(2341, "350.000,00");
        //DeclaracionCambio dc3 = new TableFormUno(3412, "200.000,00");
        //DeclaracionCambio dc4 = new TableFormUno(4123, "150.000,00");

        listItems.add(dc1);
        //listItems.add(dc2);
        //listItems.add(dc3);
        //listItems.add(dc4);

        JRBeanCollectionDataSource itemsJRBean = new JRBeanCollectionDataSource(listItems);
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("logoBanRep", logo);
        parameters.put("tipoOperacion", "1 - INICIAL");
        parameters.put("idiCuentaCompensacion", "890907157 - 0 BTGPactual Colombia");
        parameters.put("idiFecha", "2020/03/03");
        parameters.put("idiNumero", "48757");
        parameters.put("idcaCuentaCompensacion", "");
        parameters.put("idcaFecha", "");
        parameters.put("idcaNumero", "");
        parameters.put("ipTipo", "NI");
        parameters.put("ipNumeroIdentificacion", "860032402");
        parameters.put("ipDV", "5");
        parameters.put("ipRazonSocial", "INDUSTRIAS BUFALO S.A.S");
        parameters.put("doMoneda1", "USD");
        parameters.put("doMoneda2", "");
        parameters.put("doTipoCambio1", "1,00");
        parameters.put("doTipoCambio2", "0,00");
        parameters.put("doNumeral1", "2017");
        parameters.put("doNumeral2", "");
        parameters.put("doValorMoneda1", "5.058,00");
        parameters.put("doValorMoneda2", "0,00");
        parameters.put("doValorUsd1", "5.058,00");
        parameters.put("doValorUsd2", "0,00");
        parameters.put("idNombre", "JORGE PALLINI FERNANDEZ");
        parameters.put("idNumeroIdentificacion", "8046028");
        parameters.put("idFirma", "");
        parameters.put("observaciones", "");
        parameters.put("CollectionBeanParam", itemsJRBean);

        return parameters;
    }

    private static Map<String, Object> mockDataFormularioDos(InputStream logo) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("logoBanRep", logo);
        parameters.put("tipoOperacion", "1");
        parameters.put("idiCuentaCompensacion", "890907157 BTGPactual Colombia");
        parameters.put("idiFecha", "2020-03-03");
        parameters.put("idiNumero", "48761");
        parameters.put("idcaCuentaCompensacion", "");
        parameters.put("idcaFecha", "");
        parameters.put("idcaNumero", "");
        parameters.put("ieTipo", "NI");
        parameters.put("ieNumeroIdentificacion", "901148636");
        parameters.put("ieDV", "9");
        parameters.put("ieRazonSocial", "AGRO GAIRA SAS");
        parameters.put("doCodMonedaReintegro", "USD");
        parameters.put("doValorMonedaReintegro", "15.036,00");
        parameters.put("doTipoCambioUsd", "1,00");
        parameters.put("doNumeral", "1040");
        parameters.put("doValorReintegrado", "15.036,00");
        parameters.put("doTotalValorFob", "15.036,00");
        parameters.put("doTotalGastosExport", "0,00");
        parameters.put("doDeducciones", "0,00");
        parameters.put("doReintegroNeto", "15.036,00");
        parameters.put("idNombre", "CATALINA URIBE GOMEZ");
        parameters.put("idNumeroIdentificacion", "42826138");
        parameters.put("idFirma", "");
        parameters.put("observaciones", "");

        return parameters;
    }

    private static Map<String, Object> mockDataFormularioTres(InputStream logo) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("logoBanRep", logo);
        parameters.put("tipoOperacion", "1");
        parameters.put("tipoOperacionIE", "E");
        parameters.put("idiCiudad", "MEDELLIN");
        parameters.put("idiCuentaCompensacion", "890907157 BTGPactual Colombia");
        parameters.put("idiFecha", "2020-03-17");
        parameters.put("idiNumero", "49337");
        parameters.put("idcaCuentaCompensacion", "");
        parameters.put("idcaFecha", "");
        parameters.put("idcaNumero", "");
        parameters.put("ioNumPrestamo1", "02");
        parameters.put("ioNumPrestamo2", "007");
        parameters.put("ioNumPrestamo3", "103837");
        parameters.put("ioTipo", "CC");
        parameters.put("ioNumeroIdentificacion", "79557635");
        parameters.put("ioDV", "");
        parameters.put("ioNombreResidente", "SANCHEZ BENITEZ PEDRO EMILIO");
        parameters.put("ioMonContrat", "USD");
        parameters.put("ioValMonContrat", "669,00");
        parameters.put("ioMonNeg", "USD");
        parameters.put("ioValMonNeg", "669,00");
        parameters.put("ioTipoCambioUsd", "1,00");
        parameters.put("ioValTotalDolares", "669,00");
        parameters.put("ioNombreAcreDeu", "BANCO SABADELL MIAMI");
        parameters.put("inliNumeral", "2135");
        parameters.put("inliVrMonNeg", "669,00");
        parameters.put("inliVrMonCo", "669,00");
        parameters.put("inliVrUsd", "669,00");
        parameters.put("idNombre", "SERGIO PEDRO SANCHEZ LAVERDE");
        parameters.put("idNumeroIdentificacion", "80503903");
        parameters.put("idFirma", "");

        return parameters;
    }
}
