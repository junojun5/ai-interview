package module.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import module.common.enums.CompressFileType;
import module.common.exception.ForbiddenException;
import module.common.exception.InternalServerException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Slf4j
public class CompressUtils {

    /**
     * 지정된 압축 형식으로 파일이나 디렉토리 목록을 압축해 지정된 대상 경로에 저장
     *
     * @param targetPaths      압축할 파일 또는 디렉토리 목록
     * @param destinationPath  압축된 파일을 저장할 대상 경로 (null 입력시, 압축하려는 파일이 위치한 경로에 압축파일 저장)
     * @param compressFileType 사용할 압축 형식 (ZIP, TAR)
     */
    public static Path saveCompressFile(List<Path> targetPaths, Path destinationPath,
        CompressFileType compressFileType) {
        if (CollectionUtils.isEmpty(targetPaths)) {
            throw new ForbiddenException("압축할 대상 파일 또는 디렉토리가 존재하지 않습니다.");
        }

        Path destPath = initializeDestPath(targetPaths, destinationPath, compressFileType);

        if (compressFileType == CompressFileType.ZIP) {
            try (ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(
                new BufferedOutputStream(new FileOutputStream(destPath.toFile())))) {
                compressDirectoriesAndFiles(targetPaths, zipOut);
            } catch (IOException e) {
                throw new InternalServerException("ZIP 파일 압축 중 오류가 발생했습니다.");
            }
        } else if (compressFileType == CompressFileType.TAR) {
            try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(
                new BufferedOutputStream(new FileOutputStream(destPath.toFile())))) {
                compressDirectoriesAndFiles(targetPaths, tarOut);
            } catch (IOException e) {
                throw new InternalServerException("TAR 파일 압축 중 오류가 발생했습니다.");
            }
        }

        return destPath;
    }

    /**
     * 지정된 압축 형식을 사용하여 파일이나 디렉토리 목록을 압축하여 Byte[]로 반환
     *
     * @param targetPaths      압축할 파일 또는 디렉토리 목록
     * @param compressFileType 사용할 압축 형식 (ZIP, TAR)
     * @return 압축이 성공하면 true, 그렇지 않으면 false를 반환
     */
    public static byte[] compressFileToByteArray(List<Path> targetPaths,
        CompressFileType compressFileType) {
        if (CollectionUtils.isEmpty(targetPaths)) {
            throw new ForbiddenException("압축할 대상 파일 또는 디렉토리가 존재하지 않습니다.");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (compressFileType == CompressFileType.ZIP) {
            try (ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(baos)) {
                zipOut.setEncoding("UTF-8");
                compressDirectoriesAndFiles(targetPaths, zipOut);
            } catch (IOException e) {
                throw new InternalServerException("ZIP 파일 압축 중 오류가 발생했습니다.");
            }
        } else if (compressFileType == CompressFileType.TAR) {
            try (TarArchiveOutputStream tarOut = new TarArchiveOutputStream(baos)) {
                tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
                compressDirectoriesAndFiles(targetPaths, tarOut);
            } catch (IOException e) {
                throw new InternalServerException("TAR 파일 압축 중 오류가 발생했습니다.");
            }
        }

        return baos.toByteArray();
    }

    /**
     * 압축파일 저장할 대상 경로 반환
     *
     * @param targetPaths      압축할 파일 또는 디렉토리 목록
     * @param destinationPath  압축된 파일을 저장할 대상 경로
     * @param compressFileType 사용할 압축 형식 (예: ZIP, TAR)
     * @return 압축 파일 저장 경로 + 파일명
     */
    private static Path initializeDestPath(List<Path> targetPaths, Path destinationPath,
        CompressFileType compressFileType) {
        // 저장할 압축 파일명 반환
        // 단일 파일 압축시 "압축할 파일명", 여러개의 파일 압축시 "archive"로 반환
        String saveCompressFileName = targetPaths.size() == 1 ?
            getSaveFileName(FileNameUtils.getBaseName(targetPaths.get(0)), compressFileType) :
            getSaveFileName("archive", compressFileType);

        // destinationPath 없으면, 압축할 파일이 위치한 경로 반환
        destinationPath = ObjectUtils.isEmpty(destinationPath) ? Path.of(
            targetPaths.get(0).getParent().toString() + File.separator + saveCompressFileName)
            : Path.of(
                destinationPath + File.separator + saveCompressFileName
            );

        // 파일명 중복되면 파일명 수정
        int cnt = 1;
        String baseName = FileNameUtils.getBaseName(destinationPath);
        String extension = FileNameUtils.getExtension(destinationPath);

        while (destinationPath.toFile().exists()) {
            String changeFileName = baseName + "_" + cnt + "." + extension;
            destinationPath = destinationPath.resolveSibling(changeFileName);
            cnt++;
        }

        return destinationPath;
    }

    /**
     * 원본 파일 이름과 압축 형식을 기반으로 압축된 파일 이름을 반환
     *
     * @param fileName         원본 파일 이름
     * @param compressFileType 사용할 압축 형식 (예: ZIP, TAR)
     * @return 압축된 파일 이름
     */
    private static String getSaveFileName(String fileName, CompressFileType compressFileType) {
        if (compressFileType == CompressFileType.ZIP) {
            fileName += "." + CompressFileType.ZIP.getExtension();
        } else if (compressFileType == CompressFileType.TAR) {
            fileName += "." + CompressFileType.TAR.getExtension();
        } else {
            throw new ForbiddenException("지원되지 않는 압축 형식입니다: " + compressFileType);
        }

        return fileName;
    }

    /**
     * 압축할 파일 또는 폴더 outputStream에 추가
     *
     * @param targetPaths 압축할 파일 목록
     * @param os          outputStream 타입
     */
    private static <T extends ArchiveOutputStream<?>> void compressDirectoriesAndFiles(
        List<Path> targetPaths, T os) {
        for (Path targetPath : targetPaths) {
            if (Files.exists(targetPath)) {

                File targetFile = targetPath.toFile();
                if (ValidUtils.isCheckExtensionForMac(targetFile.getName())) {
                    return;
                }

                if (targetFile.isDirectory()) {
                    addFolder(targetPath, "", os);
                } else if (targetFile.isFile()) {
                    addFile(targetPath, "", os);
                }
            } else {
                throw new ForbiddenException("대상 경로가 존재하지 않습니다: " + targetPath);
            }
        }
    }

    /**
     * 폴더 및 폴더의 하위항목 내용을 zipOutputStream에 추가
     *
     * @param targetFolderPath 압축할 폴더의 경로
     * @param parentEntryName  압축 파일 내에서 저장될 이름
     * @param os               출력 스트림
     */
    private static <T extends ArchiveOutputStream<?>> void addFolder(Path targetFolderPath,
        String parentEntryName,
        T os) {
        File folder = targetFolderPath.toFile();

        String entryName = parentEntryName + folder.getName() + File.separator;
        putArchiveEntry(entryName, targetFolderPath.toFile().length(), os);

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolder(Path.of(file.getAbsolutePath()), entryName, os);
            } else if (file.isFile()) {
                addFile(Path.of(file.getAbsolutePath()), entryName, os);
            }
        }
    }

    /**
     * 파일을 zipOutputStream에 추가
     *
     * @param targetFilePath  압축할 파일의 경로
     * @param parentEntryName 파일이 압축 파일 내에서 저장될 상위폴더명
     * @param os              출력 스트림
     */
    private static <T extends ArchiveOutputStream<?>> void addFile(Path targetFilePath,
        String parentEntryName, T os) {
        File targetFile = targetFilePath.toFile();
        long fileSize = targetFilePath.toFile().length();

        try (InputStream is = new BufferedInputStream(new FileInputStream(targetFile))) {
            String entryName = parentEntryName + targetFile.getName();
            putArchiveEntry(entryName, fileSize, os);

            IOUtils.copy(is, os);

            os.closeArchiveEntry();
        } catch (IOException e) {
            throw new InternalServerException("파일을 압축 스트림에 추가하는 중 오류가 발생했습니다: " + targetFilePath);
        }
    }

    private static <T extends ArchiveOutputStream<?>> void putArchiveEntry(String entryName,
        long fileSize, T os) {
        try {
            if (os instanceof ZipArchiveOutputStream) {
                ZipArchiveOutputStream zos = (ZipArchiveOutputStream) os;
                zos.setEncoding("UTF-8");

                ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(entryName);
                zipArchiveEntry.setSize(fileSize);
                zos.putArchiveEntry(zipArchiveEntry);
            } else if (os instanceof TarArchiveOutputStream) {
                TarArchiveOutputStream tos = (TarArchiveOutputStream) os;
                tos.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);

                TarArchiveEntry tarArchiveEntry = new TarArchiveEntry(entryName);
                tarArchiveEntry.setSize(fileSize);
                tos.putArchiveEntry(tarArchiveEntry);
            }
        } catch (IOException e) {
            throw new InternalServerException("아카이브 항목 추가 중 오류가 발생했습니다: " + entryName);
        }
    }
}
