package com.hammer.hammer.user.controller;

import com.hammer.hammer.global.service.EmailService;
import com.hammer.hammer.global.service.RedisService;
import com.hammer.hammer.user.dto.UserChangePwDto;
import com.hammer.hammer.user.dto.UserDto;
import com.hammer.hammer.user.dto.UserFindDto;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import com.hammer.hammer.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class UserApiController {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisService redisService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;


    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/currentUser")
    public ResponseEntity<User> getSessionUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = Long.parseLong(authentication.getName());
        User currentUser = userRepository.findByUserId(currentUserId).orElse(null);
        if (currentUser != null) {
            log.info("user found! {}", currentUser.getUsername());
            return ResponseEntity.ok(currentUser);
        }else{
            log.info("user not found");
            return ResponseEntity.notFound().build();
        }
    }

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserDto userDto, BindingResult bindingResult) {
        Map<String, String> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("message", "유효성 검사 오류");
            return ResponseEntity.badRequest().body(response); // HTTP 400
        }

        try {
            // 회원가입 처리
            userService.save(userDto);
            response.put("message", "회원가입 성공");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            // 예외 처리
            response.put("message", "회원가입 실패: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response); // HTTP 500
        }
    }

    // 아이디 찾기
    @PostMapping("/findId")
    public ResponseEntity<?> findId(@RequestBody UserDto userDto, BindingResult bindingResult) {
        Optional<String> userId = userService.findByUsernameAndPhoneNumber(userDto);
        if (userId.isPresent()) {
            Map<String, String> response = Map.of(
                    "message", userId.get()
            );
            return ResponseEntity.ok(response);
        }

        Map<String, String> errorResponse = Map.of(
                "message", "일치하는 정보가 없습니다."
        );
        return ResponseEntity.badRequest().body(errorResponse);

    }
    
    // 인증번호 발송
    @PostMapping("/sendCtfNo")
    public ResponseEntity<?> ctfNoReq(@RequestBody UserDto userDto) {
        Optional<User> userOptional = userService.findByNameAndEmail(userDto.getUsername(), userDto.getEmail());

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "일치하는 사용자를 찾을 수 없습니다."));
        }
        // 간단한 랜덤 난수 6자리 생성
        Random random = new Random();

        String nansu = String.valueOf(100000 + random.nextInt(900000)); // 100000 ~ 999999
        // 난수 값 레디스에 저장
        redisService.save("ctfNo", nansu, 1000 * 60 * 5);
        // 난수 이메일 발송

        emailService.sendEmail(userDto.getEmail(),"[망치마켓] 인증번호 발송 안내", "인증번호 : " + nansu + "입니다.");

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "임시 비밀번호가 이메일로 발송되었습니다."));
    }

    // 인증번호 확인
    @PostMapping("/comCtfNo")
    public ResponseEntity<?> sendCtfNo(@RequestBody UserFindDto userFindDto) {
        Object storedCtfNoObj = redisService.find("ctfNo");

        // 객체가 null이면 Redis에 인증번호가 없다는 의미
        if (storedCtfNoObj == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "인증번호가 존재하지 않거나 만료되었습니다."));
        }

        // 인증번호를 객체로 변환 후 비교
        String storedCtfNo = (String) storedCtfNoObj;

        if (!storedCtfNo.equals(userFindDto.getCtfNo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "인증번호가 일치하지 않습니다."));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "인증 성공"));
    }

    // 비밀번호 변경
    @PostMapping("/changePw")
    public ResponseEntity<?> changePw(@RequestBody UserChangePwDto userChangePwDto) {
        Optional<User> findUser = userService.findByEmail(userChangePwDto.getEmail());

        // 사용자 존재 여부 확인
        if (findUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "사용자를 찾을 수 없습니다."));
        }

        // 기존 User 객체에서 Builder를 사용하여 비밀번호 변경
        User newUser = findUser.get();

        // Builder를 사용하여 새로운 User 객체 생성
        User updatedUser = newUser.toBuilder()
                .password(bCryptPasswordEncoder.encode(userChangePwDto.getPassword()))  // 새로운 비밀번호 설정
                .build();

        // 사용자 정보를 저장
        userRepository.save(updatedUser);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "비밀번호 변경 성공"));
    }
}
