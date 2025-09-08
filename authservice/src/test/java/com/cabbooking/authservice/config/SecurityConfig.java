//package com.cabbooking.authservice.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private JwtFilter jwtFilter;
//    @Autowired
//    private JwtAuthEntryPoint jwtAuthEntryPoint;
//
//    @Bean
//    public SecurityFilterChain fiterChain(HttpSecurity http) throws Exception {
//        // for http with auth
//        http.csrf(AbstractHttpConfigurer::disable)
//                .authorizeHttpRequests(auth -> auth
//                                .requestMatchers("/api/login", "/api/singin", "/api/register").permitAll() // public endpoints
//                                .requestMatchers("/api/employees/**").authenticated() // secure endpoints
////                        .anyRequest().authenticated()
//                )
////                .httpBasic(Customizer.withDefaults())
//                .exceptionHandling(i->i.authenticationEntryPoint(jwtAuthEntryPoint))
//                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        //for http with login form
////        http.authorizeHttpRequests(auth -> auth
////                .anyRequest()
////                .authenticated()
////        ).formLogin(Customizer.withDefaults());
//
//        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder(){
//        return new BCryptPasswordEncoder();
//    }
//
////    @Bean
////    public UserDetailsService userManager(){
////
////        String r = passwordEncoder().encode("ewq");
////        UserDetails s = User.builder().username("suresh").password(r).roles("USER").build();
////        UserDetails admin = User.builder().username("surya").password(r).roles("ADMIN").build();
////        return new InMemoryUserDetailsManager(s,admin);
////    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
//        return config.getAuthenticationManager();
//    }
//}