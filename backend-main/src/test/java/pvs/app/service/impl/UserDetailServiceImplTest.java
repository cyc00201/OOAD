package pvs.app.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
//import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.DigestUtils;
import pvs.app.Application;
import pvs.app.dao.MemberDAO;
import pvs.app.entity.Members;

@RunWith(SpringRunner.class)
@PropertySource(value = "classpath:UserDetailServiceImplTest.properties")
@SpringBootTest(classes = Application.class)
public class UserDetailServiceImplTest {

    @MockBean
    private MemberDAO mockMemberDAO;

    @Autowired
    @Qualifier("userDetailsServiceImpl")
    private UserDetailsService userDetailsServiceImpl;

    private Members member;

    @BeforeEach
    public void setup() {
        this.userDetailsServiceImpl = new UserDetailsServiceImpl(mockMemberDAO);

        member = new Members();

        member.setMemberId(1L);
        member.setUsername("test");
        member.setPassword(DigestUtils.md5DigestAsHex("test".getBytes()));
    }

    @Test
    public void loadUserByUsername_found() {
        //given
        Mockito.when(mockMemberDAO.findByUsername("test")).thenReturn(member);
        //when
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("test");
        //then
      //  System.out.println( member.getUsername()+ "|" +userDetails.getUsername());
        Assert.assertEquals(member, userDetails);
    }

    @Test
    public void loadUserByUsername_notFound() {
        //given
        Mockito.when(mockMemberDAO.findByUsername("test")).thenThrow(new UsernameNotFoundException("not found"));
        //when
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("notFound");
        //then
        Assert.assertNull(userDetails);
    }
}
