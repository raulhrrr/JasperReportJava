package btg.reporteria;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class RDSConnector
{
    private LambdaLogger logger;
    
    public RDSConnector(LambdaLogger logger) {
        this.logger = logger;
    }
    
    public Connection connectToRDS(DatabaseCredentials credentials) throws ClassNotFoundException, SQLException {
        try {
            Class.forName(System.getenv("RDS_DB_DRIVER"));
        }
        catch (ClassNotFoundException e) {
            this.logger.log("There was an error when loading the database driver:" + e.getMessage());
            throw e;
        }
        String jdbcUrl = this.buildJdbcUrl(credentials);
        Connection connection;
        try {
            connection = DriverManager.getConnection(jdbcUrl);
        }
        catch (SQLException e) {
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
    
    public ResultSet getBeanList(Connection connection, String ordeCodigo) throws SQLException {
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("drop table if exists COMPROBANTE");
            statement.executeUpdate("create table COMPROBANTE ( numComprobante int(10) not null, cliente varchar(50) null, direccion varchar(50) null, ciudad varchar(50) null, nitCed varchar(50) null, telefono varchar(50) null, codigo varchar(50) null, idPosicion varchar(50) null, calidadActua varchar(50) null, especie varchar(50) null, numeralCambiario varchar(50) null, descripcion varchar(250) null, fechaOperacion varchar(50) null, fechaCumplimiento varchar(50) null, nroRegistroBolsa varchar(50) null, divisa varchar(50) null, cantidad varchar(50) null, tasaUsd varchar(50) null, totalUsd varchar(50) null, cantBruta varchar(50) null, iva varchar(50) null, reteiva varchar(50) null, cantNeta varchar(50) null, cocSumValor varchar(50) null, cocSumIva varchar(50) null, cocSumTotal varchar(50) null, toTotalBruto varchar(50) null, toRetefuente varchar(50) null, toReteica varchar(50) null, toIva varchar(50) null, toReteiva varchar(50) null, toTotalNeto varchar(50) null, observaciones varchar(250) null, condicionesPago varchar(50) null, representanteLegal varchar(50) null, promotorNegocios varchar(50) null, PRIMARY KEY (numComprobante) );");
            statement.executeUpdate("insert into COMPROBANTE values ( 267675, 'TORREJAR VELASQUEZ Y CIA SAS', 'CR 43 A 1 85 OF 705 ED BANCO CAJA SOCIA', 'MEDELLIN', '900329389 - 8', '2662330', '2862', 'VENTA DE DIVISAS', 'POSICION PROPIA', 'TRANSFERENCIA', '2074', 'UTILIDADES, RENDIMIENTOS Y DIVIDENDOS DE LA INVERSIÓN DIRECTA DE CAPITALES EN EL EXTERIOR', '23/08/2021', '23/08/2021', '', 'USD', '120.000,00', '1,0000', '120.000,00', '465.060.000,00', '0,00', '0,00', '465.060.000,00', '150.000,00', '28.500,00', '178.500,00', '465.210.000,00', '0,00', '0,00', '28.500,00', '0,00', '465.238.500,00', 'Operación registrada en ET por FRL -', 'TRANSFERENCIA', 'PABLO GONZALEZ MORENO', 'SANTIAGO PEREZ BOTERO' );");
            
            resultSet = statement.executeQuery("select * from COMPROBANTE where numComprobante = " + ordeCodigo + ";");
            
            statement.executeUpdate("drop table COMPROBANTE");
            
        }
        catch (SQLException e) {
            this.logger.log("There was an error when executing the SQL: " + e.getMessage());
            throw e;
        }
        return resultSet;
    }
}