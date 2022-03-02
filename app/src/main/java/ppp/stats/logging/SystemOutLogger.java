package ppp.stats.logging;

public class SystemOutLogger implements ILogger {
    public static SystemOutLogger shared = new SystemOutLogger();
    
    @Override
    public void error(String log) {
        System.out.println("E/" + log);
    }

    @Override
    public void warn(String log) {
        System.out.println("W/" + log);
    }

    @Override
    public void info(String log) {
        System.out.println("I/" + log);        
    }

    @Override
    public void debug(String log) {
        System.out.println("D/" + log);        
    }

    @Override
    public void trace(String log) {
        System.out.println("T/" + log);        
    }
}
