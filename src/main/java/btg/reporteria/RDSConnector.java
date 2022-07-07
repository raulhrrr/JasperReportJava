package btg.reporteria;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
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

    public ResultSet getBeanList(Connection connection, String ordeCodigo) throws SQLException {
        ResultSet resultSet = null;
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("drop table if exists REPORTE");

            switch (ordeCodigo) {
                case "1":
                    statement.executeUpdate("create table REPORTE ( numComprobante int(10) not null, cliente varchar(50) null, direccion varchar(50) null, ciudad varchar(50) null, nitCed varchar(50) null, telefono varchar(50) null, codigo varchar(50) null, idPosicion varchar(50) null, calidadActua varchar(50) null, especie varchar(50) null, numeralCambiario varchar(50) null, descripcion varchar(250) null, fechaOperacion varchar(50) null, fechaCumplimiento varchar(50) null, nroRegistroBolsa varchar(50) null, divisa varchar(50) null, cantidad varchar(50) null, tasaUsd varchar(50) null, totalUsd varchar(50) null, cantBruta varchar(50) null, iva varchar(50) null, reteiva varchar(50) null, cantNeta varchar(50) null, tasaBruta varchar(50), tasaNeta varchar(50), cocSumValor varchar(50) null, cocSumIva varchar(50) null, cocSumTotal varchar(50) null, toTotalBruto varchar(50) null, toRetefuente varchar(50) null, toReteica varchar(50) null, toIva varchar(50) null, toReteiva varchar(50) null, toTotalNeto varchar(50) null, observaciones varchar(250) null, condicionesPago varchar(50) null, representanteLegal varchar(50) null, promotorNegocios varchar(50) null, PRIMARY KEY (numComprobante) );");
                    statement.executeUpdate("insert into REPORTE values ( 1, 'TORREJAR VELASQUEZ Y CIA SAS', 'CR 43 A 1 85 OF 705 ED BANCO CAJA SOCIA', 'MEDELLIN', '900329389 - 8', '2662330', '2862', 'VENTA DE DIVISAS', 'POSICION PROPIA', 'TRANSFERENCIA', '2074', 'UTILIDADES, RENDIMIENTOS Y DIVIDENDOS DE LA INVERSIÓN DIRECTA DE CAPITALES EN EL EXTERIOR', '23/08/2021', '23/08/2021', '', 'USD', '120.000,00', '1,0000', '120.000,00', '465.060.000,00', '0,00', '0,00', '465.060.000,00', '3.000,00' , '3.000,00' ,'150.000,00', '28.500,00', '178.500,00', '465.210.000,00', '0,00', '0,00', '28.500,00', '0,00', '465.238.500,00', 'Operación registrada en ET por FRL -', 'TRANSFERENCIA', 'PABLO GONZALEZ MORENO', 'SANTIAGO PEREZ BOTERO' );");
                    break;
                case "2":
                    statement.executeUpdate("create table REPORTE ( numComprobante int(10) not null, tipoOperacion varchar(50) null, idiCuentaCompensacion varchar(50) null, idiFecha varchar(50) null, idiNumero varchar(50) null, idcaCuentaCompensacion varchar(50) null, idcaFecha varchar(50) null, idcaNumero varchar(50) null, ipTipo varchar(50) null, ipNumeroIdentificacion varchar(50) null, ipDV varchar(50) null, ipRazonSocial varchar(50) null, doMoneda1 varchar(50) null, doMoneda2 varchar(50) null, doTipoCambio1 varchar(50) null, doTipoCambio2 varchar(50) null, doNumeral1 varchar(50) null, doNumeral2 varchar(50) null, doValorMoneda1 varchar(50) null, doValorMoneda2 varchar(50) null, doValorUsd1 varchar(50) null, doValorUsd2 varchar(50) null, idNombre varchar(50) null, idNumeroIdentificacion varchar(50) null, idFirma varchar(50) null, observaciones varchar(50) null, PRIMARY KEY (numComprobante) );");
                    statement.executeUpdate("insert into REPORTE values ( 2, '1 - INICIAL', '890907157 - 0 BTGPactual Colombia', '2020/03/03', '48757', '', '', '', 'NI', '860032402', '5', 'INDUSTRIAS BUFALO S.A.S', 'USD', '', '1,00', '0,00', '2017', '', '5.058,00', '0,00', '5.058,00', '0,00', 'JORGE PALLINI FERNANDEZ', '8046028', '', '' )");
                    break;
                case "3":
                    statement.executeUpdate("create table REPORTE ( numComprobante int(10) not null, tipoOperacion varchar(50) null, idiCuentaCompensacion varchar(50) null, idiFecha varchar(50) null, idiNumero varchar(50) null, idcaCuentaCompensacion varchar(50) null, idcaFecha varchar(50) null, idcaNumero varchar(50) null, ieTipo varchar(50) null, ieNumeroIdentificacion varchar(50) null, ieDV varchar(50) null, ieRazonSocial varchar(50) null, doCodMonedaReintegro varchar(50) null, doValorMonedaReintegro varchar(50) null, doTipoCambioUsd varchar(50) null, doNumeral varchar(50) null, doValorReintegrado varchar(50) null, doTotalValorFob varchar(50) null, doTotalGastosExport varchar(50) null, doDeducciones varchar(50) null, doReintegroNeto varchar(50) null, idNombre varchar(50) null, idNumeroIdentificacion varchar(50) null, idFirma varchar(50) null, observaciones varchar(50) null, PRIMARY KEY (numComprobante) );");
                    statement.executeUpdate("insert into REPORTE values ( 3, '1', '890907157 BTGPactual Colombia', '2020-03-03', '48761', '', '', '', 'NI', '901148636', '9', 'AGRO GAIRA SAS', 'USD', '15.036,00', '1,00', '1040', '15.036,00', '15.036,00', '0,00', '0,00', '15.036,00', 'CATALINA URIBE GOMEZ', '42826138', '', '' )");
                    break;
                case "4":
                    statement.executeUpdate("create table REPORTE ( numComprobante int(10) not null, tipoOperacion varchar(50) null, tipoOperacionIE varchar(50) null, idiCiudad varchar(50) null, idiCuentaCompensacion varchar(50) null, idiFecha varchar(50) null, idiNumero varchar(50) null, idcaCuentaCompensacion varchar(50) null, idcaFecha varchar(50) null, idcaNumero varchar(50) null, ioNumPrestamo1 varchar(50) null, ioNumPrestamo2 varchar(50) null, ioNumPrestamo3 varchar(50) null, ioTipo varchar(50) null, ioNumeroIdentificacion varchar(50) null, ioDV varchar(50) null, ioNombreResidente varchar(50) null, ioMonContrat varchar(50) null, ioValMonContrat varchar(50) null, ioMonNeg varchar(50) null, ioValMonNeg varchar(50) null, ioTipoCambioUsd varchar(50) null, ioValTotalDolares varchar(50) null, ioNombreAcreDeu varchar(50) null, inliNumeral varchar(50) null, inliVrMonNeg varchar(50) null, inliVrMonCo varchar(50) null, inliVrUsd varchar(50) null, idNombre varchar(50) null, idNumeroIdentificacion varchar(50) null, idFirma varchar(50) null, PRIMARY KEY (numComprobante) );");
                    statement.executeUpdate("insert into REPORTE values ( 4, '1', 'E', 'MEDELLIN', '890907157 BTGPactual Colombia', '2020-03-17', '49337', '', '', '', '02', '007', '103837', 'CC', '79557635', '', 'SANCHEZ BENITEZ PEDRO EMILIO', 'USD', '669,00', 'USD', '669,00', '1,00', '669,00', 'BANCO SABADELL MIAMI', '2135', '669,00', '669,00', '669,00', 'SERGIO PEDRO SANCHEZ LAVERDE', '80503903', '' );");
                    break;
            }

            resultSet = statement.executeQuery("select * from REPORTE where numComprobante = " + ordeCodigo + ";");

            statement.executeUpdate("drop table REPORTE");

        } catch (SQLException e) {
            this.logger.log("There was an error when executing the SQL: " + e.getMessage());
            throw e;
        }
        return resultSet;
    }
}
