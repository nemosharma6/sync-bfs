import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
The idea is to keep track of who to receive feedback from and who to send feedback to. If the node receives feedback
from all the children then it can send a feedback to its parent. Thus the tree is created from the leaf to the root.
 */
public class Node implements Runnable {

    private Thread thread;
    private boolean initiate, terminate;
    private int id, round;
    private Link parentLink;
    private Message bestMsg ;
    private List<Integer> children;
    private Map<Integer, Link> linkMap;
    private Map<Integer, Message> waitingForThem;
    private Map<Integer, Message> respondToThem;
    private String name;
    private int minLevel;

    public Node(int id, List<Link> links) {
        this.id = id;
        round = 0;
        initiate = true;
        terminate = false;
        waitingForThem = new HashMap<Integer, Message>();
        respondToThem = new HashMap<Integer, Message>();
        linkMap = new HashMap<Integer, Link>();
        children = new ArrayList<Integer>();
        bestMsg = null;
        parentLink = null;
        name = "node-" + id;
        this.minLevel = Integer.MAX_VALUE;

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Link l : links) {
            linkMap.put(l.linkedTo(id), l);
            sb.append(l.toString());
            sb.append(" ");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        Log.write(name, " edges " + sb.toString());
    }

    public void start() {
        Log.write(name, " round :" + (round++));
        thread = new Thread(this, String.valueOf(id));
        thread.start();
    }

    public void join() {
        try {
            thread.join();
            Log.write(name, " finish round :" + (round - 1));
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public String parent() {
        if (parentLink == null) return "Just an Orphan";
        else return String.valueOf(parentLink.linkedTo(id));
    }

    public void clearAfterResponse(int sender) {
        if (waitingForThem.containsKey(sender)) {
            Log.write(name, " Delete processed message - " + waitingForThem.get(sender));
            waitingForThem.remove(sender);
        }
    }

    private boolean anyMsg() {
        for (Integer i : linkMap.keySet()) {
            Message msg = linkMap.get(i).peekMsg(id);
            if (msg != null) return true;
        }
        return false;
    }

    public void run() {

        if (terminate)
            return;

        if (id == ParseInput.rootProcess) {
            if (initiate) {
                initiate = false;
                for (Integer recipient : linkMap.keySet()) {
                    Message explore = new Message('e', id, recipient, 1);
                    waitingForThem.put(recipient, explore);
                    linkMap.get(recipient).sendMsg(id, explore);
                }
            } else {
                if (!waitingForThem.isEmpty()) {
                    for (Link e : linkMap.values()) {
                        int sender = e.linkedTo(id);
                        Message m = e.readMsg(id);
                        if (m == null) continue;

                        if (m.getType() == MessageType.Ack && !children.contains(e.linkedTo(id))) {
                            children.add(e.linkedTo(id));
                            Log.write(name, sender + " sent me an Ack, he is my newest child.");
                        }
                        clearAfterResponse(sender);
                    }
                } else
                    terminate = true;
            }
        } else {
            /*
            if child process receives a message then it can decide on its parent from among its options. After deciding
            on its parent it can send messages on edges that are not connected to its parent in the hope that some of
            them would become its child. This ends the initial phase.
             */
            if (initiate) {
                boolean temp = anyMsg();
                if (temp) {
                    for (Link e : linkMap.values()) {
                        int sender = e.linkedTo(id);
                        Message m = e.readMsg(id);
                        if (m == null) continue;

                        if (m.getType() == MessageType.Exp) {
                            respondToThem.put(sender, m);
                            if (m.getLevel() < minLevel) {
                                minLevel = m.getLevel();
                                parentLink = e;
                                bestMsg = m;
                            }
                        }
                    }

                    for (Link e : linkMap.values()) {
                        int sender = e.linkedTo(id);
                        if (respondToThem.containsKey(sender)) continue;
                        Message childMsg = new Message('e', id, sender, bestMsg.getLevel() + 1);
                        e.sendMsg(id, childMsg);
                        waitingForThem.put(sender, childMsg);
                    }

                    initiate = false;
                }
            } else {
                /*
                Check messages from neighbours. Act according to the message received. Finally when there are no
                waiting messages then send an ack to the parent.
                 */
                for (Link e : linkMap.values()) {
                    int sender = e.linkedTo(id);
                    Message m = e.readMsg(id);
                    if (m == null) continue;

                    if (m.getType() == MessageType.Exp) respondToThem.put(sender, m);

                    if (m.getType() == MessageType.Ack && !children.contains(e.linkedTo(id))) {
                        children.add(e.linkedTo(id));
                        Log.write(name, sender + " sent me an Ack, he is my newest child.");
                    }

                    if (m.getType() == MessageType.Nack)
                        Log.write(name, sender + " sent me an Nack, he is not my child.");

                    clearAfterResponse(sender);

                }

                ArrayList<Integer> toDelete = new ArrayList<Integer>();
                for (Integer sender : respondToThem.keySet()) {
                    Message m = respondToThem.get(sender);
                    if (bestMsg != m) {
                        toDelete.add(sender);
                        Message nack = new Message('n', m.getSrc(), m.getDest(), m.getLevel());
                        linkMap.get(sender).sendMsg(id, nack);
                    }
                }

                for (Integer sender : toDelete) {
                    respondToThem.remove(sender);
                }

                if (waitingForThem.isEmpty()) {
                    if (parentLink != null) {
                        Message ack = new Message('a', bestMsg.getSrc(), bestMsg.getDest(),
                                bestMsg.getLevel());
                        parentLink.sendMsg(id, ack);
                        respondToThem.remove(parentLink.linkedTo(id));
                    }

                    terminate = true;
                }
            }
        }
    }

    public boolean isTerminate() {
        return terminate;
    }

    public String getChildren() {
        StringBuilder sb = new StringBuilder();
        for (Integer i : children) {
            sb.append(i);
            sb.append(",");
        }

        if (children.size() != 0)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public int getId() {
        return id;
    }

}
