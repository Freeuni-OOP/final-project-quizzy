package quizzy.model;

public class Message {
    private int id;
    private int senderId;
    private int receiverId;
    private MessageType type;
    private Integer quizId;   // only set for CHALLENGE messages
    private String body;

    public Message(int id, int senderId, int receiverId, MessageType type, Integer quizId, String body) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.type = type;
        this.quizId = quizId;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public MessageType getType() {
        return type;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public String getBody() {
        return body;
    }
}
