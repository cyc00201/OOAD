package pvs.app.controller;

import kong.unirest.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pvs.app.config.ApplicationConfig;
import pvs.app.config.SecurityConfig;
import pvs.app.dto.MemberDTO;
import pvs.app.service.AuthService;



@RestController


public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/auth/verifyJwt")
    public void isValidToken(@RequestHeader("Authorization") String token) {
        final boolean isValidToken = authService.isValidToken(token);
        if (isValidToken) ResponseEntity.ok().build();
        else ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping(value = "/auth/login")
    public String login(@NotNull @RequestBody MemberDTO memberDTO) {
        // return jwt if login success
        return authService.login(memberDTO.getUsername(), memberDTO.getPassword());
    }

    @PostMapping(value = "/auth/register")
    public String register(@RequestBody MemberDTO memberDTO) {
        if (!authService.isValidPassword(memberDTO.getPassword())) return "InvalidPassword";
        return authService.register(memberDTO) ? "RegisterSuccess" : "RegisterFailed";
    }


    @GetMapping(value = "/auth/memberId")
    public Long getMemberID(@RequestParam("username") String username) {
        System.out.println(username);
        return 262L;//authService.getMemberId(username);
    }
}
