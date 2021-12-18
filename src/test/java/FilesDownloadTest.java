import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Тесты на файлы")
public class FilesDownloadTest {

    @Test
    @DisplayName("Имя файла отображается после загрузки")
    void filenameShouldDisplayedAfterUploadActionFromClasspathTest() {
        open("https://demoqa.com/upload-download");

        $("#uploadFile").uploadFromClasspath("test.txt");
        $("#uploadedFilePath").shouldHave(text("test.txt"));
    }

    @Test
    @DisplayName("Скачивание текстового файла и проверка его содержимого")
    void txtFileDownloadTest() throws IOException {
        open("https://filesamples.com/formats/txt");
        File download = $("a[href*='sample1.txt']").download();
        String fileContent = IOUtils.toString(new FileReader(download));

        assertTrue(fileContent.contains("Lorem ipsum dolor sit amet, consectetur adipiscing elit."));
    }

    @Test
    @DisplayName("Скачивание PDF файла и проверка его содержимого")
    void pdfFileDownloadTest() throws IOException {
        open("https://file-examples.com/index.php/sample-documents-download/sample-pdf-download/");
        File pdf = $("a[href*='example_PDF_1MB.pdf']").download();
        PDF parsedPdf = new PDF(pdf);

        assertEquals(30, parsedPdf.numberOfPages);
    }

    @Test
    @DisplayName("Скачивание XLS файла и проверка его содержимого")
    void xlsFileDownloadTest() throws IOException {
        open("https://file-examples.com/index.php/sample-documents-download/sample-xls-download/");
        File xlsFile = $("a[download='file_example_XLS_100.xls']").download();
        XLS parsedXls = new XLS(xlsFile);
        boolean checkCell = parsedXls.excel
                .getSheetAt(0)
                .getRow(5)
                .getCell(2)
                .getStringCellValue()
                .contains("Magwood");

        assertTrue(checkCell);
    }

    @Test
    @DisplayName("Парсинг CSV файлов")
    void parseCsvFileTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("csv.csv");
             Reader reader = new InputStreamReader(is)) {
             CSVReader csvReader = new CSVReader(reader);

             List<String[]> strings = csvReader.readAll();
             assertEquals(5001, strings.size());
        }
    }

    @Test
    @DisplayName("Парсинг ZIP файлов")
    void parseZipFileTest() throws IOException, CsvException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream is = classLoader.getResourceAsStream("sample-zip-file.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());
                assertEquals("sample.txt", entry.getName());
            }
        }
    }

}
