package yarin.yal.gametheory.impartial.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import yarin.yal.gametheory.impartial.CombinedGameState;
import yarin.yal.gametheory.impartial.IGameState;

public class TrebleCross implements IGameState {
    private final int n, m;

    public TrebleCross(int n, int m) {
        this.n = n; // Size of strip
        this.m = m; // Number of X at the edges (0, 1 or 2)
    }

    public static CombinedGameState createGames(String state) {
        String[] split = state.split("X");
        int start = 0;
        while (start < split.length && split[start].length() == 0) start++;

        List<IGameState> states = new ArrayList<IGameState>();
        for (int i = 0; i < split.length; i++) {
            if (split[i].length() == 0) continue; // Ignore empty entries
            int m = 0;
            if (i > start || state.startsWith("X"))
                m++;
            if (i < split.length - 1 || state.endsWith("X"))
                m++;
            states.add(new TrebleCross(split[i].length(), m));
        }
        return new CombinedGameState(states);
    }

    public Collection<IGameState[]> nextStates() {
        List<IGameState[]> list = new ArrayList<IGameState[]>();
        for (int i = 0; i < n; i++) {
            if (i == 0 && m > 0)
                continue;
            if (i == n - 1 && m == 2)
                continue;
            IGameState[] moveResult = Move(i);
            if (moveResult != null) {
                list.add(moveResult);
            }
        }
        return list;
    }

    private IGameState[] Move(int pos) {
        TrebleCross a, b;
        if (pos == 0 || pos == n - 1) {
            if (n == 2)
                return null;
            return new IGameState[] { new TrebleCross(n - 1, m + 1) };
        }

        if (m == 2) {
            a = new TrebleCross(pos, 2);
            b = new TrebleCross(n - pos - 1, 2);
        }
        else if (m == 1) {
            a = new TrebleCross(pos, 1);
            b = new TrebleCross(n - pos - 1, 2);
        }
        else {
            a = new TrebleCross(pos, 1);
            b = new TrebleCross(n - pos - 1, 1);
        }

        if ((a.n == 1 && a.m == 2) || (b.n == 1 && b.m == 2))
            return null;
        return new IGameState[] { a, b };
    }


    public boolean equals(TrebleCross obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        return obj.n == n && obj.m == m;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof TrebleCross)) return false;
        return equals((TrebleCross) obj);
    }

    public int hashCode() {
        return (n * 397) ^ m;
    }
}