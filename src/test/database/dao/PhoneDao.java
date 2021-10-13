package test.database.dao;

import test.database.entity.Phone;
import test.database.exception.DaoException;
import test.database.util.MyConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PhoneDao implements Dao<Integer, Phone> {

    private PhoneDao() {
    }

    private static class PhoneDaoHolder {
        public static final PhoneDao INSTANCE = new PhoneDao();
    }

    public static PhoneDao getInstance() {
        return PhoneDaoHolder.INSTANCE;
    }

    private static final String UPDATE_SQL = """
            UPDATE phone
            SET phone = ?,
            SET type = ?
            WHERE id = ?
            """;

    private static final String SAVE_SQL = """
            INSERT 
            INTO phone 
                 (id
                 phone,
                 type)
            VALUES(?, ?, ?);
            """;

    public static final String DELETE_BY_ID_SQL = """
            DELETE 
            FROM phone
            WHERE id = ?
            """;

    public static final String FIND_ALL_SQL = """
            SELECT id,
                   phone,
                   type
            FROM phone
            """;

    public static final String FIND_BY_ID_SQL = """
            SELECT id,
                   phone,
                   type
            FROM phone
            WHERE id = ?
            """;

    @Override
    public List<Phone> findAll() {
        List<Phone> phones = new ArrayList<>();
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                phones.add(new Phone(
                        resultSet.getInt("id"),
                        resultSet.getString("phone"),
                        resultSet.getString("type")
                ));
            }
            return phones;
        } catch (SQLException throwables) {
            throw new DaoException();
        }
    }

    // для билда только телефона
    @Override
    public Optional<Phone> findById(Integer id) {
        try (var connection = MyConnectionPool.get()) {
            return findById(id, connection);
        } catch (SQLException throwables) {
            throw new DaoException();
        }
    }

    // для билда вместе с user и его соединением
    public Optional<Phone> findById(Integer id, Connection connection) {
        try (var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            var resultSet = preparedStatement.executeQuery();
            Phone phone = null;

            while (resultSet.next()) {
                phone = new Phone(resultSet.getInt("id"),
                        resultSet.getString("phone"),
                        resultSet.getString("tupe"));
            }
            return Optional.ofNullable(phone);
        } catch (SQLException throwables) {
            throw new DaoException();
        }
    }

    @Override
    public void update(Phone phone) {
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, phone.phone());
            preparedStatement.setString(2, phone.type());
            preparedStatement.setInt(3, phone.id());

            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException();
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(DELETE_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException throwables) {
            throw new DaoException();
        }
    }

    @Override
    public Phone save(Phone phone) {
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            preparedStatement.setInt(1, phone.id());
            preparedStatement.setString(2, phone.phone());
            preparedStatement.setString(3, phone.type());

            preparedStatement.executeUpdate();

            return phone;
        } catch (SQLException throwables) {
            throw new DaoException();
        }
    }
}
