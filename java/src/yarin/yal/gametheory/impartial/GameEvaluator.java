package yarin.yal.gametheory.impartial;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GameEvaluator {
    private final Map<IGameState, Integer> nimValue = new HashMap<IGameState, Integer>();

    public int evaluateFiniteGame(CombinedGameState state) {
        int xor = 0;
        for (IGameState gameState : state.getSubGames()) {
            xor ^= evaluateFiniteGame(gameState);
        }
        return xor;
    }

    public int evaluateFiniteGame(IGameState state) {
        Integer value = nimValue.get(state);
        if (value != null) {
            return value;
        }

        Collection<IGameState[]> nextStates = state.nextStates();
        boolean[] found = new boolean[nextStates.size() + 1];
        for (IGameState[] states : nextStates) {
            int xor = 0;
            for (IGameState gameState : states) {
                int v = evaluateFiniteGame(gameState);
                xor ^= v;
            }
            if (xor < found.length)
                found[xor] = true;
        }
        int result = 0;
        while (found[result]) {
            result++;
        }
        nimValue.put(state, result);
        return result;
    }

    public int evaluateInfiniteGame(IGame game, CombinedGameState state) {
        // -1 = draw, 0 = losing, >0 = winning
        int xor = 0;
        IGameState infiniteSubGame = null;
        for (IGameState gameState : state.getSubGames()) {
            int res = evaluateInfiniteGame(game, gameState);
            if (res < 0) {
                if (infiniteSubGame != null)
                    return -1;
                infiniteSubGame = gameState;
            }
            else {
                xor ^= res;
            }
        }
        if (infiniteSubGame == null) {
            return xor;
        }

        for (IGameState[] states : infiniteSubGame.nextStates()) {
            int v = evaluateInfiniteGame(game, states[0]);
            if (v >= 0 && (v ^ xor) == 0) {
                return 1;
            }
        }
        return -1;
    }

    public int evaluateInfiniteGame(IGame game, IGameState startState) {
        for (IGameState state : game.allLosingStates()) {
            nimValue.put(state, 0);
        }

        boolean updated;
        do {
            if (nimValue.containsKey(startState))
                return nimValue.get(startState);

            updated = false;
            for (IGameState state : game.allStates()){
                if (nimValue.containsKey(state))
                    continue;
                Collection<IGameState[]> nextStates = state.nextStates();
                boolean[] found = new boolean[nextStates.size() + 1];
                for (IGameState[] states : nextStates) {
                    if (states.length > 1)
                        throw new RuntimeException(); // Not supported
                    Integer v = nimValue.get(states[0]);
                    if (v != null && v < found.length) {
                        found[v] = true;
                    }
                }
                int result = 0;
                while (found[result]) result++;
                boolean ok = true;
                for (IGameState[] states : nextStates) {
                    if (!nimValue.containsKey(states[0])) {
                        ok = false;
                        for (IGameState[] nextState : states[0].nextStates()) {
                            Integer w = nimValue.get(nextState[0]);
                            if (w != null && w == result) {
                                ok = true;
                            }
                        }
                    }
                }
                if (ok) {
                    updated = true;
                    nimValue.put(state, result);
                }
            }
        } while (updated);
        return -1;
    }
}
