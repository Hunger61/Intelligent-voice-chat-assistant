package host.hunger.vocalchat.api.rest.controller;

import host.hunger.vocalchat.api.rest.annotation.AutoResult;
import host.hunger.vocalchat.api.rest.annotation.OperateLog;
import host.hunger.vocalchat.api.rest.dto.KnowledgeBaseConfigDTO;
import host.hunger.vocalchat.api.rest.vo.KnowledgeBaseFileVO;
import host.hunger.vocalchat.api.rest.vo.KnowledgeBaseVO;
import host.hunger.vocalchat.application.service.KnowledgeBaseApplicationService;
import host.hunger.vocalchat.application.service.KnowledgeBaseFileApplicationService;
import host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.shared.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/knowledge-base")
public class KnowledgeBaseController {

    private final KnowledgeBaseApplicationService knowledgeBaseApplicationService;
    private final KnowledgeBaseFileApplicationService knowledgeBaseFileApplicationService;

    @PostMapping
    @AutoResult
    @OperateLog("创建知识库")
    public void createKnowledgeBase(@RequestBody KnowledgeBaseConfigDTO dto) {
        knowledgeBaseApplicationService.createKnowledgeBase(dto.getName(), dto.getDescription());
    }

    @GetMapping
    @AutoResult
    @OperateLog("查询知识库列表")
    public List<KnowledgeBaseVO> getKnowledgeBases() {
        UserId userId = UserContext.require().getId();
        return knowledgeBaseApplicationService.getKnowledgeBasesByUserId(userId).stream()
                .map(kb -> new KnowledgeBaseVO(
                        kb.getId().toString(),
                        kb.getName().getName(),
                        kb.getDescription() != null ? kb.getDescription().getDescription() : null,
                        kb.getStatus(),
                        kb.getDocumentCount(),
                        kb.getChunkCount(),
                        kb.getCreatedAt(),
                        kb.getUpdatedAt()
                ))
                .toList();
    }

    @GetMapping("/{id}")
    @AutoResult
    @OperateLog("查询知识库详情")
    public KnowledgeBaseVO getKnowledgeBase(@PathVariable String id) {
        var kb = knowledgeBaseApplicationService.getKnowledgeBaseById(new KnowledgeBaseId(id));
        return new KnowledgeBaseVO(
                kb.getId().toString(),
                kb.getName().getName(),
                kb.getDescription() != null ? kb.getDescription().getDescription() : null,
                kb.getStatus(),
                kb.getDocumentCount(),
                kb.getChunkCount(),
                kb.getCreatedAt(),
                kb.getUpdatedAt()
        );
    }

    @PutMapping("/{id}")
    @AutoResult
    @OperateLog("修改知识库")
    public void modifyKnowledgeBase(@PathVariable String id, @RequestBody KnowledgeBaseConfigDTO dto) {
        knowledgeBaseApplicationService.modifyKnowledgeBase(id, dto.getName(), dto.getDescription());
    }

    @DeleteMapping("/{id}")
    @AutoResult
    @OperateLog("删除知识库")
    public void deleteKnowledgeBase(@PathVariable String id) {
        knowledgeBaseFileApplicationService.deleteFilesByKnowledgeBaseId(id);
        knowledgeBaseApplicationService.deleteKnowledgeBase(id);
    }

    // ─── 文件管理 ─────────────────────────────────────────────

    @PostMapping("/{id}/file")
    @AutoResult
    @OperateLog("上传文件到知识库")
    public KnowledgeBaseFileVO uploadFile(@PathVariable String id, @RequestParam("file") MultipartFile file) {
        var kbFile = knowledgeBaseFileApplicationService.uploadFile(id, file);
        return toFileVO(kbFile);
    }

    @GetMapping("/{id}/file")
    @AutoResult
    @OperateLog("查询知识库文件列表")
    public List<KnowledgeBaseFileVO> getFiles(@PathVariable String id) {
        return knowledgeBaseFileApplicationService.getFilesByKnowledgeBaseId(new KnowledgeBaseId(id)).stream()
                .map(this::toFileVO)
                .toList();
    }

    @DeleteMapping("/{id}/file/{fileId}")
    @AutoResult
    @OperateLog("删除知识库文件")
    public void deleteFile(@PathVariable String id, @PathVariable String fileId) {
        knowledgeBaseFileApplicationService.deleteFile(fileId);
    }

    @GetMapping("/{id}/file/{fileId}/status")
    @AutoResult
    @OperateLog("查询文件处理状态")
    public KnowledgeBaseFileVO getFileStatus(@PathVariable String id, @PathVariable String fileId) {
        var file = knowledgeBaseFileApplicationService.getFilesByKnowledgeBaseId(new KnowledgeBaseId(id)).stream()
                .filter(f -> f.getId().toString().equals(fileId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("File not found"));
        return toFileVO(file);
    }

    private KnowledgeBaseFileVO toFileVO(host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseFile f) {
        return new KnowledgeBaseFileVO(
                f.getId().toString(),
                f.getKnowledgeBaseId().toString(),
                f.getFileName(),
                f.getFileType(),
                f.getFileSize(),
                f.getStatus(),
                f.getChunkCount(),
                f.getCreatedAt(),
                f.getUpdatedAt()
        );
    }
}
