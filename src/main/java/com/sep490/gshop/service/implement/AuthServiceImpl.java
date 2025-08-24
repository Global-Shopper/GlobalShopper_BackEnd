package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.CustomerBusiness;
import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.business.WalletBusiness;
import com.sep490.gshop.common.constants.ErrorCode;
import com.sep490.gshop.common.enums.CacheType;
import com.sep490.gshop.common.enums.UserRole;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.config.handler.ErrorException;
import com.sep490.gshop.config.security.jwt.JwtUtils;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.entity.Wallet;
import com.sep490.gshop.payload.dto.UserDTO;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;
import com.sep490.gshop.payload.response.MessageResponse;
import com.sep490.gshop.payload.response.ResetPasswordValidResponse;
import com.sep490.gshop.service.AuthService;
import com.sep490.gshop.service.EmailService;
import com.sep490.gshop.service.TypedCacheService;
import com.sep490.gshop.utils.AuthUtils;
import com.sep490.gshop.utils.DateTimeUtil;
import com.sep490.gshop.utils.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class AuthServiceImpl implements AuthService {

    private static final int MAX_RETRY = 5;
    @Value("${global-shopper.base-url}")
    private String baseUrl;

    private final UserBusiness userBusiness;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final ModelMapper modelMapper;

    private final TypedCacheService<String,String> typedCacheService;

    private final TypedCacheService<String,Integer> failCountCache;
    private CustomerBusiness customerBusiness;
    private WalletBusiness walletBusiness;

    @Autowired
    public AuthServiceImpl(UserBusiness userBusiness, PasswordEncoder passwordEncoder, EmailService emailService, AuthenticationManager authenticationManager, WalletBusiness walletBusiness, JwtUtils jwtUtils, ModelMapper modelMapper, TypedCacheService<String,String> typedCacheService, TypedCacheService<String, Integer> failCountCache, CustomerBusiness customerBusiness) {
        this.userBusiness = userBusiness;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.modelMapper = modelMapper;
        this.typedCacheService = typedCacheService;
        this.failCountCache = failCountCache;
        this.customerBusiness = customerBusiness;
        this.walletBusiness = walletBusiness;
    }

    @Override
    public AuthUserResponse login(String email, String password) {
        try {
            log.debug("login() AuthServiceImpl Start | email: {}",email );
            User user = userBusiness.getUserByEmail(email);
            if (user == null) {
                throw new AppException(401, "Email chưa được đăng ký");
            }
            if (!user.isActive()) {
                throw new AppException(401, "Tài khoản của bạn đã bị khóa");
            }
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new AppException(401, "Mật khẩu không đúng");
            }
            if (!user.isEmailVerified()) {
                String cachedOtp = typedCacheService.get(CacheType.OTP, email);
                if (cachedOtp != null) {
                    throw ErrorException.builder()
                            .message("Hệ thống đã gửi mã OTP đến email của bạn. Vui lòng xác thực email trước khi đăng nhập!")
                            .httpCode(401)
                            .errorCode(ErrorCode.EMAIL_UNCONFIRMED)
                            .build();
                }
                sendOTPWithTemplate(user.getEmail(), user.getName(), CacheType.OTP);
                throw ErrorException.builder()
                        .message("Email chưa được xác thực. Vui lòng kiểm tra email để xác thực tài khoản")
                        .httpCode(401)
                        .errorCode(ErrorCode.EMAIL_UNCONFIRMED)
                        .build();
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
    public MessageResponse register(RegisterRequest registerRequest) {
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
            sendOTPWithTemplate(user.getEmail(), user.getName(), CacheType.OTP);
            log.debug("register() AuthServiceImpl End |");
            return MessageResponse.builder()
                    .message("Đăng ký thành công. Vui lòng kiểm tra email để xác thực tài khoản")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("register() AuthServiceImpl Exception | email: {}, message: {}", registerRequest.getEmail(), e.getMessage());
            throw e;
        }
    }


    @Override
    public MessageResponse resendOtp(String email) {
        try {
            log.debug("resendOtp() AuthServiceImpl Start | email: {}", email);
            Integer failCount = failCountCache.get(CacheType.OTP_ATTEMPT, email);
            if (failCount != null && failCount >= MAX_RETRY) {
                String timeRemain = DateTimeUtil.secondToTime(failCountCache.getTimeRemaining(CacheType.OTP_ATTEMPT, email));
                throw new AppException(429,"Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau " + timeRemain + ".");
            }
            User user = userBusiness.getUserByEmail(email);
            if (user == null) {
                throw new AppException(404, "Email không tồn tại");
            }
            if (user.isEmailVerified()) {
                log.debug("resendOtp() AuthServiceImpl End | Email already verified");
                throw ErrorException.builder()
                        .message("Email đã được xác thực trước đó! Bạn có thể đăng nhập ngay bây giờ.")
                        .httpCode(400)
                        .errorCode(ErrorCode.ALREADY_VERIFIED)
                        .build();
            } else {
                String cachedOtp = typedCacheService.get(CacheType.OTP, email);
                long timeRemain = typedCacheService.getTimeRemaining(CacheType.OTP, email);
                if (cachedOtp != null && timeRemain > 60*(CacheType.OTP.getTtlMinutes() - 1)) {
                    String timeRemainStr = DateTimeUtil.secondToTime(timeRemain - 60*(CacheType.OTP.getTtlMinutes() - 1));
                    log.debug("resendOtp() AuthServiceImpl End | Mã OTP đã được gửi trước đó và còn hiệu lực");
                    throw new AppException(400, "Vui lòng đợi " + timeRemainStr + " để gửi lại mã OTP mới");

                }
                sendOTPWithTemplate(user.getEmail(), user.getName(), CacheType.OTP);
                log.debug("resendOtp() AuthServiceImpl End | Mã OTP đã được gửi lại");
                return MessageResponse.builder()
                        .message("Mã OTP đã được gửi lại. Vui lòng kiểm tra email")
                        .isSuccess(true)
                        .build();
            }
        } catch (Exception e) {
            log.error("resendOtp() AuthServiceImpl Exception | email: {}, message: {}", email, e.getMessage());
            throw e;
        }
    }

    @Override
    public MessageResponse forgotPassword(String email) {
        try {
            log.debug("forgotPassword() AuthServiceImpl Start | email: {}", email);

            Integer failCount = failCountCache.get(CacheType.OTP_ATTEMPT, email);
            if (failCount != null && failCount >= MAX_RETRY) {
                String timeRemain = DateTimeUtil.secondToTime(failCountCache.getTimeRemaining(CacheType.OTP_ATTEMPT, email));
                throw new AppException(429, "Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau " + timeRemain + ".");
            }

            User user = userBusiness.getUserByEmail(email);
            if (user == null) {
                throw new AppException(400, "Email chưa được đăng ký hoặc không tồn tại");
            }

            String cachedOtp = typedCacheService.get(CacheType.OTP_RESET_PASSWORD, email);
            long timeRemain = typedCacheService.getTimeRemaining(CacheType.OTP_RESET_PASSWORD, email);
            long otpTtlSeconds = TimeUnit.MINUTES.toSeconds(CacheType.OTP_RESET_PASSWORD.getTtlMinutes());

            if (cachedOtp != null && timeRemain > otpTtlSeconds - 60) {
                String timeRemainStr = DateTimeUtil.secondToTime(timeRemain - (otpTtlSeconds - 60));
                log.debug("forgotPassword() AuthServiceImpl End | OTP đã được gửi trước đó");
                throw new AppException(400, "Vui lòng đợi " + timeRemainStr + " để gửi lại mã OTP mới");
            }

            sendOTPWithTemplate(user.getEmail(), user.getName(), CacheType.OTP_RESET_PASSWORD);

            return MessageResponse.builder()
                    .message("Mã OTP đã được gửi đến email của bạn. Vui lòng kiểm tra email để đặt lại mật khẩu")
                    .isSuccess(true)
                    .build();

        } catch (AppException ae) {
            log.error("forgotPassword() AppException | email: {}, message: {}", email, ae.getMessage());
            throw ae;
        } catch (Exception e) {
            log.error("forgotPassword() Unexpected Exception | email: {}, message: {}", email, e.getMessage());
            throw e;
        }
    }

    @Override
    public MessageResponse changePassword(String oldPassword, String newPassword) {
        log.debug("changePassword() Start | userId: {}", AuthUtils.getCurrentUserId());
        try {
            var currentUserId = AuthUtils.getCurrentUserId();
            if (currentUserId == null) {
                throw AppException.builder().code(401).message("Bạn cần đăng nhập để sử dụng dịch vụ").build();
            }
            Customer customer = customerBusiness.getById(currentUserId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy khách hàng với ID: " + currentUserId));

            if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
                throw AppException.builder().code(400).message("Mật khẩu cũ không đúng").build();
            }

            if (newPassword.equals(oldPassword)) {
                throw AppException.builder().code(400).message("Mật khẩu mới không được trùng với mật khẩu cũ").build();
            }

            customer.setPassword(passwordEncoder.encode(newPassword));
            customerBusiness.update(customer);

            log.debug("changePassword() End | userId: {}, message: Cập nhật mật khẩu thành công", currentUserId);
            return MessageResponse.builder()
                    .message("Cập nhật mật khẩu thành công")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("changePassword() Exception | message: {}", e.getMessage());
            throw e;
        }
    }



    @Override
    public ResetPasswordValidResponse verifyOtpResetPassword(String otp, String email) {
        try {
            if (otp == null || otp.trim().isEmpty()) {
                throw new AppException(400, "OTP không được để trống");
            }

            Integer failCount = failCountCache.get(CacheType.OTP_ATTEMPT, email);
            if (failCount != null && failCount >= MAX_RETRY) {
                String timeRemain = DateTimeUtil.secondToTime(failCountCache.getTimeRemaining(CacheType.OTP_ATTEMPT, email));
                throw new AppException(429, "Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau " + timeRemain + ".");
            }

            String cachedOtp = typedCacheService.get(CacheType.OTP_RESET_PASSWORD, email);
            if (cachedOtp == null) {
                throw new AppException(400, "Mã OTP đã hết hạn hoặc không tồn tại");
            }

            if (!cachedOtp.equals(otp)) {
                failCountCache.put(CacheType.OTP_ATTEMPT, email, (failCount == null ? 1 : failCount + 1));
                throw new AppException(400, "Mã OTP không đúng");
            }
            typedCacheService.remove(CacheType.OTP_RESET_PASSWORD, email);
            failCountCache.remove(CacheType.OTP_ATTEMPT, email);

            String tempToken = jwtUtils.generateTempToken(email);

            return new ResetPasswordValidResponse("Xác thực OTP thành công", tempToken);

        } catch (AppException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AppException(500, "Lỗi hệ thống, vui lòng thử lại sau");
        }
    }
//Implement ví ở đâyy

    @Override
    public AuthUserResponse verifyOtp(String email, String otp) {
        try {
            log.debug("verifyOtp() AuthServiceImpl Start | email: {}", email);
            Integer failCount = failCountCache.get(CacheType.OTP_ATTEMPT, email);
            if (failCount != null && failCount >= MAX_RETRY) {
                String timeRemain = DateTimeUtil.secondToTime(failCountCache.getTimeRemaining(CacheType.OTP_ATTEMPT, email));
                throw new AppException(429,"Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau " + timeRemain + ".");
            }
            User user = userBusiness.getUserByEmail(email);

            if (user == null) {
                throw new AppException(404, "Email không tồn tại");
            }
            if (!user.isEmailVerified()) {
                String cachedOtp = typedCacheService.get(CacheType.OTP,email);
                if (cachedOtp == null) {
                    throw new AppException(400, "Mã OTP không đúng hoặc hết hạn");
                }
                if (!cachedOtp.equals(otp)) {
                    failCountCache.put(CacheType.OTP_ATTEMPT, email, (failCount == null ? 1 : failCount + 1));
                    throw new AppException(400, "Mã OTP không đúng hoặc hết hạn");
                } else {
                    user.setEmailVerified(true);
                    Wallet newWallet = new Wallet();
                    Customer customer = customerBusiness.getById(user.getId()).orElseThrow(() -> new AppException(404, "Không có user trong hệ thống"));
                    newWallet.setCustomer(customer);
                    walletBusiness.update(newWallet);
                    typedCacheService.remove(CacheType.OTP, email);
                    failCountCache.remove(CacheType.OTP_ATTEMPT, email);
                    user = userBusiness.update(user);
                    UserDTO userDTO = modelMapper.map(user, UserDTO.class);
                    String jwt = jwtUtils.generateJwtToken(user);
                    log.debug("verifyOtp() AuthServiceImpl End | Email verified successfully");
                    return new AuthUserResponse(jwt, userDTO);
                }
            } else {
                log.debug("verifyOtp() AuthServiceImpl End | Email already verified");
                throw ErrorException.builder()
                        .message("Email đã được xác thực trước đó! Bạn có thể đăng nhập ngay bây giờ.")
                        .httpCode(400)
                        .errorCode(ErrorCode.ALREADY_VERIFIED)
                        .build();
            }
        } catch (Exception e) {
            log.error("verifyOtp() AuthServiceImpl Exception | email: {}, message: {}", email, e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthUserResponse resetPassword(String newPassword, String token) {
        try {
            log.debug("resetPassword() AuthServiceImpl Start");

            String email = jwtUtils.getEmailFromToken(token);
            if (email == null || email.isEmpty()) {
                throw ErrorException.builder()
                        .message("Token xác thực không hợp lệ hoặc đã hết hạn.")
                        .httpCode(400)
                        .errorCode(ErrorCode.EXPIRED_OTP)
                        .build();
            }
            User user = userBusiness.getUserByEmail(email);
            if (user == null) {
                throw new AppException(404, "Email không tồn tại");
            }

            if (passwordEncoder.matches(newPassword, user.getPassword())) {
                throw new AppException(400, "Mật khẩu mới trùng với mật khẩu cũ");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            user.setEmailVerified(true);

            user = userBusiness.update(user);

            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            String jwt = jwtUtils.generateJwtToken(user);

            log.debug("resetPassword() AuthServiceImpl End | password updated successfully");
            return new AuthUserResponse(jwt, userDTO);

        } catch (AppException ae) {
            log.error("resetPassword() AppException | message: {}", ae.getMessage());
            throw ae;
        } catch (Exception e) {
            log.error("resetPassword() Unexpected Exception | message: {}", e.getMessage());
            throw e;
        }
    }



    @Override
    public MessageResponse changeMail() {
        UUID currentId = AuthUtils.getCurrentUserId();
        log.debug("changeMail() Start | userId: {}", currentId);
        try {
            if (currentId == null) {
                throw AppException.builder()
                        .message("Bạn cần đăng nhập để sử dụng dịch vụ")
                        .code(401)
                        .build();
            }
            Customer customer = customerBusiness.getById(currentId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy khách hàng với ID: " + currentId));

            String currentEmail = customer.getEmail();
            if (currentEmail == null) {
                return MessageResponse.builder()
                        .message("Email của bạn hiện không tồn tại, vui lòng thử lại")
                        .isSuccess(false)
                        .build();
            }
            sendOTPWithTemplate(currentEmail, customer.getName(), CacheType.OTP_CHANGE_MAIL);
            log.debug("changeMail() End | userId: {}, message: OTP đã được gửi đến email hiện tại", currentId);
            return MessageResponse.builder()
                    .message("Hãy xác nhận email hiện tại của bạn còn khả dụng trước khi tiếp tục. Mã OTP đã được gửi đến email để xác nhận.")
                    .isSuccess(true)
                    .build();
        } catch (AppException ae) {
            log.error("changeMail() AppException | userId: {}, message: {}", currentId, ae.getMessage());
            throw ae;
        } catch (Exception e) {
            log.error("changeMail() Unexpected Exception | userId: {}, message: {}", currentId, e.getMessage());
            throw e;
        }
    }
    @Override
    public ResponseEntity<Void> verifyToUpdateEmail(String token) {
        log.debug("verifyToUpdateEmail() Start | token: {}", token);
        try {
            if (!jwtUtils.validateJwtToken(token)) {
                throw new AppException(400, "Token xác thực không hợp lệ hoặc đã hết hạn");
            }
            UUID userId = jwtUtils.getUserIdFromToken(token);
            String email = jwtUtils.getEmailFromToken(token);
            User user = userBusiness.getById(userId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng"));

            user.setEmail(email);
            userBusiness.update(user);
            var jwt = jwtUtils.generateJwtToken(user);
            log.debug("verifyToUpdateEmail() End | Email verified successfully for email: {}", email);

            String redirectUrl = "http://localhost:5173/account-center?token=" + URLEncoder.encode(jwt, StandardCharsets.UTF_8);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirectUrl)
                    .build();

        } catch (Exception e) {
            log.error("verifyToUpdateEmail() Unexpected Exception | message: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthUserResponse loginByToken() {
        try {
            log.debug("loginByToken() AuthServiceImpl Start");
            UUID currentUserId = AuthUtils.getCurrentUserId();
            User user = userBusiness.getById(currentUserId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy người dùng với id: " + currentUserId));
            String jwt = jwtUtils.generateJwtToken(user);
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            log.debug("loginByToken() AuthServiceImpl End");
            return new AuthUserResponse(jwt, userDTO);
        } catch (Exception e) {
            log.error("loginByToken() AuthServiceImpl Exception | message: {}", e.getMessage());
            throw e;
        }
    }


    @Override
    public MessageResponse verifyMail(String otp, String newMail) {
        log.debug("verifyMail() Start | email: {}", newMail);
        try {
            Integer failCount = failCountCache.get(CacheType.OTP_ATTEMPT, newMail);
            if (failCount != null && failCount >= MAX_RETRY) {
                String timeRemain = DateTimeUtil.secondToTime(failCountCache.getTimeRemaining(CacheType.OTP_ATTEMPT, newMail));
                throw new AppException(429, "Bạn đã nhập sai quá nhiều lần. Vui lòng thử lại sau " + timeRemain + ".");
            }
            UUID userId = AuthUtils.getCurrentUserId();
            User user = userBusiness.getById(userId)
                    .orElseThrow(() -> new AppException(404, "Không tìm thấy khách hàng với ID: " + userId));
            if (user.getEmail().equalsIgnoreCase(newMail)) {
                throw new AppException(400, "Email trùng với email hiện tại");
            }
            if(customerBusiness.existsByEmail(newMail)){
                throw AppException.builder().message("Email trùng với email có trong hệ thống, hãy thử mail khác").code(400).build();
            }
            String cachedOtp = typedCacheService.get(CacheType.OTP_CHANGE_MAIL, user.getEmail());
            if (cachedOtp == null || !cachedOtp.equals(otp)) {
                failCountCache.put(CacheType.OTP_ATTEMPT, newMail, (failCount == null ? 1 : failCount + 1));
                throw new AppException(400, "Mã OTP không đúng hoặc đã hết hạn");
            }

            typedCacheService.remove(CacheType.OTP_CHANGE_MAIL, newMail);
            failCountCache.remove(CacheType.OTP_ATTEMPT, newMail);

            var response = sendVerificationEmail(user, newMail);

            log.debug("verifyMail() End | Email verified successfully for email: {}", newMail);
            return response;
        }catch (Exception e) {
            log.error("verifyMail() Unexpected Exception | email: {}, message: {}", newMail, e.getMessage());
            throw e;
        }
    }

    public MessageResponse sendVerificationEmail(User user, String email) {
        log.debug("sendVerificationEmail() Start | email: {}", email);
        try {
            String token = jwtUtils.generateTempTokenChangeMail(user, email);
            String verificationLink = baseUrl + "/auth/verify-new-email?token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);
            Context context = new Context();
            context.setVariable("verificationLink", verificationLink);
            context.setVariable("email", email);
            context.setVariable("name", user.getName());
            emailService.sendEmailTemplate(email, user.getName(), verificationLink, "urlMailTemplate", context);
            log.debug("sendVerificationEmail() End | Verification email sent to: {}", email);
            return MessageResponse.builder()
                    .message("Đã gửi xác thực đến email của bạn, vui lòng check mail")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("sendVerificationEmail() Unexpected Exception | email: {}, message: {}", email, e.getMessage());
            throw e;
        }
    }

    private void sendOTP(String email, String name, CacheType cacheType) {
        String otp = RandomUtil.randomNumber(6);
        typedCacheService.put(cacheType, email, otp);
        emailService.sendEmail(email,
                "Chào mừng bạn đến với GShop",
                "Cảm ơn "+ name + " đã sử dụng dịch vụ tại GShop. Mã OTP của bạn là: " + otp );
    }
    private void sendOTPWithTemplate(String email, String name, CacheType cacheType) {
        String otp = RandomUtil.randomNumber(6);
        typedCacheService.put(cacheType, email, otp);
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("email", email);
        context.setVariable("otp", otp);
        emailService.sendEmailTemplate(email, name, otp, "otpMailTemplate", context);
    }
}
