package com.iae.files;

public class OutputComparator {

    private String lastDiffMessage = "";

    /**
     * actualOutput ile expectedOutput'u satır bazlı karşılaştırır.
     * \r\n ile \n aynı sayılır; baş/son boşluklar trim edilir.
     * Eşleşirse true, farklıysa false döner.
     * Fark varsa getCombinedOutput() hangi satırın farklı olduğunu söyler.
     */
    public boolean compare(String actualOutput, String expectedOutput) {
        return compare(actualOutput, expectedOutput, false);
    }

    /**
     * ignoreCase=true ise büyük/küçük harf farkı gözetmeden karşılaştırır.
     * Diğer normalleştirmeler (satır sonu, trailing whitespace) her iki modda da uygulanır.
     */
    public boolean compare(String actualOutput, String expectedOutput, boolean ignoreCase) {
        String[] actualLines   = splitLines(actualOutput);
        String[] expectedLines = splitLines(expectedOutput);

        if (actualLines.length != expectedLines.length) {
            lastDiffMessage = "Satır sayısı farklı: beklenen "
                    + expectedLines.length + " satır, alınan " + actualLines.length + " satır.";
            return false;
        }

        for (int i = 0; i < expectedLines.length; i++) {
            String a = ignoreCase ? actualLines[i].toLowerCase()   : actualLines[i];
            String e = ignoreCase ? expectedLines[i].toLowerCase() : expectedLines[i];
            if (!a.equals(e)) {
                lastDiffMessage = "Satır " + (i + 1) + " farklı:\n"
                        + "  Beklenen : [" + expectedLines[i] + "]\n"
                        + "  Alınan   : [" + actualLines[i] + "]";
                return false;
            }
        }

        lastDiffMessage = "";
        return true;
    }

    /**
     * Son karşılaştırmada fark varsa hangi satırın farklı olduğunu açıklar.
     * Eşleşme varsa boş string döner.
     */
    public String getCombinedOutput() {
        return lastDiffMessage;
    }

    /**
     * compare() ile aynı davranışı sağlar; SubmissionProcessor tarafından kullanılır.
     */
    public boolean compareAgainstExpected(String actualOutput, String expectedOutput) {
        return compare(actualOutput, expectedOutput, false);
    }

    /**
     * ignoreCase seçeneğiyle SubmissionProcessor tarafından kullanılır.
     */
    public boolean compareAgainstExpected(String actualOutput, String expectedOutput, boolean ignoreCase) {
        return compare(actualOutput, expectedOutput, ignoreCase);
    }

    // \r\n → \n, \r → \n; trim baş/son; sonra satırlara böl
    private String[] splitLines(String text) {
        if (text == null) {
            return new String[]{};
        }
        String normalised = text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .trim();

        if (normalised.isEmpty()) {
            return new String[]{};
        }

        String[] lines = normalised.split("\n", -1);
        // Her satırın sağ boşluğunu da temizle
        for (int i = 0; i < lines.length; i++) {
            lines[i] = lines[i].stripTrailing();
        }
        return lines;
    }
}
