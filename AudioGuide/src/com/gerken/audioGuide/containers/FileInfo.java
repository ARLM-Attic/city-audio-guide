package com.gerken.audioGuide.containers;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class FileInfo {
	private FileInputStream _fileInputStream;
	private long _offset;
	private long _length;
	
	public FileInfo(FileInputStream fileInputStream, long length) {
		_fileInputStream = fileInputStream;
		_offset = 0L;
		_length = length;
	}
	
	public FileInfo(FileInputStream fileInputStream, long offset, long length) {
		_fileInputStream = fileInputStream;
		_offset = offset;
		_length = length;
	}
	
	public FileDescriptor getFileDescriptor() throws IOException {
		return _fileInputStream.getFD();
	}
	
	public long getStartOffset() {
		return _offset;
	}
	
	public long getLength() {
		return _length;
	}
	
	public void close() throws IOException {
		if(_fileInputStream != null)
			_fileInputStream.close();
	}
}
