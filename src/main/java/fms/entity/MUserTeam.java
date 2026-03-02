package fms.entity;

import java.io.Serializable;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import lombok.Data;

/**
 * 所属管理エンティティ
 *
 * @author 大塚 月愛
 */
@Component
@SessionScope
@Data
public class MUserTeam extends BaseEntity implements Serializable {

    /** ユーザID */
    private String userId;

    /** teamId */
    private Integer teamId;

}
