package org.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("project")
public class ProjectEntity {
    /** 主键，自增 */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** 项目名称 */
    private String name;

    /** 项目状态 */
    private String status;

    /** 项目封面 */
    private String cover;

    /** 创建时间 */
    @TableField(value = "create_time", update = "false") // 更新时不修改此字段
    private LocalDateTime createTime;

}
