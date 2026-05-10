package host.hunger.vocalchat.application.service;

import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseFile;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseFileId;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.repository.KnowledgeBaseFileRepository;
import host.hunger.vocalchat.domain.service.ObjectStorageService;
import host.hunger.vocalchat.infrastructure.exception.BaseException;
import host.hunger.vocalchat.shared.enums.ErrorEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeBaseFileApplicationService {

    private final KnowledgeBaseFileRepository knowledgeBaseFileRepository;
    private final ObjectStorageService objectStorageService;

    public List<KnowledgeBaseFile> getFilesByKnowledgeBaseId(KnowledgeBaseId knowledgeBaseId) {
        return knowledgeBaseFileRepository.findByKnowledgeBaseId(knowledgeBaseId);
    }

    @Transactional
    public KnowledgeBaseFile uploadFile(String knowledgeBaseId, MultipartFile file) {
        KnowledgeBaseId kbId = new KnowledgeBaseId(knowledgeBaseId);
        String originalFilename = file.getOriginalFilename();
        String fileType = extractFileType(originalFilename);
        String storageKey = knowledgeBaseId + "/" + UUID.randomUUID() + "_" + originalFilename;

        KnowledgeBaseFile kbFile = new KnowledgeBaseFile(
                kbId, originalFilename, fileType, file.getSize(), storageKey);

        try (InputStream content = file.getInputStream()) {
            objectStorageService.putObject(
                    "knowledge-base",
                    storageKey,
                    content,
                    file.getSize(),
                    file.getContentType(),
                    null
            );
            kbFile.setStatus("COMPLETED");
        } catch (Exception e) {
            log.warn("ObjectStorageService not available, file metadata saved. storageKey={}, error={}",
                    storageKey, e.getMessage());
            kbFile.setStatus("UPLOADED");
        }

        knowledgeBaseFileRepository.save(kbFile);
        return kbFile;
    }

    @Transactional
    public void deleteFile(String fileId) {
        KnowledgeBaseFileId kbFileId = new KnowledgeBaseFileId(fileId);
        KnowledgeBaseFile file = knowledgeBaseFileRepository.findById(kbFileId)
                .orElseThrow(() -> new BaseException(ErrorEnum.COMMON_ERROR));
        try {
            objectStorageService.deleteObject("knowledge-base", file.getStorageKey());
        } catch (Exception e) {
            log.warn("ObjectStorageService delete failed, removing metadata anyway. storageKey={}",
                    file.getStorageKey());
        }
        knowledgeBaseFileRepository.delete(kbFileId);
    }

    @Transactional
    public void deleteFilesByKnowledgeBaseId(String knowledgeBaseId) {
        KnowledgeBaseId kbId = new KnowledgeBaseId(knowledgeBaseId);
        List<KnowledgeBaseFile> files = knowledgeBaseFileRepository.findByKnowledgeBaseId(kbId);
        for (KnowledgeBaseFile file : files) {
            try {
                objectStorageService.deleteObject("knowledge-base", file.getStorageKey());
            } catch (Exception e) {
                log.warn("ObjectStorageService delete failed for storageKey={}", file.getStorageKey());
            }
        }
        knowledgeBaseFileRepository.deleteByKnowledgeBaseId(kbId);
    }

    private String extractFileType(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "unknown";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
