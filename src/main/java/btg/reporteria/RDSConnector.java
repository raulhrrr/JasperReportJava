package btg.reporteria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class RDSConnector {

    private LambdaLogger logger;

    public RDSConnector(LambdaLogger logger) {
        this.logger = logger;
    }

    public Connection connectToRDS(DatabaseCredentials credentials) throws ClassNotFoundException, SQLException {
        try {
            Class.forName(System.getenv("RDS_DB_DRIVER"));
        } catch (ClassNotFoundException e) {
            this.logger.log("There was an error when loading the database driver:" + e.getMessage());
            throw e;
        }
        String jdbcUrl = this.buildJdbcUrl(credentials);
        Connection connection;
        try {
            connection = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            this.logger.log("There was an error when creating the connection: " + e.getMessage());
            throw e;
        }
        return connection;
    }

    public String buildJdbcUrl(DatabaseCredentials credentials) {
        String dbName = credentials.getDbname();
        String userName = credentials.getUsername();
        String password = credentials.getPassword();
        String hostname = credentials.getHost();
        String port = credentials.getPort();
        return "jdbc:mariadb://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
    }
}
