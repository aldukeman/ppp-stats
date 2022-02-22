package ppp.stats.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ppp.stats.data.model.UserModel;
import ppp.stats.logging.ILogger;

public class SQLiteDataManager implements IDataManager {
    final private String filename;
    final private ILogger logger;

    public SQLiteDataManager(String filename, ILogger logger) {
        this.filename = filename;
        this.logger = logger;

        this.setupDB();
    }

    private Connection connection() {
        // SQLite connection string
        String url = "jdbc:sqlite:" + this.filename;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }
        return conn;
    }

    private void setupDB() {
        Connection connection = this.connection();
        try {
            connection.createStatement().executeUpdate(this.createUserTableAndIndicesString());
            connection.createStatement().executeUpdate(this.createMiniTableAndIndicesString());
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }
    }

    final static private String USER_TABLE_NAME = "User";
    final static private String USER_ID_NAME = "Id";
    final static private String USER_NAME_NAME = "Name";

    private String createUserTableAndIndicesString() {
        String createTable = "CREATE TABLE " + USER_TABLE_NAME + "(" +
                USER_ID_NAME + " UNSIGNED BIGINT PRIMARY KEY, " +
                USER_NAME_NAME + " VARCHAR(200));";
        return createTable;
    }

    private String insertUserNameStatement(long id, String name) {
        return "INSERT INTO " + USER_TABLE_NAME + "(" + USER_ID_NAME + ", " + USER_NAME_NAME + ") " +
                "VALUES (" + id + ", \"" + name + "\");";
    }

    private String updateUserNameStatement(long id, String name) {
        return "UPDATE " + USER_TABLE_NAME + " " +
                "SET " + USER_NAME_NAME + "=\"" + name + "\" " +
                "WHERE " + USER_ID_NAME + "=" + id + ";";
    }

    @Override
    public void setUserName(long id, String name) {
        try {
            Connection connection = this.connection();
            if (connection.createStatement().executeUpdate(this.updateUserNameStatement(id, name)) == 0) {
                connection.createStatement().executeUpdate(this.insertUserNameStatement(id, name));
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }
    }

    @Override
    public List<UserModel> getUserModels() {
        try {
            ResultSet resultSet = this.connection().createStatement().executeQuery(this.selectAllUsersStatement());

            List<UserModel> results = new ArrayList<>(resultSet.getFetchSize());
            while (resultSet.next()) {
                long id = resultSet.getLong(USER_ID_NAME);
                String name = resultSet.getString(USER_NAME_NAME);
                results.add(new UserModel(id, name));
            }

            return results;
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }

        return null;
    }

    private String selectAllUsersStatement() {
        return "SELECT * FROM " + USER_TABLE_NAME + ";";
    }

    final static private String MINI_TABLE_NAME = "Mini";
    final static private String MINI_USER_ID_NAME = "UserId";
    final static private String MINI_DATE_NAME = "Date";
    final static private String MINI_TIME_NAME = "Time";

    private String createMiniTableAndIndicesString() {
        String createTable = "CREATE TABLE " + MINI_TABLE_NAME + "(" +
                MINI_USER_ID_NAME + " UNSIGNED BIGINT, " +
                MINI_DATE_NAME + " CHAR(10), " + // SQLite doesn't do DATE, so instead we store the string representation
                MINI_TIME_NAME + " UNSIGNED SMALLINT, " +
                "FOREIGN KEY (" + MINI_USER_ID_NAME + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_ID_NAME + ")," +
                "PRIMARY KEY(" + MINI_USER_ID_NAME + ", " + MINI_TIME_NAME + "));";
        return createTable;
    }

    @Override
    public void addUserTime(long id, int seconds) {
        try {
            Connection connection = this.connection();
            if (connection.createStatement().executeUpdate(this.insertMiniTimeStatement(id, seconds)) == 0) {
                connection.createStatement().executeUpdate(this.updateMiniTimeStatement(id, seconds));
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }
    }

    private static LocalDate MiniDate() {
        return LocalDate.now(ZoneId.of("America/New_York"));
    }

    private String insertMiniTimeStatement(long id, int seconds) {
        return "INSERT INTO " + MINI_TABLE_NAME + "(" + MINI_USER_ID_NAME + ", " + MINI_DATE_NAME + ", " + MINI_TIME_NAME + ") " +
                "VALUES (" + id + ", \"" + SQLiteDataManager.MiniDate() + "\", " + seconds + ");";
    }

    private String updateMiniTimeStatement(long id, int seconds) {
        return "UPDATE " + MINI_TABLE_NAME + " " +
                "SET " + MINI_TIME_NAME + " " + seconds + " " +
                "WHERE " + MINI_USER_ID_NAME + "=" + id + " AND " + MINI_DATE_NAME + "=\"" + this.MiniDate() + "\";";
    }

    @Override
    public UserTimesDictionary getTimesForUserId(long id) {
        try {
            ResultSet resultSet = this.connection().createStatement().executeQuery(this.selectAllTimesForUserStatement(id));

            UserTimesDictionary dict = new UserTimesDictionary();
            while (resultSet.next()) {
                Integer time = Integer.valueOf(resultSet.getInt(MINI_TIME_NAME));
                LocalDate date = LocalDate.parse(resultSet.getString(MINI_DATE_NAME));
                dict.put(date, time);
            }

            return dict;
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }

        return null;
    }

    private String selectAllTimesForUserStatement(long id) {
        return "SELECT " + MINI_DATE_NAME + ", " + MINI_TIME_NAME + " " +
                "FROM " + MINI_TABLE_NAME + " " +
                "WHERE " + MINI_USER_ID_NAME + "=" + id + ";";
    }

    @Override
    public Map<Long, Integer> getTimesForDate(LocalDate date) {
        try {
            ResultSet resultSet = this.connection().createStatement().executeQuery(this.selectAllTimesForDateStatement(date));

            Hashtable<Long, Integer> results = new Hashtable<>();
            while (resultSet.next()) {
                Long id = Long.valueOf(resultSet.getLong(MINI_USER_ID_NAME));
                Integer time = Integer.valueOf(resultSet.getInt(MINI_TIME_NAME));
                results.put(id, time);
            }

            return results;
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }

        return null;
    }

    private String selectAllTimesForDateStatement(LocalDate date) {
        return "SELECT " + MINI_USER_ID_NAME + ", " + MINI_TIME_NAME + " " +
                "FROM " + MINI_TABLE_NAME + " " +
                "WHERE " + MINI_DATE_NAME + "=\"" + date + "\";";
    }
}
