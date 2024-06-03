package org.acme.data;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "clinic_procedures")
public class Procedure {
    @Id
    @Column(name="procedure_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @OneToMany(mappedBy = "procedure", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Appointments> appointmentsForProcedure;
}
