package io.pptool.project.services;

import io.pptool.project.domain.Backlog;
import io.pptool.project.domain.Project;
import io.pptool.project.domain.ProjectTask;
import io.pptool.project.exceptions.ProjectNotFoundException;
import io.pptool.project.repositories.BacklogRepository;
import io.pptool.project.repositories.ProjectRepository;
import io.pptool.project.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTaskService {
    @Autowired
    private BacklogRepository backlogRepository;
    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;


    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask, String username){
        try {
            Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier, username).getBacklog();

            projectTask.setBacklog(backlog);

            Integer backlogSequence = backlog.getPTSequence();
            backlogSequence++;
            backlog.setPTSequence(backlogSequence);

            projectTask.setProjectSequence(projectIdentifier+"-"+backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            if (projectTask.getStatus() =="" || projectTask.getStatus()==null){
                projectTask.setStatus("TO_DO");
            }

            if (projectTask.getPriority()==null || projectTask.getPriority()== 0){
                projectTask.setPriority(3);
            }
            return projectTaskRepository.save(projectTask);
        }catch (Exception ex){
            throw new ProjectNotFoundException("Project '"+projectIdentifier+"' Not Found in your Account!");
        }
    }

    public Iterable<ProjectTask> findBacklogById(String backlog_id, String username) {
        Project project = projectRepository.findByProjectIdentifier(backlog_id);

        projectService.findProjectByIdentifier(backlog_id, username);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(backlog_id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id,String pt_id, String username){
        projectService.findProjectByIdentifier(backlog_id, username);

        //make sure that our task exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);

        if(projectTask == null){
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' not found");
        }

        //make sure that the backlog/project id in the path corresponds to the right project
        if(!projectTask.getProjectIdentifier().equals(backlog_id)){
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' does not exist in project: '"+backlog_id);
        }



        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id, String pt_id, String username){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id,username);

        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id, String username){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id,username);

        projectTaskRepository.delete(projectTask);
    }
}
