package ppp.stats.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ppp.stats.data.model.UserModel;
import ppp.stats.logging.ILogger;

public class SQLiteDataManager implements IChannelDataManager {
    final private String filename;
    final private ILogger logger;
    final private Connection connection;

    public SQLiteDataManager(String filename, ILogger logger) throws SQLException {
        this.filename = filename;
        this.logger = logger;

        String url = "jdbc:sqlite:" + this.filename;
        this.connection = DriverManager.getConnection(url);

        this.setupDB();
    }

    private void setupDB() {
        try {
            ResultSet tablesSet = this.connection.createStatement().executeQuery(this.checkForTablesString());
            List<String> tables = new ArrayList<>();
            while(tablesSet.next()) {
                tables.add(tablesSet.getString("name"));
            }

            Map<String, String> tableCreationMap = Map.ofEntries(
                Map.entry(USER_TABLE_NAME, this.createUserTableAndIndicesString()),
                Map.entry(MINI_TABLE_NAME, this.createMiniTableAndIndicesString())
            );
            for(var entry: tableCreationMap.entrySet()) {
                if(!tables.contains(entry.getKey())) {
                    this.connection.createStatement().executeUpdate(entry.getValue());
                    this.logger.debug("Creating " + entry.getKey() + " table");
                }
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }
    }

    private String checkForTablesString() {
        return "SELECT name FROM sqlite_master WHERE type='table';";
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

    private PreparedStatement insertUserNameStatement(long id, String name) {
        String sqlStr = "INSERT INTO " + USER_TABLE_NAME + "(" + USER_ID_NAME + ", " + USER_NAME_NAME + ") " +
                "VALUES (" + id + ", ?);";
        try {
            PreparedStatement pStatement = this.connection.prepareStatement(sqlStr);
            pStatement.setString(1, name.substring(0, Integer.min(200, name.length())));
            return pStatement;
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
            return null;
        }
    }

    private PreparedStatement updateUserNameStatement(long id, String name) {
        String sqlStr = "UPDATE " + USER_TABLE_NAME + " " +
                "SET " + USER_NAME_NAME + "=? " +
                "WHERE " + USER_ID_NAME + "=" + id + ";";
        try {
            PreparedStatement pStatement = this.connection.prepareStatement(sqlStr);
            pStatement.setString(1, name.substring(0, Integer.min(200, name.length())));
            return pStatement;
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public void setUserName(long id, String name) {
        try {
            if (this.updateUserNameStatement(id, name).executeUpdate() == 0) {
                this.insertUserNameStatement(id, name).execute();
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }
    }

    @Override
    public Map<Long, UserModel> getUserModels() {
        try {
            ResultSet resultSet = this.connection.createStatement().executeQuery(this.selectAllUsersStatement());

            Hashtable<Long, UserModel> results = new Hashtable<>(resultSet.getFetchSize());
            while (resultSet.next()) {
                long id = resultSet.getLong(USER_ID_NAME);
                String name = resultSet.getString(USER_NAME_NAME);
                results.put(id, new UserModel(id, name));
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
                MINI_DATE_NAME + " CHAR(10), " + // SQLite doesn't do DATE, so instead we store the string
                                                 // representation
                MINI_TIME_NAME + " UNSIGNED SMALLINT, " +
                "FOREIGN KEY (" + MINI_USER_ID_NAME + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_ID_NAME + ")," +
                "PRIMARY KEY(" + MINI_USER_ID_NAME + ", " + MINI_DATE_NAME + "));";
        return createTable;
    }

    @Override
    public void addUserTime(long id, LocalDate date, int seconds) {
        try {
            if (this.connection.createStatement().executeUpdate(this.updateMiniTimeStatement(id, date, seconds)) == 0) {
                this.connection.createStatement().executeUpdate(this.insertMiniTimeStatement(id, date, seconds));
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }
    }

    private String insertMiniTimeStatement(long id, LocalDate date, int seconds) {
        return "INSERT INTO " + MINI_TABLE_NAME + "(" + MINI_USER_ID_NAME + ", " + MINI_DATE_NAME + ", "
                + MINI_TIME_NAME + ") " +
                "VALUES (" + id + ", \"" + date + "\", " + seconds + ");";
    }

    private String updateMiniTimeStatement(long id, LocalDate date, int seconds) {
        return "UPDATE " + MINI_TABLE_NAME + " " +
                "SET " + MINI_TIME_NAME + "=" + seconds + " " +
                "WHERE " + MINI_USER_ID_NAME + "=" + id + " AND " + MINI_DATE_NAME + "=\"" + date + "\";";
    }

    @Override
    public Map<LocalDate, Integer> getTimesForUserId(long id) {
        try {
            ResultSet resultSet = this.connection.createStatement()
                    .executeQuery(this.selectAllTimesForUserStatement(id));

            Hashtable<LocalDate, Integer> dict = new Hashtable<>();
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
            ResultSet resultSet = this.connection.createStatement()
                    .executeQuery(this.selectAllTimesForDateStatement(date));

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
