package yarin.yal.gametheory.impartial.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import yarin.yal.gametheory.impartial.IGameState;

public class NimCity2State implements IGameState {
    public boolean equals(NimCity2State obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        return obj.city == city;
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof NimCity2State)) return false;
        return equals((NimCity2State) obj);
    }

    public int hashCode() {
        return city;
    }

    private NimCity2Game game;
    private int city;
    private List<Integer> edges;

    public NimCity2State(NimCity2Game game, int city, List<Integer> edges) {
        this.game = game;
        this.city = city;
        this.edges = edges;
    }

    public Collection<IGameState[]> nextStates() {
        List<IGameState[]> nextStates = new ArrayList<IGameState[]>();
        for (int i : edges) {
            nextStates.add(new IGameState[]{game.states.get(i)});
        }
        return nextStates;
    }
}
