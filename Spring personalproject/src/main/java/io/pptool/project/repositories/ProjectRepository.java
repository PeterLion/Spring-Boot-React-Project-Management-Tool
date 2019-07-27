package io.pptool.project.repositories;

import io.pptool.project.domain.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends CrudRepository<Project,Integer> {
    Project findByProjectIdentifier(String projectId);
    @Override
    Iterable<Project> findAll();
    Iterable<Project> findAllByOrderByIdDesc();
    Iterable<Project> findAllByProjectLeader(String username);
}
