package mk.ukim.finki.wp.kol2022.g2.service.impl;

import mk.ukim.finki.wp.kol2022.g2.model.Course;
import mk.ukim.finki.wp.kol2022.g2.model.Student;
import mk.ukim.finki.wp.kol2022.g2.model.StudentType;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidStudentIdException;
import mk.ukim.finki.wp.kol2022.g2.repository.CourseRepository;
import mk.ukim.finki.wp.kol2022.g2.repository.StudentRepository;
import mk.ukim.finki.wp.kol2022.g2.service.StudentService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
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
public class StudentServiceImpl implements StudentService, UserDetailsService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(StudentRepository studentRepository, CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Student> listAll() {
        return this.studentRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        return this.studentRepository.findById(id).orElseThrow(() -> new InvalidStudentIdException());
    }

    @Override
    public Student create(String name, String email, String password, StudentType type, List<Long> courseId, LocalDate enrollmentDate) {
        List<Course> courses = this.courseRepository.findAllById(courseId);
        Student student = new Student(name,email,passwordEncoder.encode(password),type,courses,enrollmentDate);
        return this.studentRepository.save(student);
    }

    @Override
    public Student update(Long id, String name, String email, String password, StudentType type, List<Long> coursesId, LocalDate enrollmentDate) {
        Student student = this.studentRepository.findById(id).orElseThrow(() -> new InvalidStudentIdException());
        List<Course> courses = this.courseRepository.findAllById(coursesId);
        student.setName(name);
        student.setEmail(email);
        student.setPassword(passwordEncoder.encode(password));
        student.setType(type);
        student.setCourses(courses);
        student.setEnrollmentDate(enrollmentDate);
        return this.studentRepository.save(student);
    }

    @Override
    public Student delete(Long id) {
        Student student = this.studentRepository.findById(id).orElseThrow(() -> new InvalidStudentIdException());
        this.studentRepository.delete(student);
        return student;
    }

    @Override
    public List<Student> filter(Long courseId, Integer yearsOfStudying) {
        Course course;
        LocalDate localDate;
        if(courseId==null)
        {
            course=null;
        }
        else
        {
            course=this.courseRepository.findById(courseId)
                    .orElseGet(null);
        }
        if(yearsOfStudying==null)
        {
            localDate=null;
        }
        else
        {
            localDate=LocalDate.now().minusYears((long) yearsOfStudying);
        }
        if(course!=null&&localDate!=null)
        {
            return this.studentRepository.findAllByCoursesContainingAndEnrollmentDateBefore(course,localDate);
        }
        else if(course!=null&&localDate==null)
        {
            return this.studentRepository.findAllByCoursesContaining(course);
        }
        else if(course==null&&localDate!=null)
        {
            return this.studentRepository.findAllByEnrollmentDateBefore(localDate);
        }
        else
        {
            return this.studentRepository.findAll();
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student user=this.studentRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));
        UserDetails userDetails=new org.springframework.security.core.userdetails.User
                (user.getEmail()
                        ,user.getPassword()
                        , Stream.of(new SimpleGrantedAuthority("ROLE_"+user.getType())).collect(Collectors.toList()));
        return userDetails;
    }
}
