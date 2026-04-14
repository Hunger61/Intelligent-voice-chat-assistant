//package host.hunger.vocalchat.infrastructure.filter;
//
//import host.hunger.vocalchat.domain.model.user.User;
//import host.hunger.vocalchat.domain.model.user.UserEmail;
//import host.hunger.vocalchat.domain.repository.UserRepository;
//import host.hunger.vocalchat.infrastructure.util.JwtUtil;
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//import java.lang.ScopedValue;
//
//@Component
//@WebFilter(filterName = "UserFilter", urlPatterns = "/*")
//@RequiredArgsConstructor
//public class UserFilter implements Filter {
//
//    public static final ScopedValue<User> USER = ScopedValue.newInstance();
//
//    private final JwtUtil jwtUtil;
//    private final UserRepository userRepository;
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain){
//        HttpServletRequest request = (HttpServletRequest) servletRequest;
//
//        String token = request.getHeader("token");
//        if(token == null || token.trim().isBlank()){
//            return;
//        }
//        String email = jwtUtil.resolveToken(token);
//        User user = userRepository.findByEmail(new UserEmail(email));
//        ScopedValue.where(USER, user)
//                .run(() -> {
//                    try {
//                        filterChain.doFilter(servletRequest, servletResponse);
//                    } catch (IOException | ServletException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//    }
//}


//todo,升级到JDK25