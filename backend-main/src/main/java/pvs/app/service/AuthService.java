package pvs.app.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import pvs.app.dao.MemberDAO;
import pvs.app.dto.MemberDTO;
import pvs.app.entity.Members;
import pvs.app.service.impl.SampleAuthenticationManager;
import pvs.app.service.impl.UserDetailsServiceImpl;
import pvs.app.utils.JwtTokenUtil;


@Service
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true", methods = {RequestMethod.GET, RequestMethod.POST})


public class AuthService {


    private final AuthenticationManager am = new SampleAuthenticationManager();
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    private final MemberDAO memberDAO;

    AuthService(
                JwtTokenUtil jwtTokenUtil,
                MemberDAO memberDAO) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.memberDAO = memberDAO;
        this.userDetailsServiceImpl = new UserDetailsServiceImpl(this.memberDAO);
    }

    public boolean isValidToken(String token) {
        return jwtTokenUtil.isValidToken(token);
    }

    public String login(String username, String password) {
        try {
            //log(password);
            UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username,  password);
            Authentication authentication = am.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            String Token = jwtTokenUtil.generateToken(userDetails);
            System.out.println(userDetails);
            return Token;
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return null;
        }
    }

    public boolean register(MemberDTO memberDTO) {
        if (memberDAO.findByUsername(memberDTO.getUsername()) != null) return false;


        ModelMapper modelMapper = new ModelMapper();

        // Hash Password with Argon2 Algorithm
        Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        String hashedPassword = argon2.hash(4, 1024 * 1024, 8, memberDTO.getPassword());
        memberDTO.setPassword(hashedPassword);
        Members member = modelMapper.map(memberDTO, Members.class);
        this.memberDAO.save(member);

        boolean r = memberDAO.findById(member.getMemberId()).isEmpty();
        //System.out.println(!r);
        return !r;
    }

    /**
     * Ensure that we have met the following password criteria:
     *  1. At least one number
     *  2. At least one lowercase
     *  3. At least one uppercase
     *  4. At least one special character
     *  5. More than 8 digits
     */
    public boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*?[0-9])(?=.*?[a-z])(?=.*?[A-Z])(?=(?=.*?[`!@#$%^&*()_+-])|(?=.*?[=\\[\\]{};'\":|,.<>/?~])).{8,}$";
        return password.matches(passwordRegex);
    }

    public Long getMemberId(String username) {
        Members member = this.memberDAO.findByUsername(username);
        System.out.println(member.getMemberId());
        return member == null ? null : member.getMemberId();
    }
}
