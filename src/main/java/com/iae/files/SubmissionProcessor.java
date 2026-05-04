package com.iae.files;

import com.iae.core.Configuration;
import com.iae.core.StudentResult;
import com.iae.execution.CommandRunner;
import com.iae.execution.ProcessResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

// TODO: Gözde Yılıkyılmaz tarafından implement edilecek (Core + Installer modülü — AssignmentRunner)
public class SubmissionProcessor {

    private final ZipExtractor zipExtractor           = new ZipExtractor();
    private final WorkspaceManager workspaceManager   = new WorkspaceManager();
    private final FileLocator fileLocator             = new FileLocator();
    private final OutputComparator outputComparator   = new OutputComparator();
    private final CommandRunner commandRunner         = new CommandRunner();

    /**
     * submissionsDir içindeki tüm ZIP dosyalarını işler.
     * Her ZIP için: aç → kaynak dosyayı bul → derle → çalıştır → karşılaştır → sonuç üret.
     * Bir öğrencide hata olsa bile diğerleri işlenmeye devam eder (NR-01 robustness).
     *
     * @param submissionsDir  Öğrenci ZIP dosyalarının bulunduğu dizin
     * @param config          Proje yapılandırması (derleyici yolu, komutlar vb.)
     * @param expectedOutput  Beklenen program çıktısı (karşılaştırma için)
     * @param ignoreCase      Çıktı karşılaştırmasında büyük/küçük harf duyarsızlığı
     * @return                Her öğrenci için bir StudentResult listesi
     */
    public List<StudentResult> processAll(File submissionsDir,
                                          Configuration config,
                                          String expectedOutput,
                                          boolean ignoreCase) {
        // TODO: Gözde
        return new ArrayList<>();
    }

    /**
     * Tek bir öğrencinin ZIP dosyasını işler ve StudentResult döndürür.
     * Hata durumunda (bozuk ZIP, compile hatası, dosya bulunamıyor) exception fırlatmak yerine
     * StudentResult içinde hata durumunu kaydeder.
     *
     * @param zipFile        Öğrencinin ZIP dosyası
     * @param config         Proje yapılandırması
     * @param expectedOutput Beklenen program çıktısı
     * @param ignoreCase     Büyük/küçük harf duyarsız karşılaştırma
     * @return               Öğrencinin değerlendirme sonucu
     */
    public StudentResult processSingle(File zipFile,
                                       Configuration config,
                                       String expectedOutput,
                                       boolean ignoreCase) {
        // TODO: Gözde
        // Akış:
        // 1. ZipExtractor.extract(zipFile, workspaceDir)           → InvalidZipException
        // 2. WorkspaceManager.createWorkspace(studentId)           → IOException
        // 3. FileLocator.locate(workspace, config.getSourceFilename()) → FileNotFoundException
        // 4. CommandRunner.compile(config, workspace)              → ProcessResult
        // 5. Compile başarısızsa → StudentResult(COMPILE_ERROR) döndür, workspace temizle
        // 6. CommandRunner.run(...)                                → ProcessResult
        // 7. OutputComparator.compare(actual, expected, ignoreCase) → boolean
        // 8. StudentResult(PASS / FAIL) oluştur
        // 9. WorkspaceManager.cleanWorkspace(studentId)
        return null;
    }

    /**
     * Dizin içindeki tüm .zip dosyalarını döndürür.
     */
    private File[] findZipFiles(File dir) {
        // TODO: Gözde
        return dir.listFiles(f -> f.isFile() && f.getName().toLowerCase().endsWith(".zip"));
    }

    /**
     * ZIP dosyasının adından öğrenci ID'sini çıkarır.
     * Örnek: "20220602021.zip" → "20220602021"
     */
    private String extractStudentId(File zipFile) {
        // TODO: Gözde
        String name = zipFile.getName();
        return name.endsWith(".zip") ? name.substring(0, name.length() - 4) : name;
    }

    /**
     * Beklenen çıktıyı bir dosyadan okur.
     */
    public String readExpectedOutput(File expectedOutputFile) throws IOException {
        // TODO: Gözde
        return Files.readString(expectedOutputFile.toPath());
    }
}
