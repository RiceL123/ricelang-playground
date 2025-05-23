package ricelang.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ricelang.VC.vc;

import jasmin.Main;

@RestController
@CrossOrigin(origins = { "http://localhost:3000", "https://ricelang-playground.vercel.app/" })
public class RicelangController {

    @PostMapping("/run")
    public Output run(@RequestBody SourceCodeBody sourceCodebody) {
        vc vc = new vc();
        String outputFileBase = "temp" + UUID.randomUUID().toString().replace("-", "");
        String jasminFile = outputFileBase + ".j";
        StringBuilder verbose = new StringBuilder();

        if (sourceCodebody.getSourceCode() == null)
            return new Output("404 no source code", "", true);

        // generate temp.j
        Optional<String> opt = vc.compile(outputFileBase, sourceCodebody.getSourceCode(), verbose);
        if (opt.isPresent()) {
            new File(jasminFile).delete();
            return new Output(opt.get(), verbose.toString(), true);
        }
        verbose.append("Generated: " + jasminFile + "\n");

        // compile temp.j to a .class file
        Main.main(new String[] { jasminFile });
        new File(jasminFile).delete();
        verbose.append("Running: " + outputFileBase + ".class\n");

        StringBuilder output = new StringBuilder();
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
            } else {
                output.append("\nError: Timed out after [2s] ...\n");
                process.destroyForcibly();
            }

            new File(outputFileBase + ".class").delete();
            return new Output(output.toString(), verbose.toString(), false);
        } catch (Exception e) {
            e.printStackTrace();
            new File(outputFileBase + ".class").delete();
            return new Output("Internal error: " + e.getMessage(), verbose.toString(), true);
        }
    }

    @GetMapping("/healthcheck")
    public String healthcheck() {
        return "OK";
    }
}
