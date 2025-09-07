package org.acme.data.repoistory;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.data.User;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public void deleteUserByUsername(String username) {
        delete("username", username);
    }

    public User findByUsername(String username) {
        return find("username", username).firstResult();
    }

}


