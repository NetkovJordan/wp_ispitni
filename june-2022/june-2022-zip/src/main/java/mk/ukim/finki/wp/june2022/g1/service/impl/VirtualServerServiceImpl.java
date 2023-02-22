package mk.ukim.finki.wp.june2022.g1.service.impl;


import mk.ukim.finki.wp.june2022.g1.model.OSType;
import mk.ukim.finki.wp.june2022.g1.model.User;
import mk.ukim.finki.wp.june2022.g1.model.VirtualServer;
import mk.ukim.finki.wp.june2022.g1.model.exceptions.InvalidUserIdException;
import mk.ukim.finki.wp.june2022.g1.model.exceptions.InvalidVirtualMachineIdException;
import mk.ukim.finki.wp.june2022.g1.repository.UserRepository;
import mk.ukim.finki.wp.june2022.g1.repository.VirtualServerRepository;
import mk.ukim.finki.wp.june2022.g1.service.UserService;
import mk.ukim.finki.wp.june2022.g1.service.VirtualServerService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VirtualServerServiceImpl implements VirtualServerService {
    private final VirtualServerRepository virtualServerRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public VirtualServerServiceImpl(VirtualServerRepository virtualServerRepository, UserRepository userRepository, UserService userService) {
        this.virtualServerRepository = virtualServerRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public List<VirtualServer> listAll() {
        return this.virtualServerRepository.findAll();
    }

    @Override
    public VirtualServer findById(Long id) {
        return this.virtualServerRepository.findById(id).orElseThrow(() -> new InvalidVirtualMachineIdException());
    }

    @Override
    public VirtualServer create(String name, String ipAddress, OSType osType, List<Long> owners, LocalDate launchDate) {
        List<User> users=owners
                .stream()
                .map(i->this.userService.findById(i))
                .collect(Collectors.toList());
        VirtualServer virtualServer=new VirtualServer(name,ipAddress,osType,users,launchDate);

        return this.virtualServerRepository.save(virtualServer);
    }

    @Override
    public VirtualServer update(Long id, String name, String ipAddress, OSType osType, List<Long> owners) {
        VirtualServer virtualServer = findById(id);
        List<User> users = this.userRepository.findAllById(owners);
        virtualServer.setInstanceName(name);
        virtualServer.setIpAddress(ipAddress);
        virtualServer.setOSType(osType);
        virtualServer.setOwners(users);
        return this.virtualServerRepository.save(virtualServer);
    }

    @Override
    public VirtualServer delete(Long id) {
        VirtualServer virtualServer = findById(id);
        this.virtualServerRepository.delete(virtualServer);
        return virtualServer;
    }

    @Override
    public VirtualServer markTerminated(Long id) {
        VirtualServer virtualServer = this.virtualServerRepository.findById(id)
                .orElseThrow(() -> new InvalidVirtualMachineIdException());
        virtualServer.setTerminated(true);
        return this.virtualServerRepository.save(virtualServer);
    }

    @Override
    public List<VirtualServer> filter(Long ownerId, Integer activeMoreThanDays) {
        User user;
        LocalDate localDate;
        if(ownerId==null){
            user=null;
        }else{
            user = this.userRepository.findById(ownerId).orElseThrow(() -> new InvalidUserIdException());
        }
        if(activeMoreThanDays==null){
            localDate=null;
        }else{
            localDate = LocalDate.now().minusDays(activeMoreThanDays);
        }
        if (user != null && localDate != null) {
            return this.virtualServerRepository.findAllByOwnersAndLaunchDateBefore(user,localDate);
        }else if(user!=null && localDate==null){
            return this.virtualServerRepository.findAllByOwners(user);
        }else if(user==null && localDate!=null){
            return this.virtualServerRepository.findAllByLaunchDateBefore(localDate);
        }else{
            return this.virtualServerRepository.findAll();
        }
    }
}
