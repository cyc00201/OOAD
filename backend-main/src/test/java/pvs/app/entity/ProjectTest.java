package pvs.app.entity;

import okio.Source;
import org.glassfish.jersey.internal.util.Property;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
//import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import pvs.app.Application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;



@RunWith(SpringRunner.class)
@PropertySource(value = "classpath:ProjectTest.properties")
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ProjectTest {
    final String testProjectName = "myProject";
    Project givenProject;

    @BeforeEach
    public void setup() {
        givenProject = new Project();
        givenProject.setName(testProjectName);
    }

    @Test
    public void correctlyGetProjectName() {

        if(givenProject == null)
            assertFalse(true);
        else{
            assertEquals(testProjectName, givenProject.getName());
        }
    }

    @Test
    public void removedAttributeShouldHaveDefaultValue() {

        if(givenProject == null)
            assertFalse(true);
        else{
            assertFalse(givenProject.isRemoved());
        }
    }

    @Test
    public void setProjectAsRemoved() {

        if(givenProject == null)
            assertFalse(true);
        else{
        givenProject.setRemoved(true);
        assertTrue(givenProject.isRemoved());
        }
    }
}
