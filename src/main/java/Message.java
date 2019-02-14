/*
store the message and the nodes communicating through the message
 */
public class Message {

    private MessageType type;
    private int src, dest;
    private String msg2Str;
    private int level;

    public Message(char msgType, int s, int d, int l){
        switch (msgType){
            case 'e' : type = MessageType.Exp; break;
            case 'a' : type = MessageType.Ack; break;
            case 'n' : type = MessageType.Nack; break;
        }

        src = s;
        dest = d;
        level = l;
        msg2Str = "{ " + s + " -> " + d + " : " + level + " }";
    }

    public MessageType getType() {
        return type;
    }

    public int getSrc() {
        return src;
    }

    public int getDest() {
        return dest;
    }

    @Override
    public String toString(){
        return msg2Str;
    }

    public int getLevel(){
        return level;
    }
}
