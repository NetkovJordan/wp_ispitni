package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidInterestIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ForumUserServiceImpl implements ForumUserService, UserDetailsService {
    private final ForumUserRepository forumUserRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    public ForumUserServiceImpl(ForumUserRepository forumUserRepository, InterestRepository interestRepository, PasswordEncoder passwordEncoder) {
        this.forumUserRepository = forumUserRepository;
        this.interestRepository = interestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<ForumUser> listAll() {
        return this.forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id) {
        return this.forumUserRepository.findById(id).orElseThrow(() -> new InvalidForumUserIdException());
    }

    @Override
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        List<Interest> interests = this.interestRepository.findAllById(interestId);
        ForumUser forumUser = new ForumUser(name,email,passwordEncoder.encode(password),type,interests,birthday);
        return this.forumUserRepository.save(forumUser);
    }

    @Override
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        List<Interest> interests = this.interestRepository.findAllById(interestId);
        ForumUser forumUser = this.forumUserRepository.findById(id).orElseThrow(() -> new InvalidForumUserIdException());
        forumUser.setName(name);
        forumUser.setEmail(email);
        forumUser.setPassword(passwordEncoder.encode(password));
        forumUser.setType(type);
        forumUser.setInterests(interests);
        forumUser.setBirthday(birthday);
        return this.forumUserRepository.save(forumUser);

    }

    @Override
    public ForumUser delete(Long id) {
        ForumUser forumUser = this.forumUserRepository.findById(id).orElseThrow(() -> new InvalidForumUserIdException());
        this.forumUserRepository.delete(forumUser);
        return forumUser;
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age) {
        Interest interest;
        LocalDate localDate;
        if(interestId==null){
            interest = null;
        }else{
            interest = this.interestRepository.findById(interestId).orElseThrow(() -> new InvalidInterestIdException());
        }
        if(age==null){
            localDate=null;
        }else{
            localDate = LocalDate.now().minusYears((long) age);
        }if(interest!=null && localDate!=null){
                return this.forumUserRepository.findAllByInterestsContainingAndBirthdayBefore(interest,localDate);
        }else if(interest!=null && localDate==null){
            return this.forumUserRepository.findAllByInterestsContaining(interest);
        }else if(interest==null && localDate!=null){
            return this.forumUserRepository.findAllByBirthdayBefore(localDate);
        }else{
            return this.forumUserRepository.findAll();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ForumUser user = this.forumUserRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        UserDetails userDetails=new org.springframework.security.core.userdetails.User
                (user.getEmail()
                        ,user.getPassword()
                        , Stream.of(new SimpleGrantedAuthority("ROLE_"+user.getType())).collect(Collectors.toList()));
        return userDetails;
    }
}
