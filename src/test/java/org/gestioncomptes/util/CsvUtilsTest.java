package org.gestioncomptes.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CsvUtilsTest {

    @Test
    void formatsAndParsesFieldsWithCommas() {
        String line = CsvUtils.formatLine("1", "Achat, courses", "SANTE");
        List<String> fields = CsvUtils.parseLine(line);
        assertEquals(List.of("1", "Achat, courses", "SANTE"), fields);
    }

    @Test
    void parsesSimpleLine() {
        List<String> fields = CsvUtils.parseLine("1,GOUGANG,Alvine");
        assertEquals(List.of("1", "GOUGANG", "Alvine"), fields);
    }
}
