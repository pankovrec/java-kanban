package manager;

import model.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * В памяти история.
 */
public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<Task>> history = new HashMap<>();
    private CustomLinkedList<Task> customLinkedList = new CustomLinkedList<>();

    private static class Node<T> {
        T item;
        Node<T> next;
        Node<T> prev;

        public Node(T item) {
            this.item = item;
            this.next = null;
            this.prev = null;
        }
    }

    public static class CustomLinkedList<T> {
        private Node<T> head;
        private Node<T> tail;

        public CustomLinkedList() {
            this.head = null;
            this.tail = null;
        }

        public Node<T> linkLast(T task) {
            Node<T> newNode = new Node<>(task);

            if (head == null) {
                head = newNode;
            }
            if (tail != null) {
                tail.next = newNode;
                newNode.prev = tail;
            }
            tail = newNode;
            return newNode;
        }

        public T removeNode(Node<T> node) {
            if (node == null) {
                return null;
            }

            Node<T> prevNode = node.prev;
            Node<T> nextNode = node.next;

            if (prevNode != null && nextNode != null) {
                prevNode.next = nextNode;
                nextNode.prev = prevNode;
            } else if (prevNode == null && nextNode != null) {
                nextNode.prev = null;
                head = nextNode;
            } else if (nextNode == null && prevNode != null) {
                prevNode.next = null;
                tail = prevNode;
            }

            node.prev = null;
            node.next = null;
            return node.item;
        }

        public List<T> getTasks() {
            List<T> list = new LinkedList<>();
            Node<T> node = head;
            while (node != null) {
                list.add(node.item);
                node = node.next;
            }
            return list;
        }
    }

    @Override
    public List<Task> getHistory() {
        return customLinkedList.getTasks();
    }

    @Override
    public void add(Task T) {
        if (history.containsKey(T.getId())) {
            remove(T.getId());
        }
        Node<Task> node = customLinkedList.linkLast(T);
        history.put(T.getId(), node);
    }

    @Override
    public void remove(int id) {
        if (!history.containsKey(id)) {
            return;
        }
        Node<Task> node = history.get(id);
        customLinkedList.removeNode(node);
        history.remove(id);
    }
}