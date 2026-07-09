package quizzy.service;

import quizzy.dao.FriendshipDAO;
import quizzy.dao.MessageDAO;
import quizzy.dao.UserDAO;
import quizzy.model.Friendship;
import quizzy.model.FriendshipStatus;
import quizzy.model.MessageType;
import quizzy.model.User;

import java.util.List;

// two-step friend flow: send request (+ a friend-request mail message), then accept/decline from the inbox
public class FriendService {

    public enum RequestOutcome {
        SENT,
        SELF,
        TARGET_NOT_FOUND,
        ALREADY_FRIENDS,
        REQUEST_ALREADY_PENDING,   // I already sent them one
        INCOMING_REQUEST_EXISTS    // they already sent me one - accept it instead
    }

    private final UserDAO userDAO;
    private final FriendshipDAO friendshipDAO;
    private final MessageDAO messageDAO;

    public FriendService() {
        this(new UserDAO(), new FriendshipDAO(), new MessageDAO());
    }

    public FriendService(UserDAO userDAO, FriendshipDAO friendshipDAO, MessageDAO messageDAO) {
        this.userDAO = userDAO;
        this.friendshipDAO = friendshipDAO;
        this.messageDAO = messageDAO;
    }

    public RequestOutcome sendRequest(int requesterId, int receiverId) {
        if (requesterId == receiverId) {
            return RequestOutcome.SELF;
        }
        User requester = userDAO.findById(requesterId);
        User target = userDAO.findById(receiverId);
        if (target == null || requester == null) {
            return RequestOutcome.TARGET_NOT_FOUND;
        }

        Friendship existing = friendshipDAO.findBetween(requesterId, receiverId);
        if (existing != null) {
            if (existing.getStatus() == FriendshipStatus.ACCEPTED) {
                return RequestOutcome.ALREADY_FRIENDS;
            }
            if (existing.getStatus() == FriendshipStatus.PENDING) {
                return existing.getRequesterId() == requesterId
                        ? RequestOutcome.REQUEST_ALREADY_PENDING
                        : RequestOutcome.INCOMING_REQUEST_EXISTS;
            }
            // was declined before - drop the old row so a new request can go out
            friendshipDAO.delete(requesterId, receiverId);
        }

        friendshipDAO.insertRequest(requesterId, receiverId);
        messageDAO.insert(requesterId, receiverId, MessageType.FRIEND_REQUEST, null,
                requester.getUsername() + " sent you a friend request.");
        return RequestOutcome.SENT;
    }

    public boolean acceptRequest(int currentUserId, int requesterId) {
        return friendshipDAO.updatePendingStatus(requesterId, currentUserId, FriendshipStatus.ACCEPTED);
    }

    public boolean declineRequest(int currentUserId, int requesterId) {
        return friendshipDAO.updatePendingStatus(requesterId, currentUserId, FriendshipStatus.DECLINED);
    }

    public boolean removeFriend(int currentUserId, int otherUserId) {
        return friendshipDAO.delete(currentUserId, otherUserId);
    }

    public List<User> getFriends(int userId) {
        return friendshipDAO.getFriends(userId);
    }

    public List<User> search(String query, int excludeUserId) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return userDAO.searchByUsername(query.trim(), excludeUserId);
    }

    // used by the profile page to figure out what the add-friend button should say
    public Friendship relationship(int userA, int userB) {
        return friendshipDAO.findBetween(userA, userB);
    }
}
