package ppp.stats.data;

public class InMemoryDataManagerTest extends IDataManagerTest {
    protected void setUp() {
        this.testDataManager = new InMemoryDataManager();
    }
}