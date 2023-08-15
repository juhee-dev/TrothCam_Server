package trothly.trothcam.service.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trothly.trothcam.domain.like.LikeProduct;
import trothly.trothcam.domain.like.LikeProductRepository;
import trothly.trothcam.domain.member.Member;
import trothly.trothcam.domain.product.Product;
import trothly.trothcam.domain.product.ProductRepository;
import trothly.trothcam.dto.web.ProductReqDto;
import trothly.trothcam.dto.web.LikeResDto;
import trothly.trothcam.exception.base.BaseException;
import trothly.trothcam.exception.custom.BadRequestException;

import java.util.Optional;

import static trothly.trothcam.exception.base.ErrorCode.ALREADY_LIKED;
import static trothly.trothcam.exception.base.ErrorCode.NOT_LIKED;

@Service
@Transactional
@RequiredArgsConstructor
public class LikeProductService {

    private final LikeProductRepository likeProductRepository;
    private final ProductRepository productRepository;

    // 좋아요 저장
    public LikeResDto saveLike(ProductReqDto req, Member member) {
        Optional<LikeProduct> like = likeProductRepository.findByProductIdAndMemberId(req.getProductId(), member.getId());

        if(like.isPresent()) {
            throw new BaseException(ALREADY_LIKED);
        }

        Product product = productRepository.findById(req.getProductId()).orElseThrow(
                () -> new BadRequestException("존재하지 않는 상품입니다.")
        );

        LikeProduct newLike = likeProductRepository.save(new LikeProduct(product, member));

        return new LikeResDto("좋아요 성공");
    }

    // 좋아요 삭제
    public LikeResDto deleteLike(ProductReqDto req, Member member) {
        LikeProduct likeProduct = likeProductRepository.findByProductIdAndMemberId(req.getProductId(), member.getId()).orElseThrow(
                () -> new BaseException(NOT_LIKED)
        );

        likeProductRepository.deleteById(likeProduct.getId());

        return new LikeResDto("좋아요 해제 성공");
    }
}