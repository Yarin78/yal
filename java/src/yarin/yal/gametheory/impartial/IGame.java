package yarin.yal.gametheory.impartial;

import java.util.Collection;

public interface IGame {
    Collection<IGameState> allStates();
    Collection<IGameState> allLosingStates();
}


