package quizzy.model.question;

import java.util.ArrayList;

public class PictureQuestion extends TextQuestion {
    private String imageUrl;

    public PictureQuestion(int id, int quizId, String prompt, String imageUrl, ArrayList<String> correctAnswers) {
        super(id, quizId, prompt, correctAnswers);
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public QuestionType getType() {
        return QuestionType.PICTURE_RESPONSE;
    }
}