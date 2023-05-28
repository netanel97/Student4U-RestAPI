package superapp.miniapps;

import java.util.ArrayList;
import java.util.List;

public class ForumThread {
    private String subject;
    private String description;
    private List<String> comments = new ArrayList<>();

    public ForumThread() {
        super();
    }

    public ForumThread(String subject, String description, List<String> comments) {
        this.subject = subject;
        this.description = description;
        this.comments = comments;
    }


    public String getSubject() {
        return subject;
    }

    public ForumThread setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ForumThread setDescription(String description) {
        this.description = description;
        return this;
    }

    public List<String> getComments() {
        return comments;
    }

    public ForumThread setComments(List<String> comments) {
        this.comments = comments;
        return this;
    }

    @Override
    public String toString() {
        return "Thread{" +
                "subject='" + subject + '\'' +
                ", description='" + description + '\'' +
                ", comments=" + comments +
                '}';
    }
}
