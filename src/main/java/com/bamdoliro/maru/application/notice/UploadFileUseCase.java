package com.bamdoliro.maru.application.notice;

import com.bamdoliro.maru.domain.notice.domain.Notice;
import com.bamdoliro.maru.domain.user.domain.User;
import com.bamdoliro.maru.infrastructure.s3.UploadFileService;
import com.bamdoliro.maru.infrastructure.s3.constants.FolderConstant;
import com.bamdoliro.maru.infrastructure.s3.dto.response.UploadResponse;
import com.bamdoliro.maru.infrastructure.s3.exception.FileSizeLimitExceededException;
import com.bamdoliro.maru.infrastructure.s3.exception.MediaTypeMismatchException;
import com.bamdoliro.maru.shared.annotation.UseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import static com.bamdoliro.maru.shared.constants.FileConstant.MB;

@RequiredArgsConstructor
@UseCase
public class UploadFileUseCase {

    private final UploadFileService uploadFileService;

    public UploadResponse execute(User user, MultipartFile noticeFile) {
        return uploadFileService.execute(noticeFile, FolderConstant.NOTICE_FILE, user.getUuid().toString(), file -> {
            if (file.getSize() > 20 * MB) {
                throw new FileSizeLimitExceededException();
            }
        });
    }
}
