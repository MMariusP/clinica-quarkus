package org.acme.data.controller;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.data.Procedure;
import org.acme.data.boundry.dto.ProcedureDto;
import org.acme.data.repoistory.ProcedureRepository;

import java.util.List;
import java.util.stream.Collectors;


@ApplicationScoped
public class ProcedureService {
    @Inject
    ProcedureRepository repo;

    public List<ProcedureDto> listAll() {
        return repo.listAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public ProcedureDto findById(Long id) {
        return mapToDto(repo.findById(id));
    }

    @Transactional
    public ProcedureDto create(ProcedureDto procedureDto) {
        Procedure p = Procedure.builder().name(procedureDto.getName()).build();
        repo.persist(mapToModel(procedureDto));
        return mapToDto(p);
    }

    @Transactional
    public ProcedureDto update(Long id, ProcedureDto procedureDto) {
        Procedure procedure = repo.findById(id);
        if (procedure == null) return null;
        procedure.setName(procedureDto.getName());
        return mapToDto(procedure);
    }

    @Transactional
    public boolean delete(Long id) {
        return repo.deleteById(id);
    }

    public ProcedureDto mapToDto(Procedure p) {
        return ProcedureDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }

    public Procedure mapToModel(ProcedureDto p) {
        return Procedure.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .build();
    }
}
