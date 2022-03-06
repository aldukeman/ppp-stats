package ppp.stats.logging;

import java.time.LocalTime;

public class SystemOutLogger implements ILogger {
    public static SystemOutLogger shared = new SystemOutLogger();
    
    @Override
    public void error(String log) {
        System.out.println("[E][" + LocalTime.now() + "]" + log);
    }

    @Override
    public void warn(String log) {
        System.out.println("[W][" + LocalTime.now() + "]" + log);
    }

    @Override
    public void info(String log) {
        System.out.println("[I][" + LocalTime.now() + "]" + log);
    }

    @Override
    public void debug(String log) {
        System.out.println("[D][" + LocalTime.now() + "]" + log);
    }

    @Override
    public void trace(String log) {
        System.out.println("[T][" + LocalTime.now() + "]" + log);
    }
}
