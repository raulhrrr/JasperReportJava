package btg.reporteria;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;

import net.sf.jasperreports.engine.JRException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class LambdaFunctionHandler implements RequestStreamHandler {

    LambdaLogger logger;

    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        this.logger = context.getLogger();

        JSONObject responseJson = new JSONObject();

        try {

            JSONObject queryParameters = extractQueryStringParameters(inputStream);
            String ordeCodigo = (String) queryParameters.get((Object) "ordeCodigo");

            // dependes on SECRET_NAME, SECRET_REGION
            DatabaseCredentials credentials = new DatabaseCredentials(this.logger);
            credentials.buildCredentials();

            // depends on RDS_DB_DRIVER
            RDSConnector connector = new RDSConnector(this.logger);
            Connection connection = connector.connectToRDS(credentials);
            ResultSet resultSet = connector.getBeanList(connection, ordeCodigo);

            // depends on BUCKET_NAME
            AmazonS3Consumer s3Client = new AmazonS3Consumer(this.logger);
            InputStream logo = null;
            InputStream template = null;

            switch (ordeCodigo) {
                case "1":
                    logo = s3Client.retrieveObjectFromS3("logos/logo-banco.png");
                    template = s3Client.retrieveObjectFromS3("templates/comprobante.jasper");
                    break;
                case "2":
                    logo = s3Client.retrieveObjectFromS3("logos/escudo-banrep.jpg");
                    template = s3Client.retrieveObjectFromS3("templates/formulario-uno.jasper");
                    break;
                case "3":
                    logo = s3Client.retrieveObjectFromS3("logos/escudo-banrep.jpg");
                    template = s3Client.retrieveObjectFromS3("templates/formulario-dos.jasper");
                    break;
                case "4":
                    logo = s3Client.retrieveObjectFromS3("logos/escudo-banrep.jpg");
                    template = s3Client.retrieveObjectFromS3("templates/formulario-tres.jasper");
                    break;
            }

            ReportGenerator reportGenerator = new ReportGenerator(this.logger);
            String encodedReport = reportGenerator.generateBase64EncodedReport(logo, template, resultSet);

            s3Client.putObjectBucket("/tmp/export.pdf");
            
            buildSuccessfulResponse(encodedReport, responseJson);

        } catch (JRException ex) {
            this.buildErrorResponse(ex.getMessage(), 400, responseJson);
            this.logger.log("Error generando el comprobante " + ex.getMessage());
        } catch (IOException ex) {
            this.buildErrorResponse(ex.getMessage(), 500, responseJson);
            this.logger.log("Error generando el comprobante " + ex.getMessage());
        } catch (ParseException ex) {
            this.buildErrorResponse(ex.getMessage(), 500, responseJson);
            this.logger.log("Error generando el comprobante " + ex.getMessage());
        } catch (Exception ex) {
            this.buildErrorResponse(ex.getMessage(), 500, responseJson);
            this.logger.log("Error generando el comprobante " + ex.getMessage());
        }

        OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
        writer.write(responseJson.toString());
        writer.close();
    }

    public JSONObject extractQueryStringParameters(InputStream inputStream) throws ParseException, IOException {
        JSONParser parser = new JSONParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JSONObject queryParameters = null;
        try {
            JSONObject event = (JSONObject) parser.parse((Reader) reader);
            if (event.get("queryStringParameters") != null) {
                queryParameters = (JSONObject) event.get("queryStringParameters");
            }
            return queryParameters;
        } catch (ParseException e) {
            logger.log("Error when parsing query string parameters.");
            throw e;
        } catch (IOException e) {
            logger.log("Error extracting query string parameters.");
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public void buildSuccessfulResponse(String encodedReport, JSONObject responseJson) {
        JSONObject headerJson = new JSONObject();
        headerJson.put("Content-Type", "application/pdf");
        headerJson.put("Accept", "application/pdf");
        //headerJson.put("Content-disposition", "attachment; filename=file.pdf");
        responseJson.put("body", encodedReport);
        responseJson.put("statusCode", 200);
        responseJson.put("isBase64Encoded", true);
        responseJson.put("headers", headerJson);
    }

    @SuppressWarnings("unchecked")
    public void buildErrorResponse(String body, int statusCode, JSONObject responseJson) {
        responseJson.put("body", body);
        responseJson.put("statusCode", statusCode);
    }
}
