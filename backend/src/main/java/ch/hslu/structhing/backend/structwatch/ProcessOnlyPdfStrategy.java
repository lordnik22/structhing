package ch.hslu.structhing.backend.structwatch;

import ch.hslu.structhing.backend.jooq.generated.Tables;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jooq.DSLContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcessOnlyPdfStrategy
        implements ProcessWatchPathStrategy {

    private final DSLContext dsl;
    private final Path child;
    public static final String PDF = ".pdf";
    public static final String COUNTER_SEPARATOR = "-";
    public ProcessOnlyPdfStrategy(DSLContext dsl, Path child) {
        this.dsl = dsl;
        this.child = child;
    }

    @Override
    public ProcessWatchPathType getType() {
        return ProcessWatchPathType.PDF_ONLY;
    }

    @Override
    public void process() {
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
                if ((counter >= 0)
                        && dsl.fetchExists(dsl.selectOne()
                        .from(Tables.STRUCT_WATCH_FILE)
                        .where(Tables.STRUCT_WATCH_FILE.CURRENT_FILE_PATH.eq(child.toAbsolutePath().toString())))) {
                    return;
                } else {
                    Files.move(child, newPathName, StandardCopyOption.ATOMIC_MOVE);
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
}
