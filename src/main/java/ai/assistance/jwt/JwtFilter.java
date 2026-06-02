package ai.assistance.jwt;

import ai.assistance.helpers.JwtHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtHandler;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;
    @Autowired
    private JwtHelper jwtHelper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api/login") || request.getRequestURI().startsWith("/api/register")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String token = extract_token_from_header(request);
            Claims claims = jwtHelper.claims(token);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);
            List<GrantedAuthority> authority = List.of(new SimpleGrantedAuthority(role));
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, null, authority);
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        } catch (ExpiredJwtException | MalformedJwtException e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
            return;
        }
        filterChain.doFilter(request, response);
    }


    //extract token from header
    //exception is handing inside AuthenticationEntryPoint class
    private String extract_token_from_header(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader.substring(7);
    }
}
