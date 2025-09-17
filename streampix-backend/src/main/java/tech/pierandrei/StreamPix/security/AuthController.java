package tech.pierandrei.StreamPix.security;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(JwtUtil jwtUtil, AuthService authService) {
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    /**
     * Login do Streamer
     * @param dto - Dto para login
     * @return - Retorna o token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * Registro do Streamer
     * @param dto - Dto para registrar
     * @return - Retorna o token
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    /**
     * Refresh do Token
     * @param body - Envia o refreshToken
     * @return - Retorna o token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        // valida token
        jwtUtil.isTokenValid(refreshToken);
        // pega username
        String username = jwtUtil.extractUsername(refreshToken);
        // gera novos tokens
        AuthResponseDto tokens = jwtUtil.generateTokens(username);

        return ResponseEntity.ok(tokens);
    }


    /**
     * Valida a autenticidade do Token
     * authHeader - Bearer Token
     * @return - Retorna o token
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "").trim();

            jwtUtil.isTokenValid(token); // lança exception se inválido

            String username = jwtUtil.extractUsername(token);

            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "username", username
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "valid", false,
                    "error", e.getMessage()
            ));
        }
    }
}
