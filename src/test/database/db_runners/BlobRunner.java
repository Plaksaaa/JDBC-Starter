package test.database.db_runners;

import test.database.util.ConnectionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class BlobRunner {
    public void getImage(int id) throws SQLException {
        var sql = """
                SELECT image
                FROM aircraft
                WHERE id = ?
                """;
        try (var dbConnection = ConnectionManager.getDbConnection();
             var preparedStatement = dbConnection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            var resultSet = preparedStatement.executeQuery();
//            I expect one value thats why i use if
            if (resultSet.next()) {
                var image = resultSet.getByte("image");
//                Files.write(Path.of("resources", "boing_new.jpg"), image, StandardOpenOption.CREATE);
            }
        }
    }
    public void saveImagePostgres() throws SQLException, IOException {
        var sql = """
                UPDATE aircraft
                SET image = ?
                WHERE id = 1
                """;
        try (var dbConnection = ConnectionManager.getDbConnection();
             var preparedStatement = dbConnection.prepareStatement(sql)) {

            preparedStatement.setBytes(1, Files.readAllBytes(Path.of("resources", "boing.jgp")));
            preparedStatement.executeUpdate();
//      Postgres has its own methods to process Blobs and Clobs and these methods allow us not to close the transaction
        }
    }
        public void saveImage () throws SQLException, IOException {
            var sql = """
                    UPDATE aircraft
                    SET image = ?
                    WHERE id = 1
                    """;
            try (var dbConnection = ConnectionManager.getDbConnection();
                 var preparedStatement = dbConnection.prepareStatement(sql)) {
                dbConnection.setAutoCommit(false);

                var blob = dbConnection.createBlob();
                blob.setBytes(1, Files.readAllBytes(Path.of("resources", "boing.jgp")));

                preparedStatement.setBlob(1, blob);
                preparedStatement.executeUpdate();
                dbConnection.commit();
//            it's simple path to process transactions
            }
        }
    }
