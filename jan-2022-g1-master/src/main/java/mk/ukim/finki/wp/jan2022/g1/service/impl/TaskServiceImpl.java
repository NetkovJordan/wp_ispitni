package mk.ukim.finki.wp.jan2022.g1.service.impl;

import mk.ukim.finki.wp.jan2022.g1.model.Task;
import mk.ukim.finki.wp.jan2022.g1.model.TaskCategory;
import mk.ukim.finki.wp.jan2022.g1.model.User;
import mk.ukim.finki.wp.jan2022.g1.model.exceptions.InvalidTaskIdException;
import mk.ukim.finki.wp.jan2022.g1.model.exceptions.InvalidUserIdException;
import mk.ukim.finki.wp.jan2022.g1.repository.TaskRepository;
import mk.ukim.finki.wp.jan2022.g1.repository.UserRepository;
import mk.ukim.finki.wp.jan2022.g1.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Task> listAll() {
        return this.taskRepository.findAll();
    }

    @Override
    public Task findById(Long id) {
        return this.taskRepository.findById(id)
                .orElseThrow(() -> new InvalidTaskIdException());
    }

    @Override
    public Task create(String title, String description, TaskCategory category, List<Long> assignees, LocalDate dueDate) {
        List<User> users = this.userRepository.findAllById(assignees);
        Task task = new Task(title,description,category,users,dueDate);
        return this.taskRepository.save(task);
    }

    @Override
    public Task update(Long id, String title, String description, TaskCategory category, List<Long> assignees) {
        Task task = findById(id);
        List<User> users = this.userRepository.findAllById(assignees);
        task.setTitle(title);
        task.setDescription(description);
        task.setCategory(category);
        task.setAssignees(users);
        return this.taskRepository.save(task);
    }

    @Override
    public Task delete(Long id) {
        Task task = findById(id);
        this.taskRepository.delete(task);
        return task;
    }

    @Override
    public Task markDone(Long id) {
        Task task = findById(id);
        task.setDone(true);
        this.taskRepository.save(task);
        return task;
    }

    @Override
    public List<Task> filter(Long assigneeId, Integer lessThanDayBeforeDueDate) {
        User user;
        LocalDate localDate;
       if(assigneeId!=null && lessThanDayBeforeDueDate!=null){
           user = this.userRepository.findById(assigneeId)
                   .orElseThrow(() -> new InvalidUserIdException());
           localDate = LocalDate.now().plusDays(lessThanDayBeforeDueDate);
           return this.taskRepository.findAllByAssigneesAndDueDateBefore(user,localDate);
       }else if(assigneeId!=null){
           user = this.userRepository.findById(assigneeId).orElseThrow(() -> new InvalidUserIdException());
           return this.taskRepository.findAllByAssignees(user);
       }else if(lessThanDayBeforeDueDate!=null){
           localDate = LocalDate.now().plusDays(lessThanDayBeforeDueDate);
           return this.taskRepository.findAllByDueDateBefore(localDate);
       }
       return this.taskRepository.findAll();
    }
}
