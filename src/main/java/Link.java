import java.util.concurrent.ArrayBlockingQueue;

public class Link {
    private static int msgCapacity = 1;
    private int node1, node2;
    private ArrayBlockingQueue<Message> node1Writer;
    private ArrayBlockingQueue<Message> node2Writer;

    public Link(int n1, int n2){
        node1 = n1;
        node2 = n2;
        node1Writer = new ArrayBlockingQueue<>(msgCapacity);
        node2Writer = new ArrayBlockingQueue<>(msgCapacity);
    }

    public boolean sendMsg(int writer, Message msg){
        try {
            if (writer == node1) node1Writer.put(msg);
            if (writer == node2) node2Writer.put(msg);
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public Message readMsg(int reader){
        if (reader == node1) return node2Writer.poll();
        else return node1Writer.poll();
    }

    public Message peekMsg(int reader){
        if (reader == node1) return node2Writer.peek();
        else return node1Writer.peek();
    }

    public int linkedTo(int id){
        return (id == node1) ? node2 : node1;
    }

    @Override
    public String toString(){
        return "(" + node1 + "---" + node2 + ")";
    }

    public int getNode1(){
        return node1;
    }

    public int getNode2(){
        return node2;
    }
}
