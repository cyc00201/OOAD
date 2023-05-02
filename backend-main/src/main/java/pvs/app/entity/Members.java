package pvs.app.entity;

import lombok.Data;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pvs.app.config.ApplicationConfig;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@Entity
public class Members implements UserDetails {
    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long memberId;

    @Column(unique = true)
    @NotNull
    private String username;

    @NotNull
    private String password;

    /**
     * Authority function implements from UserDetails interface
     * Default return null
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    /**
     * 用户账号是否过期
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 用户账号是否被锁定
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 用户密码是否过期
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户是否可用
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
