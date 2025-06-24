package com.sep490.gshop.service.implement;

import com.sep490.gshop.business.UserBusiness;
import com.sep490.gshop.common.UserRole;
import com.sep490.gshop.config.handler.AppException;
import com.sep490.gshop.config.security.jwt.JwtUtils;
import com.sep490.gshop.entity.Customer;
import com.sep490.gshop.entity.User;
import com.sep490.gshop.payload.dto.UserDTO;
import com.sep490.gshop.payload.request.RegisterRequest;
import com.sep490.gshop.payload.response.AuthUserResponse;
import com.sep490.gshop.service.AuthService;
import com.sep490.gshop.service.EmailService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class AuthServiceImpl implements AuthService {

    private UserBusiness userBusiness;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private AuthenticationManager authenticationManager;
    private JwtUtils jwtUtils;
    private ModelMapper modelMapper;


    @Autowired
    public AuthServiceImpl(UserBusiness userBusiness, PasswordEncoder passwordEncoder, EmailService emailService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, ModelMapper modelMapper) {
        this.userBusiness = userBusiness;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.modelMapper = modelMapper;
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
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            log.debug("login() AuthServiceImpl End |");
            return new AuthUserResponse(jwt,userDTO);
        } catch (Exception e) {
            log.error("Login failed for email: {}", email, e);
            throw e;
        }
    }

    @Override
    public AuthUserResponse register(RegisterRequest registerRequest) {
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
            emailService.sendEmail(registerRequest.getEmail(),
                    "Chào mừng bạn đến với GShop",
                    "Cảm ơn "+ registerRequest.getName() + " đã đăng ký tài khoản tại GShop. Chúng tôi hy vọng bạn sẽ có những trải nghiệm tuyệt vời!");
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(registerRequest.getEmail(),registerRequest.getPassword() ));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            log.debug("register() AuthServiceImpl End |");
            return new AuthUserResponse(jwt, userDTO);
        } catch (Exception e) {
            log.error("Register failed for email: {}", registerRequest.getEmail(), e);
            throw e;
        }
    }
}
