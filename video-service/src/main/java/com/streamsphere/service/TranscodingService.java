package com.streamsphere.service;

import java.io.File;

public interface TranscodingService {
    File transcode(File inputFile, String resolution) throws Exception;
}
