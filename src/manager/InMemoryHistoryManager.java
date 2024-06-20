package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final Node headNode = new Node(null, null, null);
    private final Node tailNode = new Node(null, null, headNode);
    private final HashMap<Long, Node> historyMap = new HashMap<>();


    @Override
    public void add(Task task) {
        boolean isNew = !(historyMap.containsKey(task.getId()));
        if (!isNew) {
            Node newNode = linkLast(task);
            removeNode(historyMap.get(task.getId()));
            historyMap.replace(task.getId(), newNode);
        } else {
            historyMap.put(task.getId(), linkLast(task));
        }
    }

    @Override
    public void remove(long id) {
        if (historyMap.containsKey(id)) {
            removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public void clear() {
        historyMap.clear();
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void removeNode(Node node) {
        node.nextNode.setPrevNode(node.prevNode);
        node.prevNode.setNextNode(node.nextNode);
    }

    private Node linkLast(Task task) {
        Node node = new Node(task, tailNode, tailNode.prevNode);
        tailNode.prevNode.setNextNode(node);
        tailNode.setPrevNode(node);
        return node;
    }

    private List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node currentNode = tailNode.prevNode;
        for (int i = 0; i < historyMap.size(); i++) {
            historyList.add(currentNode.data);
            currentNode = currentNode.prevNode;
        }
        return historyList;
    }



}
