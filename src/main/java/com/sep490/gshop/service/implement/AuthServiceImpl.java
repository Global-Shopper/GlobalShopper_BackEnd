package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.common.constants.ErrorCode;
import com.sep490.gshop.common.enums.CacheType;
import com.sep490.gshop.common.enums.UserRole;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.config.handler.RedirectException;
import com.sep490.gshop.config.handler.RedirectMessage;
import com.sep490.gshop.config.security.jwt.JwtUtils;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.payload.dto.UserDTO;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;
import com.sep490.gshop.service.AuthService;
import com.sep490.gshop.service.EmailService;
import com.sep490.gshop.service.TypedCacheService;
import com.sep490.gshop.utils.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    private UserBusiness userBusiness;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private ModelMapper modelMapper;

    private TypedCacheService<String,String> typedCacheService;


    @Autowired
    public AuthServiceImpl(UserBusiness userBusiness, PasswordEncoder passwordEncoder, EmailService emailService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, ModelMapper modelMapper, TypedCacheService<String,String> typedCacheService) {
        this.userBusiness = userBusiness;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.modelMapper = modelMapper;
        this.typedCacheService = typedCacheService;
    }

    @Override
    public AuthUserResponse login(String email, String password) {
        try {
            log.debug("login() AuthServiceImpl Start | email: {}",email );
            User user = userBusiness.getUserByEmail(email);
            if (user == null) {
                throw new AppException(401, "Email chưa được đăng ký");
            }
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new AppException(401, "Mật khẩu không đúng");
            }
            if (!user.isEmailVerified()) {
                sendOTP(user.getEmail(), user.getName());
                throw new RedirectException("Vui lòng xác thực email trước khi đăng nhập", 401, ErrorCode.EMAIL_UNCONFIRMED);
            } else {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(email, password));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);
                UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                log.debug("login() AuthServiceImpl End |");
                return new AuthUserResponse(jwt,userDTO);
            }

        } catch (Exception e) {
            log.error("login() AuthServiceImpl Exception | email: {}, message: {}", email, e.getMessage());
            throw e;
        }
    }

    @Override
    public RedirectMessage register(RegisterRequest registerRequest) {
        try {
            log.debug("register() AuthServiceImpl Start | email: {}", registerRequest.getEmail());
            User user = userBusiness.getUserByEmail(registerRequest.getEmail());
            if (user != null) {
                throw new AppException(400, "Email đã được đăng ký");
            }
            user = new Customer();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setName(registerRequest.getName());
            user.setPhone(registerRequest.getPhone());
            user.setAddress(registerRequest.getAddress());
            user.setAvatar(registerRequest.getAvatar());
            user.setGender(registerRequest.getGender());
            user.setRole(UserRole.CUSTOMER);
            user = userBusiness.create(user);
            sendOTP(user.getEmail(), user.getName());
            log.debug("register() AuthServiceImpl End |");
            return RedirectMessage.builder()
                    .message("Đăng ký thành công. Vui lòng kiểm tra email để xác thực tài khoản")
                    .errorCode(ErrorCode.EMAIL_UNCONFIRMED)
                    .build();
        } catch (Exception e) {
            log.error("register() AuthServiceImpl Exception | email: {}, message: {}", registerRequest.getEmail(), e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthUserResponse verifyOtp(String email, String otp) {
        try {
            log.debug("verifyOtp() AuthServiceImpl Start | email: {}", email);
            User user = userBusiness.getUserByEmail(email);
            if (user == null) {
                throw new AppException(404, "Email không tồn tại");
            }
            if (!user.isEmailVerified()) {
                String cachedOtp = typedCacheService.get(CacheType.OTP,email);
                if (cachedOtp == null) {
                    throw new AppException(400, "Mã OTP đã hết hạn hoặc không tồn tại");
                }
                if (!cachedOtp.equals(otp)) {
                    throw new AppException(400, "Mã OTP không đúng");
                } else {
                    user.setEmailVerified(true);
                    typedCacheService.remove(CacheType.OTP, email);
                    user = userBusiness.update(user);
                    log.debug("verifyOtp() AuthServiceImpl End | Email verified successfully");
                    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                    String jwt = jwtUtils.generateJwtToken(user);
                    return new AuthUserResponse(jwt, userDTO);
                }
            } else {
                log.debug("verifyOtp() AuthServiceImpl End | Email already verified");
                throw new AppException(400, "Email đã được xác thực trước đó");
            }
        } catch (Exception e) {
            log.error("verifyOtp() AuthServiceImpl Exception | email: {}, message: {}", email, e.getMessage());
            throw e;
        }
    }

    private void sendOTP(String email, String name){
        String otp = RandomUtil.randomNumber(6);
        typedCacheService.put(CacheType.OTP, email, otp);
        emailService.sendEmail(email,
                "Chào mừng bạn đến với GShop",
                "Cảm ơn "+ name + " đã đăng ký tài khoản tại GShop. Mã OTP của bạn là: " + otp );
    }
}
