package btg.reporteria;

import java.io.InputStream;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.File;
import java.nio.file.Paths;

public class AmazonS3Consumer {

    private LambdaLogger logger;
    private AmazonS3 s3Client;

    public AmazonS3Consumer(LambdaLogger logger) {
        this.logger = logger;
        s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(System.getenv("SECRET_REGION"))
                    .build();
    }

    public InputStream retrieveObjectFromS3(String key) {
        S3Object object = null;
        try {
            // Get an object and return InputStream
            object = this.s3Client.getObject(new GetObjectRequest(System.getenv("BUCKET_NAME"), key));
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            this.logger.log("Error obteniendo el template " + e.getMessage());
            throw e;
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            this.logger.log("Error obteniendo el template " + e.getMessage());
            throw e;
        }

        return object.getObjectContent();
    }

    public void putObjectBucket(String filePath) {
        try {

            String keyName = Paths.get(filePath).getFileName().toString();

            this.s3Client.putObject(System.getenv("BUCKET_NAME"), keyName, new File(filePath));

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process 
            // it, so it returned an error response.
            this.logger.log("Error obteniendo el template " + e.getMessage());
            throw e;
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            this.logger.log("Error obteniendo el template " + e.getMessage());
            throw e;
        }

    }
}