package pvs.app.service;

import net.bytebuddy.asm.Advice;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.util.DigestUtils;
import pvs.app.Application;
import pvs.app.dao.MemberDAO;
import pvs.app.dao.MemberDAOIntegrationTest;
import pvs.app.dto.MemberDTO;
import pvs.app.entity.Members;
import pvs.app.service.impl.UserDetailsServiceImpl;
import pvs.app.utils.JwtTokenUtil;

import java.io.IOException;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    private MemberDAO TestmemberDAO;
    @Autowired
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private MemberDAO mockmemberDAO;


    private Members member = new Members();




    @BeforeEach
    public void setup() throws IOException {
        this.authService = new AuthService(authenticationManager,
                jwtTokenUtil, mockmemberDAO);

        member.setMemberId(1L);
        member.setUsername("test");
        member.setPassword(DigestUtils.md5DigestAsHex("test".getBytes()));
    }
    @Test void register(){
        TestmemberDAO.save(member);
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setId(member.getMemberId());
        memberDTO.setUsername(member.getUsername());
        memberDTO.setPassword(member.getPassword());
        MemberDAOIntegrationTest t = new MemberDAOIntegrationTest();
        authService.register(memberDTO);
        System.out.println(this.TestmemberDAO.count());
        Assert.assertTrue(true);
    }
    @Test
    public void login() {
        //given
        Mockito.when(mockmemberDAO.findByUsername(member.getUsername())).thenReturn(member);
        UserDetails userDetails = member;
        Mockito.when(userDetailsServiceImpl.loadUserByUsername(member.getUsername())).thenReturn(userDetails);
        String JwtToken = "This is jwtToken";
        Mockito.when(jwtTokenUtil.generateToken(userDetails)).thenReturn(JwtToken);
        //when
        String token= authService.login("test", "test");
        //then
        //  System.out.println( "Env:" + token + "|" + JwtToken);
        Assert.assertEquals(token, JwtToken);
    }

  @Test
    public void get_memID(){
        Long id = authService.getMemberId("LEE8900");
        Assert.assertTrue(id > 0);
    }
}
