package com.example.demo.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @Autoor:杨文彬
 * @Date:2019/1/2
 * @Description：
 */
@Data
public class JwtUser implements UserDetails {

    private Long id;

    private String username;
    private String password;
    private  Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public JwtUser(
            Long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities
            ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }



    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }


    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
