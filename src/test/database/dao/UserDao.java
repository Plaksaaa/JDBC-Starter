package test.database.dao;

import test.database.dto.UserFilter;
import test.database.entity.Phone;
import test.database.entity.User;
import test.database.exception.DaoException;
import test.database.util.MyConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.joining;

/**
 * always singleTone
 */
public class UserDao implements Dao<Integer, User> {

    private UserDao() {
    }

    private static class UserDaoHolder {
        private static final UserDao INSTANCE = new UserDao();
    }

    public static UserDao getInstance() {
        return UserDaoHolder.INSTANCE;
    }

    private static final String DELETE_SQL = """
            DELETE 
            FROM user
            WHERE id = ?
            """;
    private static final String SAVE_SQL = """
            INSERT INTO user (firstname, lastname, username, password, location, gender, phone_id)
            """ + "VALUES(?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = """
            UPDATE user
            SET firstname = ?,
                lastname = ?,
                username = ?,
                password = ?,
                location = ?,
                gender = ?
                phone_id = ?
            WHERE id = ?    
            """;

    private static final String FIND_ALL_SQL = """
            SELECT id, 
                   firstname, 
                   lastname, 
                   username, 
                   password, 
                   location, 
                   gender
            FROM user
            """;
    //    fields
    private static final String FIND_ALL_ENTITY_SQL_ = """
            SELECT u.id, 
                   firstname, 
                   lastname, 
                   username, 
                   password, 
                   location, 
                   gender,
                   p.id,
                   p.phone,
                   p.type
            FROM user u
            JOIN phone p 
                ON p.id = u.phone_id
            """;

    private static final String FIND_BY_ID_SQL = FIND_ALL_ENTITY_SQL_ + """
            WHERE u.id = ?
            """;

    private final PhoneDao phoneDao = PhoneDao.getInstance();

    public List<User> findAll(UserFilter filter) {
        List<Object> parameters = new ArrayList<>();
        List<String> whereSql = new ArrayList<>();

        if (filter.location() != null) {
            whereSql.add("location LIKE ?");
            parameters.add("%" + filter.location() + "%");
        }
        if (filter.firstname() != null) {
            whereSql.add("firstname = ?");
            parameters.add(filter.firstname());
        }
        var where = whereSql.stream()
                .collect(joining(" AND ", " WHERE ", " LIMIT ? OFFSET ? "));

        parameters.add(filter.limit());
        parameters.add(filter.offset());

        var sql = FIND_ALL_SQL + where;
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.size(); i++) {
//                так как в prst установка вопросов с 1 а в листе отсчет с 0
                preparedStatement.setObject(i + 1, parameters.get(i));
            }
            System.out.println(preparedStatement);
            var resultSet = preparedStatement.executeQuery();
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(buildUser(resultSet));
            }
            return users;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public List<User> findAll() {
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            List<User> users = new ArrayList<>();
            var resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(buildUser(resultSet));
            }
            return users;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public Optional<User> findById(Integer id) {
//        так как может не быть user-а возвращаем optional. В коллецкиях - пустую коллекцию.
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            preparedStatement.setInt(1, id);

            var resultSet = preparedStatement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = buildUser(resultSet);
            }
            return Optional.ofNullable(user);
//            так как может быть null
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    private User buildUser(ResultSet resultSet) throws SQLException {
        var phone = new Phone(
                resultSet.getInt("id"),
                resultSet.getString("phone"),
                resultSet.getString("type")
        );
        return new User(
                resultSet.getInt("id"),
                resultSet.getString("firstname"),
                resultSet.getString("lastname"),
                resultSet.getString("username"),
                resultSet.getString("password"),
                resultSet.getString("location"),
                resultSet.getString("gender"),
                phone
//                phoneDao.findById(resultSet.getInt("phone_id"),
//                        resultSet.getStatement().getConnection()).orElse(null)
        );
    }

    public void update(User user) {
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getUserName());
            preparedStatement.setString(4, user.getPass());
            preparedStatement.setString(5, user.getLocation());
            preparedStatement.setString(6, user.getGender());
            preparedStatement.setInt(7, user.getPhone().id());
            preparedStatement.setInt(8, user.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }

    public boolean delete(Integer id) {
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public User save(User user) {
        try (var connection = MyConnectionPool.get();
             var preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getFirstName());
            preparedStatement.setString(2, user.getLastName());
            preparedStatement.setString(3, user.getUserName());
            preparedStatement.setString(4, user.getPass());
            preparedStatement.setString(5, user.getLocation());
            preparedStatement.setString(6, user.getGender());
            preparedStatement.setInt(7, user.getPhone().id());

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            }
            return user;
        } catch (SQLException throwables) {
            throw new DaoException(throwables);
        }
    }
}
