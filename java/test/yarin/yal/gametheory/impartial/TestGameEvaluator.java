package yarin.yal.gametheory.impartial;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import yarin.yal.gametheory.impartial.examples.NimCity;
import yarin.yal.gametheory.impartial.examples.NimCity2Game;
import yarin.yal.gametheory.impartial.examples.SquaringTheNumberState;
import yarin.yal.gametheory.impartial.examples.TrebleCross;

public class TestGameEvaluator {
    @Test
    public void testSquaringTheNumber() {
        // Squaring the number
        int[]
            expected =
            new int[]{0, 1, 0, 1, 2, 0, 1, 0, 1, 2, 0, 1, 0, 1, 2, 0, 1, 0, 1, 2, 0, 1, 0, 1, 2, 3,
                      2, 3, 4, 5};
        GameEvaluator ge = new GameEvaluator();
        for (int i = 0; i < expected.length; i++) {
            int actual = ge.evaluateFiniteGame(new SquaringTheNumberState(i));
            Assert.assertEquals(expected[i], actual);
        }
    }

    @Test
    public void testNimCity() {
        String[] map = new String[]
            {
                "", "h", "hA", "hB", "h", "hABD", "BCE", "CF", "D", "hDEGH", "CFI", "CGJ"
            };
        GameEvaluator ge = new GameEvaluator();
        for (int i = 1; i < 12; i++) {
            int v = ge.evaluateFiniteGame(new NimCity(map, i));
        }

        int g1 = ge.evaluateFiniteGame(new NimCity(map, 6));
        int g2 = ge.evaluateFiniteGame(new NimCity(map, 10));
        int g3 = ge.evaluateFiniteGame(new NimCity(map, 11));
        Assert.assertEquals(2, g1 ^ g2 ^ g3);
    }

    @Test
    public void testTrebleCross() {
        // Treblecross
        String[] input = new String[]
            {
                ".....",
                "X.....X..X.............X....X..X",
                ".X.X...X",
                "...............................................",

                "..................",
                "...........................................",
                "X..............................X..........X............X.X..X..X.......................",
                "....................................................X.................................................................",
                "......................................................................................"
            };
        ;
        int[][] expectedOutput = new int[][]
            {
                new int[] {3},
                new int[0],
                new int[] {3},
                new int[] {1, 12, 15, 17, 20, 24, 28, 31, 33, 36, 47},

                new int[] {5, 6, 13, 14},
                new int[] {6, 9, 22, 35, 38},
                new int[] {57},
                new int[] {58, 64, 68, 69, 74, 77, 78, 79, 85, 89, 95, 96, 97, 100, 105, 106, 110, 116},
                new int[0]
            };

        GameEvaluator ge = new GameEvaluator();
        for (int caseNo = 0; caseNo < input.length; caseNo++) {
            List<Integer> winningMoves = new ArrayList<Integer>();
            String s = input[caseNo];
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == 'X') continue;
                String t = s.substring(0, i) + "X" + s.substring(i + 1);
                if (t.contains("XXX")) {
                    winningMoves.add(i + 1);
                } else if (!t.contains("XX") && !t.contains("X.X")) {
                    CombinedGameState game = TrebleCross.createGames(t);
                    if (ge.evaluateFiniteGame(game) == 0)
                        winningMoves.add(i + 1);
                }
            }

            Assert.assertEquals(expectedOutput[caseNo].length, winningMoves.size());
            for (int i = 0; i < winningMoves.size(); i++) {
                Assert.assertEquals(expectedOutput[caseNo][i], (int) winningMoves.get(i));
            }
        }
    }

    @Test
    public void testNimCity2() {
        // Infinite game evaluation
        String[] map = new String[]
            {
                "", "h", "AE", "h", "hAC", "hADGH", "hCE", "CEG", "BEG"
            };

        NimCity2Game game = new NimCity2Game(map);
        GameEvaluator ge = new GameEvaluator();

        int[] expected = new int[] {0, 1, 0, 1, 2, -1, 2, -1, 1};
        int ix = 0;
        for (IGameState state : game.allStates()) {
            int res = ge.evaluateInfiniteGame(game, state);
            Assert.assertEquals(expected[ix++], res);
        }

        CombinedGameState cgs = new CombinedGameState(
            Arrays.asList(game.states.get(2), game.states.get(4), game.states.get(8)));
        int cgsRes = ge.evaluateInfiniteGame(game, cgs);
        Assert.assertTrue(cgsRes > 0);

        cgs = new CombinedGameState(Arrays.asList(game.states.get(7)));
        cgsRes = ge.evaluateInfiniteGame(game, cgs);
        Assert.assertTrue(cgsRes < 0);

        cgs = new CombinedGameState(Arrays.asList(game.states.get(5)));
        cgsRes = ge.evaluateInfiniteGame(game, cgs);
        Assert.assertTrue(cgsRes > 0);

        cgs = new CombinedGameState(Arrays.asList(game.states.get(5), game.states.get(3)));
        cgsRes = ge.evaluateInfiniteGame(game, cgs);
        Assert.assertTrue(cgsRes > 0);

        cgs = new CombinedGameState(Arrays.asList(game.states.get(5), game.states.get(3), game.states.get(4)));
        cgsRes = ge.evaluateInfiniteGame(game, cgs);
        Assert.assertTrue(cgsRes < 0);
    }
}
