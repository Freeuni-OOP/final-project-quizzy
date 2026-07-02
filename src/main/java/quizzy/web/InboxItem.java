package quizzy.web;

import quizzy.model.Message;

// message + sender username, so the inbox JSP doesn't have to do lookups
public class InboxItem {
    private final Message message;
    private final String senderName;

    public InboxItem(Message message, String senderName) {
        this.message = message;
        this.senderName = senderName;
    }

    public Message getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }
}
