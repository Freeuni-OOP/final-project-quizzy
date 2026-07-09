package quizzy.model;

public class Friendship {
    private int id;
    private int requesterId;
    private int receiverId;
    private FriendshipStatus status;

    public Friendship(int id, int requesterId, int receiverId, FriendshipStatus status) {
        this.id = id;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getRequesterId() {
        return requesterId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public FriendshipStatus getStatus() {
        return status;
    }
}
