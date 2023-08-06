package ovh.bricklou.slbot_plugin.modules.blacklist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class DatabaseLayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseLayer.class);

    private static final String DB_URL = "jdbc:sqlite:blacklist.db";

    private final Connection dbConnection;

    public DatabaseLayer() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this.dbConnection = DriverManager.getConnection(DB_URL);
        this.init();

        LOGGER.info("Database initialized");
    }

    private void init() throws SQLException {
        Statement statement = dbConnection.createStatement();
        statement.setQueryTimeout(30);
        statement.execute("""
                    CREATE TABLE IF NOT EXISTS blacklist (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        user_id LONG NOT NULL,
                        message_id LONG NOT NULL,
                        reason VARCHAR NOT NULL
                    )
                """);
    }

    public void insertUser(long userId, long messageId, String reason) throws SQLException {
        PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO blacklist (user_id, message_id, reason) VALUES (?, ?, ?)");
        statement.setQueryTimeout(30);
        statement.setLong(1, userId);
        statement.setLong(2, messageId);
        statement.setString(3, reason);
        statement.execute();
    }

    public void removeUser(long userId) throws SQLException {
        PreparedStatement statement = this.dbConnection.prepareStatement("DELETE FROM blacklist WHERE user_id = ?");
        statement.setQueryTimeout(30);
        statement.setLong(1, userId);
        statement.execute();

    }

    public boolean isBlacklisted(long userId) throws SQLException {
        PreparedStatement statement = this.dbConnection.prepareStatement("SELECT COUNT(*) FROM blacklist WHERE user_id = ?");
        statement.setQueryTimeout(30);
        statement.setLong(1, userId);
        ResultSet rs = statement.executeQuery();

        return rs.last() && rs.getRow() > 0;
    }

    public long getMessageId(long userId) throws SQLException {
        PreparedStatement statement = this.dbConnection.prepareStatement("SELECT message_id FROM blacklist WHERE user_id = ?");
        statement.setQueryTimeout(30);
        statement.setLong(1, userId);
        ResultSet rs = statement.executeQuery();

        if (!rs.last())
            return -1;

        return rs.getLong("message_id");
    }

    public void closeConnection() throws SQLException {
        if (!dbConnection.isClosed())
            dbConnection.close();
    }
}
