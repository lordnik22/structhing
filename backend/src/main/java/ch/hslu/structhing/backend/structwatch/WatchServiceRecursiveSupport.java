package ch.hslu.structhing.backend.structwatch;
/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice,  this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchServiceRecursiveSupport {

    public static final String PDF = ".pdf";
    public static final String COUNTER_SEPARATOR = "-";
    private final java.nio.file.WatchService watcher;
    private final Map<WatchKey,Path> keys;
    private final boolean recursive;
    private boolean trace = true;
    private java.nio.file.WatchService watchService = FileSystems.getDefault().newWatchService();

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watcher, ENTRY_CREATE);
        if (trace) {
            Path prev = keys.get(key);
            if (prev == null) {
                System.out.format("register: %s\n", dir);
            } else {
                if (!dir.equals(prev)) {
                    System.out.format("update: %s -> %s\n", prev, dir);
                }
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    WatchServiceRecursiveSupport(List<Path> directoryPaths, boolean recursive) throws IOException {
        this.watcher = watchService;
        this.keys = new HashMap<WatchKey,Path>();
        this.recursive = recursive;
        for (Path directoryPath : directoryPaths) {
            register(directoryPath);
        }
        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException x) {
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = cast(event);
                Path name = ev.context();
                Path child = dir.resolve(name);

                // PDF Detection
                if (getFileExtension(name).equalsIgnoreCase(PDF)) {
                    System.out.println("It's a PDF!!!!");

                    try (PDDocument document = Loader.loadPDF(child.toFile())) {
                        System.out.println("our Title: " + document.getDocumentInformation().getTitle());// TODO consider title if not null
                        PDFTextStripper stripper = new PDFTextStripper();
                        String text = stripper.getText(document);

                        List<String> words = Stream.of(text.split(" "))
                                .filter(a -> a != null && !a.equals("") && !a.equals("\n"))// TODO white space regex
                                .map(a -> a.replace("\n", "")) // TODO regex for filtering special characters
                                .map(a -> a.replace(",", ""))
                                .map(a -> a.replace(File.separator, ""))
                                .collect(Collectors.toList());
                        Path newPathName = Path.of(child.getParent().toAbsolutePath()+File.separator+words.get(0)+words.get(1)+words.get(2)+PDF);
                        int counter = 1;
                        while(Files.exists(newPathName)) {
                            newPathName = Path.of(newPathName.toAbsolutePath().toString().replace(PDF, COUNTER_SEPARATOR + counter + PDF));
                            if (counter >= Integer.MAX_VALUE) {
                                throw new RuntimeException("I cant handle that anymore");
                            } else {
                                counter++;
                            }
                        }
                        Files.move(child,
                                newPathName,
                                REPLACE_EXISTING); // TODO better move options

                        System.out.println("our new file name: " + newPathName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                // AI Themen
                // Ist AI-predict langsam?
                // ISt AI Model laufen auf Customer Gerät, machbar? sinnhaft?


                System.out.format("%s: %s\n", event.kind().name(), child);



                // if directory is created, and watching recursively, then
                // register it and its sub-directories
                if (recursive && (kind == ENTRY_CREATE)) {
                    try {
                        if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                            registerAll(child);
                        }
                    } catch (IOException x) {
                        // ignore to keep sample readbale
                    }
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return null; // empty extension
        }
        return name.substring(lastIndexOf);
    }

    private String getFileExtension(Path file) {
     return getFileExtension(file.toFile());
    }

    public static void main(String[] args) throws IOException {
        // register directory and process its events
        List<Path> directoryPaths = Stream.of(
                        "/home/lordnik/Pictures/",
                        "/home/lordnik/Downloads/",
                        "/home/lordnik/Videos/")
                .map(Paths::get)
                .collect(Collectors.toList());

        WatchServiceRecursiveSupport a = new WatchServiceRecursiveSupport(directoryPaths, false);
        // API CALL
        // a.addWatchPath(unmarshalred json);
        // WATCH THREAD
        a.processEvents();
    }
}
