package com.price.tracker.controller;

import com.price.tracker.controller.payload.*;
import com.price.tracker.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Esta controller fornece metodos para login ou criação de um novo usuário .
 */
@RestController
@RequestMapping("${url-base}/api/auth")
public class AuthController
{

  @Autowired
  private AuthService authService;


  /**
   * @return Representação do JWT .
   */
  @PostMapping("/signin")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
  {
    Authentication authentication = authService.getAuthentication(loginRequest.getUsernameOrEmail(),
      loginRequest.getPassword());
    String jwt = authService.getJasonWebToken(authentication);
    return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
  }

  /**
   * @param signUpClientRequest {@link SignUpRequest} .
   * @return Pagina de usuários .
   */
  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpClientRequest signUpClientRequest)
  {
    authService.validateExists(signUpClientRequest);
    authService.criateCliente(signUpClientRequest);
    Authentication authentication = authService.getAuthentication(signUpClientRequest.getEmail(),
      signUpClientRequest.getPassword());
    String jwt = authService.getJasonWebToken(authentication);
    return ResponseEntity.ok(jwt);
  }

  /**
   * Envia o email de recuperação de senha para o usuário .
   * @param forgotPassword payload {@link ForgotPassword}
   * @return ResponseEntity .
   */
  @PostMapping("/forgotpassword")
  public ResponseEntity<?> forgotPasswordLeitor(@Valid @RequestBody ForgotPassword forgotPassword)
  {
    authService.sendEmailRecoverPassword(forgotPassword);
    return ResponseEntity.ok(new ApiResponse(true, forgotPassword.getEmail()));
  }

  /**
   * Altera a senha do usuário .
   * @return ResponseEntity .
   */
  @PostMapping("/resetpassword")
  public ResponseEntity<?> resetPasswordLeitor(@Valid @RequestBody ResetPassword resetPassword)
  {
    authService.resetPassword(resetPassword);
    return ResponseEntity.ok(new ApiResponse(true, "Senha alterada com sucesso"));
  }

}
