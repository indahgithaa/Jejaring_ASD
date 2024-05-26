import java.util.Scanner;

class Vertex {
    String username;
    String[] favorites;
    Vertex next;
    int numberOfFollowers;

    Vertex(String username, String[] favorites) {
        this.username = username;
        this.favorites = favorites;
        this.numberOfFollowers = 0;
        this.next = null;
    }
}

class LinkedList {
    Vertex head;
    int size;

    public LinkedList() {
        this.head = null;
        this.size = 0;
    }

    void add(Vertex vertex) {
        if (head == null) {
            head = vertex;
        } else {
            Vertex temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = vertex;
        }
        size++;
    }

    void remove(String username) {
        if (head == null) {
            return;
        } else if (head.username.equals(username)) {
            head = head.next;
        } else {
            Vertex temp = head;
            while (temp.next != null && !temp.next.username.equals(username)) {
                temp = temp.next;
            }
            if (temp.next != null) {
                temp.next = temp.next.next;
            }
        }
        size--;
    }

    int indexOf(String username) {
        Vertex temp = head;
        int index = 0;
        while (temp != null) {
            if (temp.username.equals(username)) {
                return index;
            }
            temp = temp.next;
            index++;
        }
        return -1;
    }
}

class Graph {
    Vertex[] vertices;
    LinkedList[] adjacencyLists;
    int size;
    int count = 0;

    public Graph(int size) {
        this.vertices = new Vertex[size];
        this.adjacencyLists = new LinkedList[size];
        this.size = size;
        for (int i = 0; i < size; i++) {
            adjacencyLists[i] = new LinkedList();
        }
    }

    void addVertex(String username, String[] favorites) {
        if (count < size) {
            Vertex vertex = new Vertex(username, favorites);
            vertices[count] = vertex;
            count++;
        }
    }

    void addDirectionalEdge(String username1, String username2) {
        int index1 = getVertexIndex(username1);
        int index2 = getVertexIndex(username2);
        if (index1 != -1 && index2 != -1) {
            Vertex vertexToAdd = new Vertex(vertices[index2].username, vertices[index2].favorites);
            adjacencyLists[index1].add(vertexToAdd);
            vertices[index2].numberOfFollowers++;
        }
    }

    void addBidirectionalEdge(String username1, String username2) {
        addDirectionalEdge(username1, username2);
        addDirectionalEdge(username2, username1);
    }

    int getVertexIndex(String username) {
        for (int i = 0; i < count; i++) {
            if (vertices[i].username.equals(username)) {
                return i;
            }
        }
        return -1;
    }

    int shortestDistance(String startUsername, String endUsername) {
        int startIndex = getVertexIndex(startUsername);
        int endIndex = getVertexIndex(endUsername);
        if (startIndex == -1 || endIndex == -1) {
            return -1;
        }
    
        boolean[] visited = new boolean[size];
        int[] distance = new int[size];
        Queue queue = new Queue();
    
        visited[startIndex] = true;
        distance[startIndex] = 0;
        queue.enqueue(startUsername);
    
        while (!queue.isEmpty()) {
            String current = queue.dequeue();
            int currentIndex = getVertexIndex(current);
            Vertex temp = adjacencyLists[currentIndex].head;
            while (temp != null) {
                int neighborIndex = getVertexIndex(temp.username);
                if (!visited[neighborIndex]) {
                    visited[neighborIndex] = true;
                    distance[neighborIndex] = distance[currentIndex] + 1;
                    queue.enqueue(temp.username);
                }
                if (temp.username.equals(endUsername)) {
                    return distance[neighborIndex] - 1;
                }
                temp = temp.next;
            }
        }
    
        return -1;
    }
    
    void DFSUtil(int start, boolean[] visited) {
        visited[start] = true;
        Vertex temp = adjacencyLists[start].head;
        while (temp != null) {
            int index = getVertexIndex(temp.username);
            if (!visited[index]) {
                DFSUtil(index, visited);
            }
            temp = temp.next;
        }
    }

    int numberOfGroups() {
        boolean[] visited = new boolean[size];
        int groupCount = 0;

        for (int i = 0; i < size; i++) {
            if (!visited[i] && vertices[i] != null) {
                DFSUtil(i, visited);
                groupCount++;
            }
        }
        return groupCount;
    }
}

class Queue {
    String[] data;
    int front;
    int rear;
    int size;

    public Queue() {
        data = new String[1];
        front = 0;
        rear = 0;
        size = 0;
    }

    void enqueue(String value) {
        if (size == data.length) {
            String[] newData = new String[data.length * 2];
            for (int i = 0; i < size; i++) {
                newData[i] = data[(front + i) % data.length];
            }
            data = newData;
            front = 0;
            rear = size;
        }
        data[rear] = value;
        rear = (rear + 1) % data.length;
        size++;
    }

    String dequeue() {
        if (size == 0) {
            return null;
        }
        String value = data[front];
        front = (front + 1) % data.length;
        size--;
        return value;
    }

    boolean isEmpty() {
        return size == 0;
    }

    void print() {
        for (int i = 0; i < size; i++) {
            System.out.println(data[(front + i) % data.length]);
        }
    }
}

public class Jejaring {
    Graph actualGraph;
    Graph undiGraph;
    int size;
    int connection;

    public Jejaring(int size, int connection) {
        this.size = size;
        this.connection = connection;
        this.actualGraph = new Graph(size);
        this.undiGraph = new Graph(size);
    }

    public void insertUser(String username, String[] favorites) {
        actualGraph.addVertex(username, favorites);
        undiGraph.addVertex(username, favorites);
    }

    public void connectUser(String username1, String username2) {
        actualGraph.addDirectionalEdge(username1, username2);
        undiGraph.addBidirectionalEdge(username1, username2);
    }

    public String[] mostFollowedUser() {
        int maxFollowers = 0;
        for (int i = 0; i < actualGraph.count; i++) {
            if (actualGraph.vertices[i].numberOfFollowers > maxFollowers) {
                maxFollowers = actualGraph.vertices[i].numberOfFollowers;
            }
        }

        int count = 0;
        for (int i = 0; i < actualGraph.count; i++) {
            if (actualGraph.vertices[i].numberOfFollowers == maxFollowers) {
                count++;
            }
        }

        String[] mostFollowedUsers = new String[count];
        int index = 0;
        for (int i = 0; i < actualGraph.count; i++) {
            if (actualGraph.vertices[i].numberOfFollowers == maxFollowers) {
                mostFollowedUsers[index] = actualGraph.vertices[i].username;
                index++;
            }
        }

        for (int i = 0; i < mostFollowedUsers.length - 1; i++) {
            for (int j = i + 1; j < mostFollowedUsers.length; j++) {
                if (mostFollowedUsers[i].compareTo(mostFollowedUsers[j]) > 0) {
                    String temp = mostFollowedUsers[i];
                    mostFollowedUsers[i] = mostFollowedUsers[j];
                    mostFollowedUsers[j] = temp;
                }
            }
        }

        return mostFollowedUsers;
    }

    public int minimumCuitUlang(String username1, String username2) {
        //jarak terpendek dari username2 ke username1
        int distance = actualGraph.shortestDistance(username2, username1);
        return distance;
    }

    public int numberOfGroups(){
        return undiGraph.numberOfGroups();
    }

    public void printJejaring() {
        for (int i = 0; i < actualGraph.count; i++) {
            Vertex vertex = actualGraph.vertices[i];
            LinkedList list = actualGraph.adjacencyLists[i];
            System.out.print(vertex.username + " -> ");
            Vertex temp = list.head;
            while (temp != null) {
                System.out.print(temp.username + " -> ");
                temp = temp.next;
            }
            System.out.println();
        }

        System.out.println();

        for (int i = 0; i < undiGraph.count; i++) {
            Vertex vertex = undiGraph.vertices[i];
            LinkedList list = undiGraph.adjacencyLists[i];
            System.out.print(vertex.username + " -> ");
            Vertex temp = list.head;
            while (temp != null) {
                System.out.print(temp.username + " -> ");
                temp = temp.next;
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int jumlahPengguna = scanner.nextInt();
        int jumlahConnection = scanner.nextInt();

        scanner.nextLine();

        Jejaring jejaring = new Jejaring(jumlahPengguna, jumlahConnection);

        Queue commandQueue = new Queue();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            if (command.isEmpty()) {
                break;
            } else {
                commandQueue.enqueue(command);
            }
        }

        while (commandQueue.size != 0) {
            String command = commandQueue.dequeue();
            String firstCommand = command.split(" ")[0];
            if (firstCommand.equals("mostfollowed")) {
                String[] mostFollowedUsers = jejaring.mostFollowedUser();
                if (mostFollowedUsers.length == 1) {
                    System.out.println(mostFollowedUsers[0]);
                } else {
                    for (int i = 0; i < mostFollowedUsers.length; i++) {
                        System.out.print(mostFollowedUsers[i]);
                        if (i < mostFollowedUsers.length - 1) {
                            System.out.print(", ");
                        }
                    }
                    System.out.println();
                }
            } else if (firstCommand.equals("insert")) {
                String username = command.split(" ")[1];
                String[] favorites = new String[3];
                for (int i = 0; i < 3; i++) {
                    favorites[i] = command.split(" ")[i + 2];
                }
                jejaring.insertUser(username, favorites);
                System.out.println(username + " inserted");
            } else if (firstCommand.equals("connect")) {
                String username1 = command.split(" ")[1];
                String username2 = command.split(" ")[2];
                jejaring.connectUser(username1, username2);
                System.out.println("connect " + username1 + " " + username2 + " success");
            } else if (firstCommand.equals("mincuit")) {
                String username1 = command.split(" ")[1];
                String username2 = command.split(" ")[2];
                System.out.println(jejaring.minimumCuitUlang(username1, username2));
            } else if (firstCommand.equals("numgroup")) {
                System.out.println(jejaring.numberOfGroups());
            }
        }

        jejaring.printJejaring();

        scanner.close(); 
    }
}
