package pvs.app.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import pvs.app.Application;
import pvs.app.entity.Members;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MemberDAOIntegrationTest {
    @Autowired
    private MemberDAO memberDAO;

    private Members member01 = new Members();

    @BeforeEach
    public void setup() {
        member01.setUsername("LEE8900");
        member01.setPassword("LEE89700#j");

    }
    @Test
    public void deleteAll(){
        this.memberDAO.deleteAll();
        boolean isSuccess =  this.memberDAO.count() == 0;
       //
        assertTrue(isSuccess);
    }

    @Test
    public void whenFindById_thenReturnMember() {
        this.memberDAO.save(member01);
        Optional<Members> foundEntity = this.memberDAO.findById(member01.getMemberId());

        assertEquals(member01.getUsername(), foundEntity.orElse(null).getUsername());

    }

    @Test
    public void whenFindByAccount_thenReturnMember() {

        Members foundEntity = memberDAO.findByUsername(member01.getUsername());
        assertEquals(foundEntity.getUsername(), member01.getUsername());
    }




}