package ricelang.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ricelang.VC.vc;

import jasmin.Main;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class RicelangController {

    @PostMapping("/compile")
    public Compile compile(@RequestBody SourceCodeBody sourceCodebody) {
        vc vc = new vc();
        int exitCode = 1;
        String outputFileBase = "temp" + UUID.randomUUID().toString().replace("-", "");
        String jasminFile = outputFileBase + ".j";
        StringBuilder output = new StringBuilder();
        
        if (sourceCodebody.getSourceCode() == null) return new Compile("404 no source code", 1);
        
        // generate temp.j
        Optional<String> opt = vc.compile(outputFileBase, sourceCodebody.getSourceCode(), output);
        if (opt.isPresent()) {
            new File(jasminFile).delete();
            return new Compile(opt.get(), exitCode);
        }
        output.append("Generated: " + jasminFile + "\n");

        // compile temp.j to a .class file
        Main.main(new String[] { jasminFile });
        new File(jasminFile).delete();
        output.append("Running: " + outputFileBase + ".class\n");

        // run the .class file on the jvm (pipe output to string) to return to request
        try {
            ProcessBuilder builder = new ProcessBuilder("java", outputFileBase);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            
            if (process.waitFor(2, TimeUnit.SECONDS)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
    
                exitCode = process.exitValue();
            } else {
                output.append("\nError: Timed out after [2s] ...\n");
                process.destroyForcibly();
                exitCode = 1;
            }

            new File(outputFileBase + ".class").delete();
            return new Compile(output.toString(), exitCode);
        } catch (Exception e) {
            e.printStackTrace();
            new File(outputFileBase + ".class").delete();
            return new Compile("Internal error: " + e.getMessage(), exitCode);
        }
    }

    @PostMapping("/ast")
    public Mermaid ast(@RequestBody SourceCodeBody sourceCodebody) {
        vc vc = new vc();
        StringBuilder output = new StringBuilder();
        StringBuilder verbose = new StringBuilder();
        Optional<String> opt = vc.mermaidAST(sourceCodebody.getSourceCode(), output, verbose);
        if (opt.isPresent()) {
            return new Mermaid(output.toString(), verbose.toString(), opt.get());
        }

        return new Mermaid(output.toString(), verbose.toString());
    }
}
