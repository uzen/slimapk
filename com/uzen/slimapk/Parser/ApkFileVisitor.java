package com.uzen.slimapk.Parser;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;

public abstract class ApkFileVisitor extends SimpleFileVisitor <Path> {
	
		public Path target;
		
		public ApkFileVisitor() {
			//nothing   	
		};
			
		public ApkFileVisitor(Path dir) {
			target = dir;
		}
		
		public abstract void actionFile(Path file);
		public abstract void actionDir(Path dir);
		
		@Override
		public FileVisitResult visitFile(Path path, BasicFileAttributes fileAttributes) {
			actionFile(path);
			return FileVisitResult.CONTINUE;
		}
		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
			actionDir(dir);
			return FileVisitResult.CONTINUE;
		}
}

