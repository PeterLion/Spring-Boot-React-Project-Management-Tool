package io.pptool.project.web;

import io.pptool.project.domain.Project;
import io.pptool.project.services.MapValidationErrorService;
import io.pptool.project.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping("")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody Project project, BindingResult result, Principal principal){
       ResponseEntity<?> errorMap = mapValidationErrorService.mapValidationService(result);
       if (errorMap != null) return errorMap;
       return new ResponseEntity<Project>(projectService.saveOrUpdateProject(project,principal.getName()), HttpStatus.CREATED);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> findProjectById(@PathVariable String projectId, Principal principal){
        Project project = projectService.findProjectByIdentifier(projectId,principal.getName());
      return new ResponseEntity<Project>(project,HttpStatus.OK);
    }

    @GetMapping("/all")
    public Iterable<Project> getAllprojects(Principal principal){return projectService.findAllProjects(principal.getName());}

    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable String projectId, Principal principal){
        projectService.deleteProjectByIdentifier(projectId,principal.getName());
        return new ResponseEntity<String>("Project with ID: '"+projectId+"' was deleted", HttpStatus.OK);
    }
}
