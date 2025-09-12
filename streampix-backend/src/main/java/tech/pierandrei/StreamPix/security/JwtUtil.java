package tech.pierandrei.StreamPix.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import tech.pierandrei.StreamPix.entities.StreamerEntity;
import tech.pierandrei.StreamPix.exceptions.JwtInvalidException;
import tech.pierandrei.StreamPix.exceptions.StreamerNotFoundException;
import tech.pierandrei.StreamPix.repositories.StreamerRepository;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Autowired
    private StreamerRepository streamerRepository;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Gera os Tokens
     * @param username - username para ser usado como subject do token
     * @return - Retorna os tokens
     */
    public AuthResponseDto generateTokens(String username) {
        long now = System.currentTimeMillis();

        // Recupera o streamer do banco para pegar a role
        StreamerEntity streamer = streamerRepository.findByEmail(username)
                .orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado!"));

        // Access Token
        long accessExpiration = now + expiration;
        String accessToken = Jwts.builder()
                .setSubject(username)
                .claim("roles", List.of(streamer.getRole()))  // <<< adiciona a role
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(accessExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token
        long refreshExpiration = now + (1000 * 60 * 60 * 24 * 7); // 7 dias
        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(refreshExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        return new AuthResponseDto(accessToken, accessExpiration, refreshToken, refreshExpiration);
    }


    /**
     * Obtém o streamer através do email obtido do token
     * @param token - Bearer Token
     * @return - Retorna o Streamer
     */
    public StreamerEntity getStreamerWithToken(String token){
        isTokenValid(token);
        var email = extractUsername(token);
        var streamer = streamerRepository.findByEmail(email).orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado!"));
        return streamer;
    }

    /**
     * Extrai o subject do token
     * @param token - Bearer Token
     * @return - Retorna o email
     */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Verifica se o token é válido
     * @param token - Bearer Token
     */
    public void isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // chave secreta usada para assinar o token
                    .build()
                    .parseClaimsJws(token);   // verifica assinatura e validade
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtInvalidException("Token inválido!");
        }
    }


    /**
     * Autenticar e autorizar aplicativos e APIs
     * @param token - Bearer Token
     * @return - Retorna o JWT Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
