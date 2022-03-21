package ppp.stats.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.units.qual.K;

public class WordleResultModel {
    public enum CellType {
        BAD(1), WRONG_SPOT(2), GOOD(3);

        private int value;

        public int getValue() {
            return this.value;
        }

        private CellType(int value) {
            this.value = value;
        }

        static List<CellType> listFrom(int intRepresentation) {
            if (intRepresentation <= 0) {
                return null;
            }

            int n = intRepresentation;
            List<CellType> retVal = new ArrayList<>();

            while (n > 0) {
                int e = n % 10;
                switch (e) {
                    case 1: {
                        retVal.add(BAD);
                    }
                        break;
                    case 2: {
                        retVal.add(WRONG_SPOT);
                    }
                        break;
                    case 3: {
                        retVal.add(GOOD);
                    }
                        break;
                    default:
                        return null;
                }
                n /= 10;
            }

            Collections.reverse(retVal);
            return retVal;
        }

        static String dbRepresentation(List<CellType> row) {
            return row.stream()
                    .map(e -> Integer.toString(e.value))
                    .reduce("", (a, b) -> a + b);
        }
    }

    private final List<List<CellType>> rows;
    private final boolean isHard;

    private WordleResultModel(List<List<CellType>> rows, boolean isHard) {
        this.rows = rows;
        this.isHard = isHard;
    }

    public List<List<CellType>> getRows() {
        return this.rows;
    }

    public String getDbRepresentation() {
        List<String> rowStrings = this.rows.stream()
                .map(e -> CellType.dbRepresentation(e))
                .toList();
        return String.join(" ", rowStrings);
    }

    public boolean isHard() {
        return this.isHard;
    }

    public static WordleResultModel from(String dbRepresentation, boolean isHard) {
        List<List<CellType>> rows = List.of(dbRepresentation.split(" ")).stream()
                .map(e -> Integer.valueOf(e))
                .map(e -> CellType.listFrom(e.intValue()))
                .toList();
        return WordleResultModel.from(rows, isHard);
    }

    public static WordleResultModel from(List<List<CellType>> rows, boolean isHard) {
        if (rows.isEmpty()) {
            return null;
        }
        if (rows.size() > 6) {
            return null;
        }

        for (List<CellType> row : rows) {
            if (row.size() != 5) {
                return null;
            }
        }

        return new WordleResultModel(rows, isHard);
    }
}
