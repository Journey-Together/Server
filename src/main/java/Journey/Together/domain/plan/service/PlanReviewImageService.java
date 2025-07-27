package Journey.Together.domain.plan.service;

import Journey.Together.domain.plan.entity.PlanReview;
import Journey.Together.domain.plan.entity.PlanReviewImage;
import Journey.Together.domain.plan.repository.PlanReviewImageRepository;
import Journey.Together.domain.plan.service.factory.PlanReviewImageFactory;
import Journey.Together.global.util.S3Client;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private final PlanReviewImageFactory planReviewImageFactory;

    public void uploadAndSaveImages(List<MultipartFile> images, PlanReview planReview, String profileUuid) {
        for (MultipartFile file : images) {
            String uuid = UUID.randomUUID().toString();
            String url = s3Client.upload(file, profileUuid, uuid);
            PlanReviewImage image = planReviewImageFactory.createPlanReviewImage(planReview,url);
            planReviewImageRepository.save(image);
        }
    }

    public List<String> getImageUrls(PlanReview planReview) {
        return planReviewImageRepository.findAllByPlanReviewAndDeletedAtIsNull(planReview)
                .stream()
                .map(PlanReviewImage::getImageUrl)
                .collect(Collectors.toList());
    }

    public void deleteImages(List<String> deleteImgUrls) {
        for (String url : deleteImgUrls) {
            planReviewImageRepository.deletePlanReviewImageByImageUrl(url);
            s3Client.delete(StringUtils.substringAfter(url, "com/"));
        }
    }

    public void deleteAllImages(PlanReview planReview) {
        List<PlanReviewImage> images = planReviewImageRepository.findAllByPlanReviewAndDeletedAtIsNull(planReview);

        for (PlanReviewImage image : images) {
            String filename = image.getImageUrl().replace(s3Client.baseUrl(), "");
            s3Client.delete(filename); // S3에서 이미지 삭제
            planReviewImageRepository.deletePlanReviewImageByPlanReviewImageId(image.getPlanReviewImageId());
        }
    }
}
