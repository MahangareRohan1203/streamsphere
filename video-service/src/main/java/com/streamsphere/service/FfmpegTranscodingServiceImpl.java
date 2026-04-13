package com.streamsphere.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FfmpegTranscodingServiceImpl implements TranscodingService {

    public File transcode(File inputFile, String resolution) throws Exception {
        String outputFileName = inputFile.getParent() + "/" + resolution + "_" + inputFile.getName();
        File outputFile = new File(outputFileName);

        String scale;
        switch (resolution) {
            case "1080p": scale = "1920:1080"; break;
            case "720p":  scale = "1280:720";  break;
            case "480p":  scale = "854:480";   break;
            default: throw new IllegalArgumentException("Unknown resolution: " + resolution);
        }

        List<String> command = new ArrayList<>();
        command.add("ffmpeg");
        command.add("-i");
        command.add(inputFile.getAbsolutePath());
        command.add("-vf");
        command.add("scale=" + scale);
        command.add("-c:v");
        command.add("libx264");
        command.add("-crf");
        command.add("23");
        command.add("-preset");
        command.add("veryfast");
        command.add("-c:a");
        command.add("copy");
        command.add(outputFile.getAbsolutePath());

        log.info("Executing command: {}", String.join(" ", command));

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.debug("FFmpeg: {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("FFmpeg process failed with exit code " + exitCode);
        }

        return outputFile;
    }
}
