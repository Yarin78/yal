package yarin.yal.gametheory.impartial;

import java.util.Collection;

public class CombinedGameState {
    private final IGameState[] subGames;

    public CombinedGameState(Collection<IGameState> subGames) {
        this.subGames = subGames.toArray(new IGameState[0]);
    }

    public IGameState[] getSubGames() {
        return subGames;
    }
}
