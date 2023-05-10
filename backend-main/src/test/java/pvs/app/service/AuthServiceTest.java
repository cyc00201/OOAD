package pvs.app.service;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import pvs.app.Application;
import pvs.app.dao.MemberDAO;
import pvs.app.dto.MemberDTO;
import pvs.app.entity.Members;
import pvs.app.service.impl.UserDetailsServiceImpl;
import pvs.app.utils.JwtTokenUtil;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthServiceTest {

    @Autowired
    AuthService authService;





    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    private AuthService testauth;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private MemberDAO mockmemberDAO;




    private Members member = new Members();




    @BeforeEach
    public void setup() throws IOException {
        this.authService = new AuthService(
                jwtTokenUtil, mockmemberDAO);

        this.member.setMemberId(23232L);
        this.member.setUsername("Tester31102#k");
        this.member.setPassword("Tester31102#j");
    }
   @Test
    public void register(){

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUsername(member.getUsername());
        memberDTO.setPassword(member.getPassword());
        memberDTO.setId(member.getMemberId());
        Boolean ok ;
        //System.out.println(ok == null);
        Mockito.when(ok = testauth.register(memberDTO)).thenReturn(ok);
        Assert.assertTrue(ok);
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
        System.out.println( "Env:" + token + "|" + JwtToken);
        Assert.assertEquals(token, JwtToken);
    }

  @Test
    public void get_memID(){

       Boolean ok ;
        Long id;
        Mockito.when(id = testauth.getMemberId("LEE8900")).thenReturn(id);

        ok = id > 0;
        System.out.println(testauth.getMemberId("LEE8900"));
        Assert.assertTrue(ok);
    }
}
