package yarin.yal;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class DijkstraHelper<TDist extends Comparable<TDist>> {
    private class State implements Comparable<State> {
        public final int Node;
        public final TDist Distance;

        public State(int node, TDist distance) {
            Node = node;
            Distance = distance;
        }

        public int compareTo(State other) {
            int cmp = Distance.compareTo(other.Distance);
            if (cmp != 0)
                return cmp;
            return Node - other.Node;
        }
    }

    private final TreeSet<State> stateByDistance;
    private final List<TDist> distanceByNode;
    private final byte[] state;

    public DijkstraHelper(int noNodes, TDist initialDistance) {
        distanceByNode = new ArrayList<TDist>(noNodes);
        for (int i = 0; i < noNodes; i++) {
            distanceByNode.add(initialDistance);
        }

        stateByDistance = new TreeSet<State>();
        state = new byte[noNodes];
    }

    public void add(int node, TDist distance) {
        switch (state[node]) {
            case 0:
                distanceByNode.set(node, distance);
                state[node] = 1;
                stateByDistance.add(new State(node, distance));
                break;
            case 1:
                if (distance.compareTo(distanceByNode.get(node)) < 0) {
                    stateByDistance.remove(new State(node, distanceByNode.get(node)));
                    distanceByNode.set(node, distance);
                    stateByDistance.add(new State(node, distance));
                }
                break;
        }
    }

    public int getNext() {
        if (stateByDistance.size() == 0) return -1;
        State next = stateByDistance.first();
        state[next.Node] = 2;
        stateByDistance.remove(next);
        return next.Node;
    }

    public TDist getDistance(int node) {
        return distanceByNode.get(node);
    }
}
