/* EMOB-2025 */
package com.example.emob.service;

import com.example.emob.constant.AccountStatus;
import com.example.emob.constant.ErrorCode;
import com.example.emob.constant.Role;
import com.example.emob.entity.Account;
import com.example.emob.entity.Dealer;
import com.example.emob.entity.Otp;
import com.example.emob.entity.RefreshToken;
import com.example.emob.exception.GlobalException;
import com.example.emob.mapper.AccountMapper;
import com.example.emob.mapper.PageMapper;
import com.example.emob.model.request.*;
import com.example.emob.model.response.*;
import com.example.emob.repository.AccountRepository;
import com.example.emob.repository.DealerRepository;
import com.example.emob.repository.OtpRepository;
import com.example.emob.service.impl.IAuthentication;
import com.example.emob.util.AccountUtil;
import com.example.emob.util.NotificationHelper;
import io.swagger.v3.oas.annotations.Operation;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
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
  @Autowired DealerRepository dealerRepository;
  @Autowired PageMapper pageMapper;

  @Autowired RedisTemplate<String, Object> redisTemplate;

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
  public APIResponse<OtpResponse> verifyOtp(OtpVerifyRequest request) {
    Account account = accountRepository.findAccountByEmail(request.getEmail());
    if (account == null) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }
    Otp otp =
        otpRepository.findByAccountId(account.getId().toString()).stream()
            .findFirst()
            .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND));
    if (otp == null) {
      throw new GlobalException(ErrorCode.INVALID_CODE, "No OTP set or expired");
    }
    if (!otp.getOtp().equals(request.getOtpCode())) {
      throw new GlobalException(ErrorCode.INVALID_CODE, "Invalid OTP");
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
    System.out.println("AccountID: " + account.getEmail());
    if (account == null) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }
    account.setPassword(passwordEncoder.encode(newPassword));
    accountRepository.save(account);
    return APIResponse.success(null, "Reset password successfully");
  }

  public boolean checkSpamOtp(String accountId) {
    // check xem tài khoản này có bị cooldown k
    //    String lockKey = "otp locked: " + accountId;
    Boolean isLocked = redisTemplate.hasKey(accountId);
    if (isLocked) {
      return true; // đang cooldown 24h
    }

    // kiểm tra OTP hiện tại trong Redis
    Optional<Otp> otpToken = otpRepository.findById(accountId);
    if (otpToken.isEmpty()) return false; // ko tìm thấy
    Otp otp = otpToken.get();
    // nếu gửi quá 3 lần
    if (otp.getResendCount() >= 3) {
      // Xóa OTP hiện tại
      otpRepository.deleteById(accountId);

      // đăt khóa 24h
      redisTemplate.opsForValue().set(accountId, otp, 24, TimeUnit.HOURS);

      return true; // bị spam => chặn
    }
    return false; // chưa spam
  }

  @Override
  public APIResponse<Void> resendOtp(String email) {
    // tìm email
    Account account = accountRepository.findAccountByEmail(email);
    if (account == null) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }

    String accountId = account.getId().toString();

    // check spam và check locked
    if (checkSpamOtp(accountId)) {
      throw new GlobalException(ErrorCode.TOO_MANY_OTP);
    }
    // tạo ra otp mới
    String newOtpCode = String.format("%05d", secureRandom.nextInt(100_000));

    // lấy ra otp hiện tại
    Optional<Otp> otpOpt = otpRepository.findById(accountId);
    Otp otp = otpOpt.orElse(new Otp());

    otp.setAccountId(accountId);
    otp.setOtp(newOtpCode);
    otp.setTtl(DURATION); // 5 phút
    //    otp.setToken(tokenService.generateResetToken(account)); // set token mới vào

    // count++ số lần gửi lại
    otp.setResendCount(otp.getResendCount() + 1);

    // lưu otp này vào redis lại
    otpRepository.save(otp);

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
                            """
            .formatted(newOtpCode),
        "Nếu bạn không yêu cầu gửi lại, vui lòng bỏ qua email này.",
        account.getFullName(),
        "Xác thực ngay",
        account.getEmail());
    return APIResponse.success(null, "Resend Otp Successfully");
  }

  @Override
  public APIResponse<Void> deleteAccount(UUID id) {
    Account account = AccountUtil.getCurrentUser();
    Account targetAccount = accountRepository.findAccountById(id);
    // nếu tài khoản tìm bằng tài khoản xóa => kh hợp lệ
    if (account.getId().equals(id)) {
      throw new GlobalException(ErrorCode.INVALID_CODE, "Cannot delete your own account");
    }
    // case tài khoản bị xóa rồi
    if (targetAccount.getStatus() == AccountStatus.BANNED) {
      throw new GlobalException(ErrorCode.INVALID_CODE, "Account is already deleted");
    }
    // Kiểm tra quyền xóa
    boolean canDelete = false;
    if (account.getRole() == Role.ADMIN) {
      canDelete = targetAccount.getRole() == Role.MANAGER ||
              targetAccount.getRole() == Role.EVM_STAFF;
    } else if (account.getRole() == Role.MANAGER) {
      canDelete = targetAccount.getRole() == Role.DEALER_STAFF;
    }
    if (!canDelete) {
      throw new GlobalException(ErrorCode.UNAUTHORIZED, "You are not allowed to delete this account");
    }
    targetAccount.setStatus(AccountStatus.BANNED);
    accountRepository.save(targetAccount);

    return APIResponse.success(null, "Delete account successfully");
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
      if (account.getDealer() != null) {
        accountResponse.setDealerId(account.getDealer().getId());
      }
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
  @PreAuthorize("hasRole('ADMIN')")
  public APIResponse<AccountResponse> registerByAdmin(RegisterRequest request) {
    // Map RegisterRequest => Account
    Account account = accountMapper.toAccount(request);
    if (request.getDealerId() != null) {
      Dealer dealer =
          dealerRepository
              .findById(request.getDealerId())
              .orElseThrow(() -> new GlobalException(ErrorCode.NOT_FOUND, "Dealer not found"));
      account.setDealer(dealer);
      // tạo account cho manager
      account.setRole(Role.MANAGER);
    } else {
      // tạo account cho evm staff
      account.setRole(Role.EVM_STAFF);
    }
    try {
      // Mã hóa mật khẩu trước khi lưu
      account.setPassword(passwordEncoder.encode(request.getPassword()));
      account.setStatus(AccountStatus.ACTIVE);
      // Lưu tài khoản vào DB
      Account newAccount = accountRepository.save(account);

      AccountResponse response = accountMapper.toAccountResponse(newAccount);
      if (account.getDealer() != null) {
        response.setDealerId(account.getDealer().getId());
      }
      return APIResponse.success(response, "Login Successful");

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
  @PreAuthorize("hasRole('MANAGER')")
  public APIResponse<AccountResponse> registerByManager(RegisterRequest request) {
    // Map RegisterRequest => Account
    Account account = accountMapper.toAccount(request);
    account.setRole(Role.DEALER_STAFF);
    // Mã hóa mật khẩu trước khi lưu
    account.setDealer(AccountUtil.getCurrentUser().getDealer());
    account.setStatus(AccountStatus.ACTIVE);
    account.setPassword(passwordEncoder.encode(request.getPassword()));
    // Lưu tài khoản vào DB
    Account newAccount = accountRepository.save(account);
    AccountResponse response = accountMapper.toAccountResponse(newAccount);
    response.setDealerId(account.getDealer().getId());
    return APIResponse.success(response, "Login Successful");
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
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public APIResponse<AccountResponse> get(UUID id) {
    Account account = accountRepository.findAccountById(id);
    if (account == null) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }

    Account current = AccountUtil.getCurrentUser();
    if (current == null) {
      throw new GlobalException(ErrorCode.NOT_FOUND);
    }

    // Dealer staff can be accessed only by manager of the same dealer
    if (account.getRole() == Role.DEALER_STAFF) {
      if (current.getRole() != Role.MANAGER
          || current.getDealer() == null
          || account.getDealer() == null
          || !current.getDealer().getId().equals(account.getDealer().getId())) {
        throw new GlobalException(ErrorCode.UNAUTHORIZED);
      }
    } else {
      // Manager and EVM_STAFF can be accessed only by admin (EVM_STAFF)
      if (account.getRole() == Role.MANAGER || account.getRole() == Role.EVM_STAFF) {
        if (current.getRole() != Role.ADMIN) {
          throw new GlobalException(ErrorCode.UNAUTHORIZED);
        }
      }
    }

    AccountResponse response = accountMapper.toAccountResponse(account);
    return APIResponse.success(response);
  }

  @Override
  @Operation(summary = "Get all by admin")
  public APIResponse<PageResponse<AccountResponse>> getAllByAdmin(Pageable pageable) {
    try {
      List<Role> roles = List.of(Role.MANAGER, Role.EVM_STAFF);
      Page<Account> page = accountRepository.findByRoleIn(roles, pageable);
      PageResponse<AccountResponse> response =
          pageMapper.toPageResponse(page, accountMapper::toAccountResponse);
      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
  }

  @Override
  @PreAuthorize("hasRole('MANAGER')")
  public APIResponse<PageResponse<AccountResponse>> getAllByManager(Pageable pageable) {
    try {
      Account current = AccountUtil.getCurrentUser();
      if (current == null) {
        throw new GlobalException(ErrorCode.NOT_FOUND);
      }
      if (current.getDealer() == null) {
        throw new GlobalException(ErrorCode.UNAUTHORIZED);
      }

      Page<Account> page =
          accountRepository.findByRoleAndDealer(Role.DEALER_STAFF, current.getDealer(), pageable);
      PageResponse<AccountResponse> response =
          pageMapper.toPageResponse(page, accountMapper::toAccountResponse);

      return APIResponse.success(response);
    } catch (Exception e) {
      throw new GlobalException(ErrorCode.INVALID_CODE);
    }
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
