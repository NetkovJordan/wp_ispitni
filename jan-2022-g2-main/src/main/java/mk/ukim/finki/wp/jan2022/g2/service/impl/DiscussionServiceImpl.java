package mk.ukim.finki.wp.jan2022.g2.service.impl;

import mk.ukim.finki.wp.jan2022.g2.model.Discussion;
import mk.ukim.finki.wp.jan2022.g2.model.DiscussionTag;
import mk.ukim.finki.wp.jan2022.g2.model.User;
import mk.ukim.finki.wp.jan2022.g2.model.exceptions.InvalidDiscussionIdException;
import mk.ukim.finki.wp.jan2022.g2.model.exceptions.InvalidUserIdException;
import mk.ukim.finki.wp.jan2022.g2.repository.DiscussionRepository;
import mk.ukim.finki.wp.jan2022.g2.repository.UserRepository;
import mk.ukim.finki.wp.jan2022.g2.service.DiscussionService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiscussionServiceImpl implements DiscussionService {
    private final DiscussionRepository discussionRepository;
    private final UserRepository userRepository;

    public DiscussionServiceImpl(DiscussionRepository discussionRepository, UserRepository userRepository) {
        this.discussionRepository = discussionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Discussion> listAll() {
        return this.discussionRepository.findAll();
    }

    @Override
    public Discussion findById(Long id) {
        return this.discussionRepository.findById(id)
                .orElseThrow(() -> new InvalidDiscussionIdException());
    }

    @Override
    public Discussion create(String title, String description, DiscussionTag discussionTag, List<Long> participants, LocalDate dueDate) {
        List<User> users = this.userRepository.findAllById(participants);
        Discussion discussion = new Discussion(title,description,discussionTag,users,dueDate);
        return this.discussionRepository.save(discussion);
    }

    @Override
    public Discussion update(Long id, String title, String description, DiscussionTag discussionTag, List<Long> participants) {
        Discussion discussion = this.discussionRepository.findById(id)
                .orElseThrow(() -> new InvalidDiscussionIdException());
        List<User> users = this.userRepository.findAllById(participants);
        discussion.setTitle(title);
        discussion.setDescription(description);
        discussion.setTag(discussionTag);
        discussion.setParticipants(users);
        return this.discussionRepository.save(discussion);
    }

    @Override
    public Discussion delete(Long id) {
        Discussion discussion = this.discussionRepository.findById(id)
                .orElseThrow(() -> new InvalidDiscussionIdException());
        this.discussionRepository.delete(discussion);
        return discussion;
    }

    @Override
    public Discussion markPopular(Long id) {
        Discussion discussion = this.discussionRepository.findById(id)
                .orElseThrow(() -> new InvalidDiscussionIdException());
        discussion.setPopular(true);
        return this.discussionRepository.save(discussion);

    }

    @Override
    public List<Discussion> filter(Long participantId, Integer daysUntilClosing) {
        User user;
        LocalDate localDate;
        if(participantId==null){
            user=null;
        }else{
            user = this.userRepository.findById(participantId).orElseThrow(() -> new InvalidUserIdException());
        }
        if(daysUntilClosing==null){
            localDate=null;
        }else{
            localDate = LocalDate.now().plusDays(daysUntilClosing);
        }if(user!=null && localDate!=null){
            return this.discussionRepository.findAllByParticipantsAndDueDateBefore(user,localDate);
        }else if(user==null && localDate!=null){
            return this.discussionRepository.findAllByDueDateBefore(localDate);
        }else if(user!=null && localDate==null){
            return this.discussionRepository.findAllByParticipants(user);
        }else{
            return this.discussionRepository.findAll();
        }
    }
}
