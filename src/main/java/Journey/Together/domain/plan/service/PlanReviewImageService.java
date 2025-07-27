package Journey.Together.domain.plan.service;

import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.entity.PlanReviewImage;
import Journey.Together.domain.plan.repository.PlanReviewImageRepository;
import Journey.Together.global.util.S3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanReviewImageService {
    private final S3Client s3Client;
    private final PlanReviewImageRepository planReviewImageRepository;

    public void uploadAndSaveImages(List<MultipartFile> images, PlanReview planReview, String profileUuid) {
        for (MultipartFile file : images) {
            String uuid = UUID.randomUUID().toString();
            String url = s3Client.upload(file, profileUuid, uuid);
            PlanReviewImage image = PlanReviewImage.builder()
                    .planReview(planReview)
                    .imageUrl(url)
                    .build();
            planReviewImageRepository.save(image);
        }
    }

    public List<String> getImageUrls(PlanReview planReview) {
        return planReviewImageRepository.findAllByPlanReviewAndDeletedAtIsNull(planReview)
                .stream()
                .map(PlanReviewImage::getImageUrl)
                .collect(Collectors.toList());
    }
}
