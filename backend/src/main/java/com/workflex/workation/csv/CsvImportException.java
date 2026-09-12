package com.workflex.workation.csv;

/**
 * Thrown when the CSV cannot be read, or a row cannot be understood.
 *
 * <p>Unchecked: no caller can recover from a malformed source file.
 */
public class CsvImportException extends RuntimeException {

	public CsvImportException(String message) {
		super(message);
	}

	public CsvImportException(String message, Throwable cause) {
		super(message, cause);
	}
}
