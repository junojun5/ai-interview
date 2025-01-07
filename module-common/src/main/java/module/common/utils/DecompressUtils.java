package module.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import module.common.enums.CompressFileType;
import module.common.exception.ForbiddenException;
import module.common.exception.InternalServerException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ObjectUtils;

@Slf4j
public class DecompressUtils {

    /**
     * 지정된 압축 형식으로 파일이나 디렉토리 목록을 압축 해제해 지정된 대상 경로에 저장
     *
     * @param targetPath      압축 해제할 압축 파일
     * @param destinationPath 압축된 파일을 저장할 대상 경로 (null 입력시, 압축하려는 파일이 위치한 경로에 압축파일 저장)
     */
    public static void saveDecompressFile(Path targetPath, Path destinationPath) {
        // 압축해제할 파일 없으면 throw
        if (ObjectUtils.isEmpty(targetPath)) {
            throw new ForbiddenException("압축 해제할 대상 파일이 존재하지 않습니다.");
        }

        String extension = FileNameUtils.getExtension(targetPath);
        destinationPath = initializeDestPath(targetPath, destinationPath);

        if (CompressFileType.ZIP.getExtension().equals(extension)) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(targetPath.toFile()));
                ZipArchiveInputStream zais = new ArchiveStreamFactory().createArchiveInputStream(
                    CompressFileType.ZIP.getExtension(), is)) {
                decompressDirectoriesAndFiles(destinationPath, zais);
            } catch (IOException | ArchiveException e) {
                throw new InternalServerException("ZIP 파일 압축 해제 중 오류가 발생했습니다: " + targetPath);
            }
        } else if (CompressFileType.TAR.getExtension().equals(extension)) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(targetPath.toFile()));
                TarArchiveInputStream tais = new ArchiveStreamFactory().createArchiveInputStream(
                    CompressFileType.TAR.getExtension(), is)) {
                decompressDirectoriesAndFiles(destinationPath, tais);
            } catch (IOException | ArchiveException e) {
                throw new InternalServerException("TAR 파일 압축 해제 중 오류가 발생했습니다: " + targetPath);
            }
        } else {
            throw new ForbiddenException("지원되지 않는 압축 형식입니다: " + extension);
        }
    }

    /**
     * 압축파일 저장할 대상 경로 반환
     *
     * @param targetPath      압축 해제할 파일 또는 디렉토리 항목
     * @param destinationPath
     * @return
     */
    private static Path initializeDestPath(Path targetPath, Path destinationPath) {
        // destinationPath 없으면, 압축할 파일과 동일한 경로 반환
        destinationPath = ObjectUtils.isEmpty(destinationPath) ?
            Path.of(targetPath.getParent().toString()) :
            destinationPath;

        // 상위 디렉토리 생성
        String targetFileName = FileNameUtils.getBaseName(targetPath);
        Path dirPath = Path.of(destinationPath + File.separator + targetFileName);

        int cnt = 1;
        while (dirPath.toFile().exists()) {
            dirPath = dirPath.resolveSibling(targetFileName + "_" + cnt);
            cnt++;
        }

        if (!dirPath.toFile().mkdirs()) {
            throw new ForbiddenException("디렉토리를 생성할 수 없습니다: " + dirPath);
        }

        return dirPath;
    }

    /**
     * 압축해제할 파일 또는 폴더 outputStream에 추가
     *
     * @param destinationPath 저장될 경로
     * @param ais             압축파일 inputstream
     */
    private static <T extends ArchiveInputStream> void decompressDirectoriesAndFiles(
        Path destinationPath, T ais) {
        try {
            ArchiveEntry entry = ais.getNextEntry();
            do {
                if (ValidUtils.isCheckExtensionForMac(entry.getName())) {
                    entry = ais.getNextEntry();
                    continue;
                }

                File saveFile = new File(destinationPath.toFile(), entry.getName());

                if (entry.isDirectory()) {
                    if (!saveFile.mkdirs()) {
                        throw new ForbiddenException("디렉토리를 생성할 수 없습니다: " + saveFile);
                    }
                } else {
                    try (OutputStream os = new BufferedOutputStream(
                        new FileOutputStream(saveFile))) {
                        IOUtils.copy(ais, os);
                    } catch (IOException e) {
                        throw new InternalServerException("파일을 저장하는 중 오류가 발생했습니다: " + saveFile);
                    }
                }

                entry = ais.getNextEntry();
            } while (entry != null);
        } catch (IOException e) {
            throw new InternalServerException("압축 파일 처리 중 오류가 발생했습니다.");
        }
    }
}
