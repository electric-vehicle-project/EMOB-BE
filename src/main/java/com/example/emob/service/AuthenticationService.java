/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.ErrorCode;
import com.example.emob.entity.Account;
import com.example.emob.entity.Otp;
import com.example.emob.entity.RefreshToken;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.AccountMapper;
import com.example.emob.model.request.LoginRequest;
import com.example.emob.model.request.OtpRequest;
import com.example.emob.model.request.RegisterRequest;
import com.example.emob.model.request.TokenRequest;
import com.example.emob.model.response.APIResponse;
import com.example.emob.model.response.AccountResponse;
import com.example.emob.model.response.OtpResponse;
import com.example.emob.repository.AccountRepository;
import com.example.emob.repository.OtpRepository;
import com.example.emob.service.impl.IAuthentication;
import com.example.emob.util.AccountUtil;
import com.example.emob.util.NotificationHelper;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService implements IAuthentication, UserDetailsService {
  @Autowired OtpRepository otpRepository;

  @Autowired EmailService emailService;

  @Autowired PasswordEncoder passwordEncoder;

  @Autowired AuthenticationManager authenticationManager;

  @Autowired AccountMapper accountMapper;

  @Autowired TokenService tokenService;

  @Autowired AccountRepository accountRepository;

  @Autowired RefreshTokenService refreshTokenService;

  private final SecureRandom secureRandom = new SecureRandom();

  final long DURATION = 300L;

  @Override
  public void forgotPassword(OtpRequest request) {
    Otp otp = new Otp();
    Account account = accountRepository.findAccountByEmail(request.getEmail());
    if (account == null) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }
    // tạo otp sinh ra 6 số
    String otpCode = String.format("%05d", secureRandom.nextInt(100_000));
    // lưu vào redis và sẽ hết hạn 5p
    otp.setOtp(otpCode);
    otp.setTtl(DURATION);
    otp.setAccountId(account.getId().toString());
    otpRepository.save(otp);

    // gửi email
    emailService.sendEmail(
        "Đặt lại mật khẩu của bạn",
        "Đặt lại mật khẩu",
        "Khôi phục quyền truy cập tài khoản của bạn",
        NotificationHelper.OTP,
        "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!",
        "chưa truyền",
        """
                        Your OTP code is: %s
                        It will expire in 5 minutes
                        """
            .formatted(otp.getOtp()),
        "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này. "
            + "Tài khoản của bạn vẫn an toàn.",
        account.getFullName(),
        "Đặt lại mật khẩu ngay",
            account.getEmail());
  }

  @Override
  public APIResponse<OtpResponse> verifyOtp(OtpRequest request, String otpCode) {
    Account account = accountRepository.findAccountByEmail(request.getEmail());
    if (account == null) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }
    Otp otp =
        otpRepository.findByAccountId(account.getId().toString()).stream()
            .findFirst()
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    if (otp == null) {
      throw new RuntimeException("No OTP set or expired");
    }
    if (!otp.getOtp().equals(otpCode)) {
      throw new RuntimeException("Invalid OTP");
    }

    // xóa otp
    otpRepository.delete(otp);

    // tạo ra reset token
    OtpResponse otpResponse = new OtpResponse();
    otpResponse.setToken(tokenService.generateResetToken(account));
    return APIResponse.success(otpResponse, "Verify Successfully");
  }

  @Override
  public APIResponse<Void> resetPassword(String newPassword) {
    Account account = AccountUtil.getCurrentUser();
    if (account == null) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }
    account.setPassword(passwordEncoder.encode(newPassword));
    accountRepository.save(account);
    return APIResponse.success(null, "Reset password successfully");
  }

  public boolean checkSpamOtp (String accountId) {
    Optional<Otp> otpToken = otpRepository.findById(accountId);
    if (otpToken.isEmpty()) return true;
    Otp otp = otpToken.get();
    return otp.getResendCount() < 3;
  }

  @Override
  public void resendOtp() {
    Account account = AccountUtil.getCurrentUser();
    if (account == null) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }
    if (checkSpamOtp(account.getId().toString())) {
      throw new GlobalException(ErrorCode.TOO_MANY_OTP);
    }
    String newOtpCode = String.format("%05d", secureRandom.nextInt(100_000));
    Otp newOtp = new Otp();
    newOtp.setOtp(newOtpCode);
    newOtp.setTtl(DURATION);
    newOtp.setAccountId(account.getId().toString());
    // check spam otp
    newOtp.setResendCount(newOtp.getResendCount() + 1);
    otpRepository.save(newOtp);
    // Gửi lại email
    emailService.sendEmail(
            "Mã OTP mới của bạn",
            "Gửi lại OTP",
            "Mã OTP xác thực tài khoản",
            NotificationHelper.OTP,
            "Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!",
            "chưa truyền",
            """
                            Mã OTP mới của bạn là: %s
                            Mã này sẽ hết hạn sau 5 phút.
                            """.formatted(newOtpCode),
            "Nếu bạn không yêu cầu gửi lại, vui lòng bỏ qua email này.",
            account.getFullName(),
            "Xác thực ngay",
            account.getEmail()
    );
  }


  @Override
  public APIResponse<AccountResponse> login(LoginRequest request) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
      Object principal = authentication.getPrincipal();
      if (!(principal instanceof Account)) {
        throw new GlobalException(ErrorCode.INVALID_CREDENTIALS);
      }
      Account account = (Account) principal;

      AccountResponse accountResponse = accountMapper.toAccountResponse(account);

      String accessToken = tokenService.generateToken(account);
      accountResponse.setToken(accessToken);

      String refreshToken = refreshTokenService.createRefreshToken(account).getToken();
      accountResponse.setRefreshToken(refreshToken);

      return APIResponse.success(accountResponse, "Login Successful");

    } catch (BadCredentialsException ex) {
      // Sai email hoặc password
      throw new GlobalException(ErrorCode.INVALID_CREDENTIALS);
    } catch (UsernameNotFoundException ex) {
      // UserDetailsService không tìm thấy user
      throw new GlobalException(ErrorCode.NOT_FOUND);
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new GlobalException(ErrorCode.INVALID_CREDENTIALS);
    }
  }

  @Override
  public APIResponse<AccountResponse> register(RegisterRequest request) {
    // Map RegisterRequest => Account
    Account account = accountMapper.toAccount(request);

    try {
      // Mã hóa mật khẩu trước khi lưu
      account.setPassword(passwordEncoder.encode(request.getPassword()));

      // Lưu tài khoản vào DB
      Account newAccount = accountRepository.save(account);

      return APIResponse.success(accountMapper.toAccountResponse(newAccount), "Login Successful");

    } catch (Exception e) {
      // Kiểm tra lỗi từ database
      String errorMessage = e.getMessage().toLowerCase();
      //      AuthenticationController.log.info(errorMessage);
      if (errorMessage.contains("email")) {
        throw new GlobalException(ErrorCode.EMAIL_EXISTED);
      } else if (errorMessage.contains("phone")) {
        throw new GlobalException(ErrorCode.PHONE_EXISTED);
      } else {
        throw new GlobalException(ErrorCode.OTHER);
      }
    }
  }

  @Override
  public APIResponse<AccountResponse> refresh(TokenRequest refreshRequest) {
    RefreshToken validToken =
        refreshTokenService
            .verifyToken(refreshRequest.getToken())
            .orElseThrow(() -> new GlobalException(ErrorCode.INVALID_REFRESH_TOKEN));
    Account account = accountRepository.findAccountById(UUID.fromString(validToken.getAccountId()));

    // rotation: revoke cũ + cấp mới
    RefreshToken newRefreshToken =
        refreshTokenService.rotateToken(refreshRequest.getToken(), account);
    String newToken = tokenService.generateToken(account);
    AccountResponse accountResponse = accountMapper.toAccountResponse(account);
    accountResponse.setRefreshToken(newRefreshToken.getToken());
    accountResponse.setToken(newToken);
    return APIResponse.success(accountResponse, "Refresh Successful");
  }

  @Override
  public void logout(TokenRequest refreshRequest) {
    refreshTokenService.revokeToken(refreshRequest.getToken());
  }

  @Override
  @NonNull
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return accountRepository.findAccountByEmail(email);
  }
}
