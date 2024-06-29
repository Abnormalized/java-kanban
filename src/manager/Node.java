package manager;

import tasks.Task;

public class Node {
    Task data;
    Node nextNode;
    Node prevNode;

    public Node(Task data, Node nextNode, Node prevNode) {
        this.data = data;
        this.nextNode = nextNode;
        this.prevNode = prevNode;
    }

    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    public void setPrevNode(Node prevNode) {
        this.prevNode = prevNode;
    }
}
