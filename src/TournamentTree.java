import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TournamentTree {
    private Node root;
    private Queue<Node> nextMatches = new LinkedList<>();

    public TournamentTree(List<PlayerStatus> players) {
        // Assumed players.size() is a power of 2, error checking should be added for robustness
        Queue<Node> matchesQueue = new LinkedList<>();
        for (int i = 0; i < players.size(); i += 2) {
            matchesQueue.add(new Node(players.get(i), players.get(i + 1)));
        }

        while (matchesQueue.size() > 1) {
            matchesQueue.add(new Node(matchesQueue.poll(), matchesQueue.poll()));
        }

        this.root = matchesQueue.poll();
        this.nextMatches.add(root); // Add the root node as the initial match
    }

    public Node nextGame() {
        return nextMatches.poll();
    }

    public void reportMatchResult(Node matchNode, PlayerStatus winner) {
        matchNode.winner = winner;

        if (matchNode.parent != null) {
            // If matchNode's sibling has a winner, and the parent hasn't, add parent to nextMatches
            Node sibling = (matchNode == matchNode.parent.leftChild) ?
                    matchNode.parent.rightChild : matchNode.parent.leftChild;
            if (sibling.winner != null && matchNode.parent.winner == null) {
                nextMatches.add(matchNode.parent);
            }
        }
    }

    public static class Node {
        PlayerStatus player1;
        PlayerStatus player2;
        PlayerStatus winner = null;
        Node parent;
        Node leftChild;
        Node rightChild;

        // Node for a match with actual players
        public Node(PlayerStatus player1, PlayerStatus player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        // Node for a match with child matches
        public Node(Node leftChild, Node rightChild) {
            this.leftChild = leftChild;
            this.rightChild = rightChild;
            leftChild.parent = this;
            rightChild.parent = this;
        }
    }
}