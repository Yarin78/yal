package yarin.yal.gametheory.impartial.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import yarin.yal.gametheory.impartial.IGameState;

public class NimCity implements IGameState {
    private final String[] map;
    private final int city;

    public NimCity(String[] map, int city) {
        this.map = map;
        this.city = city;
    }

    public Collection<IGameState[]> nextStates() {
        List<IGameState[]> states = new ArrayList<IGameState[]>();
        for (char c : map[city].toCharArray()) {
            int next = (c == 'h') ? 0 : (c - 'A' + 1);
            states.add(new IGameState[]{new NimCity(map, next)});
        }
        return states;
    }

    public boolean equals(NimCity obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        return obj.city == city && obj.map.equals(map);
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof NimCity)) return false;
        return equals((NimCity) obj);
    }

    public int hashCode() {
        return ((map != null ? map.hashCode() : 0) * 397) ^ city;
    }
}