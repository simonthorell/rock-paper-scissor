public class Node {
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
