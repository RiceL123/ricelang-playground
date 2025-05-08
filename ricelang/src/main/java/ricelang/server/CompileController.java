package ricelang.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ricelang.VC.vc;

import jasmin.Main;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class CompileController {

    @PostMapping("/compile")
    public Compile compile(@RequestBody SourceCodeBody sourceCodebody) {
        vc vc = new vc();
        int exitCode = 1;
        String outputFileBase = "temp" + UUID.randomUUID().toString().replace("-", "");
        String jasminFile = outputFileBase + ".j";

        if (sourceCodebody.getSourceCode() == null) return new Compile("null mate", 1);
        
        // generate temp.j
        Optional<String> opt = vc.compile(outputFileBase, sourceCodebody.getSourceCode());
        if (opt.isPresent()) {
            new File(jasminFile).delete();
            return new Compile(opt.get(), exitCode);
        }

        // compile temp.j to a .class file
        Main.main(new String[] { jasminFile });
        new File(jasminFile).delete();

        // run the .class file on the jvm (pipe output to string) to return to request
        try {
            ProcessBuilder builder = new ProcessBuilder("java", outputFileBase);
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
            exitCode = process.exitValue();

            new File(outputFileBase + ".class").delete();
            return new Compile(output.toString(), exitCode);
        } catch (Exception e) {
            e.printStackTrace();
            new File(outputFileBase + ".class").delete();
            return new Compile("Internal error: " + e.getMessage(), exitCode);
        }
    }
}
