package quizzy.model.question;

import quizzy.model.Quiz;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("PICTURE_RESPONSE")
public class PictureQuestion extends TextQuestion {

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    protected PictureQuestion() {
    }

    public PictureQuestion(int id, Quiz quiz, String prompt, String imageUrl,
                           int questionOrder, List<String> correctAnswers) {
        super(id, quiz, prompt, questionOrder, correctAnswers);
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
