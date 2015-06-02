package org.educoins.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;

public class IO {

	public static void createDirectory(String path) throws IOException {
		IO.createDirectory(Paths.get(path));
	}

	public static void createDirectory(Path path) throws IOException {
		if (Files.exists(path) && Files.isDirectory(path)) {
			System.out.println("IO.createDirectory: directory already exists");
			return;
		}
		Files.createDirectories(path);
	}

	public static void createFile(String path) throws IOException {
		IO.createFile(Paths.get(path));
	}

	public static void createFile(Path path) throws IOException {
		if (Files.exists(path) && Files.isRegularFile(path)) {
			System.out.println("IO.createFile: file already exists");
			return;
		}
		Files.createFile(path);
	}

	public static void deleteDirectory(String path) throws IOException {
		IO.deleteDirectory(Paths.get(path));
	}

	public static void deleteDirectory(Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public static void writeToFile(String path, String content) throws IOException {
		IO.writeToFile(Paths.get(path), content);
	}

	public static void writeToFile(Path path, String content) throws IOException {
		BufferedWriter writer = Files.newBufferedWriter(path);
		writer.write(content);
		writer.close();
	}

	public static void appendToFile(String path, String content) throws IOException {
		IO.appendToFile(Paths.get(path), content);
	}

	public static void appendToFile(Path path, String content) throws IOException {
		if (!Files.exists(path)) {
			IO.writeToFile(path, content);
		} else {
			BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND);
			writer.write(content);
			writer.close();
		}
	}

	public static String readFromFile(String path) throws IOException {
		return IO.readFromFile(Paths.get(path));
	}

	public static String readFromFile(Path path) throws IOException {
		BufferedReader reader = Files.newBufferedReader(path);
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(System.lineSeparator());
		}
		String string = stringBuilder.toString();
		reader.close();
		return string;
	}

	public static void copyFile(String srcPath, String dstPath) throws IOException {
		IO.copyFile(Paths.get(srcPath), Paths.get(dstPath));
	}

	public static void copyFile(Path srcPath, Path dstPath) throws IOException {
		if (Files.exists(dstPath)) {
			System.out.println("IO.copyFile: file already exists");
			return;
		}
		Files.copy(srcPath, dstPath);
	}

}
