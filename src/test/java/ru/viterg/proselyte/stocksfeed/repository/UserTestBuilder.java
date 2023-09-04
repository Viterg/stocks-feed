package ru.viterg.proselyte.stocksfeed.repository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;
import ru.viterg.proselyte.stocksfeed.user.RegisteredUser;
import ru.viterg.proselyte.stocksfeed.user.Role;

@With
@NoArgsConstructor
@AllArgsConstructor
public class UserTestBuilder implements TestBuilder<RegisteredUser> {

    private Long id;
    private final Role role = Role.AUTHORIZED_NEW;
    private final String mail = "email@mail.com";
    private final String password = "password";
    private final String username = "test_point";

    @Override
    public RegisteredUser build() {
        var user = new RegisteredUser();
        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(mail);
        user.setRole(role);
        user.setActive(true);
        return user;
    }

    public static UserTestBuilder aUser() {
        return new UserTestBuilder();
    }
}