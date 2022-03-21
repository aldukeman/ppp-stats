package ppp.stats.models;

import org.junit.Test;

import ppp.stats.data.model.WordleResultModel;
import ppp.stats.data.model.WordleResultModel.CellType;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

public class WordleResultModelTest {
    private static final Map<String, List<List<WordleResultModel.CellType>>> TESTS = Map.of(
            "11111 22222 33333",
            List.of(
                    List.of(CellType.BAD, CellType.BAD, CellType.BAD, CellType.BAD, CellType.BAD),
                    List.of(CellType.WRONG_SPOT, CellType.WRONG_SPOT, CellType.WRONG_SPOT, CellType.WRONG_SPOT,
                            CellType.WRONG_SPOT),
                    List.of(CellType.GOOD, CellType.GOOD, CellType.GOOD, CellType.GOOD, CellType.GOOD)),
            "11311 22311 33322 33333",
            List.of(
                    List.of(CellType.BAD, CellType.BAD, CellType.GOOD, CellType.BAD, CellType.BAD),
                    List.of(CellType.WRONG_SPOT, CellType.WRONG_SPOT, CellType.GOOD, CellType.BAD,
                            CellType.BAD),
                    List.of(CellType.GOOD, CellType.GOOD, CellType.GOOD, CellType.WRONG_SPOT, CellType.WRONG_SPOT),
                    List.of(CellType.GOOD, CellType.GOOD, CellType.GOOD, CellType.GOOD, CellType.GOOD)));

    @Test
    public void testFromDbRepresentation() {
        for (var entry : WordleResultModelTest.TESTS.entrySet()) {
            WordleResultModel model = WordleResultModel.from(entry.getKey(), false);
            assertEquals(entry.getValue(), model.getRows());
        }
    }

    @Test
    public void testToDbRepresentation() {
        for(var entry: WordleResultModelTest.TESTS.entrySet()) {
            WordleResultModel model = WordleResultModel.from(entry.getValue(), false);
            assertEquals(entry.getKey(), model.getDbRepresentation());
        }
    }
}
