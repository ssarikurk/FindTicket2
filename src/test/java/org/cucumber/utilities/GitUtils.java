package org.cucumber.utilities;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;


public class GitUtils {

    public static void gitPullFullstack() {
        String projectPath = "C:\\Users\\Administrator\\vscoderet\\fullstack\\ret-fullstack";
        String gitPath = projectPath + File.separator + ".git";
//        System.out.println("gitPath = " + gitPath);

        // Replace "your_personal_access_token" with your actual GitHub Personal Access Token
        String personalAccessToken = ConfigurationReader.get("githubToken");

        try {
            // Initialize Git repository
            Repository repository = new FileRepository(gitPath);
            Git git = new Git(repository);

            // Set up the credential provider with the Personal Access Token
            UsernamePasswordCredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("token", personalAccessToken);

            // Pull changes from the remote repository
            git.pull().setCredentialsProvider(credentialsProvider).call();

            System.out.println("Git pull operation completed successfully.");

        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
        }
    }


    public static void gitPullFullstack2(){
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("git", "pull");

            processBuilder.directory(new File("C:\\Users\\Administrator\\vscoderet\\fullstack\\ret-fullstack"));

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Pull successful");
            } else {
                System.err.println("Error while pulling changes");
            }
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static void sendChangesToGithub() {

            String projectPath = "C:\\Users\\Administrator\\IdeaProjects\\QA";

//        System.out.println("Project Path: " + projectPath);
            String gitPath = projectPath + File.separator + ".git";
            System.out.println("gitPath = " + gitPath);
            try {
                // Initialize Git repository
                FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
                Repository repository = repositoryBuilder.setGitDir(new File(gitPath))
                        .readEnvironment() // Scan environment GIT_* variables
                        .findGitDir() // Find the Git directory within the project
                        .build();

                // Create a Git object
                Git git = new Git(repository);
                // Stage files

//                git.add().addFilepattern(".").call(); // Stage all changes in the repository
                git.add()
                        .addFilepattern("src/test/resources/Downloads/crosswalk/semantic_payer_crosswalk_attachment.csv")
                        .addFilepattern("src/test/resources/Downloads/crosswalk/semantic_payer_crosswalk_eligibility.csv")
                        .addFilepattern("src/test/resources/Downloads/crosswalk/semantic_payer_crosswalk_preauth.csv")
                        .addFilepattern("src/test/resources/Downloads/crosswalk/semantic_payer_crosswalk_submission.csv")
                        .call();

                // Commit
                git.commit().setMessage("Sent from test framework-Suleyman").call();

                // Perform git checkout to switch to the main branch
                git.checkout().setName("main").call();

                // Pull (fetch + merge)
                git.pull().call();

//                // Merge with the main branch
//                MergeResult mergeResult = git.merge()
//                        .include(repository.resolve("main")) // Specify the branch to merge with
//                        .setCommit(true) // Automatically commit the merge result
//                        .setMessage("Merge changes from main branch")
//                        .call();
//
//                // Check for merge conflicts
//                if (mergeResult.getConflicts() != null && !mergeResult.getConflicts().isEmpty()) {
//                    // Resolve conflicts in CukesRunner.java by accepting the local version
//                    for (String conflict : mergeResult.getConflicts().keySet()) {
//                        if (conflict.equals(projectPath + File.separator + "src/test/java/org/cucumber/runners/CukesRunner.java")) {
//                            // Resolve conflicts by accepting the local version
//                            git.checkout().setStage(CheckoutCommand.Stage.THEIRS).addPath(conflict).call();
//                        }
//                    }
//                    // Commit the resolved conflicts
//                    git.commit().setMessage("Resolved conflicts by accepting local version").call();
//                }

                // Push
                git.push().call();

                System.out.println("Git operations completed successfully.");

            } catch (IOException | GitAPIException e) {
                e.printStackTrace();
            }

    }

}