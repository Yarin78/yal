package yarin.yal.gametheory.impartial.examples;

import java.util.ArrayList;
import java.util.Collection;

import yarin.yal.gametheory.impartial.IGameState;

public class SquaringTheNumberState implements IGameState {
    public int value;

    public SquaringTheNumberState(int value) {
        this.value = value;
    }

    public Collection<IGameState[]> nextStates() {
        Collection<IGameState[]> result = new ArrayList<IGameState[]>();
        for (int i = 1; i * i <= value; i++)
            result.add(new IGameState[]{new SquaringTheNumberState(value - i * i)});
        return result;
    }

    public boolean equals(SquaringTheNumberState obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        return obj.value == value;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof SquaringTheNumberState)) return false;
        return equals((SquaringTheNumberState) obj);
    }

    public int hashCode() {
        return value;
    }
}