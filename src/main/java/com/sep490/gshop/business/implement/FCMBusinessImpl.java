package com.sep490.gshop.business.implement;

import com.sep490.gshop.business.FCMBusiness;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.entity.FCMToken;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.payload.request.FCMTokenRequest;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.repository.FCMTokenRepository;
import com.sep490.gshop.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FCMBusinessImpl extends BaseBusinessImpl<FCMToken, FCMTokenRepository> implements FCMBusiness {

    private final UserRepository userRepository;
    protected FCMBusinessImpl(FCMTokenRepository repository, UserRepository userRepository) {
        super(repository);
        this.userRepository = userRepository;
    }

    @Override
    public MessageResponse saveToken(FCMTokenRequest request, UUID customerId) {
        User user = userRepository.findById(customerId).orElseThrow(()->new AppException(401,"Không tìm thấy người dùng"));
        FCMToken fcmToken = FCMToken.builder()
                .token(request.getToken())
                .user(user)
                .deviceType(request.getDeviceType())
                .isActive(true)
                .build();
        repository.save(fcmToken);
        return new MessageResponse("Lưu token thành công",true);
    }


}
