package ch.hslu.structhing.backend.structwatch;

import ch.hslu.structhing.backend.jooq.generated.Tables;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchServiceRecursiveSupport {

    public static final String PDF = ".pdf";
    public static final String COUNTER_SEPARATOR = "-";
    private final ConcurrentMap<WatchKey,Path> keys;
    private final DSLContext dsl;
    private boolean trace = true;
    private final WatchService watchService = FileSystems.getDefault().newWatchService();

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }

    /**
     * Register the given directory with the WatchService
     */
    public void register(Path dir) throws IOException {
        initalProcess(dir);
        WatchKey key = dir.register(watchService, ENTRY_CREATE);
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

    private void initalProcess(Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs)
                throws IOException
            {
                processPDFFiles(start.resolve(dir));
                return FileVisitResult.CONTINUE;
            }
        });
    }

    WatchServiceRecursiveSupport(List<Path> directoryPaths, DSLContext dsl) throws IOException {
        this.dsl = dsl;
        this.keys = new ConcurrentHashMap<>();
        for (Path directoryPath : directoryPaths) {
            register(directoryPath);
        }
        // enable trace after initial registration
        this.trace = true;
    }

    /**
     * Process all events for keys queued to the watcher
     */
    public void processEvents() {
        for (;;) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watchService.take();
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
                processPDFFiles(child);

                System.out.format("%s: %s\n", event.kind().name(), child);
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

    private void processPDFFiles(Path child) {
        String fileExtenstion = getFileExtension(child);
        if (fileExtenstion != null && fileExtenstion.equalsIgnoreCase(PDF)) {
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

                // AI Themen
                // Ist AI-predict langsam?
                // ISt AI Model laufen auf Customer Gerät, machbar? sinnhaft?
                String stringPath = child.getParent().toAbsolutePath()
                        + File.separator
                        + words.get(0)
                        + words.get(1)
                        + words.get(2);
                int counter = 0;
                Path newPathName = Path.of(stringPath + PDF);
                while(Files.exists(newPathName)) {
                    if (counter >= Integer.MAX_VALUE) {
                        throw new RuntimeException("I cant handle that anymore");
                    } else {
                        counter++;
                    }
                    newPathName = Path.of(stringPath + COUNTER_SEPARATOR + counter + PDF);
                }
                System.out.println("our new file name: " + stringPath);
                boolean existNew = dsl.fetchExists(dsl.selectOne()
                        .from(Tables.STRUCT_WATCH_FILE)
                        .where(Tables.STRUCT_WATCH_FILE.CURRENT_FILE_PATH.eq(child.toAbsolutePath().toString())));
                if (existNew && counter >= 0) {
                    return;
                } else {
                    Files.move(child, newPathName, ATOMIC_MOVE);
                    dsl.insertInto(Tables.STRUCT_WATCH_FILE, Tables.STRUCT_WATCH_FILE.CURRENT_FILE_PATH,Tables.STRUCT_WATCH_FILE.OLD_FILE_PATH)
                            .values(newPathName.toAbsolutePath().toString(),
                                    child.toAbsolutePath().toString())
                            .execute();
                }
            } catch (IOException e) {
                e.printStackTrace();
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

        ExecutorService pool = Executors.newFixedThreadPool(2);
        // register directory and process its events

        DSLContext dsl = DSL.using("jdbc:h2:/home/lordnik/Documents/structhing/backend/src/main/resources/database/structhing","sa","");
        WatchServiceRecursiveSupport a = new WatchServiceRecursiveSupport(new ArrayList<>(), dsl);
        // API CALL
        // a.addWatchPath(unmarshalred json);
        // WATCH THREAD
        pool.submit(() -> a.processEvents());
        pool.submit(() -> {
            List<Path> directoryPaths = Stream.of(
                            "/home/lordnik/Pictures/",
                            "/home/lordnik/Downloads/",
                            "/home/lordnik/Videos/")
                    .map(Paths::get)
                    .collect(Collectors.toList());
            System.out.println("Starting to add paths...");
            for(Path p : directoryPaths) {
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    a.register(p);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
