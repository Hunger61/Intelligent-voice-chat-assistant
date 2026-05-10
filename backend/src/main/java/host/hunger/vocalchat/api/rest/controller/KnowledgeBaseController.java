package host.hunger.vocalchat.api.rest.controller;

import host.hunger.vocalchat.api.rest.annotation.AutoResult;
import host.hunger.vocalchat.api.rest.annotation.OperateLog;
import host.hunger.vocalchat.api.rest.dto.KnowledgeBaseConfigDTO;
import host.hunger.vocalchat.api.rest.vo.KnowledgeBaseVO;
import host.hunger.vocalchat.application.service.KnowledgeBaseApplicationService;
import host.hunger.vocalchat.domain.model.user.UserId;
import host.hunger.vocalchat.shared.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/knowledge-base")
public class KnowledgeBaseController {

    private final KnowledgeBaseApplicationService knowledgeBaseApplicationService;

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
        var kb = knowledgeBaseApplicationService.getKnowledgeBaseById(
                new host.hunger.vocalchat.domain.model.knowledgeabase.KnowledgeBaseId(id));
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
        knowledgeBaseApplicationService.deleteKnowledgeBase(id);
    }
}
