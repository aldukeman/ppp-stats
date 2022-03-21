package ppp.stats.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ppp.stats.data.model.MiniTimeMessageModel;
import ppp.stats.data.model.UserModel;
import ppp.stats.data.model.WordleResultModel;
import ppp.stats.logging.ILogger;
import ppp.stats.utility.Pair;

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
            while (tablesSet.next()) {
                tables.add(tablesSet.getString("name"));
            }

            Map<String, Pair<String, Map<String, String>>> tableCreationMap = Map.ofEntries(
                    Map.entry(USER_TABLE_NAME,
                            Pair.of(this.createUserTableAndIndicesString(), USER_TABLE_COLS)),
                    Map.entry(MINI_TABLE_NAME,
                            Pair.of(this.createMiniTableAndIndicesString(), MINI_TABLE_COLS)),
                    Map.entry(WORDLE_TABLE_NAME,
                            Pair.of(this.createWordleTableAndIndicesString(), WORDLE_TABLE_COLS)));
            for (var entry : tableCreationMap.entrySet()) {
                if (tables.contains(entry.getKey())) {
                    String colNamesStmt = this.columnNamesString(entry.getKey());
                    ResultSet columns = this.connection.createStatement().executeQuery(colNamesStmt);
                    Set<String> colNames = new HashSet<>();
                    while (columns.next()) {
                        colNames.add(columns.getString("name"));
                    }

                    List<String> alterStatements = entry.getValue().second
                            .entrySet()
                            .stream()
                            .filter(e -> !colNames.contains(e.getKey()))
                            .peek(e -> this.logger.debug(entry.getKey() + " is missing column: " + e.getKey()))
                            .map(e -> this.addColumnToTableString(entry.getKey(), e.getKey(), e.getValue()))
                            .toList();
                    for (String statement : alterStatements) {
                        this.connection.createStatement().executeUpdate(statement);
                    }
                } else {
                    this.connection.createStatement().executeUpdate(entry.getValue().first);
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

    private String columnNamesString(String tableName) {
        return "PRAGMA table_info(" + tableName + ")";
    }

    private String addColumnToTableString(String tableName, String columnName, String dataType) {
        return "ALTER TABLE " + tableName + " ADD " + columnName + " " + dataType + ";";
    }

    final static private String USER_TABLE_NAME = "User";
    final static private String USER_ID_NAME = "Id";
    final static private String USER_NAME_NAME = "Name";

    final static private Map<String, String> USER_TABLE_COLS = Map.of(
            USER_ID_NAME, "UNSIGNED BIGINT PRIMARY KEY",
            USER_NAME_NAME, "VARCHAR(200)");

    private String columnString(Map<String, String> colsNameTypeMap) {
        List<String> colsStrings = colsNameTypeMap.entrySet().stream()
                .map(e -> e.getKey() + " " + e.getValue())
                .toList();
        return String.join(", ", colsStrings);
    }

    private String createUserTableAndIndicesString() {
        return "CREATE TABLE " + USER_TABLE_NAME + "(" + this.columnString(USER_TABLE_COLS) + ")";
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
    final static private String MINI_TIME_MESSAGE_ID_NAME = "MessageId";

    final static private Map<String, String> MINI_TABLE_COLS = Map.of(
            MINI_USER_ID_NAME, "UNSIGNED BIGINT",
            MINI_DATE_NAME, "CHAR(10)", // SQLite doesn't do DATE, so instead we store as string
            MINI_TIME_NAME, "UNSIGNED SMALLINT",
            MINI_TIME_MESSAGE_ID_NAME, "UNSIGNED BIGINT");

    private String createMiniTableAndIndicesString() {
        return "CREATE TABLE " + MINI_TABLE_NAME + "(" +
                this.columnString(MINI_TABLE_COLS) + "," +
                "FOREIGN KEY(" + MINI_USER_ID_NAME + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_ID_NAME + ")," +
                "PRIMARY KEY(" + MINI_USER_ID_NAME + ", " + MINI_DATE_NAME + "));";
    }

    @Override
    public void addUserTime(long userId, LocalDate date, int seconds, long messageId) {
        try {
            if (this.connection.createStatement()
                    .executeUpdate(this.updateMiniTimeStatement(userId, date, seconds, messageId)) == 0) {
                this.connection.createStatement()
                        .executeUpdate(this.insertMiniTimeStatement(userId, date, seconds, messageId));
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }
    }

    private String insertMiniTimeStatement(long id, LocalDate date, int seconds, long messageId) {
        return "INSERT INTO " + MINI_TABLE_NAME + "(" + MINI_USER_ID_NAME + ", " + MINI_DATE_NAME + ", "
                + MINI_TIME_NAME + ", " + MINI_TIME_MESSAGE_ID_NAME + ") " +
                "VALUES (" + id + ", \"" + date + "\", " + seconds + ", " + messageId + ");";
    }

    private String updateMiniTimeStatement(long id, LocalDate date, int seconds, long messageId) {
        return "UPDATE " + MINI_TABLE_NAME + " " +
                "SET " + MINI_TIME_NAME + "=" + seconds + ", " + MINI_TIME_MESSAGE_ID_NAME + "=" + messageId + " " +
                "WHERE " + MINI_USER_ID_NAME + "=" + id + " AND " + MINI_DATE_NAME + "=\"" + date + "\";";
    }

    @Override
    public Map<LocalDate, MiniTimeMessageModel> getTimesForUserId(long userId) {
        Hashtable<LocalDate, MiniTimeMessageModel> dict = new Hashtable<>();

        try {
            ResultSet resultSet = this.connection.createStatement()
                    .executeQuery(this.selectAllTimesForUserStatement(userId));

            while (resultSet.next()) {
                Long messageId = null;
                try {
                    messageId = Long.valueOf(resultSet.getLong(MINI_TIME_MESSAGE_ID_NAME));
                } catch (SQLException e) {
                    // this is fine
                }
                int time = resultSet.getInt(MINI_TIME_NAME);
                LocalDate date = LocalDate.parse(resultSet.getString(MINI_DATE_NAME));
                dict.put(date, new MiniTimeMessageModel(messageId, time, userId));
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }

        return dict;
    }

    private String selectAllTimesForUserStatement(long userId) {
        return "SELECT * " +
                "FROM " + MINI_TABLE_NAME + " " +
                "WHERE " + MINI_USER_ID_NAME + "=" + userId + ";";
    }

    @Override
    public Map<Long, MiniTimeMessageModel> getTimesForDate(LocalDate date) {
        Hashtable<Long, MiniTimeMessageModel> results = new Hashtable<>();

        try {
            ResultSet resultSet = this.connection.createStatement()
                    .executeQuery(this.selectAllTimesForDateStatement(date));

            while (resultSet.next()) {
                long userId = resultSet.getLong(MINI_USER_ID_NAME);
                Integer time = Integer.valueOf(resultSet.getInt(MINI_TIME_NAME));
                long messageId = resultSet.getLong(MINI_TIME_MESSAGE_ID_NAME);
                results.put(userId, new MiniTimeMessageModel(messageId, time, userId));
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }

        return results;
    }

    private String selectAllTimesForDateStatement(LocalDate date) {
        return "SELECT * " +
                "FROM " + MINI_TABLE_NAME + " " +
                "WHERE " + MINI_DATE_NAME + "=\"" + date + "\";";
    }

    final static private String WORDLE_TABLE_NAME = "Wordle";
    final static private String WORDLE_USER_ID_NAME = "UserId";
    final static private String WORDLE_DATE_NAME = "Date";
    final static private String WORDLE_RESULT_NAME = "Result";
    final static private String WORDLE_IS_HARD_NAME = "IsHard";
    final static private String WORDLE_MESSAGE_ID_NAME = "MessageId";

    final static private Map<String, String> WORDLE_TABLE_COLS = Map.of(
            WORDLE_USER_ID_NAME, "UNSIGNED BIGINT",
            WORDLE_DATE_NAME, "CHAR(10)", // SQLite doesn't do DATE, so instead we store as string
            WORDLE_RESULT_NAME, "VARCHAR(40)",
            WORDLE_IS_HARD_NAME, "BOOLEAN",
            WORDLE_MESSAGE_ID_NAME, "UNSIGNED BIGINT");

    private String createWordleTableAndIndicesString() {
        return "CREATE TABLE " + WORDLE_TABLE_NAME + "(" +
                this.columnString(WORDLE_TABLE_COLS) + "," +
                "FOREIGN KEY(" + WORDLE_USER_ID_NAME + ") REFERENCES " + USER_TABLE_NAME + "(" + USER_ID_NAME + ")," +
                "PRIMARY KEY(" + WORDLE_USER_ID_NAME + ", " + WORDLE_DATE_NAME + "));";
    }

    @Override
    public void addWordleResult(long userId, LocalDate date, WordleResultModel model, long messageId) {
        try {
            if (this.connection.createStatement()
                    .executeUpdate(this.updateWordleResultStatement(userId, date, model.getDbRepresentation(), model.isHard(), messageId)) == 0) {
                this.connection.createStatement()
                        .executeUpdate(this.insertWordleResultStatement(userId, date, model.getDbRepresentation(), model.isHard(), messageId));
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }
    }

    private String insertWordleResultStatement(long id, LocalDate date, String result, boolean isHard, long messageId) {
        return "INSERT INTO " + WORDLE_TABLE_NAME + "(" + WORDLE_USER_ID_NAME + ", " + WORDLE_DATE_NAME + ", "
                + WORDLE_RESULT_NAME + ", " + WORDLE_IS_HARD_NAME + ", " + WORDLE_MESSAGE_ID_NAME + ") " +
                "VALUES (" + id + ", \"" + date + "\", \"" + result + "\", " + isHard + ", " + messageId + ");";
    }

    private String updateWordleResultStatement(long id, LocalDate date, String result, boolean isHard, long messageId) {
        return "UPDATE " + WORDLE_TABLE_NAME + " " +
                "SET " + WORDLE_RESULT_NAME + "=\"" + result + "\", " + WORDLE_IS_HARD_NAME + "=" + isHard + ", " + WORDLE_MESSAGE_ID_NAME + "=" + messageId + " " +
                "WHERE " + WORDLE_USER_ID_NAME + "=" + id + " AND " + WORDLE_DATE_NAME + "=\"" + date + "\";";
    }

    @Override
    public Map<LocalDate, WordleResultModel> getWordleResultsForUserId(long userId) {
        Hashtable<LocalDate, WordleResultModel> results = new Hashtable<>();

        try {
            ResultSet resultSet = this.connection.createStatement()
                    .executeQuery(this.selectUsersWordleResultsStatement(userId));

            while (resultSet.next()) {
                LocalDate date = LocalDate.parse(resultSet.getString(WORDLE_DATE_NAME));
                String dbRepresentation = resultSet.getString(WORDLE_RESULT_NAME);
                boolean isHard = resultSet.getBoolean(WORDLE_IS_HARD_NAME);
                results.put(date, WordleResultModel.from(dbRepresentation, isHard));
            }
        } catch (SQLException e) {
            this.logger.error(e.getMessage());
        }

        return results;
    }

    private String selectUsersWordleResultsStatement(long userId) {
        return "SELECT * " +
                "FROM " + WORDLE_TABLE_NAME + " " +
                "WHERE " + WORDLE_USER_ID_NAME + "=\"" + userId + "\";";
    }
}
