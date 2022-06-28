package com.tma.recruit;

import com.tma.recruit.model.entity.Permission;
import com.tma.recruit.model.entity.QuestionBank;
import com.tma.recruit.model.entity.Role;
import com.tma.recruit.model.entity.User;
import com.tma.recruit.model.enums.QuestionStatus;
import com.tma.recruit.repository.PermissionRepository;
import com.tma.recruit.repository.QuestionBankRepository;
import com.tma.recruit.repository.RoleRepository;
import com.tma.recruit.repository.UserRepository;
import com.tma.recruit.util.RoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class DemoData implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private QuestionBankRepository questionBankRepository;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmailIgnoreCase("admin@tma.com.vn")) {


            //permission
            Permission createPermission = new Permission();
            createPermission.setName("CREATE");
            createPermission.setPermissionKey("CREATE");
            createPermission = permissionRepository.save(createPermission);

            Permission updatePermission = new Permission();
            updatePermission.setName("UPDATE");
            updatePermission.setPermissionKey("UPDATE");
            updatePermission = permissionRepository.save(updatePermission);

            Permission deletePermission = new Permission();
            deletePermission.setName("DELETE");
            deletePermission.setPermissionKey("DELETE");
            deletePermission = permissionRepository.save(deletePermission);

            Permission viewPermission = new Permission();
            viewPermission.setName("VIEW");
            viewPermission.setPermissionKey("VIEW");
            viewPermission = permissionRepository.save(viewPermission);

            //role
            Role adminRole = new Role();
            adminRole.setName(RoleConstant.ADMIN);
            adminRole.setPermissions(Arrays.asList(createPermission, updatePermission, deletePermission, viewPermission));
            adminRole = roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName(RoleConstant.USER);
            userRole.setPermissions(Arrays.asList(createPermission, updatePermission, viewPermission));
            userRole = roleRepository.save(userRole);

            Role questRole = new Role();
            questRole.setName(RoleConstant.GUEST);
            questRole.setPermissions(Collections.singletonList(viewPermission));
            questRole = roleRepository.save(questRole);


            //user
            User admin = new User();
            admin.setEmail("admin@tma.com.vn");
            admin.setPassword(encoder.encode("12341234"));
            admin.setRoles(Collections.singletonList(adminRole));
            admin.setName("Admin");

            User user = new User();
            user.setEmail("user@tma.com.vn");
            user.setPassword(encoder.encode("12341234"));
            user.setRoles(Collections.singletonList(userRole));
            user.setName("User");

            User guest = new User();
            guest.setEmail("guest@tma.com.vn");
            guest.setPassword(encoder.encode("12341234"));
            guest.setRoles(Collections.singletonList(questRole));
            guest.setName("Guest");

            User multipleUser = new User();
            multipleUser.setEmail("mul@tma.com.vn");
            multipleUser.setPassword(encoder.encode("12341234"));
            List<Role> roles = new ArrayList<>();
            roles.add(adminRole);
            roles.add(userRole);
            multipleUser.setRoles(roles);

            userRepository.save(admin);


            userRepository.save(user);

            userRepository.save(guest);

            userRepository.save(multipleUser);
        }


    }
}
