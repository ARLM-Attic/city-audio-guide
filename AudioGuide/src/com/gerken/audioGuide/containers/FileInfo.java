package com.gerken.audioGuide.containers;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class FileInfo {
	private FileInputStream _fileInputStream;
	private long _length;
	
	public FileInfo(FileInputStream fileInputStream, long length) {
		_fileInputStream = fileInputStream;
		_length = length;
	}
	
	public FileDescriptor getFileDescriptor() throws IOException {
		return _fileInputStream.getFD();
	}
	
	public long getLength() {
		return _length;
	}
	
	public void close() throws IOException {
		_fileInputStream.close();
	}
}
