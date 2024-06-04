package Journey.Together.domain.dairy.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.StyledEditorKit;
import java.util.List;

public record PlanReviewReq(
        float grade,
        String content,
        List<MultipartFile> images,
        Boolean isPublic
) {
}
