package com.github.Clementol.lovable_clone.controller;

import com.github.Clementol.lovable_clone.dto.project.FileContentResponse;
import com.github.Clementol.lovable_clone.dto.project.FileNode;
import com.github.Clementol.lovable_clone.dto.project.FileTreeResponse;
import com.github.Clementol.lovable_clone.service.ProjectFileService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/api/projects/{projectId}/files")
public class FileController {
    ProjectFileService fileService;

    @GetMapping
    public ResponseEntity<FileTreeResponse> getFileTree(@PathVariable Long projectId) {
        return ResponseEntity.ok(fileService.getFileTree(projectId));
    }

    @GetMapping("/content") // src/hooks/ApiHook.tsx
    public ResponseEntity<FileContentResponse> getFileTree(
            @PathVariable Long projectId,
            @RequestParam String path
    ) {
        Long userId = 1L;
        return ResponseEntity.ok(fileService.getFileContent(projectId, path));
    }
}
