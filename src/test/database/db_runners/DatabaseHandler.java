package test.database.db_runners;

import test.database.db_fields.Const;
import test.database.entity.User;
import test.database.util.ConnectionManager;


import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler {
    //регистрация пользователя

    public int signUpUser(User user) throws SQLException {
        int update;
//        сам SQL запрос
        String insert = "INSERT INTO " + Const.USER_TABLE + "(" +
                        Const.USERS_FIRSTNAME + "," + Const.USERS_LASTNAME + "," +
                        Const.USERS_USERNAME + "," + Const.USERS_PASSWORD + "," +
                        Const.USERS_LOCATION + "," + Const.USERS_GENDER + ")" +
                        "VALUES(?,?,?,?,?,?)";

        try (var dbConnection = ConnectionManager.getDbConnection();
             var preparedStatement = dbConnection.prepareStatement(insert)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getUserName());
            preparedStatement.setString(4, user.getPass());
            preparedStatement.setString(5, user.getLocation());
            preparedStatement.setString(6, user.getGender());

            update = preparedStatement.executeUpdate();
        }
        return update;
    }

    //Авторизация
    public List<String> getUser(String login, String pass) throws SQLException {
        ArrayList<String> userArrayList = new ArrayList<>();
        String select = "SELECT * FROM users WHERE username =? AND password =?";

        try (var dbConnection = ConnectionManager.getDbConnection();
             var preparedStatement = dbConnection.prepareStatement(select)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, pass);

            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                userArrayList.add(resultSet.getString("id"));
            }
            return userArrayList;
        }
    }

    public List<User> printUsersCount() throws SQLException {
        String sql = "SELECT * FROM users";
        ArrayList<User> usersList = new ArrayList<>();

        try (var dbConnection = ConnectionManager.getDbConnection();
             var preparedStatement = dbConnection.prepareStatement(sql)) {
            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
//                numberRow = resultSet.getInt("idusers");
//                System.out.println(resultSet.getString(2));
                User user = new User();
                user.setFirstName(resultSet.getString("firstname"));
                user.setLastName(resultSet.getString("lastname"));
                user.setUserName(resultSet.getString("username"));
                user.setPass(resultSet.getString("password"));
                user.setLocation(resultSet.getString("location"));
                user.setGender(resultSet.getString("gender"));
                usersList.add(user);
            }
        }
        return usersList;
    }

    public String findUserFirstName(Long id) throws SQLException {
        String sql = """
                SELECT firstname 
                FROM users 
                WHERE idusers=?
                """;
        String firstName = null;

        try (var dbConnection = ConnectionManager.getDbConnection();
             var preparedStatement = dbConnection.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                firstName = resultSet.getString("firstname");
            }
        }
        return firstName;
    }

    public List<String> getUserLocationAndPass(long id) throws SQLException {
        String sql = """
                SELECT location, password 
                FROM users
                WHERE id=%s 
                """.formatted(id);
        List<String> result = new ArrayList<>();
        try (var connection = ConnectionManager.getDbConnection();
             var statement = connection.createStatement()) {
            var resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                result.add(resultSet.getString("location"));
//                Работа с null
                result.add(resultSet.getObject("password", String.class));
            }
        }
        return result;
    }

    public List<String> getUserLocationAndPassWithPrepare(long id) throws SQLException {
        String sql = """
                SELECT location, password 
                FROM users
                WHERE id= ?
                """;
        List<String> result = new ArrayList<>();
        try (var connection = ConnectionManager.getDbConnection();
             var preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setFetchSize(50);
            preparedStatement.setQueryTimeout(5);
            preparedStatement.setMaxRows(100);
            preparedStatement.setLong(1, id);

            var resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                result.add(resultSet.getString("location"));
//                Работа с null
                result.add(resultSet.getObject("password", String.class));
            }
        }
        return result;
    }

    public List<Long> getFlightsBetween(LocalDateTime start, LocalDateTime end) throws SQLException {
        String sql = """
                SELECT id 
                FROM users
                WHERE date BETWEEN ? AND ?
                """;
        List<Long> result = new ArrayList<>();

        try (var dbConnection = ConnectionManager.getDbConnection();
             var preparedStatement = dbConnection.prepareStatement(sql)) {
            preparedStatement.setTimestamp(1, Timestamp.valueOf(start));
            preparedStatement.setTimestamp(2, Timestamp.valueOf(end));

            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getLong("id"));
            }
        }
        return result;
    }

    public void getMetaDate() throws SQLException {
        try (var dbConnection = ConnectionManager.getDbConnection()) {
            var metaData = dbConnection.getMetaData();
            var catalogs = metaData.getCatalogs();

            while (catalogs.next()) {
                var catalog = catalogs.getString(1);
                System.out.println(catalog);
                var schemas = metaData.getSchemas();

                while (schemas.next()) {
                    var schema = schemas.getString("TABLE_SCHEM");
                    var tables = metaData.getTables(catalog, schema, "%", null);
                    if (schema.equals("new_schema")) {
                        while (tables.next()) {
                            System.out.println(tables.getString("TABLE_NAME"));
                        }
                    }
                }
            }
        }
    }

}
