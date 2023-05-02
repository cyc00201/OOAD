package pvs.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import pvs.app.config.ApplicationConfig;
import pvs.app.dto.CommitDTO;
import pvs.app.dto.GithubIssueDTO;
import pvs.app.dto.GithubPullRequestDTO;
import pvs.app.service.GithubApiService;
import pvs.app.service.GithubCommitService;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class GithubApiController {
    private final GithubApiService githubApiService;
    private final GithubCommitService githubCommitService;
    @Value("${message.exception}")
    private String exceptionMessage;

    public GithubApiController(GithubApiService githubApiService, GithubCommitService githubCommitService) {
        this.githubApiService = githubApiService;
        this.githubCommitService = githubCommitService;
    }

    @SneakyThrows
    @PostMapping("/github/commits/{repoOwner}/{repoName}")
    public ResponseEntity<String> postCommits(@PathVariable("repoOwner") String repoOwner, @PathVariable("repoName") String repoName) {
        boolean callAPISuccess;
        CommitDTO githubCommitDTO = githubCommitService.getLastCommit(repoOwner, repoName);
        final Date lastUpdate = githubCommitDTO == null ? Date.from(Instant.ofEpochSecond(0)) : githubCommitDTO.getCommittedDate();

        try {
            callAPISuccess = githubApiService.getCommitsFromGithub(repoOwner, repoName, lastUpdate);
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(exceptionMessage);
        }

        if (callAPISuccess) {
            return ResponseEntity.status(HttpStatus.OK).body("success get commit data and save to database");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("cannot get commit data");
        }
    }

    @GetMapping("/github/commits/{repoOwner}/{repoName}")
    public ResponseEntity<String> getCommits(@PathVariable("repoOwner") String repoOwner, @PathVariable("repoName") String repoName) {

        ObjectMapper objectMapper = new ObjectMapper();

        List<CommitDTO> githubCommitDTOs = githubCommitService.getAllCommits(repoOwner, repoName);

        String githubCommitDTOsJson;

        try {
            githubCommitDTOsJson = objectMapper.writeValueAsString(githubCommitDTOs);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(githubCommitDTOsJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(exceptionMessage);
        }
    }

    @GetMapping("/github/branchList/{repoOwner}/{repoName}")
    public ResponseEntity<List<String>> getBranchList(@PathVariable("repoOwner") String repoOwner, @PathVariable("repoName") String repoName) {
        CommitDTO githubCommitDTO = githubCommitService.getLastCommit(repoOwner, repoName);
        final Date lastUpdate = githubCommitDTO == null ? Date.from(Instant.ofEpochSecond(0)) : githubCommitDTO.getCommittedDate();

        try {
            List<String> branchNameList = this.githubApiService.getBranchNameList(repoOwner, repoName, lastUpdate);
            if (branchNameList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(branchNameList);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/github/commits")
    public ResponseEntity<String> getCommitsOfBranch(@RequestParam("repoOwner") String repoOwner, @RequestParam("repoName") String repoName, @RequestParam("branchName") String branchName) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<CommitDTO> githubCommitDTOS = this.githubCommitService.getCommitsOfSpecificBranch(repoOwner, repoName, branchName);
        try {
            String githubCommitDTOsJson = objectMapper.writeValueAsString(githubCommitDTOS);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(githubCommitDTOsJson);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Get commits from branches failed");
        }
    }

    @GetMapping("/github/issues/{repoOwner}/{repoName}")
    public ResponseEntity<String> getIssues(@PathVariable("repoOwner") String repoOwner, @PathVariable("repoName") String repoName) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<GithubIssueDTO> githubIssueDTOs = null;

        try {
            // Retry if the githubIssueDTOs is null
            int retryCount = 0;
            while (retryCount <= 5) {
                githubIssueDTOs = githubApiService.getIssuesFromGithub(repoOwner, repoName);
                if (githubIssueDTOs != null) break;
                retryCount++;
            }

            if (githubIssueDTOs == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Get issue data failed from GitHub API");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(exceptionMessage);
        }

        try {
            String githubIssueDTOsJson = objectMapper.writeValueAsString(githubIssueDTOs);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(githubIssueDTOsJson);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(exceptionMessage);
        }
    }

    @GetMapping("/github/pullRequests/{repoOwner}/{repoName}")
    public ResponseEntity<String> getPullRequests(@PathVariable("repoOwner") String repoOwner, @PathVariable("repoName") String repoName) {
        ObjectMapper objectMapper = new ObjectMapper();

        List<GithubPullRequestDTO> githubPullRequestDTOs = null;

        try {
            // Retry if the githubPullRequestDTOs is null
            int retryCount = 0;
            while (retryCount <= 5) {
                githubPullRequestDTOs = githubApiService.getPullRequestMetricsFromGithub(repoOwner, repoName);
                if (githubPullRequestDTOs != null) break;
                retryCount++;
            }

            if (githubPullRequestDTOs == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Get pull request data failed from GitHub API");
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(exceptionMessage);
        }

        try {
            String githubPullRequestDTOsJson = objectMapper.writeValueAsString(githubPullRequestDTOs);
            return ResponseEntity.status(HttpStatus.OK).body(githubPullRequestDTOsJson);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(exceptionMessage);
        }
    }
}
