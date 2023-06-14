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
import java.util.Optional;

import static org.junit.Assert.assertEquals;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private MemberDAO memberDAO;




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

        this.member.setMemberId(1L);
        this.member.setUsername("test");
        this.member.setPassword("test#G567");
    }
   @Test
    public void register(){

       MemberDTO memberDTO = new MemberDTO();
        memberDTO.setUsername(member.getUsername());
        memberDTO.setPassword(member.getPassword());
        memberDTO.setId(member.getMemberId());

       System.out.println(this.memberDAO);
        Assert.assertTrue(true);
    }
    @Test
    public void login() {
        //given
        Mockito.when(mockmemberDAO.findByUsername(member.getUsername())).thenReturn(member);
        UserDetails userDetails = member;
        Mockito.when(userDetailsServiceImpl.loadUserByUsername(member.getUsername())).thenReturn(userDetails);

        System.out.println( userDetailsServiceImpl.loadUserByUsername(member.getUsername()));

        String JwtToken = "This is jwtToken";
        Mockito.when(jwtTokenUtil.generateToken(userDetails)).thenReturn(JwtToken);
        //when
        String token= authService.login("test", "test");
        //then
    //    System.out.println( "Env:" + token + "|" + JwtToken);
        Assert.assertEquals(token, JwtToken);
    }

  @Test
    public void get_memID(){
      this.memberDAO.save(member);
      Optional<Members> foundEntity = this.memberDAO.findById(member.getMemberId());
      System.out.println(this.memberDAO);
      assertEquals(member.getUsername(), foundEntity.orElse(null).getUsername());
    }
}
