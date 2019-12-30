package yarin.yal.gametheory.impartial;

import java.util.Collection;

/**
 * Represents the state of the game.
 *
 * Make sure to implement equals and hashcode!
 */
public interface IGameState {
    /**
     * Gets a list of all possible new games that may occur after all legal moves from this state.
     * A move may lead to several independent games.
     */
    Collection<IGameState[]> nextStates();
}
