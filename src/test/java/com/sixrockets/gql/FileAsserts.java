package com.sixrockets.gql;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.Charset;

/**
 * FileAsserts
 */
public class FileAsserts {

	@SneakyThrows
	public static void assertContentsEquals(String fileName, String toMatch) {
		File fileToRead = new File("src/test/resources/scenarios/" + fileName);
		assertEquals(FileUtils.readFileToString(fileToRead, Charset.defaultCharset()).trim(), toMatch);
	}
}
