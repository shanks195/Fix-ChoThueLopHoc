package vn.thuephonghoc.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
public class JobDTO implements Serializable {

    private Long id;

    private Long sort;

    private String name;

    private Boolean enabled;

    private DeptDto dept;

    // If the branch has the same department, the name of the ADMIN department is displayed
    private String deptSuperiorName;

    public JobDTO(String name, Boolean enabled) {
        this.name = name;
        this.enabled = enabled;
    }

    private Timestamp createTime;
}
