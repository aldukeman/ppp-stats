package ppp.stats.logging;

public interface ILogger {
    void error(String log);
    void warn(String log);
    void info(String log);
    void debug(String log);
    void trace(String log);
}
