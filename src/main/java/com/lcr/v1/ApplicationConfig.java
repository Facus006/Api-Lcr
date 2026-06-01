package com.lcr.v1;

import com.lcr.v1.Repositorios.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor //creamos un constructor para que tome como argumento a userRepository y lo pueda inicializar.
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    } //Este metodo crea un AuthenticationManager y recibe como parametro un AuthenticationConfiguration para configurar la autenticación de Spring.

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }//Este metodo crea un AuthenticationProvider que realiza la autenticación en Spring Security utilizando
    //  DaoAuthenticationProvider que se encarga de la autenticación basada en un UserDetailsService y un PasswordEncoder.

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }//Este metodo es para encriptar el password.

    @Bean
    public UserDetailsService userDetailService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not fournd"));
    }//Este metodo crea un UserDetailsService para cargar los detalles del usuario durante la autenticación.
    //busca un usuario por su nombre de usuario en el repositorio (UserRepository) y lanza una excepción UsernameNotFoundException si el usuario no es encontrado.

}
