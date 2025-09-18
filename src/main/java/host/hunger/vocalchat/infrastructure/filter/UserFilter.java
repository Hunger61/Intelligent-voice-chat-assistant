package host.hunger.vocalchat.infrastructure.filter;

import host.hunger.vocalchat.infrastructure.repository.persistence.entity.UserDO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@WebFilter(filterName = "UserFilter", urlPatterns = "/*")
public class UserFilter implements Filter {

    public static final ScopedValue<UserDO> USER_DO = ScopedValue.newInstance();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain){

        ScopedValue.where(USER_DO, new UserDO())
                .run(() -> {
                    try {
                        filterChain.doFilter(servletRequest, servletResponse);
                    } catch (IOException | ServletException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
