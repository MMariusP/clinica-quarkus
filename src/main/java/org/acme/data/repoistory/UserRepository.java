package org.acme.data.repoistory;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.data.User;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
