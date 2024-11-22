package tn.enicar.enicar_platforme.auth;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.enicar.enicar_platforme.email.EmailService;
import tn.enicar.enicar_platforme.email.EmailTemplateName;
import tn.enicar.enicar_platforme.role.RoleRepository;
import tn.enicar.enicar_platforme.security.JwtService;
import tn.enicar.enicar_platforme.user.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ApprovedUserRepository approvedUserRepository ;
    @Value("${application.security.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var approvedUser = approvedUserRepository.findByCin(request.getCin())
                .orElseThrow(() -> new RuntimeException("CIN not found in the approved list"));
        var userRole = roleRepository.findByName(approvedUser.getRole().getName())
                .orElseThrow(()->new IllegalStateException("ROLE was not initialized "));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .cin(request.getCin())
                .accountLocked(false)
                .enabled(false)
                .role(userRole)
                .build();
        userRepository.save(user);
        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                (String) newToken,
                "Account Activation"
        );

    }

    private Object generateAndSaveActivationToken(User user) {
        /* gen a token */
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .user(user)
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(1))
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int lenght) {
        String characters = "0123456789" ;
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for( int i = 0 ; i<lenght ; i++){
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        ) ;
        var claims = new HashMap<String , Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName" , user.fullName()) ;
        var jwtToken = jwtService.generateToken(claims,user);
        return AuthenticationResponse.builder().
        token(jwtToken).build();
    }


    public void activateAccounte(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(()->new RuntimeException("Invalid token"));
        if(LocalDateTime.now().isAfter(savedToken.getExpiresAt())){
            sendValidationEmail(savedToken.getUser());
            throw  new RuntimeException("Activation token has Expired , A new one has been sent . ");
        }
        var user = userRepository.findById(savedToken.getUser().getId())
                .orElseThrow(()-> new UsernameNotFoundException("User not found ")) ;
        user.setEnabled(true);
        userRepository.save(user) ;
        savedToken.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }

    public void addApprovedUser(ApprovedUserRequest approvedUserRequest) throws MessagingException {
        var role = roleRepository.findByName(approvedUserRequest.getRole())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        var approvedUser = ApprovedUser.builder()
                .cin(approvedUserRequest.getCin())
                .role(role)
                .email(approvedUserRequest.getEmail())
                .build();

        approvedUserRepository.save(approvedUser);
    }
}
