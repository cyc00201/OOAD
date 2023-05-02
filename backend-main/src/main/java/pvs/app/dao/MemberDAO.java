package pvs.app.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pvs.app.entity.Members;

@Repository
public interface MemberDAO extends CrudRepository<Members, Long> {
    Members findByUsername(String username);

    Members findById(long id);
}
