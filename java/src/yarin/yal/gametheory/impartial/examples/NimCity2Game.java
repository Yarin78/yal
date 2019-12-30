package yarin.yal.gametheory.impartial.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import yarin.yal.gametheory.impartial.IGame;
import yarin.yal.gametheory.impartial.IGameState;

public class NimCity2Game implements IGame {
    public List<IGameState> states;

    public NimCity2Game(String[] map) {
        states = new ArrayList<IGameState>(map.length);
        for (int i = 0; i < map.length; i++) {
            List<Integer> edges = new ArrayList<Integer>();
            for (char c : map[i].toCharArray()) {
                int next = (c == 'h') ? 0 : (c - 'A' + 1);
                edges.add(next);
            }
            states.add(new NimCity2State(this, i, edges));
        }
    }

    public Collection<IGameState> allStates() {
        return states;
    }

    public Collection<IGameState> allLosingStates() {
        return Arrays.asList(states.get(0));
    }
}