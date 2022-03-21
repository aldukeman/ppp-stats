package ppp.stats.logging;

public class NoOpLogger implements ILogger {
    public static final NoOpLogger shared = new NoOpLogger();
    
    @Override
    public void error(String log) { }

    @Override
    public void warn(String log) { }

    @Override
    public void info(String log) { }

    @Override
    public void debug(String log) { }

    @Override
    public void trace(String log) { }
}
