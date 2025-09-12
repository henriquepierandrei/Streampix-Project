package tech.pierandrei.StreamPix.security;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tech.pierandrei.StreamPix.entities.StreamerEntity;
import tech.pierandrei.StreamPix.exceptions.InvalidCredentialsException;
import tech.pierandrei.StreamPix.repositories.StreamerRepository;

@Service
@Transactional
public class AuthService {
    private final StreamerRepository repository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthService(StreamerRepository repository, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Valida as credenciais enviadas no paylaod
     * @param dto - Payload para verificar
     */
    private void validateCredentials(RegisterRequestDto dto){
        if (repository.existsByEmail(dto.email())) throw new InvalidCredentialsException("Esse email já existe!");
        if (repository.existsByStreamerName(dto.name().toLowerCase())) throw new InvalidCredentialsException("Esse nome já existe!");
    }


    /**
     * Login do Streamer
     * @param dto - Payload
     * @return - Retornar o token
     */
    public AuthResponseDto login(AuthRequestDto dto){
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.email(),
                            dto.password()
                    )
            );

            // 2. recupera usuário autenticado
            StreamerDetails userDetails = (StreamerDetails) authentication.getPrincipal();
            StreamerEntity streamer = userDetails.getStreamer();

            // 3. gera token e retorna o token com a expiracao
            return jwtUtil.generateTokens(streamer.getEmail());

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }
    }

    /**
     * Registro do Streamer
     * @param dto - Payload
     */
    public String register(RegisterRequestDto dto){
        validateCredentials(dto);

        var newStreamer = new StreamerEntity(
                dto.name(),
                0.0,
                true,
                5.0,
                10,
                100,
                dto.email(),
                passwordEncoder.encode(dto.password()),
                "ROLE_STREAMER",
                false,
                false,
                false
        );
        repository.saveAndFlush(newStreamer);
        return "Streamer registrado com sucesso!";
    }
}
