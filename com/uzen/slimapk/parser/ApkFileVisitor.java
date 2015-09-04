package com.uzen.slimapk.parser;

import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;

public class ApkFileVisitor extends SimpleFileVisitor <Path> {
	
		protected Path target;
		
		public ApkFileVisitor() {
			//nothing   	
		};
			
		public ApkFileVisitor(Path dir) {
			target = dir;
		}
		
		public void actionFile(Path file) {
			//nothing   
		};
		
		public void actionDir(Path dir) {
			//nothing   
		};
		
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

