package org.example.helloworld.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @TableField("create_time")
    private LocalDateTime createTime;

}
