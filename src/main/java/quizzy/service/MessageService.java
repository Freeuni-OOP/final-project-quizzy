package quizzy.service;

import quizzy.dao.MessageDAO;
import quizzy.model.Message;
import quizzy.model.MessageType;

import java.util.List;

// notes, challenges and inbox handling. friend-request messages are created by FriendService.
public class MessageService {

    private final MessageDAO messageDAO;

    public MessageService() {
        this(new MessageDAO());
    }

    public MessageService(MessageDAO messageDAO) {
        this.messageDAO = messageDAO;
    }

    public void sendNote(int senderId, int receiverId, String body) {
        messageDAO.insert(senderId, receiverId, MessageType.NOTE, null, body);
    }

    public void sendChallenge(int senderId, int receiverId, int quizId, String body) {
        messageDAO.insert(senderId, receiverId, MessageType.CHALLENGE, quizId, body);
    }

    public List<Message> getInbox(int userId) {
        return messageDAO.getInbox(userId);
    }

    public boolean delete(int messageId, int ownerId) {
        return messageDAO.delete(messageId, ownerId);
    }
}
