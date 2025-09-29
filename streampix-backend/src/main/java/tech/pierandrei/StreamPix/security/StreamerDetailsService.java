package tech.pierandrei.StreamPix.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import tech.pierandrei.StreamPix.streamer.StreamerEntity;
import tech.pierandrei.StreamPix.streamer.StreamerRepository;

@Service
public class StreamerDetailsService implements UserDetailsService {

    private final StreamerRepository repository;

    public StreamerDetailsService(StreamerRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        StreamerEntity streamer = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Streamer não encontrado: " + email));
        return new StreamerDetails(streamer);
    }
}