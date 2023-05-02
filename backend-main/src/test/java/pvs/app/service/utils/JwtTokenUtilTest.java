package pvs.app.service.utils;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;
import pvs.app.Application;
import pvs.app.dao.MemberDAO;
import pvs.app.entity.Members;
import pvs.app.service.impl.UserDetailsServiceImpl;
import pvs.app.utils.JwtTokenUtil;

import java.io.IOException;


@RunWith(SpringRunner.class)
@PropertySource(value = "classpath:JwtTokenUtilTest.properties")
@SpringBootTest(classes = Application.class)
public class JwtTokenUtilTest {


    @MockBean
    private MemberDAO mockMemberDAO;

    @Autowired
    private JwtTokenUtil jwtTokenUtil ;


    private Members memberUser;

    @BeforeEach
    public void setup() throws IOException {


        memberUser = new Members();

        memberUser.setMemberId(1L);
        memberUser.setUsername("user");
        memberUser.setPassword(DigestUtils.md5DigestAsHex("user".getBytes()));

        Members memberAdmin = new Members();

        memberAdmin.setMemberId(2L);
        memberAdmin.setUsername("admin");
        memberAdmin.setPassword(DigestUtils.md5DigestAsHex("admin".getBytes()));

    }

    @Test
    public void validToken() {
        //given

        Mockito.when(mockMemberDAO.findByUsername(memberUser.getUsername())).thenReturn(memberUser);
        UserDetails userDetails = memberUser;
        //when
        String token = jwtTokenUtil.generateToken(userDetails);
        boolean tokenValidated = jwtTokenUtil.isValidToken(token);
        //then
        Assert.assertTrue(tokenValidated);
    }

    @Test
    public void invalidToken() {
        //given
        Mockito.when(mockMemberDAO.findByUsername("test")).thenReturn(memberUser);
        Members authenticatedUser = memberUser;

        //when
        String token = jwtTokenUtil.generateToken(authenticatedUser);
        boolean tokenValidated = jwtTokenUtil.isValidToken(token);

        //then
        Assert.assertFalse(tokenValidated);
    }


}
