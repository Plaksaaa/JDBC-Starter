package test.database.db_runners;

import test.database.util.ConnectionManager;
import test.database.util.MyConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TransactionRunner {
    public void deleteFromConnectedTables(long id) throws SQLException {
        String deleteFlightsSql = """
                DELETE FROM flights
                WHERE id = ?
                """;
        String deleteTicketsSql = """
                DELETE FROM ticket 
                WHERE flight_id = ?
                """;
        Connection dbConnection = null;
        PreparedStatement deleteFlightStatement = null;
        PreparedStatement deleteTicketsStatement = null;
        try {
            dbConnection = ConnectionManager.getDbConnection();
            deleteFlightStatement = dbConnection.prepareStatement(deleteFlightsSql);
            deleteTicketsStatement = dbConnection.prepareStatement(deleteTicketsSql);

            dbConnection.setAutoCommit(false);

            deleteFlightStatement.setLong(1, id);
            deleteTicketsStatement.setLong(1, id);

            deleteTicketsStatement.executeUpdate();
            if (true) {
                throw new RuntimeException("ops");
            }
            deleteFlightStatement.executeUpdate();
//          commit не произойдет если произойдет exp
            dbConnection.commit();
        } catch (Exception e) {
            if (dbConnection != null) {
                dbConnection.rollback();
            }
            throw e;
        } finally {
            if (dbConnection != null) {
                dbConnection.close();
            }
            if (deleteFlightStatement != null) {
                deleteFlightStatement.close();
            }
            if (deleteTicketsStatement != null) {
                deleteTicketsStatement.close();
            }
        }
    }

    public void deleteFromConnectedTablesButch(long id) throws SQLException {
        String deleteFlightsSql = "DELETE from flights WHERE id =" + id;
        String deleteTicketsSql = "DELETE from ticket WHERE id =" + id;
        Connection dbConnection = null;
        Statement statement = null;
        try {
            dbConnection = MyConnectionPool.get();
            dbConnection.setAutoCommit(false);

            statement = dbConnection.createStatement();
            statement.addBatch(deleteFlightsSql);
            statement.addBatch(deleteTicketsSql);

            var executeBatch = statement.executeBatch();
            if (true) {
                throw new RuntimeException("ops");
            }
//          commit не произойдет если произойдет exp
            dbConnection.commit();
        } catch (Exception e) {
            if (dbConnection != null) {
                dbConnection.rollback();
            }
            throw e;
        } finally {
            if (dbConnection != null) {
                dbConnection.close();
            }
            if (statement != null) {
                statement.close();
            }
        }
    }
}
