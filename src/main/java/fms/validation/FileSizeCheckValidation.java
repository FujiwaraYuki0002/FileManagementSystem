package fms.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.web.multipart.MultipartFile;

import fms.annotation.FileSizeCheck;
import fms.domain.FileDomain;
import fms.form.FileInputForm;

/**
 * ファイルサイズチェック用バリデーション
 *
 * @author 髙橋 真澄
 */
public class FileSizeCheckValidation implements ConstraintValidator<FileSizeCheck, FileInputForm> {

    /**
     * ファイルサイズチェック
     *
     * @author 髙橋 真澄
     */
    @Override
    public boolean isValid(FileInputForm value, ConstraintValidatorContext context) {

        if (value.getFile() == null || value.getFile().isEmpty()) {
            // ファイルがない場合は、バリデーションをスキップまたは適切に処理
            return true;
        }

        // ファイルサイズが500MBより大きならエラー
        for (MultipartFile file : value.getFile()) {
            if (file.getSize() > FileDomain.MAX_FILE_SIZE) {
                return false;
            }
        }
        return true;
    }
}
