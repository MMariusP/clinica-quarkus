package org.acme.data;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.security.jpa.RolesValue;
import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "clinic_roles")
public class Role extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    public Integer id;

    @RolesValue
    @Column(nullable = false, unique = true)
    public String name;

    @ManyToMany(mappedBy = "roles")
    public Set<User> users;
}
