package com.amonsoftware.foldermetrics;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.stream.Stream;

@RestController
public class Controller {

    private static final String MNT_FILESERVER = "/mnt/fileserver";
    private static final String PROMETEUS_METRIC_TEMPLATE = "# HELP files_count Number of files on the NFS share.\n# TYPE files_count gauge\nfiles_count{share_name=\"%s\"}  %s %s\n";

    @RequestMapping(value = "/metrics/share_name/{share_name}", produces = "text/plain; version=0.0.4")
    public String getNumberOfFiles(@PathVariable("share_name") String name, @RequestParam(value = "recursive", defaultValue = "false") Boolean recursive) {
        Path sourcePath = Paths.get(MNT_FILESERVER);

        long count;
        try (Stream<Path> stream = Files.walk(sourcePath, recursive ? Integer.MAX_VALUE : 1)) {
            count = stream.filter(path -> path.toFile().isFile()).count();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        System.out.println(String.format("Found %s files in the folder %s", count, MNT_FILESERVER));
        return String.format(PROMETEUS_METRIC_TEMPLATE, name, count, Instant.now().toEpochMilli());
    }

    @RequestMapping(value = "/")
    public String probe() {
        return "alive";
    }

}
