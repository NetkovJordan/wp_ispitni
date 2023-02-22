package mk.ukim.finki.wp.jan2022.g2.repository;

import mk.ukim.finki.wp.jan2022.g2.model.Discussion;
import mk.ukim.finki.wp.jan2022.g2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiscussionRepository extends JpaRepository<Discussion,Long> {

    List<Discussion> findAllByParticipantsAndDueDateBefore(User user, LocalDate localDate);
    List<Discussion> findAllByParticipants(User user);
    List<Discussion> findAllByDueDateBefore(LocalDate localDate);
}
