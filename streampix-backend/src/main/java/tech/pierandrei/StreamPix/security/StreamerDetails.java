package tech.pierandrei.StreamPix.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tech.pierandrei.StreamPix.entities.StreamerEntity;

import java.util.Collection;
import java.util.List;

public class StreamerDetails implements UserDetails {

    private final StreamerEntity streamer;

    public StreamerDetails(StreamerEntity streamer) {
        this.streamer = streamer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(streamer.getRole()));
    }

    @Override
    public String getPassword() {
        return streamer.getPassword();
    }

    @Override
    public String getUsername() {
        return streamer.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

    public StreamerEntity getStreamer() {
        return streamer;
    }
}
