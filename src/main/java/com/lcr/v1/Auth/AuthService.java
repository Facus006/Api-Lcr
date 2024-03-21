package com.lcr.v1.Auth;

import com.lcr.v1.Entidades.User;
import com.lcr.v1.Enums.Role;
import com.lcr.v1.Errores.MyException;
import com.lcr.v1.Jwt.JwtService;
import com.lcr.v1.Repositorios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();

    }

    public AuthResponse register(RegisterRequest request) throws MyException {
        validar(request);
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .country(request.getCountry())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }

    public void validar(RegisterRequest request) throws MyException {
        User existingUser = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (existingUser != null) {
            System.out.println("error usuario registrado");
            throw new MyException("El usuario ya está registrado");

        }
        if (request.getUsername() == null || request.getUsername().isEmpty()) {
            throw new MyException("Ingrese un email valido");
        }
        if (request.getCountry() == null || request.getCountry().isEmpty()) {
            throw new MyException("Ingrese un pais valido");
        }
        if (request.getFirstname() == null || request.getFirstname().isEmpty()) {
            throw new MyException("Ingrese un nombre valido");
        }
        if (request.getLastname() == null || request.getLastname().isEmpty()) {
            throw new MyException("Ingrese un apellido valido");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty() || request.getPassword().length() <= 5) {
            throw new MyException("La contraseña tiene que tener mas de 5 caracteres.");
        }

    }

}
